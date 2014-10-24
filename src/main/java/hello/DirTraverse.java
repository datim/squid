package hello;

import java.util.List;
import java.io.File;
import java.util.ArrayList;

public class DirTraverse {

    private List<String> filesAndDirs = new ArrayList<>();

    public DirTraverse(String directory) {

	// traveserse a file structure and save all results to the array list

	File startPath = new File(directory);

        File[] filesList = startPath.listFiles();

	for (File x : filesList) {
		System.out.println("Found dir " + x.getName());
		filesAndDirs.add(x.getName());
	
	}

    }

    public List getFilesAndDirs() {
        return filesAndDirs;
    }
}
