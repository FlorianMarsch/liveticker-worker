package de.florianmarsch.liveticker.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.florianmarsch.liveticker.gameday.Gameday;
import de.florianmarsch.liveticker.liveticker.Event;

public class EventService {

	private EmFactory emFactory = new EmFactory();
	private EntityManager em = emFactory.produceEntityManager();

	final static Logger logger = LoggerFactory.getLogger(EventService.class);

	public Set<Event> saveAndReturnNewEvents(Set<Event> someEvents) {

		if (logger.isDebugEnabled()) {
			logger.debug("recive " + someEvents.size() + " Events");
		}

		Set<Event> response = new HashSet<Event>();
		if(someEvents.isEmpty()){
			return response;
		}
		
		

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

	public Set<Event> deleteAndReturnLostEvents(Set<Event> someEvents,Gameday currentGameDay) {
		
		
		Set<Event> response = new HashSet<Event>();
		
		if(someEvents.isEmpty()){
			return response;
		}
		
		
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();
			Set<String> someIds = new HashSet<String>();
			for (Event event : someEvents) {
				someIds.add(event.getId());
			}
			
			TypedQuery<Event> query = em.createQuery("select x from Event x where x.gameday = :gameday  and not x.id in :list", Event.class);
			query.setParameter("gameday", currentGameDay.getNumber());
			query.setParameter("list", someIds);
			
			List<Event> lost = query.getResultList();
			for (Event event : lost) {
				event.setType("error");
				em.remove(event);
				response.add(event);
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
		return response;
	}

}
