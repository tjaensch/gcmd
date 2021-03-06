package gov.noaa.onestop.gcmd.service;

import com.opencsv.CSVReader;
import gov.noaa.onestop.gcmd.data.GcmdData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
public class GcmdService {

    public List modelThemeKeywordsList = get_model_theme_keywords_list();
    public List modelDatacenterKeywordsList = get_model_datacenter_keywords_list();
    public List modelPlaceKeywordsList = get_model_place_keywords_list();
    public List modelPlatformKeywordsList = get_model_platform_keywords_list();
    public List modelInstrumentKeywordsList = get_model_instrument_keywords_list();
    public List modelProjectKeywordsList = get_model_project_keywords_list();

    public GcmdService() throws IOException, SAXException {
    }

    // ALL KEYWORDS
    public HashMap<String, ArrayList> get_all_keywords() throws IOException, XPathExpressionException {
        HashMap allKeywords = new HashMap<String, ArrayList>();
        allKeywords.put("theme_keywords", get_theme_keywords(GcmdData.xmlDocument));
        allKeywords.put("datacenter_keywords", get_datacenter_keywords(GcmdData.xmlDocument));
        allKeywords.put("place_keywords", get_place_keywords(GcmdData.xmlDocument));
        allKeywords.put("platform_keywords", get_platform_keywords(GcmdData.xmlDocument));
        allKeywords.put("instrument_keywords", get_instrument_keywords(GcmdData.xmlDocument));
        allKeywords.put("project_keywords", get_project_keywords(GcmdData.xmlDocument));

        return allKeywords;
    }

