package biz.keyinsights.sda.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

import session.client.UserInterfaceRemote;

@Configuration
@ComponentScan({"session.web", "session.web.handlers", "session.client", "message.rest"})
public class AppConfig {
	@Inject UserInterfaceRemote service;
	
	@Bean
	public RmiServiceExporter rmiServiceExporter() {
		RmiServiceExporter exporter = new RmiServiceExporter();
		exporter.setServiceName("RegressionService");
		exporter.setService(service);
		exporter.setServiceInterface(UserInterfaceRemote.class);
		exporter.setRegistryPort(1199);
		return exporter;
	}
}
