package com.heroku.devcenter.db;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heroku.devcenter.liveticker.Event;

public class EventService {

	private EmFactory emFactory = new EmFactory();
	private EntityManager em = emFactory.produceEntityManager();

	final static Logger logger = LoggerFactory.getLogger(EventService.class);

	public Set<Event> saveAndReturnNewEvents(Set<Event> someEvents) {

		if (logger.isDebugEnabled()) {
			logger.debug("recive " + someEvents.size() + " Events");
		}

		Set<Event> response = new HashSet<Event>();

		for (Event event : someEvents) {
			EntityTransaction transaction = null;
			try {
				transaction = em.getTransaction();
				transaction.begin();

				Event find = em.find(Event.class, event.getId());

				if (find == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("save Event : " + event.getId());
					}
					em.persist(event);
					response.add(event);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Event : " + event.getId() + " already passed");
					}
				}

				transaction.commit();
			} catch (Exception e) {

				logger.error("Error : " + e.getMessage());

				if (transaction != null && transaction.isActive()) {
					transaction.rollback();
				}
				em.close();
				em = emFactory.produceEntityManager();
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(response.size() + " new Events");
		}
		return response;
	}

}
