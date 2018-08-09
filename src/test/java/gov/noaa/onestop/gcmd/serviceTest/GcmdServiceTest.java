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

    @Test
    public void test_get_datacenter_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_datacenter_keywords(gcmdService.get_xml_document(testfile));
        assertThat(results, hasSize(3));
        assertThat(results, containsInAnyOrder("DOC/NOAA/NESDIS/NODC", "DOC/NOAA/NESDIS/NCEI > National Centers for Environmental Information, NESDIS, NOAA, U.S. Department of Commerce", "NASA/JPL/PODAAC > Physical Oceanography Distributed Active Archive Center, Jet Propulsion Laboratory, NASA"));
        assertThat(results, not(hasItem("BLAH > DOC/NOAA/NESDIS/NODC")));
    }

    @Test
    public void test_get_place_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_place_keywords(gcmdService.get_xml_document(testfile));
        assertThat(results, hasSize(9));
        assertThat(results, containsInAnyOrder("Oceania", "OCEAN > INDIAN OCEAN", "OCEAN > INDIAN OCEAN > ARABIAN SEA", "OCEAN > INDIAN OCEAN > BAY OF BENGAL", "OCEAN > PACIFIC OCEAN > NORTH PACIFIC OCEAN", "OCEAN > PACIFIC OCEAN > SOUTH PACIFIC OCEAN", "OCEAN > PACIFIC OCEAN > WESTERN PACIFIC OCEAN > SOUTH CHINA AND EASTERN ARCHIPELAGIC SEAS", "OCEAN > PACIFIC OCEAN > WESTERN PACIFIC OCEAN > SOUTH CHINA SEA", "OCEAN > SOUTHERN OCEAN"));
        assertThat(results, not(hasItem("BLAH > SOUTHERN OCEAN")));
    }

    @Test
    public void test_get_platform_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_platform_keywords(gcmdService.get_xml_document(testfile));
        assertThat(results, hasSize(9));
        assertThat(results, containsInAnyOrder("AQUA > Earth Observing System, AQUA", "CORIOLIS > Coriolis", "ENVISAT > Environmental Satellite", "GCOM-W1 > Global Change Observation Mission 1st-Water", "METOP-A > Meteorological Operational Satellite - A", "METOP-B", "NOAA-17 > National Oceanic & Atmospheric Administration-17", "NOAA-18 > National Oceanic & Atmospheric Administration-18", "NOAA-19 > National Oceanic & Atmospheric Administration-19"));
        assertThat(results, not(hasItem("BLAH > SOUTHERN OCEAN")));
    }

    @Test
    public void test_get_instrument_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_instrument_keywords(gcmdService.get_xml_document(testfile));
        assertThat(results, hasSize(5));
        assertThat(results, containsInAnyOrder("AATSR", "AMSR-E > Advanced Microwave Scanning Radiometer-EOS", "AMSR2 > Advanced Microwave Scanning Radiometer 2", "AVHRR-3 > Advanced Very High Resolution Radiometer-3", "WINDSA"));
        assertThat(results, not(hasItem("AATSM")));
    }

    @Test
    public void test_get_project_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_project_keywords(gcmdService.get_xml_document(testfile));
        assertThat(results, hasSize(2));
        assertThat(results, containsInAnyOrder("GHRSST > Group for High Resolution Sea Surface Temperature", "NOAA OneStop"));
        assertThat(results, not(hasItem("BLAH Project")));
    }


}
