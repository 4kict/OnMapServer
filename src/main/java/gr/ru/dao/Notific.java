package gr.ru.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gr.ru.netty.protokol.Packs2Client.MsgToUser;
import gr.ru.netty.protokol.Packs2Client.ServerStat;

@Entity
public class Notific extends MainEntity{
	

	private static final long serialVersionUID = 2379061113225673667L;
	@ManyToOne 
    @JoinColumn(name = "recip_id")
    private User userRecipient;		// Получатель
    @Column
    private Long localRowId;
    @Column
    private Integer status;
	@Column
	private Long time;			// тайм
    
	
	public Notific() {
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


	public Long getLocalRowId() {
		return localRowId;
	}


	public void setLocalRowId(Long localRowId) {
		this.localRowId = localRowId;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public ServerStat fillNettyPack (ServerStat serverStat){
		serverStat.typ = this.getStatus();
		serverStat.sts = this.getTime();							
		serverStat.dat = this.getLocalRowId();
		return serverStat;
	}
	

//	@Override
//	public String toString() {
//		return "Notific [userRecipient=" + userRecipient + ", localRowId=" + localRowId + ", status=" + status
//				+ ", time=" + time + "]";
//	}
	
	
	
	
	

}
