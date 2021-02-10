package lab.end2end.concert.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton class that manages an EntityManagerFactory. When a
 * PersistenceManager is instantiated, it creates an EntityManagerFactory. An
 * EntityManagerFactory is required to create an EntityManager, which represents
 * a persistence context (session with a database). 
 * 
 * When a Web service application component (e.g. a resource object) requires a 
 * persistence context, it should call the PersistentManager's 
 * createEntityManager() method to acquire one.
 * 
 */
public class PersistenceManager {
	private static PersistenceManager instance = null;
	
	private final EntityManagerFactory entityManagerFactory;
	
	protected PersistenceManager() {
		entityManagerFactory = Persistence.createEntityManagerFactory("lab.end2end.concert");
	}
	
	public EntityManager createEntityManager() {
		return entityManagerFactory.createEntityManager();
	}
	
	public static PersistenceManager instance() {
		if(instance == null) {
			instance = new PersistenceManager();
		}
		return instance;
	}

}
