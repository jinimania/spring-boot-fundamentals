package org.example.ws.web.api;

import org.example.ws.AbstractControllerTest;
import org.example.ws.model.Greeting;
import org.example.ws.service.EmailService;
import org.example.ws.service.GreetingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author LeeSoohoon
 */
@Transactional
public class GreetingControllerMocksTest extends AbstractControllerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private GreetingService greetingService;

    @InjectMocks
    private GreetingController greetingController;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setUp(greetingController);
    }

    @Test
    public void testGetGreetings() throws Exception {
        final Collection<Greeting> list = getEntityListStubDate();
        when(greetingService.findAll()).thenReturn(list);
        final String uri = "/api/greetings";

        final MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        verify(greetingService, times(1)).findAll();

        Assert.assertEquals("Failure - expected HTTP status 200", 200, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);
    }

    @Test
    public void testGetGreeting() throws Exception {
        final Long id = new Long(1);
        final Greeting entity = getEntityStubDate();

        when(greetingService.findOne(id)).thenReturn(entity);
        final String uri = "/api/greetings/{id}";

        final MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(uri, id).accept(MediaType.APPLICATION_JSON)).andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        verify(greetingService, times(1)).findOne(id);

        Assert.assertEquals("Failure - expected HTTP status 200", 200, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);
    }

    @Test
    public void testGetGreetingNotFound() throws Exception {
        final long id = Long.MAX_VALUE;

        when(greetingService.findOne(id)).thenReturn(null);
        final String uri = "/api/greetings/{id}";

        final MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(uri, id).accept(MediaType.APPLICATION_JSON)).andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        verify(greetingService, times(1)).findOne(id);

        Assert.assertEquals("Failure - expected HTTP status 404", 404, status);
        Assert.assertTrue("Failure - expected HTTP response body to be empty", content.trim().length() == 0);
    }

    @Test
    public void testCreateGreeting() throws Exception {
        final Greeting entity = getEntityStubDate();

        when(greetingService.create(any(Greeting.class))).thenReturn(entity);

        final String uri = "/api/greetings";
        final String inputJson = super.mapToJson(entity);

        final MvcResult result = mvc.
                perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(inputJson)).
                andReturn();

        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        verify(greetingService, times(1)).create(any(Greeting.class));

        Assert.assertEquals("Failure - expected HTTP status 201", 201, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);

        final Greeting createdGreeting = super.mapFromJson(content, Greeting.class);

        Assert.assertNotNull("Failure - expected greeting not null", createdGreeting);
        Assert.assertNotNull("Failure - expected greeting.id not null", createdGreeting.getId());
        Assert.assertEquals("Failure - expected greeting.text match", entity.getText(), createdGreeting.getText());
    }

    @Test
    public void testUpdateGreeting() throws Exception {
        final Greeting entity = getEntityStubDate();
        entity.setText(entity.getText() + " test");
        final Long id = (long) 1;

        when(greetingService.update(any(Greeting.class))).thenReturn(entity);

        final String uri = "/api/greetings/{id}";
        final String inputJson = super.mapToJson(entity);

        final MvcResult result = mvc
                .perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(inputJson))
                .andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        verify(greetingService, times(1)).update(any(Greeting.class));

        Assert.assertEquals("Failure - expected HTTP status 200", 200, status);
        Assert.assertTrue("Failure - expected HTTP response body to have a value", content.trim().length() > 0);

        final Greeting updatedGreeting = super.mapFromJson(content, Greeting.class);

        Assert.assertNotNull("Failure - expected greeting not null", updatedGreeting);
        Assert.assertEquals("Failure - expected greeting.id unchanged", entity.getId(), updatedGreeting.getId());
        Assert.assertEquals("Failure - expected updated greeting text match", entity.getText(), updatedGreeting.getText());
    }

    @Test
    public void testDeleteGreeting() throws Exception {
        final Long id = (long) 1;
        final String uri = "/api/greetings/{id}";

        final MvcResult result = mvc
                .perform(MockMvcRequestBuilders.delete(uri, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        final String content = result.getResponse().getContentAsString();
        final int status = result.getResponse().getStatus();

        verify(greetingService, times(1)).delete(id);

        Assert.assertEquals("Failure - expected HTTP status 204", 204, status);
        Assert.assertTrue("Failure - expected HTTP response body to be empty", content.trim().length() == 0);

        final Greeting deletedGreeting = greetingService.findOne(id);

        Assert.assertNull("Failure - expected greeting to be null", deletedGreeting);
    }

    private Collection<Greeting> getEntityListStubDate() {
        final ArrayList<Greeting> list = new ArrayList<Greeting>();
        list.add(getEntityStubDate());
        return list;
    }

    private Greeting getEntityStubDate() {
        final Greeting entity = new Greeting();
        entity.setId(1L);
        entity.setText("hello");
        return entity;
    }
}
