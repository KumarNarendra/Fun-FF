package com.firstfuel.fafi.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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

import com.firstfuel.fafi.service.TieMatchQueryService;
import com.firstfuel.fafi.service.TieMatchService;
import com.firstfuel.fafi.service.dto.TieMatchCriteria;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
import com.firstfuel.fafi.web.rest.errors.BadRequestAlertException;
import com.firstfuel.fafi.web.rest.util.HeaderUtil;
import com.firstfuel.fafi.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing TieMatch.
 */
@RestController
@RequestMapping("/api")
public class TieMatchResource {

    private final Logger log = LoggerFactory.getLogger( TieMatchResource.class );

    private static final String ENTITY_NAME = "tieMatch";

    @Autowired
    private TieMatchService tieMatchService;

    @Autowired
    private TieMatchQueryService tieMatchQueryService;


    /**
     * POST  /tie-matches : Create a new tieMatch.
     *
     * @param tieMatchDTO the tieMatchDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tieMatchDTO, or with status 400 (Bad Request) if the tieMatch has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tie-matches")
    @Timed
    public ResponseEntity<TieMatchDTO> createTieMatch( @RequestBody TieMatchDTO tieMatchDTO )
        throws URISyntaxException {
        log.debug( "REST request to save TieMatch : {}", tieMatchDTO );
        if ( tieMatchDTO.getId() != null ) {
            throw new BadRequestAlertException( "A new tieMatch cannot already have an ID", ENTITY_NAME, "idexists" );
        }
        TieMatchDTO result = tieMatchService.save( tieMatchDTO );
        return ResponseEntity.created( new URI( "/api/tie-matches/" + result.getId() ) )
            .headers( HeaderUtil.createEntityCreationAlert( ENTITY_NAME, result.getId().toString() ) )
            .body( result );
    }

    /**
     * PUT  /tie-matches : Updates an existing tieMatch.
     *
     * @param tieMatchDTO the tieMatchDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tieMatchDTO,
     * or with status 400 (Bad Request) if the tieMatchDTO is not valid,
     * or with status 500 (Internal Server Error) if the tieMatchDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tie-matches")
    @Timed
    public ResponseEntity<TieMatchDTO> updateTieMatch( @RequestBody TieMatchDTO tieMatchDTO )
        throws URISyntaxException {
        log.debug( "REST request to update TieMatch : {}", tieMatchDTO );
        if ( tieMatchDTO.getId() == null ) {
            return createTieMatch( tieMatchDTO );
        }
        TieMatchDTO result = tieMatchService.save( tieMatchDTO );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityUpdateAlert( ENTITY_NAME, tieMatchDTO.getId().toString() ) ).body( result );
    }

    /**
     * GET  /tie-matches : get all the tieMatches.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tieMatches in body
     */
    @GetMapping("/tie-matches")
    @Timed
    public ResponseEntity<List<TieMatchDTO>> getAllTieMatches( TieMatchCriteria criteria, Pageable pageable ) {
        log.debug( "REST request to get TieMatches by criteria: {}", criteria );
        Page<TieMatchDTO> page = tieMatchQueryService.findByCriteria( criteria, pageable );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( page, "/api/tie-matches" );
        return new ResponseEntity<>( page.getContent(), headers, HttpStatus.OK );
    }

    /**
     * GET  /tie-matches/:id : get the "id" tieMatch.
     *
     * @param id the id of the tieMatchDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tieMatchDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tie-matches/{id}")
    @Timed
    public ResponseEntity<TieMatchDTO> getTieMatch( @PathVariable Long id ) {
        log.debug( "REST request to get TieMatch : {}", id );
        TieMatchDTO tieMatchDTO = tieMatchService.findOne( id );
        return ResponseUtil.wrapOrNotFound( Optional.ofNullable( tieMatchDTO ) );
    }

    /**
     * DELETE  /tie-matches/:id : delete the "id" tieMatch.
     *
     * @param id the id of the tieMatchDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tie-matches/{id}")
    @Timed
    public ResponseEntity<Void> deleteTieMatch( @PathVariable Long id ) {
        log.debug( "REST request to delete TieMatch : {}", id );
        tieMatchService.delete( id );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityDeletionAlert( ENTITY_NAME, id.toString() ) ).build();
    }
}
