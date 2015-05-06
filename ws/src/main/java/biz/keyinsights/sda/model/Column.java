package biz.keyinsights.sda.model;

public class Column {
	private String name;
	private boolean isJoinable;
	
	public Column() {}
	
	public Column(String name) {
		this.name = name;
	}
	
	/**
	 * Other fields that you might want to add about each column go here
	 */
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isJoinable() {
		return isJoinable;
	}

	public void setJoinable(boolean isJoinable) {
		this.isJoinable = isJoinable;
	}
}
