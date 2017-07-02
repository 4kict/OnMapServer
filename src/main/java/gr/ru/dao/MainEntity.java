package gr.ru.dao;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;


/*
 * ������� ����� ����������� ����� ���������� ��
 * ��������� ������������� ID
 * � ������������ ��� ������������� ����������
 */

@MappedSuperclass
public class MainEntity implements Serializable {

    private static final long NULL_LONG = 0L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Expose
    protected Long id = NULL_LONG;

    @Version
    @Column(name = "objVersion")
    private Long objVersion = NULL_LONG;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(Long objVersion) {
        this.objVersion = objVersion;
    }


}
