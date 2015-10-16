package org.example.ws.service;

import org.example.ws.model.Greeting;

import java.util.concurrent.Future;

/**
 * @author LeeSoohoon
 */
public interface EmailService {

    Boolean send(Greeting greeting);

    void sendAsync(Greeting greeting);

    Future<Boolean> sendAsyncWithResult(Greeting greeting);
}
