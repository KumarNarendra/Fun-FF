package com.firstfuel.fafi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.domain.TieMatch;
import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.repository.TieMatchRepository;
import com.firstfuel.fafi.service.TieMatchService;
import com.firstfuel.fafi.service.dto.PlayerDTO;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
import com.firstfuel.fafi.service.mapper.PlayerMapper;
import com.firstfuel.fafi.service.mapper.TieMatchMapper;

/**
 * Service Implementation for managing TieMatch.
 */
@Service
@Transactional
public class TieMatchServiceImpl
    implements TieMatchService {

    private final Logger log = LoggerFactory.getLogger(TieMatchServiceImpl.class);

    @Autowired
    private TieMatchRepository tieMatchRepository;

    @Autowired
    private TieMatchMapper tieMatchMapper;

    @Autowired
    private PlayerMapper playerMapper;

    /**
     * Save a tieMatch.
     *
     * @param tieMatchDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TieMatchDTO save(TieMatchDTO tieMatchDTO) {
        log.debug("Request to save TieMatch : {}", tieMatchDTO);
        TieMatch tieMatch = tieMatchMapper.toEntity(tieMatchDTO);
        tieMatch = tieMatchRepository.save(tieMatch);
        return tieMatchMapper.toDto(tieMatch);
    }

    /**
     * Get all the tieMatches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TieMatchDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TieMatches");
        return tieMatchRepository.findAll(pageable).map(tieMatchMapper::toDto);
    }

    /**
     * Get one tieMatch by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TieMatchDTO findOne(Long id) {
        log.debug("Request to get TieMatch : {}", id);
        TieMatch tieMatch = tieMatchRepository.findOne(id);
        return tieMatchMapper.toDto(tieMatch);
    }

    /**
     * Delete the tieMatch by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TieMatch : {}", id);
        tieMatchRepository.delete(id);
    }

    @Override
    public Map<Long, List<TieMatchDTO>> getAllTieMatchesByPlayers(List<PlayerDTO> playerDTOList) {
        List<Player> players = playerMapper.toEntity(playerDTOList);
        List<TieMatch> tieMatches = tieMatchRepository.getDistinctByTeam1_TiePlayersInOrTeam2_TiePlayersIn(players, players);
        log.debug("Tie Matches : {}", tieMatches);
        Map<TieTeam, List<TieMatch>> team1TieMatches = tieMatches.stream().collect(Collectors.groupingBy(TieMatch::getTeam1));
        log.debug("team1TieMatches : {}", team1TieMatches);
        Map<TieTeam, List<TieMatch>> team2TieMatches = tieMatches.stream().collect(Collectors.groupingBy(TieMatch::getTeam2));
        log.debug("team2TieMatches : {}", team2TieMatches);
        Map<Long, List<TieMatchDTO>> allMatchesByPlayer = Stream.concat(team1TieMatches.entrySet().stream(), team2TieMatches.entrySet().stream())
            .flatMap(entry -> entry.getKey().getTiePlayers().stream().collect(Collectors.toMap(o -> o, o -> entry.getValue())).entrySet().stream())
            .collect(Collectors.toMap(o -> o.getKey().getId(), o -> tieMatchMapper.toDto(o.getValue()), this::mergeFunction));
        log.debug("allTieMatchesByPlayer : {}", allMatchesByPlayer);
        return allMatchesByPlayer;
    }

    private List<TieMatchDTO> mergeFunction(List<TieMatchDTO> v1, List<TieMatchDTO> v2) {
        return Stream.concat(v1.stream(), v2.stream()).distinct().collect(Collectors.toList());
    }
}
