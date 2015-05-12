package biz.keyinsights.sda.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	RegressionResponse performRegression(RegressionRequest rr) throws IOException {
		RegressionResponse response = new RegressionResponse();
		List<InputStream> dataSources = new ArrayList<InputStream>();
		
		for ( RegressionTable t : rr.getTables() ) {
			TableService service = t.getHost() == null ? tableService : 
				new RemoteTableService(t.getHost(), t.getPort());
			
			try {
				dataSources.add(service.getTableData(t.getId(), t.getUsername(), t.getPassword()));
			} catch ( Exception e ) {
				response.addError(t.getId(), e.getMessage());
			}
		}
		
		//TODO: Tuan/Prasanta: The above input streams will contain the csv data that I would suppose at
		// this point you would use to perform your regression. It is one input stream for each table of data.
		
		return response;
	}
}
