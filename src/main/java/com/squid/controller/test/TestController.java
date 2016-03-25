package com.squid.controller.test;

import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.BoxDTO;
import com.squid.data.BoxData;
import com.squid.service.test.TestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * Echo the path variable back to the caller
     * @param name
     * @return
     */
    @RequestMapping(path = "/echo/{name}", method = RequestMethod.GET)
    public String echoName(@PathVariable String name) {

    	/*
    	BoxData box = new BoxData();
    	box.setLength(5);
    	box.setWidth(12);
    	box.setId(5);
    	box.setHeigth(12);
    	    	testService.saveBox(box);

    	*/
    	
    	return "Echoing " + name;
    }
    
    @RequestMapping(path="/box/{id}", method = RequestMethod.GET)
    public @ResponseBody BoxDTO getBox(@PathVariable long id) {
    	BoxData data = testService.getBox(id);
    	
    	BoxDTO dto = new BoxDTO();
    	dto.heigth = data.getHeigth();
    	dto.length = data.getLength();
    	dto.width = data.getWidth();
    	dto.id = data.getId();
    	
    	return dto;
    }
    
    @RequestMapping(path="/box", method = RequestMethod.POST) 
    public @ResponseBody BoxDTO setBox(@RequestParam(value="box") BoxDTO box) {
    	
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
