package de.florianmarsch.liveticker.gameday;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.florianmarsch.liveticker.db.EmFactory;
import de.florianmarsch.liveticker.slack.SlackConnection;

public class SwitchService {

	
	private EmFactory emFactory = new EmFactory();
	private EntityManager em = emFactory.produceEntityManager();

	final static Logger logger = LoggerFactory.getLogger(SwitchService.class);

	public Integer getLastGameDay(Gameday aGameDay){
		Integer response = null;
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();

			LastGameDay find = em.find(LastGameDay.class, "LastGameDay");
			if(find != null){
				response = find.getNumber();
			}else{
				response = aGameDay.getGameday();
				find = new LastGameDay();
				find.setId("LastGameDay");
			}
			find.setNumber(aGameDay.getGameday());
			em.persist(find);
			
			transaction.commit();
		} catch (Exception e) {
			new SlackConnection().handleException(e);
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
