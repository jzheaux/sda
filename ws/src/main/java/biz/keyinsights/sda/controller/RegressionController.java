package biz.keyinsights.sda.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import regression.Regression;
import session.client.UserInterface;
import biz.keyinsights.regression.model.DoubleMatrixBuilder;
import biz.keyinsights.sda.model.AuthenticationException;
import biz.keyinsights.sda.model.RegressionRequest;
import biz.keyinsights.sda.model.RegressionRequest.RegressionColumn;
import biz.keyinsights.sda.model.RegressionRequest.RegressionTable;
import biz.keyinsights.sda.model.RegressionResponse;
import biz.keyinsights.sda.service.RemoteTableService;
import biz.keyinsights.sda.service.TableService;

@Controller
public class RegressionController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegressionController.class);
	
	@Inject TableService tableService;
	@Inject UserInterface userInterface;
	
	@RequestMapping(value="/regression", method = RequestMethod.POST)
	public @ResponseBody
	RegressionResponse performRegression(@RequestBody RegressionRequest rr, HttpServletRequest request) throws IOException {
		RegressionResponse response = new RegressionResponse();
		DoubleMatrixBuilder predictor = new DoubleMatrixBuilder();
		DoubleMatrixBuilder dependent = new DoubleMatrixBuilder();
		
		Runnable creatorRegression = () -> {
			userInterface.setCurrentUser(request.getServerName(), request.getServerPort(), false);
			userInterface.startRegression();
		};
		
		List<Runnable> joinerRegressions = new ArrayList<Runnable>();
		
		boolean hasError = false;
		for ( RegressionTable t : rr.getTables() ) {
			TableService service = StringUtils.isEmpty(t.getHost()) ? tableService : 
				new RemoteTableService(t.getHost(), t.getPort());
			
			try {
				InputStream data = service.getTableData(t.getId(), t.getUsername(), t.getPassword());
				try ( Scanner scanner = new Scanner(data) ) {
					/*String columnHeader =*/ scanner.nextLine();
					int whichRow = 0;
					while ( scanner.hasNextLine() ) {
						String[] row = scanner.nextLine().split(",");
						List<RegressionColumn> columns = t.getPredictors();
						Double[] toAdd = columns.stream()
												.map(c -> Double.parseDouble(row[c.getId()]))
												.collect(Collectors.toList())
												.toArray(new Double[columns.size()]);
						predictor.addColumns(whichRow, toAdd);
						
						columns = t.getDependents();
						toAdd = columns.stream()
								.map(c -> Double.parseDouble(row[c.getId()]))
								.collect(Collectors.toList())
								.toArray(new Double[columns.size()]);
						dependent.addColumns(whichRow, toAdd);
						
						whichRow++;
					}
				}
				
				if ( !StringUtils.isEmpty(t.getHost()) ) {
					userInterface.addDataSourceUser(t.getHost(), Integer.parseInt(t.getPort()), false);
					joinerRegressions.add(() -> {
						userInterface.setCurrentUser(t.getHost(), Integer.parseInt(t.getPort()), false);;
						userInterface.startRegression();
					});
				}
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
			try {
				userInterface.addCreator(request.getServerName(), request.getServerPort(), true);
				userInterface.setCurrentUser(request.getServerName(), request.getServerPort(), true);
				userInterface.setDesignMatrix(predictor.toMatrix());
				userInterface.setResponseMatrix(dependent.toMatrix());
				userInterface.setRegressionType("linearRegression");
				
				ExecutorService service = Executors.newFixedThreadPool(joinerRegressions.size() + 1);
				Future<?> creator = service.submit(creatorRegression);
				
				joinerRegressions.forEach(r -> service.submit(r));
				
				// the regression code appears to be single-threaded; to keep progress from blocking, I
				// launch a creator and joiner regression, one for the local tables and one for the remote.
				
				// I believe there are better designs out there that still enable you to have the non-blocking architecture you are
				// going for, namely Promise architectures are super nice. If you are really wanting to do the progressive messaging
				// in the browser, then we could have it listen via a WebSocket. Further, since this is just a message, it isn't strongly typed
				// and there is no way for me to predictably extract the data into a graph or something similar.
				
				try {
					creator.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while ( !Regression.clientMessage.isEmpty() ) {
					response.log(Regression.clientMessage.poll());
		        }
				response.setId(UUID.randomUUID().toString()); // dummy value to demonstrate UI
			} finally {
				userInterface.removeDataSourceUser(request.getServerName(), request.getServerPort());
				rr.getTables().stream().forEach((t) -> {
					if ( !StringUtils.isEmpty(t.getHost()) ) {
						userInterface.removeDataSourceUser(t.getHost(), Integer.parseInt(t.getPort()));
					}
				});
			}
		}
		
		return response;
	}
	
	@RequestMapping(value="/regression/{id}", method = RequestMethod.GET)
	public String lookupRegression(@PathVariable("id") String id) {
		return "/result";
	}
}
