package gr.ru.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gr.ru.gutil;
import gr.ru.netty.protokol.Packs2Client.MsgToUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

@Entity
public class Mesage extends MainEntity{
	private static final Logger LOG = LogManager.getLogger(Mesage.class);
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

		msgToUser.msgtyp = this.msgType;
		msgToUser.time = this.time;
		msgToUser.to = this.userRecipient.getId();
		msgToUser.unicId = this.localRowId;
		// если это превьюха, надо взять ее из файла и положить в сообщение
		if (this.msgType == gutil.MSG_TYP_FOTO){
			msgToUser.msg = "";
			File file = new File( this.mesaga );      // В тексте сообщени содержится путь к файлу
			if (file.exists()) {
				// Читаем файл в массив байт
				int size = (int) file.length();
				byte[] bytes = new byte[size];
				try {
					BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
					buf.read(bytes, 0, bytes.length);
					buf.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					LOG.error("Preview FileNotFoundException " + this.mesaga + "\n" + e );
				} catch (IOException e) {
					e.printStackTrace();
					LOG.error("Preview IOException " + this.mesaga + "\n" + e  );
				}
				msgToUser.foto = bytes;

			}else {
				LOG.error("No preview in " + this.mesaga );
			}

		}else {
			msgToUser.msg = this.mesaga;
		}

		return msgToUser;
	}
	

	
	
		
	
}
