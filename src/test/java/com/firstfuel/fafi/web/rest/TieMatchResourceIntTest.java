package com.firstfuel.fafi.web.rest;

import com.firstfuel.fafi.FafiApp;

import com.firstfuel.fafi.domain.TieMatch;
import com.firstfuel.fafi.domain.Match;
import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.repository.TieMatchRepository;
import com.firstfuel.fafi.service.TieMatchService;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
import com.firstfuel.fafi.service.mapper.TieMatchMapper;
import com.firstfuel.fafi.web.rest.errors.ExceptionTranslator;
import com.firstfuel.fafi.service.dto.TieMatchCriteria;
import com.firstfuel.fafi.service.TieMatchQueryService;

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
import java.util.List;

import static com.firstfuel.fafi.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TieMatchResource REST controller.
 *
 * @see TieMatchResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FafiApp.class)
public class TieMatchResourceIntTest {

    private static final Double DEFAULT_POINTS_FOR_TIE_TEAM_1 = 1D;
    private static final Double UPDATED_POINTS_FOR_TIE_TEAM_1 = 2D;

    private static final Double DEFAULT_POINTS_FOR_TIE_TEAM_2 = 1D;
    private static final Double UPDATED_POINTS_FOR_TIE_TEAM_2 = 2D;

    @Autowired
    private TieMatchRepository tieMatchRepository;

    @Autowired
    private TieMatchMapper tieMatchMapper;

    @Autowired
    private TieMatchResource tieMatchResource;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTieMatchMockMvc;

    private TieMatch tieMatch;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.restTieMatchMockMvc = MockMvcBuilders.standaloneSetup(tieMatchResource)
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
    public static TieMatch createEntity(EntityManager em) {
        TieMatch tieMatch = new TieMatch()
            .pointsForTieTeam1(DEFAULT_POINTS_FOR_TIE_TEAM_1)
            .pointsForTieTeam2(DEFAULT_POINTS_FOR_TIE_TEAM_2);
        return tieMatch;
    }

    @Before
    public void initTest() {
        tieMatch = createEntity(em);
    }

