package gov.noaa.onestop.gcmd.data;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GcmdData {
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
}
