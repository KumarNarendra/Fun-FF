package com.firstfuel.fafi.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.firstfuel.fafi.service.PlayerService;
import com.firstfuel.fafi.web.rest.errors.BadRequestAlertException;
import com.firstfuel.fafi.web.rest.util.HeaderUtil;
import com.firstfuel.fafi.web.rest.util.PaginationUtil;
import com.firstfuel.fafi.service.dto.PlayerDTO;
import com.firstfuel.fafi.service.dto.PlayerCriteria;
import com.firstfuel.fafi.service.PlayerQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Player.
 */
@RestController
@RequestMapping("/api")
public class PlayerResource {

    private final Logger log = LoggerFactory.getLogger(PlayerResource.class);

    private static final String ENTITY_NAME = "player";

    private final PlayerService playerService;

    private final PlayerQueryService playerQueryService;

    public PlayerResource(PlayerService playerService, PlayerQueryService playerQueryService) {
        this.playerService = playerService;
        this.playerQueryService = playerQueryService;
    }

    /**
     * POST  /players : Create a new player.
     *
     * @param playerDTO the playerDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new playerDTO, or with status 400 (Bad Request) if the player has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/players")
    @Timed
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) throws URISyntaxException {
        log.debug("REST request to save Player : {}", playerDTO);
        if (playerDTO.getId() != null) {
            throw new BadRequestAlertException("A new player cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlayerDTO result = playerService.save(playerDTO);
        return ResponseEntity.created(new URI("/api/players/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /players : Updates an existing player.
     *
     * @param playerDTO the playerDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated playerDTO,
     * or with status 400 (Bad Request) if the playerDTO is not valid,
     * or with status 500 (Internal Server Error) if the playerDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/players")
    @Timed
    public ResponseEntity<PlayerDTO> updatePlayer(@Valid @RequestBody PlayerDTO playerDTO) throws URISyntaxException {
        log.debug("REST request to update Player : {}", playerDTO);
        if (playerDTO.getId() == null) {
            return createPlayer(playerDTO);
        }
        PlayerDTO result = playerService.save(playerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, playerDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /players : get all the players.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of players in body
     */
    @GetMapping("/players")
    @Timed
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(PlayerCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Players by criteria: {}", criteria);
        Page<PlayerDTO> page = playerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/players");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /players/:id : get the "id" player.
     *
     * @param id the id of the playerDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the playerDTO, or with status 404 (Not Found)
     */
    @GetMapping("/players/{id}")
    @Timed
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        log.debug("REST request to get Player : {}", id);
        PlayerDTO playerDTO = playerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(playerDTO));
    }

    /**
     * DELETE  /players/:id : delete the "id" player.
     *
     * @param id the id of the playerDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/players/{id}")
    @Timed
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        log.debug("REST request to delete Player : {}", id);
        playerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
