package gr.ru.dao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/*
 * ������� ����� ����������� ����� ���������� ��
 * ��������� ������������� ID
 * � ������������ ��� ������������� ����������
 */

@MappedSuperclass
public class MainEntity implements Serializable  {

	private static final long NULL_LONG = 0L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	@NotNull
	protected Long id = NULL_LONG;

	@Version
	@NotNull
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
