package com.squid.controller.test;

import org.springframework.web.bind.annotation.RestController;

import com.squid.controller.rest.BoxDTO;
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

    @RequestMapping(path = "/box", method = RequestMethod.GET)
    public @ResponseBody BoxDTO getBox() {

    	BoxDTO box = new BoxDTO();

    	box.heigth = 5;
    	box.width = 2;

    	box.area = box.heigth * box.width;

    	return box;
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
    	return "Echoing " + name;
    }
}
