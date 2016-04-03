package com.squid.controller.test;

import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.BoxDTO;
import com.squid.data.BoxData;
import com.squid.service.test.TestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Test Controller
 */
@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	TestService testService;

    @RequestMapping("/hello")
    public String index() {
        return "Greetings from Spring Boot2!";
    }

    /**
     * Get local list of files from a directory.  Directory passed in as a parameter
     */
    @RequestMapping(path = "/files", method = RequestMethod.GET)
    public List<String> getDirectories(@RequestParam(value="dir") String dir) {
    	return testService.getDirectories(dir);
    }

    @RequestMapping(path="/box/{id}", method = RequestMethod.GET)
    public @ResponseBody BoxDTO getBox(@PathVariable long id) {
    	BoxData data = testService.getBox(id);
    	
    	BoxDTO dto = new BoxDTO();

    	if (data != null) {
        	dto.heigth = data.getHeigth();
        	dto.length = data.getLength();
        	dto.width = data.getWidth();
        	dto.id = data.getId();    	
        	}
    	return dto;
    }
    
    @RequestMapping(path="/box", method = RequestMethod.POST) 
    public @ResponseBody BoxDTO setBox(@RequestBody BoxDTO box) {
    	
    	BoxData data = new BoxData();
    	data.setHeigth(box.heigth);
    	data.setLength(box.length);
    	data.setWidth(box.width);
    	
    	data = testService.saveBox(data);
    	
    	BoxDTO dto = new BoxDTO();
    	dto.heigth = data.getHeigth();
    	dto.length = data.getLength();
    	dto.width = data.getWidth();
    	
    	return dto;
    }
}
