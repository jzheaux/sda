package biz.keyinsights.sda.model;

import java.util.ArrayList;
import java.util.List;

import biz.keyinsights.sda.model.RegressionRequest.RegressionTable;

public class RegressionResponse {
	private String id; // identify the regression results
	
	private List<String> log = new ArrayList<>();
	
	private List<RegressionTable> authRequests = new ArrayList<>();
	private List<String> otherErrors = new ArrayList<>();
	
	public List<String> getLog() {
		return log;
	}
	
	public void log(String message) {
		log.add(message);
	}
	
	public void addError(String error) {
		otherErrors.add(error);
	}
	
	public List<String> getOtherErrors() {
		return otherErrors;
	}
	
	public void addAuthRequest(RegressionTable table) {
		authRequests.add(table);
	}
	
	public List<RegressionTable> getAuthRequests() {
		return authRequests;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
