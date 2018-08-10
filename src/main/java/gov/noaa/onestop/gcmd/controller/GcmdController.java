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
import java.util.List;

@RestController
public class GcmdController {

    @Autowired
    GcmdService service;
    public Document xmlDocument;

    public GcmdController() throws IOException, SAXException {
    }

    // Format http://localhost:8080/source_xml?url=https://data.nodc.noaa
    // .gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml
    @RequestMapping(value = "source_xml", method = RequestMethod.GET)
    public URL get_url_value(@RequestParam("url") URL urlValue) throws IOException, SAXException {
        URL xmlUrl = urlValue;
        xmlDocument = service.get_xml_document(xmlUrl);
        return xmlUrl;
    }

    // THEME KEYWORDS
    @RequestMapping("/show_theme_keywords")
    public List<String> show_theme_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_theme_keywords(xmlDocument);
    }

    @RequestMapping("/show_model_theme_keywords")
    public List<String> get_model_theme_keywords_list() throws IOException, SAXException {
        return service.get_model_theme_keywords_list();
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

    // PROJECT KEYWORDS
    @RequestMapping("/show_project_keywords")
    public List<String> show_project_keywords() throws IOException, XPathExpressionException, ParserConfigurationException,
            SAXException, TransformerException {
        return service.get_project_keywords(xmlDocument);
    }

}
