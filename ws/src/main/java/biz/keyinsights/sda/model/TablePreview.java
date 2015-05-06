package biz.keyinsights.sda.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;


public class TablePreview {
	private final Queue<Cell> rowOne = new LinkedList<>();
	private final Queue<Cell> rowTwo = new LinkedList<>();
	private final Queue<Column> columns = new LinkedList<>();
	
	public Collection<Column> getHeaders() {
		return columns;
	}
	
	public void addHeader(Column column) {
		columns.add(column);
	}
	
	public Collection<Cell> getRowOne() {
		return rowOne;
	}
	
	public void addRowOne(String data) {
		Column c = columns.poll();
		rowOne.offer(new Cell(c, data));
		columns.offer(c);
	}
	
	public Collection<Cell> getRowTwo() {
		return rowTwo;
	}
	
	public void addRowTwo(String data) {
		Column c = columns.poll();
		rowTwo.offer(new Cell(c, data));
		columns.offer(c);
	}
}
