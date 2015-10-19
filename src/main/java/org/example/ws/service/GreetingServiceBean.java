package org.example.ws.service;

import org.example.ws.model.Greeting;
import org.example.ws.repository.GreetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

/**
 * @author LeeSoohoon
 */
@Service
@Transactional(
        propagation = Propagation.SUPPORTS,
        readOnly = true)
public class GreetingServiceBean implements GreetingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GreetingRepository greetingRepository;

    @Override
    public Collection<Greeting> findAll() {
        Collection<Greeting> greetings = greetingRepository.findAll();
        return greetings;
    }

    @Override
    @Cacheable(
            value = "greetings",
            key = "#id")
    public Greeting findOne(final Long id) {
        Greeting greeting = greetingRepository.findOne(id);
        return greeting;
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false)
    @CachePut(
            value = "greetings",
            key = "#result.id")
    public Greeting create(final Greeting greeting) {
        if (greeting.getId() != null) {
            // Cannot create Greeting with specified ID value
            logger.error("Attempted to create a Greeting, but id attribute was not null");
            throw  new EntityExistsException("The id attribute must be null to persist a new entity.");
        }
        Greeting savedGreeting = greetingRepository.save(greeting);
        // Illustrate Tx rollback
        if (savedGreeting.getId() == 4L) {
            throw new RuntimeException("Roll me back!");
        }
        return savedGreeting;
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false)
    @CachePut(
            value = "greetings",
            key = "#greeting.id")
    public Greeting update(final Greeting greeting) {
        Greeting greetingPersisted = findOne(greeting.getId());
        if (greetingPersisted == null) {
            // Cannot update Greeting that hasn't been persisted
            logger.error("Attempted to update a Greeting, but the entity does not exist.");
            throw new NoResultException("Requested entity not found.");
        }
        Greeting updatedGreeting = greetingRepository.save(greeting);
        return updatedGreeting;
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false)
    @CacheEvict(
            value = "greetings",
            key = "#id")
    public void delete(final Long id) {
        greetingRepository.delete(id);
    }

    @Override
    @CacheEvict(
            value = "greetings",
            allEntries = true)
    public void evictCache() {
        logger.info("> evictCache");
        logger.info("< evictCache");
    }
}
