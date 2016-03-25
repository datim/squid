package com.squid.service.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squid.data.BoxData;
import com.squid.data.BoxRepository;

/**
 * Test a directory service
 * @author Datim
 */
@Service
public class TestService {
	
	@Autowired
	BoxRepository boxRepo;
	
	public List<String> getDirectories(String directory) {

		File startPath = new File(directory);

        File[] fileList = startPath.listFiles();

        List<String> fileListString = new ArrayList<>(fileList.length);

        for (File x : fileList) {
        	fileListString.add(x.getName());
        }
    	return fileListString;
	}

	// save box information to the database
	public BoxData saveBox(BoxData box) {
		return boxRepo.save(box);
		
	}

	public BoxData getBox(long id) {
		return boxRepo.findById(id);
	}
}
