package gov.noaa.onestop.gcmd.service;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
public class GcmdService {

    public Document xmlDocument;

    public Document get_xml_document(URL urlvalue) throws IOException, SAXException {
        InputStream input = urlvalue.openStream();

        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        xmlDocument = builder.parse(new InputSource(input));
        return xmlDocument;
    }

    // ALL KEYWORDS
    public HashMap<String, ArrayList> get_all_keywords() throws IOException, XPathExpressionException {
        HashMap allKeywords = new HashMap<String, ArrayList>();
        allKeywords.put("theme_keyword", get_theme_keywords(xmlDocument));
        allKeywords.put("datacenter_keyword", get_datacenter_keywords(xmlDocument));
        allKeywords.put("place_keyword", get_place_keywords(xmlDocument));
        allKeywords.put("platform_keyword", get_platform_keywords(xmlDocument));
        allKeywords.put("instrument_keyword", get_instrument_keywords(xmlDocument));
        allKeywords.put("project_keyword", get_project_keywords(xmlDocument));

        return allKeywords;
    }

    public HashMap<String, ArrayList> get_all_invalid_keywords() throws IOException, XPathExpressionException, SAXException {
        HashMap allInvalidKeywords = new HashMap<String, ArrayList>();
        allInvalidKeywords.put("invalid_theme_keyword", get_invalid_theme_keywords());
        allInvalidKeywords.put("invalid_datacenter_keyword", get_invalid_datacenter_keywords());
        allInvalidKeywords.put("invalid_place_keyword", get_invalid_place_keywords());
        allInvalidKeywords.put("invalid_platform_keyword", get_invalid_platform_keywords());
        allInvalidKeywords.put("invalid_instrument_keyword", get_invalid_instrument_keywords());
        allInvalidKeywords.put("invalid_project_keyword", get_invalid_project_keywords());

        return allInvalidKeywords;
    }


