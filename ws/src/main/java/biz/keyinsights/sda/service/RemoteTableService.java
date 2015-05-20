package biz.keyinsights.sda.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import biz.keyinsights.sda.model.Table;
import biz.keyinsights.sda.model.TablePreview;

public class RemoteTableService implements TableService {
	private static final Logger logger = LoggerFactory.getLogger(RemoteTableService.class);
	
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
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic ZGVtbzplc3NkZWVheQ==");
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return template.exchange(
				"http://" + host + ":" + port + "/sda-ws/tables", HttpMethod.GET, 
				entity, 
				List.class).getBody();
	}

	@Override
	public InputStream getTableData(String id, String username, char[] password) {		
		return template.execute(
				"http://" + host + ":" + port + "/sda-ws/table/{id}/data", HttpMethod.GET, 
				(request) -> {
					request.getHeaders().add("Authorization", "Basic ZGVtbzplc3NkZWVheQ==");
					request.getHeaders().add("Host", host);
					if ( username != null ) {
						logger.debug("Sending table-level username and password for username " + username);
						request.getHeaders().add("X-Table-Username", username);
						request.getHeaders().add("X-Table-Password", new String(password));
					} else {
						logger.debug("Not sending table-level username and password");
					}
				}, 
				(response) -> {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copy(response.getBody(), baos);
					return new ByteArrayInputStream(baos.toByteArray());
				},
				id);
		
	}

}
