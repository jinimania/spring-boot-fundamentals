package org.example.ws.service;

import org.example.ws.AbstractTest;
import org.example.ws.model.Greeting;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

/**
 * @author LeeSoohoon
 */
@Transactional
public class GreetingServiceTest extends AbstractTest {

    @Autowired
    GreetingService service;

    @Before
    public void setUp() {
        service.evictCache();
    }

    @After
    public void tearDown() {
        // clean up after each test method
    }

    @Test
    public void testFindAll() {
        Collection<Greeting> list = service.findAll();

        Assert.assertNotNull("failure - expected not null", list);
        Assert.assertEquals("failure - expected size", 2, list.size());
    }

    @Test
    public void testFindOne() {
        final Long id = new Long(1);
        final Greeting entity = service.findOne(id);

        Assert.assertNotNull("failure - expected not null", entity);
        Assert.assertEquals("failure - expected id attribute match", id, entity.getId());
    }

    @Test
    public void testFindOndNotFound() {
        final long id = Long.MAX_VALUE;
        final Greeting entity = service.findOne(id);

        Assert.assertNull("failure - expected null", entity);
    }

    @Test
    public void testCreate() {
        final Greeting entity = new Greeting();
        entity.setText("test");

        final Greeting createdEntity = service.create(entity);

        Assert.assertNotNull("failure - expected not null", createdEntity);
        Assert.assertNotNull("failure - expected id attribute not null", createdEntity.getId());
        Assert.assertEquals("failure - expected text attribute match", "test", createdEntity.getText());

        final Collection<Greeting> list = service.findAll();

        Assert.assertEquals("failure - expected size", 3, list.size());
    }

    @Test
    public void testCreateWithId() {
        Exception exception = null;

        final Greeting entity = new Greeting();
        entity.setId(Long.MAX_VALUE);
        entity.setText("test");

        try {
            service.create(entity);
        } catch (EntityExistsException e) {
            exception = e;
        }

        Assert.assertNotNull("failure - expected exception", exception);
        Assert.assertTrue("failure - expected EntityExistsException", exception instanceof EntityExistsException);
    }

    @Test
    public void testUpdate() {
        final Long id = new Long(1);
        final Greeting entity = service.findOne(id);

        Assert.assertNotNull("failure - expected not null", entity);

        final String updatedText = entity.getText() + " test";
        entity.setText(updatedText);
        final Greeting updatedEntity = service.update(entity);

        Assert.assertNotNull("failure - expected not null", updatedEntity);
        Assert.assertEquals("failure - expected id attribute match", id, updatedEntity.getId());
        Assert.assertEquals("failure - expexted text attribute match", updatedText, updatedEntity.getText());
    }

    @Test
    public void testUpdateNotFound() {
        Exception exception = null;

        final Greeting entity = new Greeting();
        entity.setId(Long.MAX_VALUE);
        entity.setText("test");

        try {
            service.update(entity);
        } catch (NoResultException e) {
            exception = e;
        }

        Assert.assertNotNull("failure - excepted exception", exception);
        Assert.assertTrue("failure - excepted NoResultException", exception instanceof NoResultException);
    }

    @Test
    public void testDelete() {
        final Long id = new Long(1);
        final Greeting entity = service.findOne(id);

        Assert.assertNotNull("failure - expected not null", entity);

        service.delete(id);

        final Collection<Greeting> list = service.findAll();

        Assert.assertEquals("failure - expected size", 1, list.size());

        final Greeting deletedEntity = service.findOne(id);

        Assert.assertNull("failure - expected null", deletedEntity);
    }
}
