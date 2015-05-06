package biz.keyinsights.sda.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class User {
	public enum Type {
		DATA_PROVIDER, ANALYST
	}
	
	@Id
	@Column
	@SequenceGenerator(name="seq", sequenceName="seq")
	@GeneratedValue(generator="seq", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column
	private String username;
	
	@Column
	private char[] password;
	
	@Column
	@Enumerated
	private Type type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
