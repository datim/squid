package com.squid.service.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Test a directory service
 * @author Datim
 */
@Service
public class TestService {

	public List<String> getDirectories(String directory) {

		File startPath = new File(directory);

        File[] fileList = startPath.listFiles();

        List<String> fileListString = new ArrayList<>(fileList.length);

        for (File x : fileList) {
        	fileListString.add(x.getName());
        }
    	return fileListString;
	}
}
