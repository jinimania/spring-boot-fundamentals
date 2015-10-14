package org.example.ws.service;

import org.example.ws.model.Greeting;

import java.util.Collection;

/**
 * @author LeeSoohoon
 */
public interface GreetingService {
    Collection<Greeting> findAll();

    Greeting findOne(Long id);

    Greeting create(Greeting greeting);

    Greeting update(Greeting greeting);

    void delete(Long id);
}
