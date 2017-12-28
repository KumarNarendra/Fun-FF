package com.firstfuel.fafi.web.rest;

import com.firstfuel.fafi.FafiApp;

import com.firstfuel.fafi.domain.TieTeam;
import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.repository.TieTeamRepository;
import com.firstfuel.fafi.service.TieTeamService;
import com.firstfuel.fafi.service.dto.TieTeamDTO;
import com.firstfuel.fafi.service.mapper.TieTeamMapper;
import com.firstfuel.fafi.web.rest.errors.ExceptionTranslator;
import com.firstfuel.fafi.service.dto.TieTeamCriteria;
import com.firstfuel.fafi.service.TieTeamQueryService;

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
 * Test class for the TieTeamResource REST controller.
 *
 * @see TieTeamResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FafiApp.class)
public class TieTeamResourceIntTest {

    private static final Double DEFAULT_POINTS = 1D;
    private static final Double UPDATED_POINTS = 2D;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private TieTeamRepository tieTeamRepository;

    @Autowired
    private TieTeamMapper tieTeamMapper;

    @Autowired
    private TieTeamResource tieTeamResource;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTieTeamMockMvc;

    private TieTeam tieTeam;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.restTieTeamMockMvc = MockMvcBuilders.standaloneSetup(tieTeamResource)
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
    public static TieTeam createEntity(EntityManager em) {
        TieTeam tieTeam = new TieTeam()
            .points(DEFAULT_POINTS)
            .name(DEFAULT_NAME);
        return tieTeam;
    }

    @Before
    public void initTest() {
        tieTeam = createEntity(em);
    }

