package gr.ru.dao;

import gr.ru.gutil;
import io.netty.channel.Channel;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static gr.ru.gutil.MSG_ONSERVER;


@Entity
public class User extends MainEntity {

    private static final long serialVersionUID = 5942047551885659834L;
    @Transient
    private Channel mapChanel;
    @Transient
    private boolean cluster = false;
    @Transient
    private Long lastPresist = 0L;            // время сохранения данных в МУСКЛ
    @Transient
    public boolean bot = false;


    @Column
    private Long lastGeoDecode = 0L;            // время обновления ГЕКОДЕРА (и сохранения в МУСКЛ)
    @Column
    private Integer status = gutil.STATUS_OFFLINE;
    @Column(unique = true)
    private Long hashkey;        // Идентификатор юзера (отпечаток уникальных данных устройства юзера, известен только серверу)
    @Column
    private String name;        // логин
    @Column
    private String country;        // Страна
    @Column
    private String city;        // Город
    @Column
    private Integer lat;        // Координаты
    @Column
    private Integer lon;
    @Column
    private Short qad;       // Номер таблицы (Квадрат)
    @Column
    private Byte icon = 0;
    @OneToMany(mappedBy = "userRecipient", cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    @Where(clause = "status = " + MSG_ONSERVER)
    private Set<Mesage> unRecivedMsg = new HashSet<Mesage>();
    @OneToMany(mappedBy = "userRecipient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Notific> unRecivedNotif = new HashSet<Notific>();

    public boolean isCluster() {
        return cluster;
    }

    public byte getIcon() {
        return icon;
    }

    public void setIcon(byte icon) {
        this.icon = icon;
    }

    public Channel getMapChanel() {
        return mapChanel;
    }

    public void setChanel(Channel chanel) {
        this.mapChanel = chanel;
    }

    public Long getHashkey() {
        return hashkey;
    }

    public void setHashkey(Long hashkey) {
        this.hashkey = hashkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public Integer getLon() {
        return lon;
    }

    public void setLon(Integer lon) {
        this.lon = lon;
    }


    public Integer getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }


    public Short getQad() {
        return qad;
    }

    public void setQad(Short qad) {
        this.qad = qad;
    }

    public Set<Notific> getUnRecivedNotif() {
        return unRecivedNotif;
    }

    public void setUnRecivedNotif(Set<Notific> unRecivedNotif) {
        this.unRecivedNotif = unRecivedNotif;
    }

    public Set<Mesage> getUnRecivedMsg() {
        return unRecivedMsg;
    }

    public void removeMesaga(Mesage msg) {
        if (msg != null) {
            this.unRecivedMsg.remove(msg);
        }
    }

    public void removeNotif(Notific notif) {
        if (notif != null) {
            notif.setUserRecipient(null);
            this.unRecivedNotif.remove(notif);
        }
    }

    public Mesage getMesagaById(long msgAutorID, long msgRowID) {
        for (Mesage mesage : this.unRecivedMsg) {
            if (mesage.getAutorId() == msgAutorID && mesage.getLocalRowId() == msgRowID) {
                return mesage;
            }
        }
        return null;
    }


    public void removeNotificById(long notifRowID, long notifStatus) {
        Iterator<Notific> iterator = this.unRecivedNotif.iterator();
        while (iterator.hasNext()) {        // Перебираем все недополоученные
            Notific notifORM = iterator.next();
            if (notifORM.getLocalRowId() == notifRowID && notifORM.getStatus() == notifStatus) {
                iterator.remove();            // Удаляем
            }
        }
    }


    public Long getLastPresist() {
        return lastPresist;
    }


    public void setLastPresist(Long lastPresist) {
        this.lastPresist = lastPresist;
    }


    public Long getLastGeoDecode() {
        return lastGeoDecode;
    }


    public void setLastGeoDecode(Long lastGeoDecode) {
        this.lastGeoDecode = lastGeoDecode;
    }


    //	@Override
    //	public String toString() {
    //		return "User [mapConnect=" + mapConnect + ", cluster=" + cluster + ", lastPresist=" + lastPresist
    //				+ ", lastGeoDecode=" + lastGeoDecode + ", status=" + status + ", hashkey=" + hashkey + ", name=" + name
    //				+ ", country=" + country + ", city=" + city + ", lat=" + lat + ", lon=" + lon + ", qad=" + qad
    //				+ ", icon=" + icon + ", unRecivedMsg=" + unRecivedMsg + ", unRecivedNotif=" + unRecivedNotif + "]";
    //	}


    @Override
    public String toString() {
        return "User [mapConnect=" + mapChanel + " cluster=" + cluster + ", lastPresist=" + lastPresist
                + ", lastGeoDecode=" + lastGeoDecode + ", status=" + status + ", hashkey=" + hashkey + ", name=" + name
                + ", country=" + country + ", city=" + city + ", lat=" + lat + ", lon=" + lon + ", qad=" + qad
                + ", icon=" + icon + " getId()=" + getId() + ", getObjVersion()=" + getObjVersion() + "]";
    }


}














