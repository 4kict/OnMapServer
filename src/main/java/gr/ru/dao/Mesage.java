package gr.ru.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gr.ru.netty.protokol.Packs2Client.MsgToUser;

@Entity
public class Mesage extends MainEntity{
 
	private static final long serialVersionUID = 3685918131344654578L;
	
	@Column
	private Long localRowId;	// ID сообщения у клиента 
//	@Column
//	private Long serverUnicId;	// ID сообщения у В системе 	
	@Column (columnDefinition="TEXT")
	private String mesaga;		// Сообщение
//	@Column
//	private Integer status;		// Статус сообщения (НА сервере/ у получателя / прочитано)
//	@Column
//	private Boolean autorInformed;		// Флаг о том что юзер проинформирован
	@Column
	private Long time;			// тайм
	@Column
	private Short msgType;			// Тип сообщения (текст, фото)
	@Column
	private Long autorId;			// Автор
    @ManyToOne 
    @JoinColumn(name = "recip_id")
    private User userRecipient;		// Получатель
	
	



    


    

	public Long getLocalRowId() {
		return localRowId;
	}
	public void setLocalRowId(Long localRowId) {
		this.localRowId = localRowId;
	}
	public String getMesaga() {
		return mesaga;
	}
	public void setMesaga(String mesaga) {
		this.mesaga = mesaga;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}

	public User getUserRecipient() {
		return userRecipient;
	}
	public void setUserRecipient(User userRecipient) {
		this.userRecipient = userRecipient;
	}
	
 
	public Short getMsgType() {
		return msgType;
	}
	public void setMsgType(Short msgType) {
		this.msgType = msgType;
	}
	public Long getAutorId() {
		return autorId;
	}
	public void setAutorId(Long autorId) {
		this.autorId = autorId;
	}
	
	@Override
	public String toString() {
		return "Mesage [mesaga=" + mesaga + ", autorId=" + autorId + ", msgType=" + msgType + " recipId="+userRecipient.getId()+"]";
	}


	public MsgToUser fillNettyPack (MsgToUser msgToUser){
		msgToUser.from = this.autorId;
		msgToUser.msg = this.mesaga;
		msgToUser.msgtyp = this.msgType;
		msgToUser.time = this.time;
		msgToUser.to = this.userRecipient.getId();
		msgToUser.unicId = this.localRowId;
		return msgToUser;
	}
	

	
	
		
	
}
