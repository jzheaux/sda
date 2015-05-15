package biz.keyinsights.sda.controller;

import java.util.HashMap;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import biz.keyinsights.sda.model.ServerConfiguration;
import biz.keyinsights.sda.service.ConfigurationService;
import biz.keyinsights.sda.service.TableService;

@Controller
public class SettingsController {
	@Inject ConfigurationService configurationService;
	@Inject TableService tableService;
	
	@RequestMapping(value="/settings")
	@Secured("ADMIN")
	public ModelAndView beginSettingsWorkflow(@ModelAttribute("model") ServerConfiguration sc) {
		ServerConfiguration stored = configurationService.getConfiguration(ServerConfiguration.class);
		sc.setAccessLogDirectory(stored.getAccessLogDirectory());
		sc.setDataDirectory(stored.getDataDirectory());
		sc.setNumberOfGpus(stored.getNumberOfGpus());
		sc.setNumberOfProcessors(stored.getNumberOfProcessors());
		
		return new ModelAndView("/admin/settings", new HashMap<String, Object>() {{
			put("model", sc);
			put("tables", tableService.findAllTables());
		}});
	}
	
	@RequestMapping(value="/settings", method=RequestMethod.POST)
	@Secured("ADMIN")
	public String commitSettingsWorkflow(@ModelAttribute("model") @Valid ServerConfiguration sc) {
		configurationService.updateConfiguration(sc);
		return "redirect:/settings";
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home() {
		return "index";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String postLogin() {
		return home();
	}
}
