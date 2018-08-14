package gov.noaa.onestop.gcmd.controller;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@RestController
public class GcmdController {

    @Autowired
    GcmdService service;
    public Document xmlDocument;

    public GcmdController() throws IOException, SAXException {
    }

    // Format http://localhost:8080/gcmd_keywords?url=https://data.nodc.noaa
    // .gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml
    @RequestMapping(value = "/gcmd_keywords", method = RequestMethod.GET)
    public HashMap<String, ArrayList> get_url_value(@RequestParam("url") URL urlValue) throws IOException, SAXException, XPathExpressionException {
        URL xmlUrl = urlValue;
        xmlDocument = service.get_xml_document(xmlUrl);
        HashMap<String, ArrayList> allKeywords = service.get_all_keywords();
        HashMap<String, ArrayList> allInvalidKeywords = service.get_all_invalid_keywords();
        allInvalidKeywords.forEach(allKeywords::putIfAbsent);

        return allKeywords;
    }

    // ALL KEYWORDS
    @RequestMapping("/show_all_keywords")
    public HashMap<String, ArrayList> show_all_keywords() throws Exception {
        return service.get_all_keywords();
    }

    @RequestMapping("/show_all_invalid_keywords")
    public HashMap<String, ArrayList> show_all_invalid_keywords() throws Exception {
        return service.get_all_invalid_keywords();
    }

    // SIMILAR KEYWORDS
    @RequestMapping("/show_similar_keywords")
    public List<String> get_similar_keywords() throws Exception {
        return service.get_similar_keywords(service.get_model_theme_keywords_list());
    }

    // THEME KEYWORDS
    @RequestMapping("/show_theme_keywords")
    public List<String> show_theme_keywords() throws Exception {
        return service.get_theme_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_theme_keywords")
    public List<String> get_model_theme_keywords_list() throws IOException, SAXException {
        return service.get_model_theme_keywords_list();
    }

    @RequestMapping("/show_invalid_theme_keywords")
    public List<String> show_invalid_theme_keywords() throws IOException, SAXException, XPathExpressionException {
        return service.get_invalid_theme_keywords();
    }

    // DATACENTER KEYWORDS
    @RequestMapping("/show_datacenter_keywords")
    public List<String> show_datacenter_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_datacenter_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_datacenter_keywords")
    public List<String> get_model_datacenter_keywords_list() throws IOException, SAXException {
        return service.get_model_datacenter_keywords_list();
    }

    @RequestMapping("/show_invalid_datacenter_keywords")
    public List<String> show_invalid_datacenter_keywords() throws IOException, SAXException, XPathExpressionException {
        return service.get_invalid_datacenter_keywords();
    }

    // PLACE KEYWORDS
    @RequestMapping("/show_place_keywords")
    public List<String> show_place_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_place_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_place_keywords")
    public List<String> get_model_place_keywords_list() throws IOException, SAXException {
        return service.get_model_place_keywords_list();
    }

    @RequestMapping("/show_invalid_place_keywords")
    public List<String> show_invalid_place_keywords() throws IOException, SAXException, XPathExpressionException {
        return service.get_invalid_place_keywords();
    }

    // PLATFORM KEYWORDS
    @RequestMapping("/show_platform_keywords")
    public List<String> show_platform_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_platform_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_platform_keywords")
    public List<String> get_model_platform_keywords_list() throws IOException, SAXException {
        return service.get_model_platform_keywords_list();
    }

    @RequestMapping("/show_invalid_platform_keywords")
    public List<String> show_invalid_platform_keywords() throws IOException, SAXException, XPathExpressionException {
        return service.get_invalid_platform_keywords();
    }

    // INSTRUMENT KEYWORDS
    @RequestMapping("/show_instrument_keywords")
    public List<String> show_instrument_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_instrument_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_instrument_keywords")
    public List<String> get_model_instrument_keywords_list() throws IOException, SAXException {
        return service.get_model_instrument_keywords_list();
    }

    @RequestMapping("/show_invalid_instrument_keywords")
    public List<String> show_invalid_instrument_keywords() throws IOException, SAXException, XPathExpressionException {
        return service.get_invalid_instrument_keywords();
    }

    // PROJECT KEYWORDS
    @RequestMapping("/show_project_keywords")
    public List<String> show_project_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_project_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_project_keywords")
    public List<String> get_model_project_keywords_list() throws IOException, SAXException {
        return service.get_model_project_keywords_list();
    }

    @RequestMapping("/show_invalid_project_keywords")
    public List<String> show_invalid_project_keywords() throws IOException, SAXException, XPathExpressionException {
        return service.get_invalid_project_keywords();
    }

}
