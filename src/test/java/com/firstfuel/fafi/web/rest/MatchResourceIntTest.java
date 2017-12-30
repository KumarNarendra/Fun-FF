package com.firstfuel.fafi.web.rest;

import com.firstfuel.fafi.FafiApp;

import com.firstfuel.fafi.domain.Match;
import com.firstfuel.fafi.domain.Tournament;
import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.repository.MatchRepository;
import com.firstfuel.fafi.service.MatchService;
import com.firstfuel.fafi.service.dto.MatchDTO;
import com.firstfuel.fafi.service.mapper.MatchMapper;
import com.firstfuel.fafi.web.rest.errors.ExceptionTranslator;
import com.firstfuel.fafi.service.dto.MatchCriteria;
import com.firstfuel.fafi.service.MatchQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.firstfuel.fafi.web.rest.TestUtil.sameInstant;
import static com.firstfuel.fafi.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MatchResource REST controller.
 *
 * @see MatchResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FafiApp.class)
public class MatchResourceIntTest {

    private static final ZonedDateTime DEFAULT_START_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Double DEFAULT_POINTS_FOR_FRANCHISE_1 = 1D;
    private static final Double UPDATED_POINTS_FOR_FRANCHISE_1 = 2D;

    private static final Double DEFAULT_POINTS_FOR_FRANCHISE_2 = 1D;
    private static final Double UPDATED_POINTS_FOR_FRANCHISE_2 = 2D;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchQueryService matchQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMatchMockMvc;