    public HashMap<String, ArrayList> get_all_invalid_keywords() throws IOException, XPathExpressionException, SAXException {
        HashMap allInvalidKeywords = new HashMap<String, ArrayList>();
        allInvalidKeywords.put("invalid_theme_keywords", get_invalid_theme_keywords());
        allInvalidKeywords.put("invalid_datacenter_keywords", get_invalid_datacenter_keywords());
        allInvalidKeywords.put("invalid_place_keywords", get_invalid_place_keywords());
        allInvalidKeywords.put("invalid_platform_keywords", get_invalid_platform_keywords());
        allInvalidKeywords.put("invalid_instrument_keywords", get_invalid_instrument_keywords());
        allInvalidKeywords.put("invalid_project_keywords", get_invalid_project_keywords());

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
                .map(n -> n.getTextContent().replace("\t", " ").replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        if (themeKeywords != null && themeKeywords.isEmpty()) {
            themeKeywords.add("no GCMD Science Keywords found");
        }

        return themeKeywords;
    }

    @Scheduled(cron = "*/5 * * * * ?")
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
        List<String> themeKeywordsList = get_theme_keywords(GcmdData.xmlDocument);
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        if (themeKeywordsList.contains("no GCMD Theme keywords found")) {
            invalidKeywordsList.add("no invalid GCMD Theme Keywords found");
        } else {
            // check if file theme keywords are in modelThemeKeywordsList ignoring case
            for (String keyword : themeKeywordsList) {
                if (!modelThemeKeywordsList.contains(keyword.toUpperCase())) {
                    invalidKeywordsList.add(keyword);
                }
            }

            if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
                invalidKeywordsList.add("no invalid GCMD Theme Keywords found");
            }
        }
        return invalidKeywordsList;
    }

    public HashMap<String, ArrayList> get_suggestions_for_invalid_theme_keywords() throws XPathExpressionException, SAXException, IOException {
        HashMap suggestionsForInvalidThemeKeywords = new HashMap<String, ArrayList>();
        if (get_invalid_theme_keywords().get(0).contains("no invalid") || get_invalid_theme_keywords().get(0).contains("found")) {
            suggestionsForInvalidThemeKeywords.put("GCMD Theme keyword suggestions", "N/A");
        } else {
            for (String keyword : get_invalid_theme_keywords()) {
                suggestionsForInvalidThemeKeywords.put("invalid GCMD Theme keyword: " + keyword, "suggestions by best cosine similarity: " + GcmdData.get_similar_keywords_cosine_similarity_method(get_model_theme_keywords_list(), keyword));
            }
        }
        return suggestionsForInvalidThemeKeywords;
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
                .map(n -> n.getTextContent().replace("\t", " ").replace("\n", "").trim().replaceAll(" +", " "))
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

        if (datacenterKeywords != null && datacenterKeywords.isEmpty()) {
            datacenterKeywords.add("no GCMD Provider keywords found");
        }

        return datacenterKeywords;
    }

    @Scheduled(cron = "*/5 * * * * ?")
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
        List<String> datacenterKeywordsList = get_datacenter_keywords(GcmdData.xmlDocument);
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        if (datacenterKeywordsList.contains("no GCMD Datacenter keywords found")) {
            invalidKeywordsList.add("no invalid GCMD Datacenter Keywords found");
        } else {
            // check if file datacenter keywords are in modelDatacenterKeywordsList ignoring case
            for (String keyword : datacenterKeywordsList) {
                if (!modelDatacenterKeywordsList.contains(keyword.toUpperCase())) {
                    invalidKeywordsList.add(keyword);
                }
            }

            if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
                invalidKeywordsList.add("no invalid GCMD Datacenter Keywords found");
            }
        }
        return invalidKeywordsList;
    }

    public HashMap<String, ArrayList> get_suggestions_for_invalid_datacenter_keywords() throws XPathExpressionException, SAXException, IOException {
        HashMap suggestionsForInvalidDatacenterKeywords = new HashMap<String, ArrayList>();
        if (get_invalid_datacenter_keywords().get(0).contains("no invalid") || get_invalid_datacenter_keywords().get(0).contains("found")) {
            suggestionsForInvalidDatacenterKeywords.put("GCMD Datacenter keyword suggestions", "N/A");
        } else {
            for (String keyword : get_invalid_datacenter_keywords()) {
                suggestionsForInvalidDatacenterKeywords.put("invalid GCMD Datacenter keyword: " + keyword, "suggestions by best cosine similarity: " + GcmdData.get_similar_keywords_cosine_similarity_method(get_model_datacenter_keywords_list(), keyword));
            }
        }
        return suggestionsForInvalidDatacenterKeywords;
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
                .map(n -> n.getTextContent().replace("\t", " ").replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        if (placeKeywords != null && placeKeywords.isEmpty()) {
            placeKeywords.add("no GCMD Location keywords found");
        }

        return placeKeywords;
    }

    @Scheduled(cron = "*/5 * * * * ?")
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
        List<String> placeKeywordsList = get_place_keywords(GcmdData.xmlDocument);
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        if (placeKeywordsList.contains("no GCMD Place keywords found")) {
            invalidKeywordsList.add("no invalid GCMD Place Keywords found");
        } else {
            // check if file place keywords are in modelPlaceKeywordsList ignoring case
            for (String keyword : placeKeywordsList) {
                if (!modelPlaceKeywordsList.contains(keyword.toUpperCase())) {
                    invalidKeywordsList.add(keyword);
                }
            }

            if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
                invalidKeywordsList.add("no invalid GCMD Place Keywords found");
            }
        }
        return invalidKeywordsList;
    }

    public HashMap<String, ArrayList> get_suggestions_for_invalid_place_keywords() throws XPathExpressionException, SAXException, IOException {
        HashMap suggestionsForInvalidPlaceKeywords = new HashMap<String, ArrayList>();
        if (get_invalid_place_keywords().get(0).contains("no invalid") || get_invalid_place_keywords().get(0).contains("found")) {
            suggestionsForInvalidPlaceKeywords.put("GCMD Place keyword suggestions", "N/A");
        } else {
            for (String keyword : get_invalid_place_keywords()) {
                suggestionsForInvalidPlaceKeywords.put("invalid GCMD Place keyword: " + keyword, "suggestions by best cosine similarity: " + GcmdData.get_similar_keywords_cosine_similarity_method(get_model_place_keywords_list(), keyword));
            }
        }
        return suggestionsForInvalidPlaceKeywords;
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
                .map(n -> n.getTextContent().replace("\t", " ").replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        if (platformKeywords != null && platformKeywords.isEmpty()) {
            platformKeywords.add("no GCMD Platform keywords found");
        }

        return platformKeywords;
    }

    @Scheduled(cron = "*/5 * * * * ?")
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
        List<String> platformKeywordsList = get_platform_keywords(GcmdData.xmlDocument);
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        if (platformKeywordsList.contains("no GCMD Platform keywords found")) {
            invalidKeywordsList.add("no invalid GCMD Platform Keywords found");
        } else {
            // check if file platform keywords are in modelPlatformKeywordsList ignoring case
            for (String keyword : platformKeywordsList) {
                if (!modelPlatformKeywordsList.contains(keyword.toUpperCase())) {
                    invalidKeywordsList.add(keyword);
                }
            }

            if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
                invalidKeywordsList.add("no invalid GCMD Platform Keywords found");
            }
        }
        return invalidKeywordsList;
    }

    public HashMap<String, ArrayList> get_suggestions_for_invalid_platform_keywords() throws XPathExpressionException, SAXException, IOException {
        HashMap suggestionsForInvalidPlatformKeywords = new HashMap<String, ArrayList>();
        if (get_invalid_platform_keywords().get(0).contains("no invalid") || get_invalid_platform_keywords().get(0).contains("found")) {
            suggestionsForInvalidPlatformKeywords.put("GCMD Platform keyword suggestions", "N/A");
        } else {
            for (String keyword : get_invalid_platform_keywords()) {
                suggestionsForInvalidPlatformKeywords.put("invalid GCMD Platform keyword: " + keyword, "suggestions by best cosine similarity: " + GcmdData.get_similar_keywords_cosine_similarity_method(get_model_platform_keywords_list(), keyword));
            }
        }
        return suggestionsForInvalidPlatformKeywords;
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
                .map(n -> n.getTextContent().replace("\t", " ").replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        if (instrumentKeywords != null && instrumentKeywords.isEmpty()) {
            instrumentKeywords.add("no GCMD Instrument keywords found");
        }

        return instrumentKeywords;
    }

    @Scheduled(cron = "*/5 * * * * ?")
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
        List<String> instrumentKeywordsList = get_instrument_keywords(GcmdData.xmlDocument);
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        if (instrumentKeywordsList.contains("no GCMD Instrument keywords found")) {
            invalidKeywordsList.add("no invalid GCMD Instrument Keywords found");
        } else {
            // check if file instrument keywords are in modelInstrumentKeywordsList ignoring case
            for (String keyword : instrumentKeywordsList) {
                if (!modelInstrumentKeywordsList.contains(keyword.toUpperCase())) {
                    invalidKeywordsList.add(keyword);
                }
            }

            if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
                invalidKeywordsList.add("no invalid GCMD Instrument Keywords found");
            }
        }
        return invalidKeywordsList;
    }

    public HashMap<String, ArrayList> get_suggestions_for_invalid_instrument_keywords() throws XPathExpressionException, SAXException, IOException {
        HashMap suggestionsForInvalidInstrumentKeywords = new HashMap<String, ArrayList>();
        if (get_invalid_instrument_keywords().get(0).contains("no invalid") || get_invalid_instrument_keywords().get(0).contains("found")) {
            suggestionsForInvalidInstrumentKeywords.put("GCMD Instrument keyword suggestions", "N/A");
        } else {
            for (String keyword : get_invalid_instrument_keywords()) {
                suggestionsForInvalidInstrumentKeywords.put("invalid GCMD Instrument keyword: " + keyword, "suggestions by best cosine similarity: " + GcmdData.get_similar_keywords_cosine_similarity_method(get_model_instrument_keywords_list(), keyword));
            }
        }
        return suggestionsForInvalidInstrumentKeywords;
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
                .map(n -> n.getTextContent().replace("\t", " ").replace("\n", "").trim().replaceAll(" +", " "))
                .collect(Collectors.toList());

        if (projectKeywords != null && projectKeywords.isEmpty()) {
            projectKeywords.add("no GCMD Project keywords found");
        }

        return projectKeywords;
    }

    @Scheduled(cron = "*/5 * * * * ?")
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
        List<String> projectKeywordsList = get_project_keywords(GcmdData.xmlDocument);
        ArrayList<String> invalidKeywordsList = new ArrayList<String>();
        if (projectKeywordsList.contains("no GCMD Project keywords found")) {
            invalidKeywordsList.add("no invalid GCMD Project Keywords found");
        } else {
            // check if file project keywords are in modelProjectKeywordsList ignoring case
            for (String keyword : projectKeywordsList) {
                if (!modelProjectKeywordsList.contains(keyword.toUpperCase())) {
                    invalidKeywordsList.add(keyword);
                }
            }

            if (invalidKeywordsList != null && invalidKeywordsList.isEmpty()) {
                invalidKeywordsList.add("no invalid GCMD Project Keywords found");
            }
        }
        return invalidKeywordsList;
    }

    public HashMap<String, ArrayList> get_suggestions_for_invalid_project_keywords() throws XPathExpressionException, SAXException, IOException {
        HashMap suggestionsForInvalidProjectKeywords = new HashMap<String, ArrayList>();
        if (get_invalid_project_keywords().get(0).contains("no invalid") || get_invalid_project_keywords().get(0).contains("found")) {
            suggestionsForInvalidProjectKeywords.put("GCMD Project keyword suggestions", "N/A");
        } else {
            for (String keyword : get_invalid_project_keywords()) {
                suggestionsForInvalidProjectKeywords.put("invalid GCMD Project keyword: " + keyword, "suggestions by best cosine similarity: " + GcmdData.get_similar_keywords_cosine_similarity_method(get_model_project_keywords_list(), keyword));
            }
        }
        return suggestionsForInvalidProjectKeywords;
    }
}
