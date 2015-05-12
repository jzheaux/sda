package biz.keyinsights.sda.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
public class Table {
	private String id;
	
	private String tableName;
	
	private List<Column> columns = new ArrayList<>();
	
	private String username;
	
	private char[] password;
	
	private Integer accessLimit;
	
	private Integer joinLimit;
	
	private Long updatedInMillis;
	
	private Long sizeInBytes;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
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

	public Integer getAccessLimit() {
		return accessLimit;
	}

	public void setAccessLimit(Integer accessLimit) {
		this.accessLimit = accessLimit;
	}

	public Integer getJoinLimit() {
		return joinLimit;
	}

	public void setJoinLimit(Integer joinLimit) {
		this.joinLimit = joinLimit;
	}

	@JsonIgnore
	public Long getLastModifiedInDays() {
		long ago = System.currentTimeMillis() - updatedInMillis;
		return ago / ( 24 * 60 * 60 * 1000 );
	}
	
	public Long getUpdatedInMillis() {
		return updatedInMillis;
	}

	public void setUpdatedInMillis(Long updatedInMillis) {
		this.updatedInMillis = updatedInMillis;
	}

	public Long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(Long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	
	@JsonIgnore
	public boolean isPasswordProtected() {
		return password != null && password.length > 0;
	}

	@JsonIgnore
	public boolean authorizes(String username, char[] password) {
		if ( this.username == null ) {
			return true;
		} else if ( this.password == null && this.username.equals(username) ) {
			return true;
		}
		
		return this.username.equals(username) && Arrays.equals(this.password, password);
	}
}
