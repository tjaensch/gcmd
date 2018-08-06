package gov.noaa.onestop.gcmd.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Component
public class GcmdService {

    public static List<String> find_xml_files() {
        List<String> results = new ArrayList<String>();

        File[] files = new File("/Users/thomasjaensch/IdeaProjects/gcmd/src/main/resources/static" + "/collection_test_files").listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getAbsolutePath());
            }
        }
        return results;
    }

    // THEME KEYWORDS
    public String get_theme_keywords(String urlValue) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, TransformerException {
        InputStream input = new URL(urlValue).openStream();

        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document xmlDocument = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            xmlDocument = builder.parse(new InputSource(input));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='MD_DataIdentification']/*[local-name()"
                + "='descriptiveKeywords']/*[local-name()='MD_Keywords'][*[local-name()='type']/*[local-name()"
                + "='MD_KeywordTypeCode'][@*[local-name() = 'codeListValue' and .='theme']]]/*[local-name()"
                + "='keyword'][../*[local-name()='thesaurusName']/*[local-name()='CI_Citation']/*[local-name()"
                + "='title']/*[contains(text(), 'GCMD')]]/*";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        DOMSource source = new DOMSource();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        for (int i = 0; i < nodeList.getLength(); ++i) {
            source.setNode(nodeList.item(i));
            transformer.transform(source, result);
        }

        return writer.toString();
    }

}


