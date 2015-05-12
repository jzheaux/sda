package biz.keyinsights.sda.model;

import java.util.ArrayList;
import java.util.List;

public class RegressionRequest {
	public static class RegressionTable {
		String id;
		String host;
		String port;
		String username;
		char[] password;
		
		List<Integer> predictors = new ArrayList<Integer>();
		List<Integer> dependents = new ArrayList<Integer>();
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
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
		public List<Integer> getPredictors() {
			return predictors;
		}
		private void setPredictors(List<Integer> predictors) {
			this.predictors = predictors;
		}
		public List<Integer> getDependents() {
			return dependents;
		}
		private void setDependents(List<Integer> dependents) {
			this.dependents = dependents;
		}
	}
	
	private List<RegressionTable> tables = new ArrayList<>();
	
	public List<RegressionTable> getTables() {
		return tables;
	}
	
	
}
