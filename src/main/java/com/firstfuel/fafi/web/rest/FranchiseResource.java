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

import com.firstfuel.fafi.service.FranchiseQueryService;
import com.firstfuel.fafi.service.FranchiseService;
import com.firstfuel.fafi.service.dto.FranchiseCriteria;
import com.firstfuel.fafi.service.dto.FranchiseDTO;
import com.firstfuel.fafi.web.rest.errors.BadRequestAlertException;
import com.firstfuel.fafi.web.rest.util.HeaderUtil;
import com.firstfuel.fafi.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Franchise.
 */
@RestController
@RequestMapping("/api")
public class FranchiseResource {

    private final Logger log = LoggerFactory.getLogger( FranchiseResource.class );

    private static final String ENTITY_NAME = "franchise";

    @Autowired
    private FranchiseService franchiseService;

    @Autowired
    private FranchiseQueryService franchiseQueryService;


    /**
     * POST  /franchises : Create a new franchise.
     *
     * @param franchiseDTO the franchiseDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new franchiseDTO, or with status 400 (Bad Request) if the franchise has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/franchises")
    @Timed
    public ResponseEntity<FranchiseDTO> createFranchise( @Valid @RequestBody FranchiseDTO franchiseDTO )
        throws URISyntaxException {
        log.debug( "REST request to save Franchise : {}", franchiseDTO );
        if ( franchiseDTO.getId() != null ) {
            throw new BadRequestAlertException( "A new franchise cannot already have an ID", ENTITY_NAME, "idexists" );
        }
        FranchiseDTO result = franchiseService.save( franchiseDTO );
        return ResponseEntity.created( new URI( "/api/franchises/" + result.getId() ) )
            .headers( HeaderUtil.createEntityCreationAlert( ENTITY_NAME, result.getId().toString() ) )
            .body( result );
    }

    /**
     * PUT  /franchises : Updates an existing franchise.
     *
     * @param franchiseDTO the franchiseDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated franchiseDTO,
     * or with status 400 (Bad Request) if the franchiseDTO is not valid,
     * or with status 500 (Internal Server Error) if the franchiseDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/franchises")
    @Timed
    public ResponseEntity<FranchiseDTO> updateFranchise( @Valid @RequestBody FranchiseDTO franchiseDTO )
        throws URISyntaxException {
        log.debug( "REST request to update Franchise : {}", franchiseDTO );
        if ( franchiseDTO.getId() == null ) {
            return createFranchise( franchiseDTO );
        }
        FranchiseDTO result = franchiseService.save( franchiseDTO );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityUpdateAlert( ENTITY_NAME, franchiseDTO.getId().toString() ) ).body( result );
    }

    /**
     * GET  /franchises : get all the franchises.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of franchises in body
     */
    @GetMapping("/franchises")
    @Timed
    public ResponseEntity<List<FranchiseDTO>> getAllFranchises( FranchiseCriteria criteria, Pageable pageable ) {
        log.debug( "REST request to get Franchises by criteria: {}", criteria );
        Page<FranchiseDTO> page = franchiseQueryService.findByCriteria( criteria, pageable );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( page, "/api/franchises" );
        return new ResponseEntity<>( page.getContent(), headers, HttpStatus.OK );
    }

    /**
     * GET  /franchises/:id : get the "id" franchise.
     *
     * @param id the id of the franchiseDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the franchiseDTO, or with status 404 (Not Found)
     */
    @GetMapping("/franchises/{id}")
    @Timed
    public ResponseEntity<FranchiseDTO> getFranchise( @PathVariable Long id ) {
        log.debug( "REST request to get Franchise : {}", id );
        FranchiseDTO franchiseDTO = franchiseService.findOne( id );
        return ResponseUtil.wrapOrNotFound( Optional.ofNullable( franchiseDTO ) );
    }

    /**
     * DELETE  /franchises/:id : delete the "id" franchise.
     *
     * @param id the id of the franchiseDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/franchises/{id}")
    @Timed
    public ResponseEntity<Void> deleteFranchise( @PathVariable Long id ) {
        log.debug( "REST request to delete Franchise : {}", id );
        franchiseService.delete( id );
        return ResponseEntity.ok().headers( HeaderUtil.createEntityDeletionAlert( ENTITY_NAME, id.toString() ) ).build();
    }
}