    // THEME KEYWORDS
    public List<String> get_theme_keywords(Document xmlDocument) throws IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='theme']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> themeKeywords = IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        return themeKeywords;
    }

    public List<String> get_model_theme_keywords_list() throws IOException, SAXException {
        URL url = new URL("https://gcmdservices.gsfc.nasa.gov/static/kms/sciencekeywords/sciencekeywords.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        CSVReader data = new CSVReader(in);
        ArrayList<String> modelThemeKeywordsList = new ArrayList<String>();
        String[] row;
        while ((row = data.readNext()) != null) {
            String keyword = row[0];
            for(int i = 1; i < 7; i++) {
                try {
                    if (!StringUtils.isBlank(row[i])) {
                        keyword = keyword + " > " + row[i];
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
            modelThemeKeywordsList.add(keyword.toUpperCase());
        }
        return modelThemeKeywordsList;
    }

    public List<String> get_invalid_theme_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> modelThemeKeywordsList = get_model_theme_keywords_list();
        List<String> themeKeywordsList = get_theme_keywords(xmlDocument);
        // check if file theme keywords are in modelThemeKeywordsList ignoring case
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        for (String keyword : themeKeywordsList) {
            if (!modelThemeKeywordsList.contains(keyword.toUpperCase())) {
                invalidKeywordsList.add(keyword);
            }
        }

        if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
            invalidKeywordsList.add("no invalid GCMD Science Keywords found");
        }

        return invalidKeywordsList;
    }

    // DATACENTER KEYWORDS
    public List<String> get_datacenter_keywords(Document xmlDocument) throws IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        // codeListValue 'dataCentre'
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='dataCentre']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> datacenterKeywords1 = IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        // codeListValue 'dataCenter'
        expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='dataCenter']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList2 = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> datacenterKeywords2 = IntStream.range(0, nodeList2.getLength())
                .mapToObj(nodeList2::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        // join both lists into one (one of them is most likely empty)
        List<String> datacenterKeywords = new ArrayList<String>();
        datacenterKeywords.addAll(datacenterKeywords1);
        datacenterKeywords.addAll(datacenterKeywords2);

        return datacenterKeywords;
    }

    public List<String> get_model_datacenter_keywords_list() throws IOException, SAXException {
        URL url = new URL("https://gcmdservices.gsfc.nasa.gov/static/kms/providers/providers.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        CSVReader data = new CSVReader(in);
        ArrayList<String> modelDatacenterKeywordsList = new ArrayList<String>();
        String[] row;
        while ((row = data.readNext()) != null) {
            String keyword = row[4];
            for(int i = 5; i < 6; i++) {
                try {
                    if (!StringUtils.isBlank(row[i])) {
                        keyword = keyword + " > " + row[i];
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
            modelDatacenterKeywordsList.add(keyword.toUpperCase());
        }
        return modelDatacenterKeywordsList;
    }

    public List<String> get_invalid_datacenter_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> modelDatacenterKeywordsList = get_model_datacenter_keywords_list();
        List<String> datacenterKeywordsList = get_datacenter_keywords(xmlDocument);
        // check if file datacenter keywords are in modelDatacenterKeywordsList ignoring case
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        for (String keyword : datacenterKeywordsList) {
            if (!modelDatacenterKeywordsList.contains(keyword.toUpperCase())) {
                invalidKeywordsList.add(keyword);
            }
        }

        if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
            invalidKeywordsList.add("no invalid GCMD Provider Keywords found");
        }

        return invalidKeywordsList;
    }

    // PLACE KEYWORDS
    public List<String> get_place_keywords(Document xmlDocument) throws IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='place']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> placeKeywords = IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        return placeKeywords;
    }

    public List<String> get_model_place_keywords_list() throws IOException, SAXException {
        URL url = new URL("https://gcmdservices.gsfc.nasa.gov/static/kms/locations/locations.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        CSVReader data = new CSVReader(in);
        ArrayList<String> modelPlaceKeywordsList = new ArrayList<String>();
        String[] row;
        while ((row = data.readNext()) != null) {
            String keyword = row[0];
            for(int i = 1; i < 5; i++) {
                try {
                    if (!StringUtils.isBlank(row[i])) {
                        keyword = keyword + " > " + row[i];
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
            modelPlaceKeywordsList.add(keyword.toUpperCase());
        }
        return modelPlaceKeywordsList;
    }

    public List<String> get_invalid_place_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> modelPlaceKeywordsList = get_model_place_keywords_list();
        List<String> placeKeywordsList = get_place_keywords(xmlDocument);
        // check if file place keywords are in modelPlaceKeywordsList ignoring case
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        for (String keyword : placeKeywordsList) {
            if (!modelPlaceKeywordsList.contains(keyword.toUpperCase())) {
                invalidKeywordsList.add(keyword);
            }
        }

        if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
            invalidKeywordsList.add("no invalid GCMD Location Keywords found");
        }

        return invalidKeywordsList;
    }

    // PLATFORM KEYWORDS
    public List<String> get_platform_keywords(Document xmlDocument) throws IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='platform']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> platformKeywords = IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        return platformKeywords;
    }

    public List<String> get_model_platform_keywords_list() throws IOException, SAXException {
        URL url = new URL("https://gcmdservices.gsfc.nasa.gov/static/kms/platforms/platforms.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        CSVReader data = new CSVReader(in);
        ArrayList<String> modelPlatformKeywordsList = new ArrayList<String>();
        String[] row;
        while ((row = data.readNext()) != null) {
            String keyword = row[2];
            for(int i = 3; i < 4; i++) {
                try {
                    if (!StringUtils.isBlank(row[i])) {
                        keyword = keyword + " > " + row[i];
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
            modelPlatformKeywordsList.add(keyword.toUpperCase());
        }
        return modelPlatformKeywordsList;
    }

    public List<String> get_invalid_platform_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> modelPlatformKeywordsList = get_model_platform_keywords_list();
        List<String> platformKeywordsList = get_platform_keywords(xmlDocument);
        // check if file platform keywords are in modelPlatformKeywordsList ignoring case
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        for (String keyword : platformKeywordsList) {
            if (!modelPlatformKeywordsList.contains(keyword.toUpperCase())) {
                invalidKeywordsList.add(keyword);
            }
        }

        if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
            invalidKeywordsList.add("no invalid GCMD Platform Keywords found");
        }

        return invalidKeywordsList;
    }

    // INSTRUMENT KEYWORDS
    public List<String> get_instrument_keywords(Document xmlDocument) throws IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='instrument']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> instrumentKeywords = IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        return instrumentKeywords;
    }

    public List<String> get_model_instrument_keywords_list() throws IOException, SAXException {
        URL url = new URL("https://gcmdservices.gsfc.nasa.gov/static/kms/instruments/instruments.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        CSVReader data = new CSVReader(in);
        ArrayList<String> modelInstrumentKeywordsList = new ArrayList<String>();
        String[] row;
        while ((row = data.readNext()) != null) {
            String keyword = row[4];
            for(int i = 5; i < 6; i++) {
                try {
                    if (!StringUtils.isBlank(row[i])) {
                        keyword = keyword + " > " + row[i];
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
            modelInstrumentKeywordsList.add(keyword.toUpperCase());
        }
        return modelInstrumentKeywordsList;
    }

    public List<String> get_invalid_instrument_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> modelInstrumentKeywordsList = get_model_instrument_keywords_list();
        List<String> instrumentKeywordsList = get_instrument_keywords(xmlDocument);
        // check if file instrument keywords are in modelInstrumentKeywordsList ignoring case
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        for (String keyword : instrumentKeywordsList) {
            if (!modelInstrumentKeywordsList.contains(keyword.toUpperCase())) {
                invalidKeywordsList.add(keyword);
            }
        }

        if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
            invalidKeywordsList.add("no invalid GCMD Instrument Keywords found");
        }

        return invalidKeywordsList;
    }

    // PROJECT KEYWORDS
    public List<String> get_project_keywords(Document xmlDocument) throws IOException, XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='project']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        List<String> projectKeywords = IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(n -> n.getTextContent().replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        return projectKeywords;
    }

    public List<String> get_model_project_keywords_list() throws IOException, SAXException {
        URL url = new URL("https://gcmdservices.gsfc.nasa.gov/static/kms/projects/projects.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        CSVReader data = new CSVReader(in);
        ArrayList<String> modelProjectKeywordsList = new ArrayList<String>();
        String[] row;
        while ((row = data.readNext()) != null) {
            String keyword = row[1];
            for(int i = 2; i < 3; i++) {
                try {
                    if (!StringUtils.isBlank(row[i])) {
                        keyword = keyword + " > " + row[i];
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
            modelProjectKeywordsList.add(keyword.toUpperCase());
        }
        return modelProjectKeywordsList;
    }

    public List<String> get_invalid_project_keywords() throws IOException, SAXException, XPathExpressionException {
        List<String> modelProjectKeywordsList = get_model_project_keywords_list();
        List<String> projectKeywordsList = get_project_keywords(xmlDocument);
        // check if file project keywords are in modelProjectKeywordsList ignoring case
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        for (String keyword : projectKeywordsList) {
            if (!modelProjectKeywordsList.contains(keyword.toUpperCase())) {
                invalidKeywordsList.add(keyword);
            }
        }

        if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
            invalidKeywordsList.add("no invalid GCMD Project Keywords found");
        }

        return invalidKeywordsList;
    }
}
