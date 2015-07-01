package biz.keyinsights.sda.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import biz.keyinsights.sda.model.Column;
import biz.keyinsights.sda.model.Table;
import biz.keyinsights.sda.model.TablePreview;
import biz.keyinsights.sda.service.RemoteTableService;
import biz.keyinsights.sda.service.TableService;

@Controller
public class TableController {
	@Inject TableService tableService;
	
	@RequestMapping(value="/table/new", method=RequestMethod.GET)
	@Secured("ADMIN")
	public String beginAddTable(@ModelAttribute("model") Table table) {
		return "/admin/tables/edit";
	}
	
	@RequestMapping(value="/table/new", method=RequestMethod.POST)
	@Secured("ADMIN")
	public String commitAddTable(@ModelAttribute("model") @Valid Table table, @RequestParam("data") MultipartFile csv) throws IOException {
		tableService.addTable(table, csv.getSize() == 0 ? null : csv.getInputStream());
		
		return "redirect:/table/" + table.getId() + "/edit";
	}
	
	@RequestMapping(value="/table/{id}/edit", method=RequestMethod.GET)
	@Secured("ADMIN")
	public ModelAndView beginEditTable(@PathVariable("id") String id) {
		return new ModelAndView("/admin/tables/edit", new HashMap<String, Object>() {{
			put("model", tableService.getTable(id));
			put("preview", tableService.getTablePreview(id));
		}});
	}
	
	@RequestMapping(value="/table/{id}/edit", method=RequestMethod.POST)
	@Secured("ADMIN")
	public String commitEditTable(@PathVariable("id") String id, @ModelAttribute("model") @Valid Table table, @RequestParam("data") MultipartFile csv)  throws IOException {
		if ( csv.getSize() == 0 ) {
			Table withColumns = tableService.getTable(id);
			table.setColumns(withColumns.getColumns());
		}
		tableService.updateTable(table, csv.getSize() == 0 ? null : csv.getInputStream());

		return "redirect:/table/" + table.getId() + "/edit";
	}

	@RequestMapping(value="/table/{id}/joincolumn/{column}", method=RequestMethod.PUT)
	@Secured("ADMIN")
	public @ResponseBody 
	String addJoinColumn(@PathVariable("id") String id, @PathVariable("column") Integer columnId) {
		Table t = tableService.getTable(id);
		List<Column> columns = t.getColumns();
		columns.get(columnId).setJoinable(true);
		tableService.updateTable(t, null);
		return "OK";
	}
	
	@RequestMapping(value="/table/{id}/joincolumn/{column}", method=RequestMethod.DELETE)
	@Secured("ADMIN")
	public @ResponseBody 
	String removeJoinColumn(@PathVariable("id") String id, @PathVariable("column") Integer columnId) {
		Table t = tableService.getTable(id);
		List<Column> columns = t.getColumns();
		columns.get(columnId).setJoinable(false);
		tableService.updateTable(t, null);
		return "OK";
	}
	
	@RequestMapping(value="/table/{id}/data/preview")
	@Secured("ADMIN")
	public ModelAndView beginTablePreview(@PathVariable("id") String id) {
		TablePreview tp = tableService.getTablePreview(id);

		return new ModelAndView("/admin/tables/preview", "model", tp);
	}
	
	@RequestMapping(value="/table/{id}/delete", method=RequestMethod.POST)
	@Secured("ADMIN")
	public String commitDeleteTable(@PathVariable("id") String id) {
		tableService.deleteTable(id);
		
		return "redirect:/admin/settings";
	}

	@RequestMapping(value="/tables", method=RequestMethod.GET)
	public @ResponseBody
	List<Table> findAllTables() {
		return tableService.findAllTables();
	}
	
	@RequestMapping(value="/analysis", method=RequestMethod.GET)
	public ModelAndView prepareAnalysis() {
		List<Table> tables = tableService.findAllTables();
		return new ModelAndView("/analysis", "model", tables);
	}
	
	@RequestMapping(value="/proxy/{host}/{port}/tables", method=RequestMethod.GET)
	public @ResponseBody
	List<Table> findAllTablesByProxy(@PathVariable("host") String host, @PathVariable("port") Integer port, @RequestHeader("Authorization") String authHeader) {
		RemoteTableService rts = new RemoteTableService(host, String.valueOf(port));
		return rts.findAllTables();
	}
	
	@RequestMapping(value="/table/{id}/data", method=RequestMethod.GET)
	public ResponseEntity readTable(@PathVariable("id") String id, @RequestHeader(value="X-Table-Username", required=false) String username, @RequestHeader(value="X-Table-Password", required=false) char[] password) {
		Table t = tableService.getTable(id);
		InputStream is = tableService.getTableData(id, username, password);
		
		InputStreamResource inputStreamResource = new InputStreamResource(is);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentLength(t.getSizeInBytes());
		return new ResponseEntity(inputStreamResource, httpHeaders, HttpStatus.OK);
	}
	
}
