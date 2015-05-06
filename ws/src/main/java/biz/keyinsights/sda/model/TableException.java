package biz.keyinsights.sda.model;

public class TableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TableException(String message) {
		super(message);
	}
	
	public TableException(String message, Throwable t) {
		super(message, t);
	}
}
