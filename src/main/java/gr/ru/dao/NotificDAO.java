package gr.ru.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class NotificDAO {
    @Autowired
    private SessionFactory sessionFactory;

    public NotificDAO() {
        // TODO Auto-generated constructor stub
    }

    @Transactional
    public void saveNotific(Notific notific) {
        sessionFactory.getCurrentSession().saveOrUpdate(notific);
    }

    // Сохраняем оповещение предварительно найдя Автора сообщения (Автор сообщения - он же получатель оповещения)
    // FixMe Что бы сохранить опопвещение со всеми необходимыми данными делается лишный запрос в БД???
    @Transactional
    public void saveNotific(Notific notifORM, Long autorId) {
        User recipient = (User) sessionFactory.getCurrentSession().get(User.class, autorId);
        if (recipient != null) {
            notifORM.setUserRecipient(recipient);
            saveNotific(notifORM);
        }
    }

    public void delete(Notific notifORM) {
        sessionFactory.getCurrentSession().delete(notifORM);
    }


}
