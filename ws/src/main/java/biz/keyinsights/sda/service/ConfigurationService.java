package biz.keyinsights.sda.service;

import biz.keyinsights.sda.model.Configuration;

public interface ConfigurationService {
	<T extends Configuration> T getConfiguration(Class<T> clazz);
	<T extends Configuration> void updateConfiguration(T t);
}
