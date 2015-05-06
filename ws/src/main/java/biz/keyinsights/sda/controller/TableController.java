package biz.keyinsights.sda.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import biz.keyinsights.sda.model.Column;
import biz.keyinsights.sda.model.Table;
import biz.keyinsights.sda.model.TablePreview;
import biz.keyinsights.sda.service.TableService;

@Controller
public class TableController {
	@Inject TableService tableService;
	
	
	@RequestMapping(value="/table/new", method=RequestMethod.GET)
	public String beginAddTable(@ModelAttribute("model") Table table) {
		return "/admin/tables/edit";
	}
	
	@RequestMapping(value="/table/new", method=RequestMethod.POST)
	public String commitAddTable(@ModelAttribute("model") @Valid Table table, @RequestParam("data") MultipartFile csv) throws IOException {
		tableService.addTable(table, csv.getSize() == 0 ? null : csv.getInputStream());
		
		return "redirect:/table/" + table.getId();
	}
	
	@RequestMapping(value="/table/{id}/edit", method=RequestMethod.GET)
	public ModelAndView beginEditTable(@PathVariable("id") String id) {
		return new ModelAndView("/admin/tables/edit", new HashMap<String, Object>() {{
			put("model", tableService.getTable(id));
			put("preview", tableService.getTablePreview(id));
		}});
	}
	
	@RequestMapping(value="/table/{id}/edit", method=RequestMethod.POST)
	public String commitEditTable(@PathVariable("id") String id, @ModelAttribute("model") @Valid Table table, @RequestParam("data") MultipartFile csv)  throws IOException {
		tableService.updateTable(table, csv.getSize() == 0 ? null : csv.getInputStream());

		return "redirect:/table/" + table.getId();
	}

	@RequestMapping(value="/table/{id}/joincolumn/{column}", method=RequestMethod.PUT)
	public @ResponseBody 
	String addJoinColumn(@PathVariable("id") String id, @PathVariable("column") Integer columnId) {
		Table t = tableService.getTable(id);
		List<Column> columns = t.getColumns();
		columns.get(columnId).setJoinable(true);
		tableService.updateTable(t, null);
		return "OK";
	}
	
	@RequestMapping(value="/table/{id}/joincolumn/{column}", method=RequestMethod.DELETE)
	public @ResponseBody 
	String removeJoinColumn(@PathVariable("id") String id, @PathVariable("column") Integer columnId) {
		Table t = tableService.getTable(id);
		List<Column> columns = t.getColumns();
		columns.get(columnId).setJoinable(false);
		tableService.updateTable(t, null);
		return "OK";
	}
	
	@RequestMapping(value="/table/{id}/data/preview")
	public ModelAndView beginTablePreview(@PathVariable("id") String id) {
		TablePreview tp = tableService.getTablePreview(id);

		return new ModelAndView("/admin/tables/preview", "model", tp);
	}
	
	@RequestMapping(value="/table/{id}/delete", method=RequestMethod.POST)
	public String commitDeleteTable(@PathVariable("id") String id) {
		tableService.deleteTable(id);
		
		return "redirect:/admin/settings";
	}

	@RequestMapping(value="/tables", method=RequestMethod.GET)
	public ModelAndView findAllTables() {
		List<Table> tables = tableService.findAllTables();
		return new ModelAndView("/admin/tables", "model", tables);
	}
	
	@RequestMapping(value="/analysis", method=RequestMethod.GET)
	public ModelAndView prepareAnalysis() {
		List<Table> tables = tableService.findAllTables();
		return new ModelAndView("/analysis", "model", tables);
	}
}
