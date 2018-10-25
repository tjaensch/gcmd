package gov.noaa.onestop.gcmd.dataTest;

import gov.noaa.onestop.gcmd.data.GcmdData;
import gov.noaa.onestop.gcmd.service.GcmdService;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertThat;

public class GcmdDataTest {

    GcmdService gcmdService = new GcmdService();
    public URL testfile;
    public Document xmlDocument;

    public GcmdDataTest() throws IOException, SAXException {
    }

    @Before
    public void setUp() throws IOException, SAXException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("static/collection_test_files/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml").getFile());
        testfile = file.toURI().toURL();
        xmlDocument = (Document) GcmdData.get_xml_document(gcmdService, testfile);
    }

    // SIMILAR KEYWORDS
    @Test
    public void test_get_similar_keywords_cosine_similarity_method() throws IOException, SAXException {
        Map<String, Integer> resultsThemeKeywords = GcmdData.get_similar_keywords_cosine_similarity_method(gcmdService.get_model_theme_keywords_list(), "Earth Science > Land Surface > Topography > Topographical Relief");
        assertThat(resultsThemeKeywords, hasValue("EARTH SCIENCE > LAND SURFACE > TOPOGRAPHY > TERRAIN ELEVATION > TOPOGRAPHICAL RELIEF MAPS"));
        Map<String, Integer> resultsProjectKeywords = GcmdData.get_similar_keywords_cosine_similarity_method(gcmdService.get_model_project_keywords_list(), "Onestop");
        assertThat(resultsProjectKeywords, hasValue("NOAA ONESTOP PROJECT"));
        Map<String, Integer> resultsInstrumentKeywords = GcmdData.get_similar_keywords_cosine_similarity_method(gcmdService.get_model_instrument_keywords_list(), "Windsa");
        assertThat(resultsInstrumentKeywords, hasValue("WINDSAT"));
    }

    @Test
    public void test_get_similar_keywords_string_method() throws IOException, SAXException {
        List<String> resultsThemeKeywords = GcmdData.get_similar_keywords_string_method(gcmdService.get_model_theme_keywords_list(), "Earth Science > Land Surface > Topography > Topographical Relief");
        assertThat(resultsThemeKeywords, hasItem("EARTH SCIENCE > LAND SURFACE > TOPOGRAPHY > TERRAIN ELEVATION > TOPOGRAPHICAL RELIEF MAPS"));
        List<String> resultsProjectKeywords = GcmdData.get_similar_keywords_string_method(gcmdService.get_model_project_keywords_list(), "Onestop");
        assertThat(resultsProjectKeywords, hasItem("NOAA ONESTOP PROJECT"));
        List<String> resultsInstrumentKeywords = GcmdData.get_similar_keywords_string_method(gcmdService.get_model_instrument_keywords_list(), "Windsa");
        assertThat(resultsInstrumentKeywords, hasItem("WINDSAT"));
    }
}
