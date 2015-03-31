package biz.keyinsights.sda.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import session.client.UserInterfaceRemote;

@Configuration
@ComponentScan("userInterface")
public class AppConfig {
	@Bean(name = "userInterface")
	public RmiProxyFactoryBean remote() {
		RmiProxyFactoryBean b = new RmiProxyFactoryBean();
		b.setServiceUrl("rmi://localhost:1199/RegressionService");
		b.setServiceInterface(UserInterfaceRemote.class);
		return b;
	}
}