    @Test
    @Transactional
    public void createTieMatch() throws Exception {
        int databaseSizeBeforeCreate = tieMatchRepository.findAll().size();

        // Create the TieMatch
        TieMatchDTO tieMatchDTO = tieMatchMapper.toDto(tieMatch);
        restTieMatchMockMvc.perform(post("/api/tie-matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieMatchDTO)))
            .andExpect(status().isCreated());

        // Validate the TieMatch in the database
        List<TieMatch> tieMatchList = tieMatchRepository.findAll();
        assertThat(tieMatchList).hasSize(databaseSizeBeforeCreate + 1);
        TieMatch testTieMatch = tieMatchList.get(tieMatchList.size() - 1);
        assertThat(testTieMatch.getPointsForTieTeam1()).isEqualTo(DEFAULT_POINTS_FOR_TIE_TEAM_1);
        assertThat(testTieMatch.getPointsForTieTeam2()).isEqualTo(DEFAULT_POINTS_FOR_TIE_TEAM_2);
    }

    @Test
    @Transactional
    public void createTieMatchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tieMatchRepository.findAll().size();

        // Create the TieMatch with an existing ID
        tieMatch.setId(1L);
        TieMatchDTO tieMatchDTO = tieMatchMapper.toDto(tieMatch);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTieMatchMockMvc.perform(post("/api/tie-matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieMatchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TieMatch in the database
        List<TieMatch> tieMatchList = tieMatchRepository.findAll();
        assertThat(tieMatchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTieMatches() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList
        restTieMatchMockMvc.perform(get("/api/tie-matches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tieMatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].pointsForTieTeam1").value(hasItem(DEFAULT_POINTS_FOR_TIE_TEAM_1.doubleValue())))
            .andExpect(jsonPath("$.[*].pointsForTieTeam2").value(hasItem(DEFAULT_POINTS_FOR_TIE_TEAM_2.doubleValue())));
    }

    @Test
    @Transactional
    public void getTieMatch() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get the tieMatch
        restTieMatchMockMvc.perform(get("/api/tie-matches/{id}", tieMatch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tieMatch.getId().intValue()))
            .andExpect(jsonPath("$.pointsForTieTeam1").value(DEFAULT_POINTS_FOR_TIE_TEAM_1.doubleValue()))
            .andExpect(jsonPath("$.pointsForTieTeam2").value(DEFAULT_POINTS_FOR_TIE_TEAM_2.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllTieMatchesByPointsForTieTeam1IsEqualToSomething() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList where pointsForTieTeam1 equals to DEFAULT_POINTS_FOR_TIE_TEAM_1
        defaultTieMatchShouldBeFound("pointsForTieTeam1.equals=" + DEFAULT_POINTS_FOR_TIE_TEAM_1);

        // Get all the tieMatchList where pointsForTieTeam1 equals to UPDATED_POINTS_FOR_TIE_TEAM_1
        defaultTieMatchShouldNotBeFound("pointsForTieTeam1.equals=" + UPDATED_POINTS_FOR_TIE_TEAM_1);
    }

    @Test
    @Transactional
    public void getAllTieMatchesByPointsForTieTeam1IsInShouldWork() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList where pointsForTieTeam1 in DEFAULT_POINTS_FOR_TIE_TEAM_1 or UPDATED_POINTS_FOR_TIE_TEAM_1
        defaultTieMatchShouldBeFound("pointsForTieTeam1.in=" + DEFAULT_POINTS_FOR_TIE_TEAM_1 + "," + UPDATED_POINTS_FOR_TIE_TEAM_1);

        // Get all the tieMatchList where pointsForTieTeam1 equals to UPDATED_POINTS_FOR_TIE_TEAM_1
        defaultTieMatchShouldNotBeFound("pointsForTieTeam1.in=" + UPDATED_POINTS_FOR_TIE_TEAM_1);
    }

    @Test
    @Transactional
    public void getAllTieMatchesByPointsForTieTeam1IsNullOrNotNull() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList where pointsForTieTeam1 is not null
        defaultTieMatchShouldBeFound("pointsForTieTeam1.specified=true");

        // Get all the tieMatchList where pointsForTieTeam1 is null
        defaultTieMatchShouldNotBeFound("pointsForTieTeam1.specified=false");
    }

    @Test
    @Transactional
    public void getAllTieMatchesByPointsForTieTeam2IsEqualToSomething() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList where pointsForTieTeam2 equals to DEFAULT_POINTS_FOR_TIE_TEAM_2
        defaultTieMatchShouldBeFound("pointsForTieTeam2.equals=" + DEFAULT_POINTS_FOR_TIE_TEAM_2);

        // Get all the tieMatchList where pointsForTieTeam2 equals to UPDATED_POINTS_FOR_TIE_TEAM_2
        defaultTieMatchShouldNotBeFound("pointsForTieTeam2.equals=" + UPDATED_POINTS_FOR_TIE_TEAM_2);
    }

    @Test
    @Transactional
    public void getAllTieMatchesByPointsForTieTeam2IsInShouldWork() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList where pointsForTieTeam2 in DEFAULT_POINTS_FOR_TIE_TEAM_2 or UPDATED_POINTS_FOR_TIE_TEAM_2
        defaultTieMatchShouldBeFound("pointsForTieTeam2.in=" + DEFAULT_POINTS_FOR_TIE_TEAM_2 + "," + UPDATED_POINTS_FOR_TIE_TEAM_2);

        // Get all the tieMatchList where pointsForTieTeam2 equals to UPDATED_POINTS_FOR_TIE_TEAM_2
        defaultTieMatchShouldNotBeFound("pointsForTieTeam2.in=" + UPDATED_POINTS_FOR_TIE_TEAM_2);
    }

    @Test
    @Transactional
    public void getAllTieMatchesByPointsForTieTeam2IsNullOrNotNull() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);

