package gr.ru.dao;

import gr.ru.gutil;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//@Repository("UserDAO")
public class UserDAO  {

	//@Autowired
	private SessionFactory sessionFactory;
	
	

	public UserDAO() {
		// TODO Auto-generated constructor stub
	}

	public UserDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


//	@Transactional
//	public List<User> list() {
//		@SuppressWarnings("unchecked")
//		List<User> listUser = (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class)
//				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
//
//		return listUser;
//
//	}


	@Transactional
	public User getUser(Long id) {
		User user = (User) sessionFactory.getCurrentSession().get(User.class, id);
		return user;
	}
	
	
//	@Transactional
//	public User getByHash(Long hashkey) {
//		String hql = "from User where hashkey=" + hashkey;
//		Query query = sessionFactory.getCurrentSession().createQuery(hql);
//
//		@SuppressWarnings("unchecked")
//		List<User> listUser = (List<User>) query.list();
//
//		if (listUser != null && !listUser.isEmpty()) {
//			return listUser.get(0);
//		}
//		return null;
//	}

	
//	@Transactional
//	public User regUser(RegData regData) {
//		Long hashKey = (long) new String(""+regData.an_id+""+regData.dev_id+""+regData.key_id+""+regData.name ).hashCode();	
//		String hql = "from User where hashkey=" + hashKey;
//		Query query = sessionFactory.getCurrentSession().createQuery(hql);
//
//		@SuppressWarnings("unchecked")
//		List<User> listUser = (List<User>) query.list();
//
//		if (listUser != null && !listUser.isEmpty()) {
//			return listUser.get(0);
//		}
//		else{
//			User user = new User();
//			user.setHashkey(hashKey);
//			user.setName(regData.name);
//			user.setTime(System.currentTimeMillis());
//			sessionFactory.getCurrentSession().save(user);
//			return user;
//		}
//	}
	
	@Transactional
	public User getUserByHashKey(Long hashKey){
		String hql = "from User where hashkey=" + hashKey;
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<User> listUser = (List<User>) query.list();

		if (listUser != null && !listUser.isEmpty()) {
			return listUser.get(0);
		}
		
		return null;
	}
	
	

//	public User saveOrGet(User user) {
//		String hql = "from User where hashkey=" + user;
//		Query query = sessionFactory.getCurrentSession().createQuery(hql);
//
//		@SuppressWarnings("unchecked")
//		List<User> listUser = (List<User>) query.list();
//
//		if (listUser != null && !listUser.isEmpty()) {
//			return listUser.get(0);
//		}
//		return null;
//	}

	@Transactional
	public void saveOrUpdate(User user) {
		user.setLastPresist(System.currentTimeMillis());		
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}

//	@Transactional
//	public void update(User user) {
//		user.setTime(System.currentTimeMillis());
//		sessionFactory.getCurrentSession().update(user); 
//	}
	
//	// ГеоДекодер - отдельным потоком пытается обновить данные юзера 
//	@Transactional
//	public void merge(User user) {
//		System.out.println( "merge= "+ sessionFactory.getCurrentSession().merge(user) );
//		//sessionFactory.getCurrentSession().merge(user); 
//	}

	// т.к. связанные с юзером сообщения могут измениться, пока Геокодер определяет локацию, 
//	public void updateProperty(User user_source){
//		User user_target = this.getUser(user_source.getId());	// Извлекаем из БД юзера 
//		BeanUtils.copyProperties(user_source, user_target);		// изменяем его свойства
//	}

	@Transactional
	public void delete(User userToDelete) {
		sessionFactory.getCurrentSession().delete(userToDelete);
	}
	
//	@Transactional
//	public void delete(Long id) {
//		User userToDelet = (User)sessionFactory.getCurrentSession().get(User.class, id);
//		if (userToDelet!=null){
//			sessionFactory.getCurrentSession().delete(userToDelet);
//		}
//	}
	
	@Transactional
	public int setAllOffline(){
		String hql = "from User where  status <> " + gutil.STATUS_OFFLINE;
		Query query = sessionFactory.getCurrentSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<User> listUser = (List<User>) query.list();

		for(User user : listUser){
			user.setStatus(gutil.STATUS_OFFLINE);
			this.saveOrUpdate(user);
		}
		return listUser.size();

	}
	
	
	
	
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}





}
