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

import com.firstfuel.fafi.service.TieTeamQueryService;
import com.firstfuel.fafi.service.TieTeamService;
import com.firstfuel.fafi.service.dto.TieTeamCriteria;
import com.firstfuel.fafi.service.dto.TieTeamDTO;
import com.firstfuel.fafi.web.rest.errors.BadRequestAlertException;
import com.firstfuel.fafi.web.rest.util.HeaderUtil;
import com.firstfuel.fafi.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing TieTeam.
 */
@RestController
@RequestMapping("/api")
public class TieTeamResource {

    private final Logger log = LoggerFactory.getLogger( TieTeamResource.class );

    private static final String ENTITY_NAME = "tieTeam";

    @Autowired
    private TieTeamService tieTeamService;

    @Autowired
    private TieTeamQueryService tieTeamQueryService;

    /**
     * POST  /tie-teams : Create a new tieTeam.
     *
     * @param tieTeamDTO the tieTeamDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tieTeamDTO, or with status 400 (Bad Request) if the tieTeam has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tie-teams")
    @Timed
    public ResponseEntity<TieTeamDTO> createTieTeam( @RequestBody TieTeamDTO tieTeamDTO )
        throws URISyntaxException {
        log.debug( "REST request to save TieTeam : {}", tieTeamDTO );
        if ( tieTeamDTO.getId() != null ) {
            throw new BadRequestAlertException( "A new tieTeam cannot already have an ID", ENTITY_NAME, "idexists" );
        }
        TieTeamDTO result = tieTeamService.save( tieTeamDTO );
        return ResponseEntity.created( new URI( "/api/tie-teams/" + result.getId() ) )
            .headers( HeaderUtil.createEntityCreationAlert( ENTITY_NAME, result.getId().toString() ) )
            .body( result );
    }

    /**
     * PUT  /tie-teams : Updates an existing tieTeam.
     *
     * @param tieTeamDTO the tieTeamDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tieTeamDTO,
     * or with status 400 (Bad Request) if the tieTeamDTO is not valid,
     * or with status 500 (Internal Server Error) if the tieTeamDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tie-teams")
    @Timed
    public ResponseEntity<TieTeamDTO> updateTieTeam( @RequestBody TieTeamDTO tieTeamDTO )
        throws URISyntaxException {
        log.debug( "REST request to update TieTeam : {}", tieTeamDTO );
        if ( tieTeamDTO.getId() == null ) {
            return createTieTeam( tieTeamDTO );
        }
        TieTeamDTO result = tieTeamService.save( tieTeamDTO );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityUpdateAlert( ENTITY_NAME, tieTeamDTO.getId().toString() ) ).body( result );
    }

    /**
     * GET  /tie-teams : get all the tieTeams.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tieTeams in body
     */
    @GetMapping("/tie-teams")
    @Timed
    public ResponseEntity<List<TieTeamDTO>> getAllTieTeams( TieTeamCriteria criteria, Pageable pageable ) {
        log.debug( "REST request to get TieTeams by criteria: {}", criteria );
        Page<TieTeamDTO> page = tieTeamQueryService.findByCriteria( criteria, pageable );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( page, "/api/tie-teams" );
        return new ResponseEntity<>( page.getContent(), headers, HttpStatus.OK );
    }

    /**
     * GET  /tie-teams/:id : get the "id" tieTeam.
     *
     * @param id the id of the tieTeamDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tieTeamDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tie-teams/{id}")
    @Timed
    public ResponseEntity<TieTeamDTO> getTieTeam( @PathVariable Long id ) {
        log.debug( "REST request to get TieTeam : {}", id );
        TieTeamDTO tieTeamDTO = tieTeamService.findOne( id );
        return ResponseUtil.wrapOrNotFound( Optional.ofNullable( tieTeamDTO ) );
    }

    /**
     * DELETE  /tie-teams/:id : delete the "id" tieTeam.
     *
     * @param id the id of the tieTeamDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tie-teams/{id}")
    @Timed
    public ResponseEntity<Void> deleteTieTeam( @PathVariable Long id ) {
        log.debug( "REST request to delete TieTeam : {}", id );
        tieTeamService.delete( id );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityDeletionAlert( ENTITY_NAME, id.toString() ) ).build();
    }
}
