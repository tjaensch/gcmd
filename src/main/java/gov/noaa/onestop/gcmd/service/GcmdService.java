package gov.noaa.onestop.gcmd.service;

import org.springframework.stereotype.Component;
import java.util.*;
import java.io.File;

@Component
public class GcmdService {


    public static List<String> findXmlFiles() {
        List<String> results = new ArrayList<String>();

        File[] files = new File("/Users/thomasjaensch/IdeaProjects/gcmd/src/main/resources/static/collection_test_files").listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getAbsolutePath());
            }
        }
        return results;
    }

}


