package gov.noaa.onestop.gcmd.data;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GcmdData {

    public static Document xmlDocument;

    public static Document get_xml_document(GcmdService gcmdService, URL urlvalue) throws IOException, SAXException {
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

    // SIMILAR KEYWORDS
    public static Map get_similar_keywords_cosine_similarity_method(List<String> modelKeywordsList, String keyword) {
        HashMap similarKeywords = new HashMap();
        CosineSimilarity dist = new CosineSimilarity();

        Map<CharSequence, Integer> leftVector =
                Arrays.stream(keyword.toLowerCase().split(""))
                        .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));

        for (String modelKeyword : modelKeywordsList) {
            Map<CharSequence, Integer> rightVector =
                    Arrays.stream(modelKeyword.toLowerCase().split(""))
                            .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));

            similarKeywords.put(dist.cosineSimilarity(leftVector,rightVector), modelKeyword);
        }
        // TreeMap to sort HashMap ascending
        TreeMap sorted = new TreeMap();
        sorted.putAll(similarKeywords);

        // Two corresponding lists
        ArrayList cosineDistances = new ArrayList();
        ArrayList matchingKeywords = new ArrayList();
        sorted.forEach((key, value) -> {
            cosineDistances.add(key);
            matchingKeywords.add(value);
        });

        // Reverse lists to get best matches first
        Collections.reverse(cosineDistances);
        Collections.reverse(matchingKeywords);

        // Add best similar keywords and cosine similarity distances to HashMap
        Map bestSimilarKeywords = new HashMap();
        for (int i = 0; i < 10; i++)
            bestSimilarKeywords.put(cosineDistances.get(i), matchingKeywords.get(i));

        // Reverse order to get matches with highest cosine similarity first
        Map<String, Integer> bestSimilarKeywordsReverseOrder = new TreeMap(Collections.reverseOrder());
        bestSimilarKeywordsReverseOrder.putAll(bestSimilarKeywords);

        return bestSimilarKeywordsReverseOrder;
    }

    public static List<String> get_similar_keywords_string_method(List<String> modelKeywordsList, String keyword) throws IOException, SAXException {
        // get last segment after " > " of keyword if exists
        String[] segments = keyword.split(" > ");
        String lastSegment = segments[segments.length-1];
        List<String> similarKeywordsList = modelKeywordsList.stream()
                .filter(str -> str.contains(lastSegment.toUpperCase()))
                .collect(Collectors.toList());
        // in no matches with the above method try first half of lastSegment of keyword string
        if (similarKeywordsList == null || similarKeywordsList.isEmpty()) {
            String keywordSubstring = lastSegment.substring(0, lastSegment.length()/2);
            similarKeywordsList = modelKeywordsList.stream()
                    .filter(str -> str.contains(keywordSubstring.toUpperCase()))
                    .collect(Collectors.toList());
        }
        // in no matches with the above method try first half of keyword string
        if (similarKeywordsList == null || similarKeywordsList.isEmpty()) {
            String keywordSubstring = keyword.substring(0, keyword.length()/2);
            similarKeywordsList = modelKeywordsList.stream()
                    .filter(str -> str.contains(keywordSubstring.toUpperCase()))
                    .collect(Collectors.toList());
        }
        // if no matches with the above method try first third of keyword string
        if (similarKeywordsList == null || similarKeywordsList.isEmpty()) {
            String keywordSubstring = keyword.substring(0, keyword.length()/3);
            similarKeywordsList = modelKeywordsList.stream()
                    .filter(str -> str.contains(keywordSubstring.toUpperCase()))
                    .collect(Collectors.toList());
        }
        // if no matches with the above method try latter fractions of keyword string
        if (similarKeywordsList == null || similarKeywordsList.isEmpty()) {
            String keywordSubstring = keyword.substring(keyword.length()*13/15, keyword.length());
            similarKeywordsList = modelKeywordsList.stream()
                    .filter(str -> str.contains(keywordSubstring.toUpperCase()))
                    .collect(Collectors.toList());
        }
        Collections.sort(similarKeywordsList);
        // limit max size of similar keywords to 10
        similarKeywordsList = similarKeywordsList.stream().limit(10).collect(Collectors.toList());
        return similarKeywordsList;
    }
}
