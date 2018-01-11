package com.firstfuel.fafi.web.rest;

import static com.firstfuel.fafi.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;

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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import com.firstfuel.fafi.FafiApp;
import com.firstfuel.fafi.domain.Franchise;
import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.domain.enumeration.Games;
import com.firstfuel.fafi.repository.PlayerRepository;
import com.firstfuel.fafi.service.PlayerQueryService;
import com.firstfuel.fafi.service.PlayerService;
import com.firstfuel.fafi.service.dto.PlayerDTO;
import com.firstfuel.fafi.service.mapper.PlayerMapper;
import com.firstfuel.fafi.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the PlayerResource REST controller.
 *
 * @see PlayerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FafiApp.class)
public class PlayerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_BASE_PRICE = 1D;
    private static final Double UPDATED_BASE_PRICE = 2D;

    private static final Double DEFAULT_BID_PRICE = 1D;
    private static final Double UPDATED_BID_PRICE = 2D;

    private static final Set<Games> DEFAULT_OPTED_GAMES = new HashSet<Games>( Arrays.asList( Games.Football ) );
    private static final Set<Games> UPDATED_OPTED_GAMES = new HashSet<Games>( Arrays.asList( Games.Chess ) );
    private static final Double DEFAULT_POINTS = 1D;
    private static final Double UPDATED_POINTS = 2D;

    private static final byte[] DEFAULT_PROFILE_PIC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PROFILE_PIC = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_PROFILE_PIC_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PROFILE_PIC_CONTENT_TYPE = "image/png";

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private PlayerResource playerResource;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPlayerMockMvc;

    private Player player;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks( this );
        this.restPlayerMockMvc = MockMvcBuilders.standaloneSetup( playerResource )
            .setCustomArgumentResolvers( pageableArgumentResolver )
            .setControllerAdvice( exceptionTranslator )
            .setConversionService( createFormattingConversionService() )
            .setMessageConverters( jacksonMessageConverter )
            .build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createEntity(EntityManager em) {
        Player player = new Player()
            .name(DEFAULT_NAME)
            .basePrice(DEFAULT_BASE_PRICE)
            .bidPrice(DEFAULT_BID_PRICE)
            .optedGames(DEFAULT_OPTED_GAMES)
            .points(DEFAULT_POINTS)
            .profilePic(DEFAULT_PROFILE_PIC)
            .profilePicContentType(DEFAULT_PROFILE_PIC_CONTENT_TYPE);
        return player;
    }

    @Before
    public void initTest() {
        player = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlayer() throws Exception {
        int databaseSizeBeforeCreate = playerRepository.findAll().size();

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);
        restPlayerMockMvc.perform(post("/api/players")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(playerDTO)))
            .andExpect(status().isCreated());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeCreate + 1);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlayer.getBasePrice()).isEqualTo(DEFAULT_BASE_PRICE);
        assertThat(testPlayer.getBidPrice()).isEqualTo(DEFAULT_BID_PRICE);
        assertThat(testPlayer.getOptedGames()).isEqualTo(DEFAULT_OPTED_GAMES);
        assertThat(testPlayer.getPoints()).isEqualTo(DEFAULT_POINTS);
        assertThat(testPlayer.getProfilePic()).isEqualTo(DEFAULT_PROFILE_PIC);
        assertThat(testPlayer.getProfilePicContentType()).isEqualTo(DEFAULT_PROFILE_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createPlayerWithExistingId()
        throws Exception {
        int databaseSizeBeforeCreate = playerRepository.findAll().size();

        // Create the Player with an existing ID
        player.setId( 1L );
        PlayerDTO playerDTO = playerMapper.toDto( player );

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlayerMockMvc.perform(post("/api/players")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(playerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = playerRepository.findAll().size();
        // set the field null
        player.setName(null);

        // Create the Player, which fails.
        PlayerDTO playerDTO = playerMapper.toDto(player);

        restPlayerMockMvc.perform(post("/api/players")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(playerDTO)))
            .andExpect(status().isBadRequest());

        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlayers() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList
        restPlayerMockMvc.perform(get("/api/players?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(player.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].basePrice").value(hasItem(DEFAULT_BASE_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].bidPrice").value(hasItem(DEFAULT_BID_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].optedGames").value(hasItem(DEFAULT_OPTED_GAMES.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS.doubleValue())))
            .andExpect(jsonPath("$.[*].profilePicContentType").value(hasItem(DEFAULT_PROFILE_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].profilePic").value(hasItem(Base64Utils.encodeToString(DEFAULT_PROFILE_PIC))));
    }

    @Test
    @Transactional
    public void getPlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get the player
        restPlayerMockMvc.perform(get("/api/players/{id}", player.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(player.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.basePrice").value(DEFAULT_BASE_PRICE.doubleValue()))
            .andExpect(jsonPath("$.bidPrice").value(DEFAULT_BID_PRICE.doubleValue()))
            .andExpect(jsonPath("$.optedGames").value(DEFAULT_OPTED_GAMES.toString()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS.doubleValue()))
            .andExpect(jsonPath("$.profilePicContentType").value(DEFAULT_PROFILE_PIC_CONTENT_TYPE))
            .andExpect(jsonPath("$.profilePic").value(Base64Utils.encodeToString(DEFAULT_PROFILE_PIC)));
    }

    @Test
    @Transactional
    public void getAllPlayersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where name equals to DEFAULT_NAME
        defaultPlayerShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the playerList where name equals to UPDATED_NAME
        defaultPlayerShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlayersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPlayerShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the playerList where name equals to UPDATED_NAME
        defaultPlayerShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlayersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where name is not null
        defaultPlayerShouldBeFound("name.specified=true");

        // Get all the playerList where name is null
        defaultPlayerShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlayersByBasePriceIsEqualToSomething() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where basePrice equals to DEFAULT_BASE_PRICE
        defaultPlayerShouldBeFound("basePrice.equals=" + DEFAULT_BASE_PRICE);

        // Get all the playerList where basePrice equals to UPDATED_BASE_PRICE
        defaultPlayerShouldNotBeFound("basePrice.equals=" + UPDATED_BASE_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlayersByBasePriceIsInShouldWork() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where basePrice in DEFAULT_BASE_PRICE or UPDATED_BASE_PRICE
        defaultPlayerShouldBeFound("basePrice.in=" + DEFAULT_BASE_PRICE + "," + UPDATED_BASE_PRICE);

        // Get all the playerList where basePrice equals to UPDATED_BASE_PRICE
        defaultPlayerShouldNotBeFound("basePrice.in=" + UPDATED_BASE_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlayersByBasePriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where basePrice is not null
        defaultPlayerShouldBeFound("basePrice.specified=true");

        // Get all the playerList where basePrice is null
        defaultPlayerShouldNotBeFound("basePrice.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlayersByBidPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where bidPrice equals to DEFAULT_BID_PRICE
        defaultPlayerShouldBeFound("bidPrice.equals=" + DEFAULT_BID_PRICE);

        // Get all the playerList where bidPrice equals to UPDATED_BID_PRICE
        defaultPlayerShouldNotBeFound("bidPrice.equals=" + UPDATED_BID_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlayersByBidPriceIsInShouldWork() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where bidPrice in DEFAULT_BID_PRICE or UPDATED_BID_PRICE
        defaultPlayerShouldBeFound("bidPrice.in=" + DEFAULT_BID_PRICE + "," + UPDATED_BID_PRICE);

        // Get all the playerList where bidPrice equals to UPDATED_BID_PRICE
        defaultPlayerShouldNotBeFound("bidPrice.in=" + UPDATED_BID_PRICE);
    }

    @Test
    @Transactional
    public void getAllPlayersByBidPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where bidPrice is not null
        defaultPlayerShouldBeFound("bidPrice.specified=true");

        // Get all the playerList where bidPrice is null
        defaultPlayerShouldNotBeFound("bidPrice.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlayersByOptedGamesIsEqualToSomething() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where optedGames equals to DEFAULT_OPTED_GAMES
        defaultPlayerShouldBeFound("optedGames.equals=" + DEFAULT_OPTED_GAMES);

        // Get all the playerList where optedGames equals to UPDATED_OPTED_GAMES
        defaultPlayerShouldNotBeFound("optedGames.equals=" + UPDATED_OPTED_GAMES);
    }

    @Test
    @Transactional
    public void getAllPlayersByOptedGamesIsInShouldWork() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where optedGames in DEFAULT_OPTED_GAMES or UPDATED_OPTED_GAMES
        defaultPlayerShouldBeFound("optedGames.in=" + DEFAULT_OPTED_GAMES + "," + UPDATED_OPTED_GAMES);

        // Get all the playerList where optedGames equals to UPDATED_OPTED_GAMES
        defaultPlayerShouldNotBeFound("optedGames.in=" + UPDATED_OPTED_GAMES);
    }

    @Test
    @Transactional
    public void getAllPlayersByOptedGamesIsNullOrNotNull() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where optedGames is not null
        defaultPlayerShouldBeFound("optedGames.specified=true");

        // Get all the playerList where optedGames is null
        defaultPlayerShouldNotBeFound("optedGames.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlayersByPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where points equals to DEFAULT_POINTS
        defaultPlayerShouldBeFound("points.equals=" + DEFAULT_POINTS);

        // Get all the playerList where points equals to UPDATED_POINTS
        defaultPlayerShouldNotBeFound("points.equals=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllPlayersByPointsIsInShouldWork() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where points in DEFAULT_POINTS or UPDATED_POINTS
        defaultPlayerShouldBeFound("points.in=" + DEFAULT_POINTS + "," + UPDATED_POINTS);

        // Get all the playerList where points equals to UPDATED_POINTS
        defaultPlayerShouldNotBeFound("points.in=" + UPDATED_POINTS);
    }

    @Test
    @Transactional
    public void getAllPlayersByPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList where points is not null
        defaultPlayerShouldBeFound("points.specified=true");

        // Get all the playerList where points is null
        defaultPlayerShouldNotBeFound("points.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlayersByFranchiseIsEqualToSomething() throws Exception {
        // Initialize the database
        Franchise franchise = FranchiseResourceIntTest.createEntity(em);
        em.persist(franchise);
        em.flush();
        player.setFranchise(franchise);
        playerRepository.saveAndFlush(player);
        Long franchiseId = franchise.getId();

        // Get all the playerList where franchise equals to franchiseId
        defaultPlayerShouldBeFound("franchiseId.equals=" + franchiseId);

        // Get all the playerList where franchise equals to franchiseId + 1
        defaultPlayerShouldNotBeFound("franchiseId.equals=" + (franchiseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPlayerShouldBeFound(String filter) throws Exception {
        restPlayerMockMvc.perform(get("/api/players?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(player.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].basePrice").value(hasItem(DEFAULT_BASE_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].bidPrice").value(hasItem(DEFAULT_BID_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].optedGames").value(hasItem(DEFAULT_OPTED_GAMES.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS.doubleValue())))
            .andExpect(jsonPath("$.[*].profilePicContentType").value(hasItem(DEFAULT_PROFILE_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].profilePic").value(hasItem(Base64Utils.encodeToString(DEFAULT_PROFILE_PIC))));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPlayerShouldNotBeFound(String filter) throws Exception {
        restPlayerMockMvc.perform(get("/api/players?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingPlayer() throws Exception {
        // Get the player
        restPlayerMockMvc.perform(get("/api/players/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player
        Player updatedPlayer = playerRepository.findOne(player.getId());
        // Disconnect from session so that the updates on updatedPlayer are not directly saved in db
        em.detach(updatedPlayer);
        updatedPlayer
            .name(UPDATED_NAME)
            .basePrice(UPDATED_BASE_PRICE)
            .bidPrice(UPDATED_BID_PRICE)
            .optedGames(UPDATED_OPTED_GAMES)
            .points(UPDATED_POINTS)
            .profilePic(UPDATED_PROFILE_PIC)
            .profilePicContentType(UPDATED_PROFILE_PIC_CONTENT_TYPE);
        PlayerDTO playerDTO = playerMapper.toDto(updatedPlayer);

        restPlayerMockMvc.perform(put("/api/players")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(playerDTO)))
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getBasePrice()).isEqualTo(UPDATED_BASE_PRICE);
        assertThat(testPlayer.getBidPrice()).isEqualTo(UPDATED_BID_PRICE);
        assertThat(testPlayer.getOptedGames()).isEqualTo(UPDATED_OPTED_GAMES);
        assertThat(testPlayer.getPoints()).isEqualTo(UPDATED_POINTS);
        assertThat(testPlayer.getProfilePic()).isEqualTo(UPDATED_PROFILE_PIC);
        assertThat(testPlayer.getProfilePicContentType()).isEqualTo(UPDATED_PROFILE_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPlayerMockMvc.perform(put("/api/players")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(playerDTO)))
            .andExpect(status().isCreated());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);
        int databaseSizeBeforeDelete = playerRepository.findAll().size();

        // Get the player
        restPlayerMockMvc.perform(delete("/api/players/{id}", player.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Player.class);
        Player player1 = new Player();
        player1.setId(1L);
        Player player2 = new Player();
        player2.setId(player1.getId());
        assertThat(player1).isEqualTo(player2);
        player2.setId(2L);
        assertThat(player1).isNotEqualTo(player2);
        player1.setId(null);
        assertThat(player1).isNotEqualTo(player2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerDTO.class);
        PlayerDTO playerDTO1 = new PlayerDTO();
        playerDTO1.setId(1L);
        PlayerDTO playerDTO2 = new PlayerDTO();
        assertThat(playerDTO1).isNotEqualTo(playerDTO2);
        playerDTO2.setId(playerDTO1.getId());
        assertThat(playerDTO1).isEqualTo(playerDTO2);
        playerDTO2.setId(2L);
        assertThat(playerDTO1).isNotEqualTo(playerDTO2);
        playerDTO1.setId(null);
        assertThat(playerDTO1).isNotEqualTo(playerDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(playerMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(playerMapper.fromId(null)).isNull();
    }
}