    private Match match;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MatchResource matchResource = new MatchResource(matchService, matchQueryService);
        this.restMatchMockMvc = MockMvcBuilders.standaloneSetup(matchResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Match createEntity(EntityManager em) {
        Match match = new Match()
            .startDateTime(DEFAULT_START_DATE_TIME)
            .endDateTime(DEFAULT_END_DATE_TIME)
            .pointsForFranchise1(DEFAULT_POINTS_FOR_FRANCHISE_1)
            .pointsForFranchise2(DEFAULT_POINTS_FOR_FRANCHISE_2);
        return match;
    }

    @Before
    public void initTest() {
        match = createEntity(em);
    }

    @Test
    @Transactional
    public void createMatch() throws Exception {
        int databaseSizeBeforeCreate = matchRepository.findAll().size();

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);
        restMatchMockMvc.perform(post("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isCreated());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeCreate + 1);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getStartDateTime()).isEqualTo(DEFAULT_START_DATE_TIME);
        assertThat(testMatch.getEndDateTime()).isEqualTo(DEFAULT_END_DATE_TIME);
        assertThat(testMatch.getPointsForFranchise1()).isEqualTo(DEFAULT_POINTS_FOR_FRANCHISE_1);
        assertThat(testMatch.getPointsForFranchise2()).isEqualTo(DEFAULT_POINTS_FOR_FRANCHISE_2);
    }

    @Test
    @Transactional
    public void createMatchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = matchRepository.findAll().size();

        // Create the Match with an existing ID
        match.setId(1L);
        MatchDTO matchDTO = matchMapper.toDto(match);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMatchMockMvc.perform(post("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMatches() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList
        restMatchMockMvc.perform(get("/api/matches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(match.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDateTime").value(hasItem(sameInstant(DEFAULT_START_DATE_TIME))))
            .andExpect(jsonPath("$.[*].endDateTime").value(hasItem(sameInstant(DEFAULT_END_DATE_TIME))))
            .andExpect(jsonPath("$.[*].pointsForFranchise1").value(hasItem(DEFAULT_POINTS_FOR_FRANCHISE_1.doubleValue())))
            .andExpect(jsonPath("$.[*].pointsForFranchise2").value(hasItem(DEFAULT_POINTS_FOR_FRANCHISE_2.doubleValue())));
    }

    @Test
    @Transactional
    public void getMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get the match
        restMatchMockMvc.perform(get("/api/matches/{id}", match.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(match.getId().intValue()))
            .andExpect(jsonPath("$.startDateTime").value(sameInstant(DEFAULT_START_DATE_TIME)))
            .andExpect(jsonPath("$.endDateTime").value(sameInstant(DEFAULT_END_DATE_TIME)))
            .andExpect(jsonPath("$.pointsForFranchise1").value(DEFAULT_POINTS_FOR_FRANCHISE_1.doubleValue()))
            .andExpect(jsonPath("$.pointsForFranchise2").value(DEFAULT_POINTS_FOR_FRANCHISE_2.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllMatchesByStartDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where startDateTime equals to DEFAULT_START_DATE_TIME
        defaultMatchShouldBeFound("startDateTime.equals=" + DEFAULT_START_DATE_TIME);

        // Get all the matchList where startDateTime equals to UPDATED_START_DATE_TIME
        defaultMatchShouldNotBeFound("startDateTime.equals=" + UPDATED_START_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllMatchesByStartDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where startDateTime in DEFAULT_START_DATE_TIME or UPDATED_START_DATE_TIME
        defaultMatchShouldBeFound("startDateTime.in=" + DEFAULT_START_DATE_TIME + "," + UPDATED_START_DATE_TIME);

        // Get all the matchList where startDateTime equals to UPDATED_START_DATE_TIME
        defaultMatchShouldNotBeFound("startDateTime.in=" + UPDATED_START_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllMatchesByStartDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where startDateTime is not null
        defaultMatchShouldBeFound("startDateTime.specified=true");

        // Get all the matchList where startDateTime is null
        defaultMatchShouldNotBeFound("startDateTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllMatchesByStartDateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where startDateTime greater than or equals to DEFAULT_START_DATE_TIME
        defaultMatchShouldBeFound("startDateTime.greaterOrEqualThan=" + DEFAULT_START_DATE_TIME);

        // Get all the matchList where startDateTime greater than or equals to UPDATED_START_DATE_TIME
        defaultMatchShouldNotBeFound("startDateTime.greaterOrEqualThan=" + UPDATED_START_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllMatchesByStartDateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where startDateTime less than or equals to DEFAULT_START_DATE_TIME
        defaultMatchShouldNotBeFound("startDateTime.lessThan=" + DEFAULT_START_DATE_TIME);

        // Get all the matchList where startDateTime less than or equals to UPDATED_START_DATE_TIME
        defaultMatchShouldBeFound("startDateTime.lessThan=" + UPDATED_START_DATE_TIME);
    }


    @Test
    @Transactional
    public void getAllMatchesByEndDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where endDateTime equals to DEFAULT_END_DATE_TIME
        defaultMatchShouldBeFound("endDateTime.equals=" + DEFAULT_END_DATE_TIME);

        // Get all the matchList where endDateTime equals to UPDATED_END_DATE_TIME
        defaultMatchShouldNotBeFound("endDateTime.equals=" + UPDATED_END_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllMatchesByEndDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where endDateTime in DEFAULT_END_DATE_TIME or UPDATED_END_DATE_TIME
        defaultMatchShouldBeFound("endDateTime.in=" + DEFAULT_END_DATE_TIME + "," + UPDATED_END_DATE_TIME);

        // Get all the matchList where endDateTime equals to UPDATED_END_DATE_TIME
        defaultMatchShouldNotBeFound("endDateTime.in=" + UPDATED_END_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllMatchesByEndDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where endDateTime is not null
        defaultMatchShouldBeFound("endDateTime.specified=true");

        // Get all the matchList where endDateTime is null
        defaultMatchShouldNotBeFound("endDateTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllMatchesByEndDateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where endDateTime greater than or equals to DEFAULT_END_DATE_TIME
        defaultMatchShouldBeFound("endDateTime.greaterOrEqualThan=" + DEFAULT_END_DATE_TIME);

        // Get all the matchList where endDateTime greater than or equals to UPDATED_END_DATE_TIME
        defaultMatchShouldNotBeFound("endDateTime.greaterOrEqualThan=" + UPDATED_END_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllMatchesByEndDateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where endDateTime less than or equals to DEFAULT_END_DATE_TIME
        defaultMatchShouldNotBeFound("endDateTime.lessThan=" + DEFAULT_END_DATE_TIME);

        // Get all the matchList where endDateTime less than or equals to UPDATED_END_DATE_TIME
        defaultMatchShouldBeFound("endDateTime.lessThan=" + UPDATED_END_DATE_TIME);
    }


    @Test
    @Transactional
    public void getAllMatchesByPointsForFranchise1IsEqualToSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where pointsForFranchise1 equals to DEFAULT_POINTS_FOR_FRANCHISE_1
        defaultMatchShouldBeFound("pointsForFranchise1.equals=" + DEFAULT_POINTS_FOR_FRANCHISE_1);

        // Get all the matchList where pointsForFranchise1 equals to UPDATED_POINTS_FOR_FRANCHISE_1
        defaultMatchShouldNotBeFound("pointsForFranchise1.equals=" + UPDATED_POINTS_FOR_FRANCHISE_1);
    }

    @Test
    @Transactional
    public void getAllMatchesByPointsForFranchise1IsInShouldWork() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where pointsForFranchise1 in DEFAULT_POINTS_FOR_FRANCHISE_1 or UPDATED_POINTS_FOR_FRANCHISE_1
        defaultMatchShouldBeFound("pointsForFranchise1.in=" + DEFAULT_POINTS_FOR_FRANCHISE_1 + "," + UPDATED_POINTS_FOR_FRANCHISE_1);

        // Get all the matchList where pointsForFranchise1 equals to UPDATED_POINTS_FOR_FRANCHISE_1
        defaultMatchShouldNotBeFound("pointsForFranchise1.in=" + UPDATED_POINTS_FOR_FRANCHISE_1);
    }

    @Test
    @Transactional
    public void getAllMatchesByPointsForFranchise1IsNullOrNotNull() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where pointsForFranchise1 is not null
        defaultMatchShouldBeFound("pointsForFranchise1.specified=true");

        // Get all the matchList where pointsForFranchise1 is null
        defaultMatchShouldNotBeFound("pointsForFranchise1.specified=false");
    }

    @Test
    @Transactional
    public void getAllMatchesByPointsForFranchise2IsEqualToSomething() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where pointsForFranchise2 equals to DEFAULT_POINTS_FOR_FRANCHISE_2
        defaultMatchShouldBeFound("pointsForFranchise2.equals=" + DEFAULT_POINTS_FOR_FRANCHISE_2);

        // Get all the matchList where pointsForFranchise2 equals to UPDATED_POINTS_FOR_FRANCHISE_2
        defaultMatchShouldNotBeFound("pointsForFranchise2.equals=" + UPDATED_POINTS_FOR_FRANCHISE_2);
    }

    @Test
    @Transactional
    public void getAllMatchesByPointsForFranchise2IsInShouldWork() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where pointsForFranchise2 in DEFAULT_POINTS_FOR_FRANCHISE_2 or UPDATED_POINTS_FOR_FRANCHISE_2
        defaultMatchShouldBeFound("pointsForFranchise2.in=" + DEFAULT_POINTS_FOR_FRANCHISE_2 + "," + UPDATED_POINTS_FOR_FRANCHISE_2);

        // Get all the matchList where pointsForFranchise2 equals to UPDATED_POINTS_FOR_FRANCHISE_2
        defaultMatchShouldNotBeFound("pointsForFranchise2.in=" + UPDATED_POINTS_FOR_FRANCHISE_2);
    }

    @Test
    @Transactional
    public void getAllMatchesByPointsForFranchise2IsNullOrNotNull() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList where pointsForFranchise2 is not null
        defaultMatchShouldBeFound("pointsForFranchise2.specified=true");

        // Get all the matchList where pointsForFranchise2 is null
        defaultMatchShouldNotBeFound("pointsForFranchise2.specified=false");
    }

    @Test
    @Transactional
    public void getAllMatchesByTournamentIsEqualToSomething() throws Exception {
        // Initialize the database
        Tournament tournament = TournamentResourceIntTest.createEntity(em);
        em.persist(tournament);
        em.flush();
        match.setTournament(tournament);
        matchRepository.saveAndFlush(match);
        Long tournamentId = tournament.getId();

        // Get all the matchList where tournament equals to tournamentId
        defaultMatchShouldBeFound("tournamentId.equals=" + tournamentId);

        // Get all the matchList where tournament equals to tournamentId + 1
        defaultMatchShouldNotBeFound("tournamentId.equals=" + (tournamentId + 1));
    }


    @Test
    @Transactional
    public void getAllMatchesByFranchise1IsEqualToSomething() throws Exception {
        // Initialize the database
        Franchise franchise1 = FranchiseResourceIntTest.createEntity(em);
        em.persist(franchise1);
        em.flush();
        match.setFranchise1(franchise1);
        matchRepository.saveAndFlush(match);
        Long franchise1Id = franchise1.getId();

        // Get all the matchList where franchise1 equals to franchise1Id
        defaultMatchShouldBeFound("franchise1Id.equals=" + franchise1Id);

        // Get all the matchList where franchise1 equals to franchise1Id + 1
        defaultMatchShouldNotBeFound("franchise1Id.equals=" + (franchise1Id + 1));
    }


    @Test
    @Transactional
    public void getAllMatchesByFranchise2IsEqualToSomething() throws Exception {
        // Initialize the database
        Franchise franchise2 = FranchiseResourceIntTest.createEntity(em);
        em.persist(franchise2);
        em.flush();
        match.setFranchise2(franchise2);
        matchRepository.saveAndFlush(match);
        Long franchise2Id = franchise2.getId();

        // Get all the matchList where franchise2 equals to franchise2Id
        defaultMatchShouldBeFound("franchise2Id.equals=" + franchise2Id);

        // Get all the matchList where franchise2 equals to franchise2Id + 1
        defaultMatchShouldNotBeFound("franchise2Id.equals=" + (franchise2Id + 1));
    }


    @Test
    @Transactional
    public void getAllMatchesByWinnerIsEqualToSomething() throws Exception {
        // Initialize the database
        Franchise winner = FranchiseResourceIntTest.createEntity(em);
        em.persist(winner);
        em.flush();
        match.setWinner(winner);
        matchRepository.saveAndFlush(match);
        Long winnerId = winner.getId();

        // Get all the matchList where winner equals to winnerId
        defaultMatchShouldBeFound("winnerId.equals=" + winnerId);

        // Get all the matchList where winner equals to winnerId + 1
        defaultMatchShouldNotBeFound("winnerId.equals=" + (winnerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMatchShouldBeFound(String filter) throws Exception {
        restMatchMockMvc.perform(get("/api/matches?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(match.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDateTime").value(hasItem(sameInstant(DEFAULT_START_DATE_TIME))))
            .andExpect(jsonPath("$.[*].endDateTime").value(hasItem(sameInstant(DEFAULT_END_DATE_TIME))))
            .andExpect(jsonPath("$.[*].pointsForFranchise1").value(hasItem(DEFAULT_POINTS_FOR_FRANCHISE_1.doubleValue())))
            .andExpect(jsonPath("$.[*].pointsForFranchise2").value(hasItem(DEFAULT_POINTS_FOR_FRANCHISE_2.doubleValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMatchShouldNotBeFound(String filter) throws Exception {
        restMatchMockMvc.perform(get("/api/matches?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingMatch() throws Exception {
        // Get the match
        restMatchMockMvc.perform(get("/api/matches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Update the match
        Match updatedMatch = matchRepository.findOne(match.getId());
        // Disconnect from session so that the updates on updatedMatch are not directly saved in db
        em.detach(updatedMatch);
        updatedMatch
            .startDateTime(UPDATED_START_DATE_TIME)
            .endDateTime(UPDATED_END_DATE_TIME)
            .pointsForFranchise1(UPDATED_POINTS_FOR_FRANCHISE_1)
            .pointsForFranchise2(UPDATED_POINTS_FOR_FRANCHISE_2);
        MatchDTO matchDTO = matchMapper.toDto(updatedMatch);

        restMatchMockMvc.perform(put("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isOk());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getStartDateTime()).isEqualTo(UPDATED_START_DATE_TIME);
        assertThat(testMatch.getEndDateTime()).isEqualTo(UPDATED_END_DATE_TIME);
        assertThat(testMatch.getPointsForFranchise1()).isEqualTo(UPDATED_POINTS_FOR_FRANCHISE_1);
        assertThat(testMatch.getPointsForFranchise2()).isEqualTo(UPDATED_POINTS_FOR_FRANCHISE_2);
    }

    @Test
    @Transactional
    public void updateNonExistingMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMatchMockMvc.perform(put("/api/matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isCreated());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);
        int databaseSizeBeforeDelete = matchRepository.findAll().size();

        // Get the match
        restMatchMockMvc.perform(delete("/api/matches/{id}", match.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Match.class);
        Match match1 = new Match();
        match1.setId(1L);
        Match match2 = new Match();
        match2.setId(match1.getId());
        assertThat(match1).isEqualTo(match2);
        match2.setId(2L);
        assertThat(match1).isNotEqualTo(match2);
        match1.setId(null);
        assertThat(match1).isNotEqualTo(match2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatchDTO.class);
        MatchDTO matchDTO1 = new MatchDTO();
        matchDTO1.setId(1L);
        MatchDTO matchDTO2 = new MatchDTO();
        assertThat(matchDTO1).isNotEqualTo(matchDTO2);
        matchDTO2.setId(matchDTO1.getId());
        assertThat(matchDTO1).isEqualTo(matchDTO2);
        matchDTO2.setId(2L);
        assertThat(matchDTO1).isNotEqualTo(matchDTO2);
        matchDTO1.setId(null);
        assertThat(matchDTO1).isNotEqualTo(matchDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(matchMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(matchMapper.fromId(null)).isNull();
    }
}
