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

import com.firstfuel.fafi.service.SeasonQueryService;
import com.firstfuel.fafi.service.SeasonService;
import com.firstfuel.fafi.service.dto.SeasonCriteria;
import com.firstfuel.fafi.service.dto.SeasonDTO;
import com.firstfuel.fafi.web.rest.errors.BadRequestAlertException;
import com.firstfuel.fafi.web.rest.util.HeaderUtil;
import com.firstfuel.fafi.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Season.
 */
@RestController
@RequestMapping("/api")
public class SeasonResource {

    private final Logger log = LoggerFactory.getLogger( SeasonResource.class );

    private static final String ENTITY_NAME = "season";

    @Autowired
    private SeasonService seasonService;

    @Autowired
    private SeasonQueryService seasonQueryService;

    /**
     * POST  /seasons : Create a new season.
     *
     * @param seasonDTO the seasonDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new seasonDTO, or with status 400 (Bad Request) if the season has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/seasons")
    @Timed
    public ResponseEntity<SeasonDTO> createSeason( @Valid @RequestBody SeasonDTO seasonDTO )
        throws URISyntaxException {
        log.debug( "REST request to save Season : {}", seasonDTO );
        if ( seasonDTO.getId() != null ) {
            throw new BadRequestAlertException( "A new season cannot already have an ID", ENTITY_NAME, "idexists" );
        }
        SeasonDTO result = seasonService.save( seasonDTO );
        return ResponseEntity.created( new URI( "/api/seasons/" + result.getId() ) )
            .headers( HeaderUtil.createEntityCreationAlert( ENTITY_NAME, result.getId().toString() ) )
            .body( result );
    }

    /**
     * PUT  /seasons : Updates an existing season.
     *
     * @param seasonDTO the seasonDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated seasonDTO,
     * or with status 400 (Bad Request) if the seasonDTO is not valid,
     * or with status 500 (Internal Server Error) if the seasonDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/seasons")
    @Timed
    public ResponseEntity<SeasonDTO> updateSeason( @Valid @RequestBody SeasonDTO seasonDTO )
        throws URISyntaxException {
        log.debug( "REST request to update Season : {}", seasonDTO );
        if ( seasonDTO.getId() == null ) {
            return createSeason( seasonDTO );
        }
        SeasonDTO result = seasonService.save( seasonDTO );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityUpdateAlert( ENTITY_NAME, seasonDTO.getId().toString() ) ).body( result );
    }

    /**
     * GET  /seasons : get all the seasons.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of seasons in body
     */
    @GetMapping("/seasons")
    @Timed
    public ResponseEntity<List<SeasonDTO>> getAllSeasons( SeasonCriteria criteria, Pageable pageable ) {
        log.debug( "REST request to get Seasons by criteria: {}", criteria );
        Page<SeasonDTO> page = seasonQueryService.findByCriteria( criteria, pageable );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( page, "/api/seasons" );
        return new ResponseEntity<>( page.getContent(), headers, HttpStatus.OK );
    }

    /**
     * GET  /seasons/:id : get the "id" season.
     *
     * @param id the id of the seasonDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the seasonDTO, or with status 404 (Not Found)
     */
    @GetMapping("/seasons/{id}")
    @Timed
    public ResponseEntity<SeasonDTO> getSeason( @PathVariable Long id ) {
        log.debug( "REST request to get Season : {}", id );
        SeasonDTO seasonDTO = seasonService.findOne( id );
        return ResponseUtil.wrapOrNotFound( Optional.ofNullable( seasonDTO ) );
    }

    /**
     * DELETE  /seasons/:id : delete the "id" season.
     *
     * @param id the id of the seasonDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/seasons/{id}")
    @Timed
    public ResponseEntity<Void> deleteSeason( @PathVariable Long id ) {
        log.debug( "REST request to delete Season : {}", id );
        seasonService.delete( id );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityDeletionAlert( ENTITY_NAME, id.toString() ) ).build();
    }
}
