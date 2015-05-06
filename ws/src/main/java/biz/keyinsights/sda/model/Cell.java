package biz.keyinsights.sda.model;

public class Cell {
	private final Column column;
	private final String data;
	
	public Cell(Column column, String data) {
		this.column = column;
		this.data = data;
	}
	
	public Column getColumn() {
		return column;
	}
	
	public String getData() {
		return data;
	}
}
