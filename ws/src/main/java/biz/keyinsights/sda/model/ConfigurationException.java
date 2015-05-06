package biz.keyinsights.sda.model;

public class ConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConfigurationException(String message, Throwable t) {
		super(message, t);
	}

	public ConfigurationException(Throwable t) {
		super(t);
	}
}