    @Test
    @Transactional
    public void createTieTeam() throws Exception {
        int databaseSizeBeforeCreate = tieTeamRepository.findAll().size();

        // Create the TieTeam
        TieTeamDTO tieTeamDTO = tieTeamMapper.toDto(tieTeam);
        restTieTeamMockMvc.perform(post("/api/tie-teams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieTeamDTO)))
            .andExpect(status().isCreated());

        // Validate the TieTeam in the database
        List<TieTeam> tieTeamList = tieTeamRepository.findAll();
        assertThat(tieTeamList).hasSize(databaseSizeBeforeCreate + 1);
        TieTeam testTieTeam = tieTeamList.get(tieTeamList.size() - 1);
        assertThat(testTieTeam.getPoints()).isEqualTo(DEFAULT_POINTS);
        assertThat(testTieTeam.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createTieTeamWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tieTeamRepository.findAll().size();

        // Create the TieTeam with an existing ID
        tieTeam.setId(1L);
        TieTeamDTO tieTeamDTO = tieTeamMapper.toDto(tieTeam);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTieTeamMockMvc.perform(post("/api/tie-teams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieTeamDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TieTeam in the database
        List<TieTeam> tieTeamList = tieTeamRepository.findAll();
        assertThat(tieTeamList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTieTeams() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList
        restTieTeamMockMvc.perform(get("/api/tie-teams?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tieTeam.getId().intValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS.doubleValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getTieTeam() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get the tieTeam
        restTieTeamMockMvc.perform(get("/api/tie-teams/{id}", tieTeam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tieTeam.getId().intValue()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS.doubleValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getAllTieTeamsByPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList where points equals to DEFAULT_POINTS
        defaultTieTeamShouldBeFound("points.equals=" + DEFAULT_POINTS);

        // Get all the tieTeamList where points equals to UPDATED_POINTS
        defaultTieTeamShouldNotBeFound("points.equals=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllTieTeamsByPointsIsInShouldWork() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList where points in DEFAULT_POINTS or UPDATED_POINTS
        defaultTieTeamShouldBeFound("points.in=" + DEFAULT_POINTS + "," + UPDATED_POINTS);

        // Get all the tieTeamList where points equals to UPDATED_POINTS
        defaultTieTeamShouldNotBeFound("points.in=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllTieTeamsByPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList where points is not null
        defaultTieTeamShouldBeFound("points.specified=true");

        // Get all the tieTeamList where points is null
        defaultTieTeamShouldNotBeFound("points.specified=false");
    }

    @Test
    @Transactional
    public void getAllTieTeamsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList where name equals to DEFAULT_NAME
        defaultTieTeamShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the tieTeamList where name equals to UPDATED_NAME
        defaultTieTeamShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTieTeamsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTieTeamShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the tieTeamList where name equals to UPDATED_NAME
        defaultTieTeamShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTieTeamsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);

        // Get all the tieTeamList where name is not null
        defaultTieTeamShouldBeFound("name.specified=true");

        // Get all the tieTeamList where name is null
        defaultTieTeamShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllTieTeamsByTiePlayersIsEqualToSomething() throws Exception {
        // Initialize the database
        Player tiePlayers = PlayerResourceIntTest.createEntity(em);
        em.persist(tiePlayers);
        em.flush();
        tieTeam.addTiePlayers(tiePlayers);
        tieTeamRepository.saveAndFlush(tieTeam);
        Long tiePlayersId = tiePlayers.getId();

        // Get all the tieTeamList where tiePlayers equals to tiePlayersId
        defaultTieTeamShouldBeFound("tiePlayersId.equals=" + tiePlayersId);

        // Get all the tieTeamList where tiePlayers equals to tiePlayersId + 1
        defaultTieTeamShouldNotBeFound("tiePlayersId.equals=" + (tiePlayersId + 1));
    }


    @Test
    @Transactional
    public void getAllTieTeamsByFranchiseIsEqualToSomething() throws Exception {
        // Initialize the database
        Franchise franchise = FranchiseResourceIntTest.createEntity(em);
        em.persist(franchise);
        em.flush();
        tieTeam.setFranchise(franchise);
        tieTeamRepository.saveAndFlush(tieTeam);
        Long franchiseId = franchise.getId();

        // Get all the tieTeamList where franchise equals to franchiseId
        defaultTieTeamShouldBeFound("franchiseId.equals=" + franchiseId);

        // Get all the tieTeamList where franchise equals to franchiseId + 1
        defaultTieTeamShouldNotBeFound("franchiseId.equals=" + (franchiseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTieTeamShouldBeFound(String filter) throws Exception {
        restTieTeamMockMvc.perform(get("/api/tie-teams?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tieTeam.getId().intValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS.doubleValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTieTeamShouldNotBeFound(String filter) throws Exception {
        restTieTeamMockMvc.perform(get("/api/tie-teams?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingTieTeam() throws Exception {
        // Get the tieTeam
        restTieTeamMockMvc.perform(get("/api/tie-teams/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTieTeam() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);
        int databaseSizeBeforeUpdate = tieTeamRepository.findAll().size();

        // Update the tieTeam
        TieTeam updatedTieTeam = tieTeamRepository.findOne(tieTeam.getId());
        // Disconnect from session so that the updates on updatedTieTeam are not directly saved in db
        em.detach(updatedTieTeam);
        updatedTieTeam
            .points(UPDATED_POINTS)
            .name(UPDATED_NAME);
        TieTeamDTO tieTeamDTO = tieTeamMapper.toDto(updatedTieTeam);

        restTieTeamMockMvc.perform(put("/api/tie-teams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieTeamDTO)))
            .andExpect(status().isOk());

        // Validate the TieTeam in the database
        List<TieTeam> tieTeamList = tieTeamRepository.findAll();
        assertThat(tieTeamList).hasSize(databaseSizeBeforeUpdate);
        TieTeam testTieTeam = tieTeamList.get(tieTeamList.size() - 1);
        assertThat(testTieTeam.getPoints()).isEqualTo(UPDATED_POINTS);
        assertThat(testTieTeam.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingTieTeam() throws Exception {
        int databaseSizeBeforeUpdate = tieTeamRepository.findAll().size();

        // Create the TieTeam
        TieTeamDTO tieTeamDTO = tieTeamMapper.toDto(tieTeam);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTieTeamMockMvc.perform(put("/api/tie-teams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tieTeamDTO)))
            .andExpect(status().isCreated());

        // Validate the TieTeam in the database
        List<TieTeam> tieTeamList = tieTeamRepository.findAll();
        assertThat(tieTeamList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTieTeam() throws Exception {
        // Initialize the database
        tieTeamRepository.saveAndFlush(tieTeam);
        int databaseSizeBeforeDelete = tieTeamRepository.findAll().size();

        // Get the tieTeam
        restTieTeamMockMvc.perform(delete("/api/tie-teams/{id}", tieTeam.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TieTeam> tieTeamList = tieTeamRepository.findAll();
        assertThat(tieTeamList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TieTeam.class);
        TieTeam tieTeam1 = new TieTeam();
        tieTeam1.setId(1L);
        TieTeam tieTeam2 = new TieTeam();
        tieTeam2.setId(tieTeam1.getId());
        assertThat(tieTeam1).isEqualTo(tieTeam2);
        tieTeam2.setId(2L);
        assertThat(tieTeam1).isNotEqualTo(tieTeam2);
        tieTeam1.setId(null);
        assertThat(tieTeam1).isNotEqualTo(tieTeam2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TieTeamDTO.class);
        TieTeamDTO tieTeamDTO1 = new TieTeamDTO();
        tieTeamDTO1.setId(1L);
        TieTeamDTO tieTeamDTO2 = new TieTeamDTO();
        assertThat(tieTeamDTO1).isNotEqualTo(tieTeamDTO2);
        tieTeamDTO2.setId(tieTeamDTO1.getId());
        assertThat(tieTeamDTO1).isEqualTo(tieTeamDTO2);
        tieTeamDTO2.setId(2L);
        assertThat(tieTeamDTO1).isNotEqualTo(tieTeamDTO2);
        tieTeamDTO1.setId(null);
        assertThat(tieTeamDTO1).isNotEqualTo(tieTeamDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tieTeamMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tieTeamMapper.fromId(null)).isNull();
    }
}
