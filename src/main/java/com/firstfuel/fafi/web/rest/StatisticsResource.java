package com.firstfuel.fafi.web.rest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import com.firstfuel.fafi.service.FranchiseService;
import com.firstfuel.fafi.service.MatchService;
import com.firstfuel.fafi.service.PlayerService;
import com.firstfuel.fafi.service.SeasonService;
import com.firstfuel.fafi.service.TieMatchService;
import com.firstfuel.fafi.service.TieTeamService;
import com.firstfuel.fafi.service.dto.FranchiseDTO;
import com.firstfuel.fafi.service.dto.FranchiseStandingsDTO;
import com.firstfuel.fafi.service.dto.MatchDTO;
import com.firstfuel.fafi.service.dto.PlayerDTO;
import com.firstfuel.fafi.service.dto.PlayerStandingsDTO;
import com.firstfuel.fafi.service.dto.SeasonDTO;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
import com.firstfuel.fafi.service.dto.TieTeamDTO;

/**
 * <p>
 * </p>
 *
 * @author Narendra Kumar
 * @version 1.0
 * @since 27-Dec-2017
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsResource.class);

    @Autowired
    private FranchiseService franchiseService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private SeasonService seasonService;

    @Autowired
    private TieMatchService tieMatchService;

    @Autowired
    private TieTeamService tieTeamService;

    /**
     * GET  //seasons/{seasonId}/franchise-standings : get franchise standings for a given season.
     *
     * @param seasonId the id of the season
     * @return the ResponseEntity with status 200 (OK) and the list of Franchise standings in body
     */
    @GetMapping("/seasons/{seasonId}/franchise-standings")
    @Timed
    public ResponseEntity<List<FranchiseStandingsDTO>> getFranchiseStandings(@PathVariable Long seasonId) {
        LOGGER.debug("REST request to get Franchise standings for seasonId : {}", seasonId);

        final List<FranchiseDTO> franchiseDTOList = franchiseService.getAllFranchiseBySeason(seasonId);
        LOGGER.debug("franchiseDTOList : {}", franchiseDTOList);

        final Map<Long, List<MatchDTO>> franchiseIdToMatches = matchService.getAllMatchesByFranchises(franchiseDTOList);

        final Map<Long, Double> franchiseIdToPoints = franchiseDTOList
            .stream()
            .collect(Collectors.toMap(FranchiseDTO::getId, o -> calculatePoints(o.getId(), franchiseIdToMatches.get(o.getId()))));
        LOGGER.debug("franchiseIdToPoints : {}", franchiseIdToPoints);

        final Map<Long, List<Boolean>> franchiseIdToForm = franchiseIdToMatches.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, o -> evaluateCurrentForm(o.getKey(), o.getValue())));

        AtomicInteger rank = new AtomicInteger(0);
        List<FranchiseStandingsDTO> franchiseStandingsDTOList = franchiseDTOList.stream()
            .map(franchiseDTO -> {
                List<MatchDTO> matches = franchiseIdToMatches.get(franchiseDTO.getId());
                FranchiseStandingsDTO franchiseStandingsDTO = new FranchiseStandingsDTO();
                franchiseStandingsDTO.setFranchise(franchiseDTO);
                franchiseStandingsDTO.setTotalMatchesPlayed(Objects.nonNull(matches) ? matches.size() : 0);
                franchiseStandingsDTO.setTotalPoints(franchiseIdToPoints.get(franchiseDTO.getId()));
                franchiseStandingsDTO.setCurrentForm(franchiseIdToForm.get(franchiseDTO.getId()));
                if (CollectionUtils.isEmpty(matches)) {
                    franchiseStandingsDTO.setMatchWiseDetails(Collections.emptyList());
                } else {
                    populateMatchWiseDetails(franchiseDTO, franchiseStandingsDTO, matches);
                }
                return franchiseStandingsDTO;
            })
            .sorted(Comparator.comparingDouble(FranchiseStandingsDTO::getTotalPoints).thenComparingInt(FranchiseStandingsDTO::getTotalMatchesPlayed).reversed())
            .peek(franchiseStandingsDTO -> franchiseStandingsDTO.setRank(rank.incrementAndGet()))
            .collect(Collectors.toList());
        LOGGER.debug("franchiseStandingsDTOList : {}", franchiseStandingsDTOList);
        return new ResponseEntity<>(franchiseStandingsDTOList, HttpStatus.OK);
    }

    private Double calculatePoints(Long franchiseId, List<MatchDTO> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            return 0d;
        }
        return matches.stream().mapToDouble(match -> {
            if (Objects.equals(franchiseId, match.getFranchise1Id())) {
                return Objects.nonNull(match.getPointsForFranchise1()) ? match.getPointsForFranchise1() : 0;
            } else {
                return Objects.nonNull(match.getPointsForFranchise2()) ? match.getPointsForFranchise2() : 0;
            }
        }).sum();
    }

    private List<Boolean> evaluateCurrentForm(Long franchiseId, List<MatchDTO> matches) {
        return matches.stream().filter(matchDTO -> Objects.nonNull(matchDTO.getWinnerId())).map(match -> {
            if (Objects.equals(franchiseId, match.getWinnerId())) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }).collect(Collectors.toList());
    }

    private void populateMatchWiseDetails(FranchiseDTO franchiseDTO, FranchiseStandingsDTO franchiseStandingsDTO, final List<MatchDTO> matches) {
        matches.stream().filter(matchDTO -> Objects.nonNull(matchDTO.getWinnerId())).forEach(match -> {
            boolean result;
            Double points = 0d;
            if (Objects.equals(franchiseDTO.getId(), match.getWinnerId())) {
                result = Boolean.TRUE;
            } else {
                result = Boolean.FALSE;
            }
            if (Objects.equals(franchiseDTO.getId(), match.getFranchise1Id())) {
                points = match.getPointsForFranchise1();
            } else if (Objects.equals(franchiseDTO.getId(), match.getFranchise2Id())) {
                points = match.getPointsForFranchise2();
            }
            franchiseStandingsDTO.addMatchWiseDetails(match.getId(), result, points);
        });
    }

    /**
     * GET  //seasons/{seasonId}/player-standings : get player standings for a given season.
     *
     * @param seasonId the id of the season
     * @return the ResponseEntity with status 200 (OK) and the list of Player standings in body
     */
    @GetMapping("/seasons/{seasonId}/player-standings")
    @Timed
    public ResponseEntity<List<PlayerStandingsDTO>> getPlayerStandings(@PathVariable Long seasonId) {
        LOGGER.debug("REST request to get Player standings for seasonId : {}", seasonId);

        final SeasonDTO seasonDTO = seasonService.findOne(seasonId);

        final List<PlayerDTO> playerDTOList = playerService.getAllPlayerBySeason(seasonDTO);
        LOGGER.debug("playerDTOList : {}", playerDTOList);

        final Map<Long, List<TieMatchDTO>> playerIdToTieMatches = tieMatchService.getAllTieMatchesByPlayers(playerDTOList);

        final Map<Long, List<Boolean>> playerIdToForm = playerIdToTieMatches.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, o -> evaluatePlayerCurrentForm(o.getKey(), o.getValue())));

        AtomicInteger rank = new AtomicInteger(0);
        List<PlayerStandingsDTO> playerStandingsDTOList = playerDTOList.stream()
            .map(playerDTO -> {
                List<TieMatchDTO> matches = playerIdToTieMatches.get(playerDTO.getId());
                PlayerStandingsDTO playerStandingsDTO = new PlayerStandingsDTO();
                FranchiseDTO franchiseDTO = new FranchiseDTO();
                franchiseDTO.setId(playerDTO.getFranchiseId());
                franchiseDTO.setName(playerDTO.getFranchiseName());
                playerStandingsDTO.setFranchise(franchiseDTO);
                playerStandingsDTO.setPlayer(playerDTO);
                playerStandingsDTO.setTotalMatchesPlayed(Objects.nonNull(matches) ? matches.size() : 0);

                playerStandingsDTO.setCurrentForm(playerIdToForm.get(playerDTO.getId()));
                Double totalPoints = 0d;
                if (CollectionUtils.isEmpty(matches)) {
                    playerStandingsDTO.setMatchWiseDetails(Collections.emptyList());
                } else {
                    totalPoints = populatePlayerTieMatchWiseDetailsAndCalculateTotalPoints(playerDTO, playerStandingsDTO, matches);
                }
                playerStandingsDTO.setTotalPoints(totalPoints);
                return playerStandingsDTO;
            })
            .sorted(Comparator.comparingDouble(PlayerStandingsDTO::getTotalPoints).thenComparingInt(PlayerStandingsDTO::getTotalMatchesPlayed).reversed())
            .peek(playerStandingsDTO -> playerStandingsDTO.setRank(rank.incrementAndGet()))
            .collect(Collectors.toList());
        LOGGER.debug("playerStandingsDTOList : {}", playerStandingsDTOList);
        return new ResponseEntity<>(playerStandingsDTOList, HttpStatus.OK);
    }

    private List<Boolean> evaluatePlayerCurrentForm(Long playerId, List<TieMatchDTO> tieMatches) {
        PlayerDTO playerDTO = playerService.findOne(playerId);
        return tieMatches.stream().filter(matchDTO -> Objects.nonNull(matchDTO.getWinnerId())).map(tieMatch -> {
            TieTeamDTO winnerTieTeam = tieTeamService.findOne(tieMatch.getWinnerId());
            if (CollectionUtils.isNotEmpty(winnerTieTeam.getTiePlayers()) && winnerTieTeam.getTiePlayers().contains(playerDTO)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }).collect(Collectors.toList());
    }

    private Double populatePlayerTieMatchWiseDetailsAndCalculateTotalPoints(PlayerDTO playerDTO, PlayerStandingsDTO playerStandingsDTO, final List<TieMatchDTO> tieMatches) {
        AtomicDouble totalPoints = new AtomicDouble(0d);
        tieMatches.stream().filter(matchDTO -> Objects.nonNull(matchDTO.getWinnerId())).forEach(tieMatch -> {
            boolean result;
            Double points = 0d;
            if (Objects.equals(playerDTO.getId(), tieMatch.getWinnerId())) {
                result = Boolean.TRUE;
            } else {
                result = Boolean.FALSE;
            }
            TieTeamDTO tieTeam1 = tieTeamService.findOne(tieMatch.getTeam1Id());
            TieTeamDTO tieTeam2 = tieTeamService.findOne(tieMatch.getTeam2Id());
            if (CollectionUtils.isNotEmpty(tieTeam1.getTiePlayers()) && tieTeam1.getTiePlayers().contains(playerDTO)) {
                points = tieMatch.getPointsForTieTeam1();
            } else if (CollectionUtils.isNotEmpty(tieTeam2.getTiePlayers()) && tieTeam2.getTiePlayers().contains(playerDTO)) {
                points = tieMatch.getPointsForTieTeam2();
            }
            totalPoints.addAndGet(points);
            playerStandingsDTO.addMatchWiseDetails(tieMatch.getId(), result, points);
        });
        return totalPoints.get();
    }
}
