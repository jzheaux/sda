package biz.keyinsights.sda.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import biz.keyinsights.sda.model.Configuration;
import biz.keyinsights.sda.model.ConfigurationException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonBasedConfigurationService implements ConfigurationService {
	private static final String DATA_LOCATION = "/home/jzheaux/dev/git/keyinsights/configuration";
	
	@Inject ObjectMapper objectMapper;
	
	@Override
	public <T extends Configuration> T getConfiguration(Class<T> clazz) {
		File file = new File(DATA_LOCATION, clazz.getSimpleName() + ".json");
		
		try ( InputStream is = new FileInputStream(file) ) {
			return (T)objectMapper.readValue(is, clazz);
		} catch ( FileNotFoundException e ) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException f) {
				throw new ConfigurationException(e);
			}
		} catch ( IOException e ) {
			throw new ConfigurationException(e);
		}
	}

	@Override
	public <T extends Configuration> void updateConfiguration(T t) {
		File file = new File(DATA_LOCATION, t.getClass().getSimpleName() + ".json");
		try {
			file.createNewFile();
			objectMapper.writeValue(file, t);
		} catch ( IOException e ) {
			throw new ConfigurationException(e);
		}
	}

}
