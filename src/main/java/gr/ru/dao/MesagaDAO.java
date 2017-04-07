package gr.ru.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Этот класс ничего не знает о авторе, но жестко связан с получателем
 */
@Component
public class MesagaDAO {
    @Autowired
    private SessionFactory sessionFactory;

    public MesagaDAO() {
        // TODO Auto-generated constructor stub
    }

    @Transactional
    public void saveMesaga(Mesage msgORM, long to) {
        User recipient = (User) sessionFactory.getCurrentSession().get(User.class, to);
        if (recipient != null) {
            msgORM.setUserRecipient(recipient);
            saveMesaga(msgORM);
        }
    }


    @Transactional
    public void saveMesaga(Mesage msgORM) {
        msgORM.setTime(System.currentTimeMillis());
        sessionFactory.getCurrentSession().saveOrUpdate(msgORM);
    }

    @Transactional
    public String getMessageTxt(long msgRowID, long msgAutorID) {
        String hql = "from Mesage where localRowId=" + msgRowID + " and autorId=" + msgAutorID;
        Query query = sessionFactory.getCurrentSession().createQuery(hql);

        @SuppressWarnings("unchecked")
        List<Mesage> listMsg = (List<Mesage>) query.list();
        if (listMsg != null && !listMsg.isEmpty()) {
            return listMsg.get(0).getMesaga();
        }
        return null;
    }


//    @Transactional
//    public void delete(Mesage msgORM) {
//        sessionFactory.getCurrentSession().delete(msgORM);
//    }


//	@Transactional
//	public void saveMesaga111(MsgFromClient msg  ){
//		User recipient = (User) sessionFactory.getCurrentSession().get(User.class, msg.to);
//		if (recipient!=null){
//			Mesage mesage = new Mesage ();
//			mesage.setMesaga(msg.msg);   				
//			mesage.setAutorId( msg.from); 
//			mesage.setLocalRowId(msg.rowid);    
//			mesage.setUserRecipient(recipient);    	
//			mesage.setTime(System.currentTimeMillis());
//			mesage.setAutorInformed(true);
//			mesage.setStatus(gutil.MSG_ONSERVER);
//			sessionFactory.getCurrentSession().save(mesage);			
//		} 
//		else{
//			System.out.println("recipient is not avaliable" ); 
//		}
//		//            UserDAO userDAO = new UserDAO();
//		//            User user = userDAO.retrieveUser(username);
//		//            
//		//            CategoryDAO categoryDAO = new CategoryDAO();
//		//            Category category = categoryDAO.retrieveCategory( categoryTitle );
//		//
//		//            Article article = new Article(title, message, user);
//		//            getSession().save(article);
//		//            
//		//            category.addArticles(article);
//		//            getSession().save(category);
//		//            
//		//            commit();
//		//            return article;
//
//	}


    //	@Transactional
    //    public List<Mesage> getMsgToRecipient (User recipient){
    //
    //		String hql = "from User where hashkey=" + hashKey;
    //		Query query = sessionFactory.getCurrentSession().createQuery(hql);
    //
    //		@SuppressWarnings("unchecked")
    //		List<User> listUser = (List<User>) query.list();
    //
    //		if (listUser != null && !listUser.isEmpty()) {
    //			return listUser.get(0);
    //		}
    //
    //    	return null;
    //    }


//	@Transactional
//	public Mesage getMessagaByTelegramm(MsgFromClient mesag){
//		String hql = "from Mesage where localRowId="+mesag.rowid+" AND mesaga='"+mesag.msg+"' AND autorId="+mesag.from+" AND userRecipient="+mesag.to ;
//		Query query = sessionFactory.getCurrentSession().createQuery(hql);
//		
//		@SuppressWarnings("unchecked")
//		List<Mesage> listMsg = (List<Mesage>) query.list();
//
//		if (listMsg != null && !listMsg.isEmpty()) {
//			return listMsg.get(0);
//		}
//		
//		return null;
//	}
//

}
