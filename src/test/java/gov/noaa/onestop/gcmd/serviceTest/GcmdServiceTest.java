package gov.noaa.onestop.gcmd.serviceTest;

import gov.noaa.onestop.gcmd.data.GcmdData;
import gov.noaa.onestop.gcmd.service.GcmdService;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


public class GcmdServiceTest {
    GcmdService gcmdService = new GcmdService();
    public URL testfile;
    public Document xmlDocument;

    @Before
    public void setUp() throws IOException, SAXException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("static/collection_test_files/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml").getFile());
        testfile = file.toURI().toURL();
        xmlDocument = (Document) GcmdData.get_xml_document(gcmdService, testfile);
    }

    // THEME KEYWORDS
    @Test
    public void test_get_theme_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_theme_keywords(GcmdData.get_xml_document(gcmdService, testfile));
        assertFalse(results.isEmpty());
        assertThat(results, hasSize(2));
        assertThat(results, containsInAnyOrder("EARTH SCIENCE > OCEANS > OCEAN TEMPERATURE > SEA SURFACE TEMPERATURE", "Earth Science > Oceans > Ocean Temperature > Sea Surface Temperature > Foundation Sea Surface Temperature"));
        assertThat(results, not(hasItem("BLAH SCIENCE > OCEANS > OCEAN TEMPERATURE > SEA SURFACE TEMPERATURE")));
    }

    @Test
    public void test_get_model_theme_keywords_list() throws IOException, SAXException {
        List<String> results = gcmdService.get_model_theme_keywords_list();
        assertThat(results.size(), greaterThan(3100));
        assertThat(results, hasItem("EARTH SCIENCE SERVICES > DATA ANALYSIS AND VISUALIZATION > CALIBRATION/VALIDATION > CALIBRATION"));
        assertThat(results, hasItem("EARTH SCIENCE > ATMOSPHERE > ATMOSPHERIC CHEMISTRY > NITROGEN COMPOUNDS > CLOUD-SCREENED TOTAL COLUMN NITROGEN DIOXIDE (NO2)"));
        assertThat(results, not(hasItem("BLAH > EARTH SCIENCE")));
    }

    @Test
    public void test_get_invalid_theme_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> results = gcmdService.get_invalid_theme_keywords();
        assertThat(results, hasSize(1));
        assertThat(results, hasItem("Earth Science > Oceans > Ocean Temperature > Sea Surface Temperature > Foundation Sea Surface Temperature"));
        assertThat(results, not(hasItem("EARTH SCIENCE > OCEANS > OCEAN TEMPERATURE > SEA SURFACE TEMPERATURE > FOUNDATION SEA SURFACE TEMPERATURE")));
        assertThat(results, not(hasItem("EARTH SCIENCE > OCEANS > OCEAN TEMPERATURE > SEA SURFACE TEMPERATURE")));
    }


    // DATACENTER KEYWORDS
    @Test
    public void test_get_datacenter_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_datacenter_keywords(GcmdData.get_xml_document(gcmdService, testfile));
        assertThat(results, hasSize(3));
        assertThat(results, containsInAnyOrder("DOC/NOAA/NESDIS/NODC", "DOC/NOAA/NESDIS/NCEI > National Centers for Environmental Information, NESDIS, NOAA, U.S. Department of Commerce", "NASA/JPL/PODAAC > Physical Oceanography Distributed Active Archive Center, Jet Propulsion Laboratory, NASA"));
        assertThat(results, not(hasItem("BLAH > DOC/NOAA/NESDIS/NODC")));
    }

    @Test
    public void test_get_model_datacenter_keywords_list() throws IOException, SAXException {
        List<String> results = gcmdService.get_model_datacenter_keywords_list();
        assertThat(results.size(), greaterThan(3600));
        assertThat(results, hasItem("USDA/CSREES/PMC/UMES/UMN > PEST MANAGEMENT CENTER, UNIVERSITY OF MINNESOTA EXTENSION SERVICES, UNIVERSITY OF MINNESOTA, USDA-CSREES"));
        assertThat(results, hasItem("DOC/NOAA/NESDIS/NCEI > NATIONAL CENTERS FOR ENVIRONMENTAL INFORMATION, NESDIS, NOAA, U.S. DEPARTMENT OF COMMERCE"));
        assertThat(results, not(hasItem("BLAH > DOC/NOAA/NESDIS/NCEI")));
    }

    @Test
    public void test_get_invalid_datacenter_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> results = gcmdService.get_invalid_datacenter_keywords();
        assertThat(results, hasSize(1));
        assertThat(results, hasItem("DOC/NOAA/NESDIS/NODC"));
        assertThat(results, not(hasItem("DOC/NOAA/NESDIS/NCEI > National Centers for Environmental Information, NESDIS, NOAA, U.S. Department of Commerce")));
        assertThat(results, not(hasItem("NASA/JPL/PODAAC > Physical Oceanography Distributed Active Archive Center, Jet Propulsion Laboratory, NASA")));
    }

    // PLACE KEYWORDS
    @Test
    public void test_get_place_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_place_keywords(GcmdData.get_xml_document(gcmdService, testfile));
        assertThat(results, hasSize(9));
        assertThat(results, containsInAnyOrder("Oceania", "OCEAN > INDIAN OCEAN", "OCEAN > INDIAN OCEAN > ARABIAN SEA", "OCEAN > INDIAN OCEAN > BAY OF BENGAL", "OCEAN > PACIFIC OCEAN > NORTH PACIFIC OCEAN", "OCEAN > PACIFIC OCEAN > SOUTH PACIFIC OCEAN", "OCEAN > PACIFIC OCEAN > WESTERN PACIFIC OCEAN > SOUTH CHINA AND EASTERN ARCHIPELAGIC SEAS", "OCEAN > PACIFIC OCEAN > WESTERN PACIFIC OCEAN > SOUTH CHINA SEA", "OCEAN > SOUTHERN OCEAN"));
        assertThat(results, not(hasItem("BLAH > SOUTHERN OCEAN")));
    }

    @Test
    public void test_get_model_place_keywords_list() throws IOException, SAXException {
        List<String> results = gcmdService.get_model_place_keywords_list();
        assertThat(results.size(), greaterThan(520));
        assertThat(results, hasItem("OCEAN > ATLANTIC OCEAN > NORTH ATLANTIC OCEAN > CARIBBEAN SEA > ANTIGUA AND BARBUDA"));
        assertThat(results, hasItem("OCEAN > ATLANTIC OCEAN > NORTH ATLANTIC OCEAN > NORWEGIAN SEA > FAEROE ISLANDS"));
        assertThat(results, not(hasItem("BLAH > OCEAN")));
    }

    @Test
    public void test_get_invalid_place_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> results = gcmdService.get_invalid_place_keywords();
        assertThat(results, hasSize(1));
        assertThat(results, hasItem("Oceania"));
        assertThat(results, not(hasItem("OCEAN > PACIFIC OCEAN > NORTH PACIFIC OCEAN")));
        assertThat(results, not(hasItem("OCEAN > INDIAN OCEAN")));
    }

    // PLATFORM KEYWORDS
    @Test
    public void test_get_platform_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_platform_keywords(GcmdData.get_xml_document(gcmdService, testfile));
        assertThat(results, hasSize(9));
        assertThat(results, containsInAnyOrder("AQUA > Earth Observing System, AQUA", "CORIOLIS > Coriolis", "ENVISAT > Environmental Satellite", "GCOM-W1 > Global Change Observation Mission 1st-Water", "METOP-A > Meteorological Operational Satellite - A", "METOP-B", "NOAA-17 > National Oceanic & Atmospheric Administration-17", "NOAA-18 > National Oceanic & Atmospheric Administration-18", "NOAA-19 > National Oceanic & Atmospheric Administration-19"));
        assertThat(results, not(hasItem("BLAH > SOUTHERN OCEAN")));
    }

    @Test
    public void test_get_model_platform_keywords_list() throws IOException, SAXException {
        List<String> results = gcmdService.get_model_platform_keywords_list();
        assertThat(results.size(), greaterThan(870));
        assertThat(results, hasItem("STS-43 > SPACE TRANSPORT SYSTEM STS-43"));
        assertThat(results, hasItem("A340-600 > AIRBUS A340-600"));
        assertThat(results, not(hasItem("BLAH > AIRCRAFT")));
    }

    @Test
    public void test_get_invalid_platform_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> results = gcmdService.get_invalid_platform_keywords();
        assertThat(results, hasSize(1));
        assertThat(results, hasItem("METOP-B"));
        assertThat(results, not(hasItem("METOP-A > Meteorological Operational Satellite - A")));
        assertThat(results, not(hasItem("AQUA > Earth Observing System, AQUA")));
    }

    // INSTRUMENT KEYWORDS
    @Test
    public void test_get_instrument_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_instrument_keywords(GcmdData.get_xml_document(gcmdService, testfile));
        assertThat(results, hasSize(5));
        assertThat(results, containsInAnyOrder("AATSR", "AMSR-E > Advanced Microwave Scanning Radiometer-EOS", "AMSR2 > Advanced Microwave Scanning Radiometer 2", "AVHRR-3 > Advanced Very High Resolution Radiometer-3", "WINDSA"));
        assertThat(results, not(hasItem("AATSM")));
    }

    @Test
    public void test_get_model_instrument_keywords_list() throws IOException, SAXException {
        List<String> results = gcmdService.get_model_instrument_keywords_list();
        assertThat(results.size(), greaterThan(1590));
        assertThat(results, hasItem("ATLAS > ADVANCED TOPOGRAPHIC LASER ALTIMETER SYSTEM"));
        assertThat(results, hasItem("MESSR > MULTISPECTRAL ELECTRONIC SELF-SCANNING RADIOMETER"));
        assertThat(results, not(hasItem("BLAH > EARTH REMOTE SENSING INSTRUMENTS")));
    }

    @Test
    public void test_get_invalid_instrument_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> results = gcmdService.get_invalid_instrument_keywords();
        assertThat(results, hasSize(2));
        assertThat(results, hasItem("AATSR"));
        assertThat(results, hasItem("WINDSA"));
        assertThat(results, not(hasItem("AMSR-E > Advanced Microwave Scanning Radiometer-EOS")));
    }

    // PROJECT KEYWORDS
    @Test
    public void test_get_project_keywords() throws IOException, XPathExpressionException, SAXException {
        List<String> results = gcmdService.get_project_keywords(GcmdData.get_xml_document(gcmdService, testfile));
        assertThat(results, hasSize(2));
        assertThat(results, containsInAnyOrder("GHRSST > Group for High Resolution Sea Surface Temperature", "NOAA OneStop"));
        assertThat(results, not(hasItem("BLAH Project")));
    }

    @Test
    public void test_get_model_project_keywords_list() throws IOException, SAXException {
        List<String> results = gcmdService.get_model_project_keywords_list();
        assertThat(results.size(), greaterThan(1700));
        assertThat(results, hasItem("NOAA ONESTOP PROJECT"));
        assertThat(results, hasItem("NORTHERN GENEALOGIES > NORTHERN GENEALOGIES: DEVELOPMENT OF AN ETHNODEMOGRAPHIC INFORMATIONAL SYSTEM ON THE PEOPLES OF SIBERIA AND THE RUSSIAN NORTH"));
        assertThat(results, not(hasItem("BLAH > NOAA")));
    }

    @Test
    public void test_get_invalid_project_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> results = gcmdService.get_invalid_project_keywords();
        assertThat(results, hasSize(1));
        assertThat(results, hasItem("NOAA OneStop"));
        assertThat(results, not(hasItem("GHRSST > Group for High Resolution Sea Surface Temperature")));
        assertThat(results, not(hasItem("BLAH > Project")));
    }

}
