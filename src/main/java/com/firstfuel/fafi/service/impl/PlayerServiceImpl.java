package com.firstfuel.fafi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstfuel.fafi.domain.Player;
import com.firstfuel.fafi.repository.PlayerRepository;
import com.firstfuel.fafi.service.PlayerService;
import com.firstfuel.fafi.service.dto.PlayerDTO;
import com.firstfuel.fafi.service.mapper.PlayerMapper;


/**
 * Service Implementation for managing Player.
 */
@Service
@Transactional
public class PlayerServiceImpl
    implements PlayerService {

    private final Logger log = LoggerFactory.getLogger( PlayerServiceImpl.class );

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;


    /**
     * Save a player.
     *
     * @param playerDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PlayerDTO save( PlayerDTO playerDTO ) {
        log.debug( "Request to save Player : {}", playerDTO );
        Player player = playerMapper.toEntity( playerDTO );
        player = playerRepository.save( player );
        return playerMapper.toDto( player );
    }

    /**
     * Get all the players.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlayerDTO> findAll( Pageable pageable ) {
        log.debug( "Request to get all Players" );
        return playerRepository.findAll( pageable ).map( playerMapper::toDto );
    }

    /**
     * Get one player by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PlayerDTO findOne( Long id ) {
        log.debug( "Request to get Player : {}", id );
        Player player = playerRepository.findOne( id );
        return playerMapper.toDto( player );
    }

    /**
     * Delete the player by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete( Long id ) {
        log.debug( "Request to delete Player : {}", id );
        playerRepository.delete( id );
    }
}
