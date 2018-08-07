package gov.noaa.onestop.gcmd.serviceTest;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.junit.Before;
import org.junit.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;


public class GcmdServiceTest {
    GcmdService gcmdService = new GcmdService();
    
    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("static/collection_test_files/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml").getFile());
        String testfile = file.getAbsolutePath();
        System.out.println(testfile);
    }

    @Test
    public void test_findXmlFiles() {
        List<String> results = gcmdService.find_xml_files();
        assertThat(results, hasSize(5));
    }

    @Test
    public void test_get_theme_keywords() throws IOException, XPathExpressionException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("static/collection_test_files/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml").getFile());
        URL testfile = file.toURI().toURL();
        List<String> results = gcmdService.get_theme_keywords(testfile);
        assertThat(results, hasSize(2));
    }


}
