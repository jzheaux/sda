package biz.keyinsights.sda.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import biz.keyinsights.sda.model.Column;
import biz.keyinsights.sda.model.ServerConfiguration;
import biz.keyinsights.sda.model.Table;
import biz.keyinsights.sda.model.TableException;
import biz.keyinsights.sda.model.TablePreview;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonBasedTableService implements TableService {
	private static final Logger logger = LoggerFactory.getLogger(JsonBasedTableService.class);
	
	private static final String DATA_LOCATION = "/home/jzheaux/dev/git/keyinsights/configuration";
	
	@Inject ConfigurationService configurationService;
	@Inject ObjectMapper objectMapper;
	
	@Override
	public Table getTable(String id) {
		try {
			File table = new File(DATA_LOCATION, id + ".json");
			return objectMapper.readValue(table, Table.class);
		} catch ( IOException e ) {
			//TODO: Give more information here, including using a logger
			throw new TableException("Couldn't retrieve table", e);
		}
	}
	
	@Override
	public TablePreview getTablePreview(String id) {
		ServerConfiguration sc = configurationService.getConfiguration(ServerConfiguration.class);
		Table table = getTable(id);
		File data = new File(sc.getDataDirectory(), table.getTableName() + ".csv");
		try ( BufferedReader br = new BufferedReader(new FileReader(data)) ) {
			TablePreview tp = new TablePreview();
			Queue<Column> columns = new LinkedList<>(table.getColumns());
			Arrays.asList(nullSafeReadline(br).split(",")).forEach((column) -> tp.addHeader(columns.poll()));
			Arrays.asList(nullSafeReadline(br).split(",")).forEach((column) -> tp.addRowOne(column));
			Arrays.asList(nullSafeReadline(br).split(",")).forEach((column) -> tp.addRowTwo(column));
			return tp;
		} catch ( IOException e ) {
			throw new TableException("Couldn't retrieve table", e);
		}
	}

	private String nullSafeReadline(BufferedReader br) throws IOException {
		String line = br.readLine();
		return line == null ? "" : line;
	}
	
	@Override
	public void addTable(Table t, InputStream csv) {
		try {
			uploadData(t, csv);
			
			// use reflection here because we don't want others to be able to specify an id
			Field id = t.getClass().getDeclaredField("id");
			id.setAccessible(true);
			id.set(t, UUID.randomUUID().toString());
			t.setUpdatedInMillis(System.currentTimeMillis());
			
			// write the configuration to a json file
			File table = new File(DATA_LOCATION, t.getId() + ".json");
			table.createNewFile();
			objectMapper.writeValue(table, t);
		} catch ( IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
			throw new TableException("Couldn't add table", e);
		}
	}

	@Override
	public void updateTable(Table t, InputStream csv) {
		try {
			Table existing = getTable(t.getId());
						
			if ( csv != null ) {
				uploadData(existing, csv);	
			} else {
				existing.setColumns(t.getColumns());
			}
			
			existing.setTableName(t.getTableName());
			existing.setAccessLimit(t.getAccessLimit());
			existing.setJoinLimit(t.getJoinLimit());
			existing.setPassword(t.getPassword());
			existing.setUsername(t.getUsername());
			existing.setUpdatedInMillis(System.currentTimeMillis());
			
			// write the configuration to a json file
			File table = new File(DATA_LOCATION, existing.getId() + ".json");
			objectMapper.writeValue(table, existing);
		} catch ( IOException e ) {
			throw new TableException("Couldn't update table", e);
		}
	}

	private void uploadData(Table table, InputStream csv) throws IOException {
		ServerConfiguration sc = configurationService.getConfiguration(ServerConfiguration.class);
		File data = new File(sc.getDataDirectory(), table.getTableName() + ".csv");
		CountingInputStream cis = new CountingInputStream(csv);
		IOUtils.copy(cis, new FileOutputStream(data));
		table.setSizeInBytes((long)cis.getCount());
		
		data = new File(sc.getDataDirectory(), data.getName());
		data.createNewFile();
		FileInputStream fis = new FileInputStream(data);
		try ( BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ) {
			List<Column> columns = Arrays.asList(br.readLine().split(",")).stream().map((name) -> new Column(name)).collect(Collectors.toList());
			table.setColumns(columns);
		}
	}
	
	@Override
	public void deleteTable(String id) {
		ServerConfiguration sc = configurationService.getConfiguration(ServerConfiguration.class);
		Table t = getTable(id);
		File data = new File(sc.getDataDirectory(), t.getTableName() + ".csv");
		File table = new File(DATA_LOCATION, id + ".json");
		
		//FIXME This should probably be an atomic operation so that we don't get a situation where the table exists, but the data doesn't
		//Java probably has an "open-for-delete" locking mechanism, just need to look it up
		if ( !data.delete() || !table.delete() ) {
			throw new TableException("Could not delete underlying data, perhaps a regression is currently in progress");
		}
	}
	
	@Override
	public List<Table> findAllTables() {
		File data = new File(DATA_LOCATION);
		File[] tables = data.listFiles((dir, name) -> name.endsWith("json"));
		List<Table> results = new ArrayList<>();
		for ( File table : tables ) {
			try {
				results.add(objectMapper.readValue(table, Table.class));
			} catch ( IOException e ) {
				logger.error("Couldn't load table: " + table.getName());
			}
		}
		return results;
	}
}