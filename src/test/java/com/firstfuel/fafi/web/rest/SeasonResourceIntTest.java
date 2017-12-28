package com.firstfuel.fafi.web.rest;

import com.firstfuel.fafi.FafiApp;

import com.firstfuel.fafi.domain.Season;
import com.firstfuel.fafi.domain.Tournament;
import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.repository.SeasonRepository;
import com.firstfuel.fafi.service.SeasonService;
import com.firstfuel.fafi.service.dto.SeasonDTO;
import com.firstfuel.fafi.service.mapper.SeasonMapper;
import com.firstfuel.fafi.web.rest.errors.ExceptionTranslator;
import com.firstfuel.fafi.service.dto.SeasonCriteria;
import com.firstfuel.fafi.service.SeasonQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.firstfuel.fafi.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SeasonResource REST controller.
 *
 * @see SeasonResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FafiApp.class)
public class SeasonResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private SeasonResource seasonResource;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSeasonMockMvc;

    private Season season;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.restSeasonMockMvc = MockMvcBuilders.standaloneSetup(seasonResource)
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
    public static Season createEntity(EntityManager em) {
        Season season = new Season()
            .name(DEFAULT_NAME)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .active(DEFAULT_ACTIVE);
        return season;
    }

    @Before
    public void initTest() {
        season = createEntity(em);
    }

    @Test
    @Transactional
    public void createSeason() throws Exception {
        int databaseSizeBeforeCreate = seasonRepository.findAll().size();

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);
        restSeasonMockMvc.perform(post("/api/seasons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isCreated());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeCreate + 1);
        Season testSeason = seasonList.get(seasonList.size() - 1);
        assertThat(testSeason.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSeason.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testSeason.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testSeason.isActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    public void createSeasonWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = seasonRepository.findAll().size();

        // Create the Season with an existing ID
        season.setId(1L);
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeasonMockMvc.perform(post("/api/seasons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = seasonRepository.findAll().size();
        // set the field null
        season.setName(null);

        // Create the Season, which fails.
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        restSeasonMockMvc.perform(post("/api/seasons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isBadRequest());

        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSeasons() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList
        restSeasonMockMvc.perform(get("/api/seasons?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(season.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    public void getSeason() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get the season
        restSeasonMockMvc.perform(get("/api/seasons/{id}", season.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(season.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllSeasonsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where name equals to DEFAULT_NAME
        defaultSeasonShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the seasonList where name equals to UPDATED_NAME
        defaultSeasonShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSeasonsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSeasonShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the seasonList where name equals to UPDATED_NAME
        defaultSeasonShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSeasonsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where name is not null
        defaultSeasonShouldBeFound("name.specified=true");

        // Get all the seasonList where name is null
        defaultSeasonShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSeasonsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startDate equals to DEFAULT_START_DATE
        defaultSeasonShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the seasonList where startDate equals to UPDATED_START_DATE
        defaultSeasonShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultSeasonShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the seasonList where startDate equals to UPDATED_START_DATE
        defaultSeasonShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startDate is not null
        defaultSeasonShouldBeFound("startDate.specified=true");

        // Get all the seasonList where startDate is null
        defaultSeasonShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSeasonsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startDate greater than or equals to DEFAULT_START_DATE
        defaultSeasonShouldBeFound("startDate.greaterOrEqualThan=" + DEFAULT_START_DATE);

        // Get all the seasonList where startDate greater than or equals to UPDATED_START_DATE
        defaultSeasonShouldNotBeFound("startDate.greaterOrEqualThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where startDate less than or equals to DEFAULT_START_DATE
        defaultSeasonShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the seasonList where startDate less than or equals to UPDATED_START_DATE
        defaultSeasonShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }


    @Test
    @Transactional
    public void getAllSeasonsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endDate equals to DEFAULT_END_DATE
        defaultSeasonShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the seasonList where endDate equals to UPDATED_END_DATE
        defaultSeasonShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultSeasonShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the seasonList where endDate equals to UPDATED_END_DATE
        defaultSeasonShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endDate is not null
        defaultSeasonShouldBeFound("endDate.specified=true");

        // Get all the seasonList where endDate is null
        defaultSeasonShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSeasonsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endDate greater than or equals to DEFAULT_END_DATE
        defaultSeasonShouldBeFound("endDate.greaterOrEqualThan=" + DEFAULT_END_DATE);

        // Get all the seasonList where endDate greater than or equals to UPDATED_END_DATE
        defaultSeasonShouldNotBeFound("endDate.greaterOrEqualThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where endDate less than or equals to DEFAULT_END_DATE
        defaultSeasonShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the seasonList where endDate less than or equals to UPDATED_END_DATE
        defaultSeasonShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }


    @Test
    @Transactional
    public void getAllSeasonsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where active equals to DEFAULT_ACTIVE
        defaultSeasonShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the seasonList where active equals to UPDATED_ACTIVE
        defaultSeasonShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultSeasonShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the seasonList where active equals to UPDATED_ACTIVE
        defaultSeasonShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllSeasonsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);

        // Get all the seasonList where active is not null
        defaultSeasonShouldBeFound("active.specified=true");

        // Get all the seasonList where active is null
        defaultSeasonShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    public void getAllSeasonsByTournamentIsEqualToSomething() throws Exception {
        // Initialize the database
        Tournament tournament = TournamentResourceIntTest.createEntity(em);
        em.persist(tournament);
        em.flush();
        season.addTournament(tournament);
        seasonRepository.saveAndFlush(season);
        Long tournamentId = tournament.getId();

        // Get all the seasonList where tournament equals to tournamentId
        defaultSeasonShouldBeFound("tournamentId.equals=" + tournamentId);

        // Get all the seasonList where tournament equals to tournamentId + 1
        defaultSeasonShouldNotBeFound("tournamentId.equals=" + (tournamentId + 1));
    }


    @Test
    @Transactional
    public void getAllSeasonsByWinnerIsEqualToSomething() throws Exception {
        // Initialize the database
        Franchise winner = FranchiseResourceIntTest.createEntity(em);
        em.persist(winner);
        em.flush();
        season.setWinner(winner);
        seasonRepository.saveAndFlush(season);
        Long winnerId = winner.getId();

        // Get all the seasonList where winner equals to winnerId
        defaultSeasonShouldBeFound("winnerId.equals=" + winnerId);

        // Get all the seasonList where winner equals to winnerId + 1
        defaultSeasonShouldNotBeFound("winnerId.equals=" + (winnerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSeasonShouldBeFound(String filter) throws Exception {
        restSeasonMockMvc.perform(get("/api/seasons?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(season.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSeasonShouldNotBeFound(String filter) throws Exception {
        restSeasonMockMvc.perform(get("/api/seasons?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingSeason() throws Exception {
        // Get the season
        restSeasonMockMvc.perform(get("/api/seasons/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSeason() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();

        // Update the season
        Season updatedSeason = seasonRepository.findOne(season.getId());
        // Disconnect from session so that the updates on updatedSeason are not directly saved in db
        em.detach(updatedSeason);
        updatedSeason
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .active(UPDATED_ACTIVE);
        SeasonDTO seasonDTO = seasonMapper.toDto(updatedSeason);

        restSeasonMockMvc.perform(put("/api/seasons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isOk());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate);
        Season testSeason = seasonList.get(seasonList.size() - 1);
        assertThat(testSeason.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSeason.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testSeason.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testSeason.isActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    public void updateNonExistingSeason() throws Exception {
        int databaseSizeBeforeUpdate = seasonRepository.findAll().size();

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSeasonMockMvc.perform(put("/api/seasons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seasonDTO)))
            .andExpect(status().isCreated());

        // Validate the Season in the database
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSeason() throws Exception {
        // Initialize the database
        seasonRepository.saveAndFlush(season);
        int databaseSizeBeforeDelete = seasonRepository.findAll().size();

        // Get the season
        restSeasonMockMvc.perform(delete("/api/seasons/{id}", season.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Season> seasonList = seasonRepository.findAll();
        assertThat(seasonList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Season.class);
        Season season1 = new Season();
        season1.setId(1L);
        Season season2 = new Season();
        season2.setId(season1.getId());
        assertThat(season1).isEqualTo(season2);
        season2.setId(2L);
        assertThat(season1).isNotEqualTo(season2);
        season1.setId(null);
        assertThat(season1).isNotEqualTo(season2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SeasonDTO.class);
        SeasonDTO seasonDTO1 = new SeasonDTO();
        seasonDTO1.setId(1L);
        SeasonDTO seasonDTO2 = new SeasonDTO();
        assertThat(seasonDTO1).isNotEqualTo(seasonDTO2);
        seasonDTO2.setId(seasonDTO1.getId());
        assertThat(seasonDTO1).isEqualTo(seasonDTO2);
        seasonDTO2.setId(2L);
        assertThat(seasonDTO1).isNotEqualTo(seasonDTO2);
        seasonDTO1.setId(null);
        assertThat(seasonDTO1).isNotEqualTo(seasonDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(seasonMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(seasonMapper.fromId(null)).isNull();
    }
}
