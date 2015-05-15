package biz.keyinsights.sda.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegressionRequest {
	public static class RegressionColumn  {
		Integer id;
		
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
	}
	
	public static class RegressionTable { 
		String id;
		String tableName;
		String host;
		String port;
		String username;
		char[] password;
		
		List<RegressionColumn> predictors = new ArrayList<RegressionColumn>();
		List<RegressionColumn> dependents = new ArrayList<RegressionColumn>();
		
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
		public String getHost() {
			return host; 
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getPort() {
			return port; 
		}
		public void setPort(String port) {
			this.port = port; 
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
		public List<RegressionColumn> getPredictors() {
			return predictors;
		}
		public void setPredictors(List<RegressionColumn> predictors) {
			this.predictors = predictors;
		}
		public List<RegressionColumn> getDependents() {
			return dependents;
		}
		public void setDependents(List<RegressionColumn> dependents) {
			this.dependents = dependents;
		}
	}
	
	private List<RegressionTable> tables = new ArrayList<>();
	
	public List<RegressionTable> getTables() {
		return tables;
	}
	
	public void setTables(List<RegressionTable> tables) {
		this.tables = tables;
	}
}
