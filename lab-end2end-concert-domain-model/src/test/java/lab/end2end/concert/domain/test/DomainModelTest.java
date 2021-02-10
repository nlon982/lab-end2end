package lab.end2end.concert.domain.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lab.end2end.concert.domain.Concert;
import lab.end2end.concert.domain.Genre;
import lab.end2end.concert.domain.Performer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class DomainModelTest {

    private static Logger LOGGER = LoggerFactory.getLogger(DomainModelTest.class);

    private EntityManagerFactory entityManagerFactory;

    /**
     * Runs before each testJPA to create a new EntityManagerFactory.
     */
    @Before
    public void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("lab.end2end.concert");
    }

    /**
     * Runs after each testJPA to destroy the EntityManagerFactory.
     */
    @After
    public void tearDown() {
        entityManagerFactory.close();
    }

    @Test
    public void testJPA() {
        Performer aliceCooper = new Performer("Alice Cooper", "aliceCooper.jpg", Genre.Rock);
        Concert concert1 = new Concert("Spend the Night with Alice Cooper",
                LocalDateTime.of(2017, 10, 27, 19, 0), aliceCooper);

        Concert concert2 = new Concert("More from Alice C", LocalDateTime.of(2017, 10, 28, 19, 0), aliceCooper);

        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(concert1);
        em.persist(concert2);
        em.getTransaction().commit();

        // With cascading persistence, deleting concert1 shouldn't delete the
        // Performer because it's shared by concert2.
        em.getTransaction().begin();
        em.remove(concert1);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Performer performer = em.find(Performer.class, aliceCooper.getId());
        em.getTransaction().commit();
        assertNotNull(performer);

        // Deleting concert2 should now delete the Performer.
        em.getTransaction().begin();
        em.remove(concert2);
        em.getTransaction().commit();

        em.getTransaction().begin();
        performer = em.find(Performer.class, aliceCooper.getId());
        em.getTransaction().commit();
        assertNull(performer);

        em.close();
    }

    @Test
    public void testJackson() throws IOException {

        Performer aliceCooper = new Performer("Alice Cooper", "aliceCooper.jpg", Genre.Rock);
        Concert concert = new Concert("Spend the Night with Alice Cooper",
                LocalDateTime.of(2017, 10, 27, 19, 0), aliceCooper);

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(concert);

        Concert deserializedConcert = mapper.readValue(json, Concert.class);

        assertEquals("Spend the Night with Alice Cooper", deserializedConcert.getTitle());
        assertEquals(LocalDateTime.of(2017, 10, 27, 19, 0), deserializedConcert.getDate());

        Performer deserializedPerformer = deserializedConcert.getPerformer();

        assertEquals("Alice Cooper", deserializedPerformer.getName());
        assertEquals("aliceCooper.jpg", deserializedPerformer.getImageUri());
        assertEquals(Genre.Rock, deserializedPerformer.getGenre());

        assertNotSame(concert, deserializedConcert);
        assertNotSame(aliceCooper, deserializedPerformer);

    }

}
