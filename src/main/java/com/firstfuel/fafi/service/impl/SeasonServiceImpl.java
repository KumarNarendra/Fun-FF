package com.firstfuel.fafi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Season;
import com.firstfuel.fafi.repository.SeasonRepository;
import com.firstfuel.fafi.service.SeasonService;
import com.firstfuel.fafi.service.dto.SeasonDTO;
import com.firstfuel.fafi.service.mapper.SeasonMapper;


/**
 * Service Implementation for managing Season.
 */
@Service
@Transactional
public class SeasonServiceImpl
    implements SeasonService {

    private final Logger log = LoggerFactory.getLogger( SeasonServiceImpl.class );

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeasonMapper seasonMapper;


    /**
     * Save a season.
     *
     * @param seasonDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SeasonDTO save( SeasonDTO seasonDTO ) {
        log.debug( "Request to save Season : {}", seasonDTO );
        Season season = seasonMapper.toEntity( seasonDTO );
        season = seasonRepository.save( season );
        return seasonMapper.toDto( season );
    }

    /**
     * Get all the seasons.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SeasonDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all Seasons" );
        return seasonRepository.findAll( pageable ).map( seasonMapper::toDto );
    }

    /**
     * Get one season by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public SeasonDTO findOne( Long id ) {
        log.debug( "Request to get Season : {}", id );
        Season season = seasonRepository.findOne( id );
        return seasonMapper.toDto( season );
    }

    /**
     * Delete the season by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete Season : {}", id );
        seasonRepository.delete( id );
    }
}
