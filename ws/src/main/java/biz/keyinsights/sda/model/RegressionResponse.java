package biz.keyinsights.sda.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegressionResponse {
	private String id; // identify the regression results
	
	private Map<String, List<String>> errorsByTable = new HashMap<>();
	
	public void addError(String tableId, String error) {
		List<String> errors = errorsByTable.get(tableId);
		if ( errors == null ) {
			errors = new ArrayList<>();
			errorsByTable.put(tableId, errors);
		}
		errors.add(error);
	}
}
