package com.firstfuel.fafi.service.impl;

import com.firstfuel.fafi.service.TieMatchService;
import com.firstfuel.fafi.domain.TieMatch;
import com.firstfuel.fafi.repository.TieMatchRepository;
import com.firstfuel.fafi.service.dto.TieMatchDTO;
import com.firstfuel.fafi.service.mapper.TieMatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing TieMatch.
 */
@Service
@Transactional
public class TieMatchServiceImpl implements TieMatchService{

    private final Logger log = LoggerFactory.getLogger(TieMatchServiceImpl.class);

    private final TieMatchRepository tieMatchRepository;

    private final TieMatchMapper tieMatchMapper;

    public TieMatchServiceImpl(TieMatchRepository tieMatchRepository, TieMatchMapper tieMatchMapper) {
        this.tieMatchRepository = tieMatchRepository;
        this.tieMatchMapper = tieMatchMapper;
    }

    /**
     * Save a tieMatch.
     *
     * @param tieMatchDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TieMatchDTO save(TieMatchDTO tieMatchDTO) {
        log.debug("Request to save TieMatch : {}", tieMatchDTO);
        TieMatch tieMatch = tieMatchMapper.toEntity(tieMatchDTO);
        tieMatch = tieMatchRepository.save(tieMatch);
        return tieMatchMapper.toDto(tieMatch);
    }

    /**
     * Get all the tieMatches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TieMatchDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TieMatches");
        return tieMatchRepository.findAll(pageable)
            .map(tieMatchMapper::toDto);
    }


    /**
     *  get all the tieMatches where Winner is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<TieMatchDTO> findAllWhereWinnerIsNull() {
        log.debug("Request to get all tieMatches where Winner is null");
        return StreamSupport
            .stream(tieMatchRepository.findAll().spliterator(), false)
            .filter(tieMatch -> tieMatch.getWinner() == null)
            .map(tieMatchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one tieMatch by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TieMatchDTO findOne(Long id) {
        log.debug("Request to get TieMatch : {}", id);
        TieMatch tieMatch = tieMatchRepository.findOne(id);
        return tieMatchMapper.toDto(tieMatch);
    }

    /**
     * Delete the tieMatch by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TieMatch : {}", id);
        tieMatchRepository.delete(id);
    }
}
