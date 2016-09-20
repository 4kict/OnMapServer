package gr.ru.dao;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class NotificDAO {
	private SessionFactory sessionFactory;
	
	public NotificDAO() {
		// TODO Auto-generated constructor stub
	}
	public NotificDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	



	
	@Transactional
	public void saveNotific(Notific notific) {
		sessionFactory.getCurrentSession().saveOrUpdate(notific);
	}
	
	// Сохраняем оповещение предварительно найдя Автора сообщения (Автор сообщения - он же получатель оповещения)
	@Transactional
	public void saveNotific(Notific notifORM, Long autorId) {
		User recipient = (User) sessionFactory.getCurrentSession().get(User.class, autorId);
		if (recipient!=null){
			notifORM.setUserRecipient(recipient);
			saveNotific(notifORM);
		}
		
	}
	
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public void delete(Notific notifORM) {
		sessionFactory.getCurrentSession().delete(notifORM);
		
	}

	
	
	 
	
	
	
	
}
