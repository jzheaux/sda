package biz.keyinsights.sda.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import biz.keyinsights.sda.model.Table;
import biz.keyinsights.sda.model.TablePreview;

public class RemoteTableService implements TableService {
	private String host;
	private String port;
	
	private RestTemplate template = new RestTemplate();
	
	public RemoteTableService(String host, String port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public Table getTable(String id) {
		return template.getForObject("http://" + host + ":" + port + "/sda-ws/table/{id}", Table.class, id);
	}

	@Override
	public TablePreview getTablePreview(String id) {
		return template.getForObject("http://" + host + ":" + port + "/sda-ws/table/{id}/preview", TablePreview.class, id);
	}

	@Override
	public void addTable(Table t, InputStream csv) {
		throw new UnsupportedOperationException("Adding a table remotely not yet supported.");
	}

	@Override
	public void updateTable(Table t, InputStream csv) {
		throw new UnsupportedOperationException("Updating a table remotely not yet supported.");
	}

	@Override
	public void deleteTable(String id) {
		throw new UnsupportedOperationException("Adding a table remotely not yet supported.");
	}

	@Override
	public List<Table> findAllTables() {
		throw new UnsupportedOperationException("Retreiving all tables remotely not yet supported.");
	}

	@Override
	public InputStream getTableData(String id, String username, char[] password) {		
		return template.execute(
				"http://" + host + ":" + port + "/sda-ws/table/{id}/data", HttpMethod.GET, 
				(request) -> {
					request.getHeaders().add("X-Table-Username", username);
					request.getHeaders().add("X-Table-Password", new String(password));
				}, 
				(response) -> response.getBody());
	}

}
