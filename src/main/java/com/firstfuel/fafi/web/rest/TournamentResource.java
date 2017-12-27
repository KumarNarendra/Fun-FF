package com.firstfuel.fafi.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import com.firstfuel.fafi.service.TournamentQueryService;
import com.firstfuel.fafi.service.TournamentService;
import com.firstfuel.fafi.service.dto.TournamentCriteria;
import com.firstfuel.fafi.service.dto.TournamentDTO;
import com.firstfuel.fafi.web.rest.errors.BadRequestAlertException;
import com.firstfuel.fafi.web.rest.util.HeaderUtil;
import com.firstfuel.fafi.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Tournament.
 */
@RestController
@RequestMapping("/api")
public class TournamentResource {

    private final Logger log = LoggerFactory.getLogger( TournamentResource.class );

    private static final String ENTITY_NAME = "tournament";

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentQueryService tournamentQueryService;

    /**
     * POST  /tournaments : Create a new tournament.
     *
     * @param tournamentDTO the tournamentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tournamentDTO, or with status 400 (Bad Request) if the tournament has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tournaments")
    @Timed
    public ResponseEntity<TournamentDTO> createTournament( @Valid @RequestBody TournamentDTO tournamentDTO )
        throws URISyntaxException {
        log.debug( "REST request to save Tournament : {}", tournamentDTO );
        if ( tournamentDTO.getId() != null ) {
            throw new BadRequestAlertException( "A new tournament cannot already have an ID", ENTITY_NAME, "idexists" );
        }
        TournamentDTO result = tournamentService.save( tournamentDTO );
        return ResponseEntity.created( new URI( "/api/tournaments/" + result.getId() ) )
            .headers( HeaderUtil.createEntityCreationAlert( ENTITY_NAME, result.getId().toString() ) )
            .body( result );
    }

    /**
     * PUT  /tournaments : Updates an existing tournament.
     *
     * @param tournamentDTO the tournamentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tournamentDTO,
     * or with status 400 (Bad Request) if the tournamentDTO is not valid,
     * or with status 500 (Internal Server Error) if the tournamentDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tournaments")
    @Timed
    public ResponseEntity<TournamentDTO> updateTournament( @Valid @RequestBody TournamentDTO tournamentDTO )
        throws URISyntaxException {
        log.debug( "REST request to update Tournament : {}", tournamentDTO );
        if ( tournamentDTO.getId() == null ) {
            return createTournament( tournamentDTO );
        }
        TournamentDTO result = tournamentService.save( tournamentDTO );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityUpdateAlert( ENTITY_NAME, tournamentDTO.getId().toString() ) ).body( result );
    }

    /**
     * GET  /tournaments : get all the tournaments.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tournaments in body
     */
    @GetMapping("/tournaments")
    @Timed
    public ResponseEntity<List<TournamentDTO>> getAllTournaments( TournamentCriteria criteria, Pageable pageable ) {
        log.debug( "REST request to get Tournaments by criteria: {}", criteria );
        Page<TournamentDTO> page = tournamentQueryService.findByCriteria( criteria, pageable );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( page, "/api/tournaments" );
        return new ResponseEntity<>( page.getContent(), headers, HttpStatus.OK );
    }

    /**
     * GET  /tournaments/:id : get the "id" tournament.
     *
     * @param id the id of the tournamentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tournamentDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tournaments/{id}")
    @Timed
    public ResponseEntity<TournamentDTO> getTournament( @PathVariable Long id ) {
        log.debug( "REST request to get Tournament : {}", id );
        TournamentDTO tournamentDTO = tournamentService.findOne( id );
        return ResponseUtil.wrapOrNotFound( Optional.ofNullable( tournamentDTO ) );
    }

    /**
     * DELETE  /tournaments/:id : delete the "id" tournament.
     *
     * @param id the id of the tournamentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tournaments/{id}")
    @Timed
    public ResponseEntity<Void> deleteTournament( @PathVariable Long id ) {
        log.debug( "REST request to delete Tournament : {}", id );
        tournamentService.delete( id );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityDeletionAlert( ENTITY_NAME, id.toString() ) ).build();
    }
}
