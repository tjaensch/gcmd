package gov.noaa.onestop.gcmd.service;

import com.opencsv.CSVReader;
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
            modelThemeKeywordsList.add(row[0] + " > " + row[1] + " > " + row[2] + " > " + row[3] + " > " + row[4]);
        }
        return modelThemeKeywordsList;
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
}