        // Get all the tieMatchList where pointsForTieTeam2 is not null
        defaultTieMatchShouldBeFound("pointsForTieTeam2.specified=true");

        // Get all the tieMatchList where pointsForTieTeam2 is null
        defaultTieMatchShouldNotBeFound("pointsForTieTeam2.specified=false");
    }

    @Test
    @Transactional
    public void getAllTieMatchesByMatchIsEqualToSomething() throws Exception {
        // Initialize the database
        Match match = MatchResourceIntTest.createEntity(em);
        em.persist(match);
        em.flush();
        tieMatch.setMatch(match);
        tieMatchRepository.saveAndFlush(tieMatch);
        Long matchId = match.getId();

        // Get all the tieMatchList where match equals to matchId
        defaultTieMatchShouldBeFound("matchId.equals=" + matchId);

        // Get all the tieMatchList where match equals to matchId + 1
        defaultTieMatchShouldNotBeFound("matchId.equals=" + (matchId + 1));
    }


    @Test
    @Transactional
    public void getAllTieMatchesByTeam1IsEqualToSomething() throws Exception {
        // Initialize the database
        TieTeam team1 = TieTeamResourceIntTest.createEntity(em);
        em.persist(team1);
        em.flush();
        tieMatch.setTeam1(team1);
        tieMatchRepository.saveAndFlush(tieMatch);
        Long team1Id = team1.getId();

        // Get all the tieMatchList where team1 equals to team1Id
        defaultTieMatchShouldBeFound("team1Id.equals=" + team1Id);

        // Get all the tieMatchList where team1 equals to team1Id + 1
        defaultTieMatchShouldNotBeFound("team1Id.equals=" + (team1Id + 1));
    }


    @Test
    @Transactional
    public void getAllTieMatchesByTeam2IsEqualToSomething() throws Exception {
        // Initialize the database
        TieTeam team2 = TieTeamResourceIntTest.createEntity(em);
        em.persist(team2);
        em.flush();
        tieMatch.setTeam2(team2);
        tieMatchRepository.saveAndFlush(tieMatch);
        Long team2Id = team2.getId();

        // Get all the tieMatchList where team2 equals to team2Id
        defaultTieMatchShouldBeFound("team2Id.equals=" + team2Id);

        // Get all the tieMatchList where team2 equals to team2Id + 1
        defaultTieMatchShouldNotBeFound("team2Id.equals=" + (team2Id + 1));
    }


    @Test
    @Transactional
    public void getAllTieMatchesByWinnerIsEqualToSomething() throws Exception {
        // Initialize the database
        TieTeam winner = TieTeamResourceIntTest.createEntity(em);
        em.persist(winner);
        em.flush();
        tieMatch.setWinner(winner);
        //winner.setTieMatch(tieMatch);
        tieMatchRepository.saveAndFlush(tieMatch);
        Long winnerId = winner.getId();

        // Get all the tieMatchList where winner equals to winnerId
        defaultTieMatchShouldBeFound("winnerId.equals=" + winnerId);

        // Get all the tieMatchList where winner equals to winnerId + 1
        defaultTieMatchShouldNotBeFound("winnerId.equals=" + (winnerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTieMatchShouldBeFound(String filter) throws Exception {
        restTieMatchMockMvc.perform(get("/api/tie-matches?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tieMatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].pointsForTieTeam1").value(hasItem(DEFAULT_POINTS_FOR_TIE_TEAM_1.doubleValue())))
            .andExpect(jsonPath("$.[*].pointsForTieTeam2").value(hasItem(DEFAULT_POINTS_FOR_TIE_TEAM_2.doubleValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTieMatchShouldNotBeFound(String filter) throws Exception {
        restTieMatchMockMvc.perform(get("/api/tie-matches?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingTieMatch() throws Exception {
        // Get the tieMatch
        restTieMatchMockMvc.perform(get("/api/tie-matches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTieMatch() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);
        int databaseSizeBeforeUpdate = tieMatchRepository.findAll().size();

        // Update the tieMatch
        TieMatch updatedTieMatch = tieMatchRepository.findOne(tieMatch.getId());
        // Disconnect from session so that the updates on updatedTieMatch are not directly saved in db
        em.detach(updatedTieMatch);
        updatedTieMatch
            .pointsForTieTeam1(UPDATED_POINTS_FOR_TIE_TEAM_1)
            .pointsForTieTeam2(UPDATED_POINTS_FOR_TIE_TEAM_2);
        TieMatchDTO tieMatchDTO = tieMatchMapper.toDto(updatedTieMatch);

        restTieMatchMockMvc.perform(put("/api/tie-matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieMatchDTO)))
            .andExpect(status().isOk());

        // Validate the TieMatch in the database
        List<TieMatch> tieMatchList = tieMatchRepository.findAll();
        assertThat(tieMatchList).hasSize(databaseSizeBeforeUpdate);
        TieMatch testTieMatch = tieMatchList.get(tieMatchList.size() - 1);
        assertThat(testTieMatch.getPointsForTieTeam1()).isEqualTo(UPDATED_POINTS_FOR_TIE_TEAM_1);
        assertThat(testTieMatch.getPointsForTieTeam2()).isEqualTo(UPDATED_POINTS_FOR_TIE_TEAM_2);
    }

    @Test
    @Transactional
    public void updateNonExistingTieMatch() throws Exception {
        int databaseSizeBeforeUpdate = tieMatchRepository.findAll().size();

        // Create the TieMatch
        TieMatchDTO tieMatchDTO = tieMatchMapper.toDto(tieMatch);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTieMatchMockMvc.perform(put("/api/tie-matches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieMatchDTO)))
            .andExpect(status().isCreated());

        // Validate the TieMatch in the database
        List<TieMatch> tieMatchList = tieMatchRepository.findAll();
        assertThat(tieMatchList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTieMatch() throws Exception {
        // Initialize the database
        tieMatchRepository.saveAndFlush(tieMatch);
        int databaseSizeBeforeDelete = tieMatchRepository.findAll().size();

        // Get the tieMatch
        restTieMatchMockMvc.perform(delete("/api/tie-matches/{id}", tieMatch.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TieMatch> tieMatchList = tieMatchRepository.findAll();
        assertThat(tieMatchList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TieMatch.class);
        TieMatch tieMatch1 = new TieMatch();
        tieMatch1.setId(1L);
        TieMatch tieMatch2 = new TieMatch();
        tieMatch2.setId(tieMatch1.getId());
        assertThat(tieMatch1).isEqualTo(tieMatch2);
        tieMatch2.setId(2L);
        assertThat(tieMatch1).isNotEqualTo(tieMatch2);
        tieMatch1.setId(null);
        assertThat(tieMatch1).isNotEqualTo(tieMatch2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TieMatchDTO.class);
        TieMatchDTO tieMatchDTO1 = new TieMatchDTO();
        tieMatchDTO1.setId(1L);
        TieMatchDTO tieMatchDTO2 = new TieMatchDTO();
        assertThat(tieMatchDTO1).isNotEqualTo(tieMatchDTO2);
        tieMatchDTO2.setId(tieMatchDTO1.getId());
        assertThat(tieMatchDTO1).isEqualTo(tieMatchDTO2);
        tieMatchDTO2.setId(2L);
        assertThat(tieMatchDTO1).isNotEqualTo(tieMatchDTO2);
        tieMatchDTO1.setId(null);
        assertThat(tieMatchDTO1).isNotEqualTo(tieMatchDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tieMatchMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tieMatchMapper.fromId(null)).isNull();
    }
}
