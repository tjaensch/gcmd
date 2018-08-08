package gov.noaa.onestop.gcmd.serviceTest;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;


public class GcmdServiceTest {
    GcmdService gcmdService = new GcmdService();
    public URL testfile;

    @Before
    public void setUp() throws MalformedURLException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("static/collection_test_files/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml").getFile());
        testfile = file.toURI().toURL();
    }

    @Test
    public void test_get_theme_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_theme_keywords(gcmdService.get_xml_document(testfile));
        assertThat(results, hasSize(2));
        assertThat(results, containsInAnyOrder("EARTH SCIENCE > OCEANS > OCEAN TEMPERATURE > SEA SURFACE TEMPERATURE", "Earth Science > Oceans > Ocean Temperature > Sea Surface Temperature > Foundation Sea Surface Temperature"));
        assertThat(results, not(hasItem("BLAH SCIENCE > OCEANS > OCEAN TEMPERATURE > SEA SURFACE TEMPERATURE")));
    }


}
