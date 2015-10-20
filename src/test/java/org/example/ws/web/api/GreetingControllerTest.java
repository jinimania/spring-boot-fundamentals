package org.example.ws.web.api;

import org.example.ws.AbstractControllerTest;
import org.example.ws.model.Greeting;
import org.example.ws.service.GreetingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author LeeSoohoon
 */
@Transactional
public class GreetingControllerTest extends AbstractControllerTest {


    @Autowired
    private GreetingService greetingService;

    @Before
    public void setUp() {
        super.setUp();
        greetingService.evictCache();
    }

    @Test
    public void testGetGreetings() throws Exception {
        String uri = "/api/greetings";
        final MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        Assert.assertEquals("Failure - expected HTTP status 200", 200, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);
    }

    @Test
    public void testGetGreeting() throws Exception {
        final String uri = "/api/greetings/{id}";
        final Long id = (long) 1;

        final MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(uri, id).accept(MediaType.APPLICATION_JSON)).andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        Assert.assertEquals("Failure - expected HTTP status 200", 200, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);
    }

    @Test
    public void testGetGreetingNotFound() throws Exception {
        final String uri = "/api/greetings/{id}";
        final long id = Long.MAX_VALUE;

        final MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(uri, id).accept(MediaType.APPLICATION_JSON)).andReturn();

        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        Assert.assertEquals("Failure - expected HTTP status 404", 404, status);
        Assert.assertTrue("Failure - expected HTTP response body to be empty", content.trim().length() == 0);
    }

    @Test
    public void testCreateGreeting() throws Exception {
        final String uri = "/api/greetings";
        final Greeting greeting = new Greeting();
        greeting.setText("test");
        final String inputJson = super.mapToJson(greeting);

        final MvcResult result = mvc.
                perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(inputJson)).
                andReturn();

        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        Assert.assertEquals("Failure - expected HTTP status 201", 201, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);

        final Greeting createdGreeting = super.mapFromJson(content, Greeting.class);

        Assert.assertNotNull("Failure - expected greeting not null", createdGreeting);
        Assert.assertNotNull("Failure - expected greeting.id not null", createdGreeting.getId());
        Assert.assertEquals("Failure - expected greeting.text match", "test", createdGreeting.getText());
    }

    @Test
    public void testUpdateGreeting() throws Exception {
        final String uri = "/api/greetings/{id}";
        final Long id = (long) 1;
        final Greeting greeting = greetingService.findOne(id);
        final String updatedText = greeting.getText() + " test";
        greeting.setText(updatedText);
        final String inputJson = super.mapToJson(greeting);

        final MvcResult result = mvc
                .perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(inputJson))
                .andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        Assert.assertEquals("Failure - expected HTTP status 200", 200, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);

        final Greeting updatedGreeting = super.mapFromJson(content, Greeting.class);

        Assert.assertNotNull("Failure - expected greeting not null", updatedGreeting);
        Assert.assertEquals("Failure - expected greeting.id unchanged", greeting.getId(), updatedGreeting.getId());
        Assert.assertEquals("Failure - expected updated greeting text match", updatedText, updatedGreeting.getText());
    }

    @Test
    public void testDeleteGreeting() throws Exception {
        final String uri = "/api/greetings/{id}";
        final Long id = (long) 1;

        final MvcResult result = mvc
                .perform(MockMvcRequestBuilders.delete(uri, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        Assert.assertEquals("Failure - expected HTTP status 204", 204, status);
        Assert.assertTrue("Failure - expected HTTP response body to be empty", content.trim().length() == 0);

        final Greeting deletedGreeting = greetingService.findOne(id);

        Assert.assertNull("Failure - expected greeting to be null", deletedGreeting);
    }
}
