package biz.keyinsights.sda.service;

import java.io.InputStream;
import java.util.List;

import biz.keyinsights.sda.model.Table;
import biz.keyinsights.sda.model.TablePreview;

public interface TableService {
	Table getTable(String id);
	TablePreview getTablePreview(String id);
	void addTable(Table t, InputStream csv);
	void updateTable(Table t, InputStream csv);
	void deleteTable(String id);
	List<Table> findAllTables();
}
