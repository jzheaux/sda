package biz.keyinsights.sda.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import biz.keyinsights.sda.model.AuthenticationException;
import biz.keyinsights.sda.model.RegressionRequest;
import biz.keyinsights.sda.model.RegressionRequest.RegressionTable;
import biz.keyinsights.sda.model.RegressionResponse;
import biz.keyinsights.sda.service.RemoteTableService;
import biz.keyinsights.sda.service.TableService;

@Controller
public class RegressionController {
	@Inject TableService tableService;
	
	@RequestMapping(value="/regression", method = RequestMethod.POST)
	public @ResponseBody
	RegressionResponse performRegression(@RequestBody RegressionRequest rr) throws IOException {
		RegressionResponse response = new RegressionResponse();
		List<InputStream> dataSources = new ArrayList<InputStream>();
		
		boolean hasError = false;
		for ( RegressionTable t : rr.getTables() ) {
			TableService service = StringUtils.isEmpty(t.getHost()) ? tableService : 
				new RemoteTableService(t.getHost(), t.getPort());
			
			try {
				dataSources.add(service.getTableData(t.getId(), t.getUsername(), t.getPassword()));
			} catch ( AuthenticationException e ) {
				response.addAuthRequest(t);
				hasError = true;
			} catch ( HttpClientErrorException e ) {
				if ( e.getStatusCode() == HttpStatus.UNAUTHORIZED ) {
					response.addAuthRequest(t);
				} else {
					response.addError(e.getMessage());
				}
				hasError = true;
			} catch ( Exception e ) {
				response.addError(e.getMessage());
				hasError = true;
			}
		}
		
		//TODO: Tuan/Prasanta: The above input streams will contain the csv data that I would suppose at
		// this point you would use to perform your regression. It is one input stream for each table of data.
		if ( !hasError ) {
			response.setId("1"); // dummy value to demonstrate UI
		}
		
		return response;
	}
	
	@RequestMapping(value="/regression/{id}", method = RequestMethod.GET)
	public String lookupRegression(@PathVariable("id") String id) {
		return "/result";
	}
}
