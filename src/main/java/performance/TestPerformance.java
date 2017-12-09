package performance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import search.SearchEngine;

import java.io.*;

/**
 * Created by hoangnh on 27/11/2017.
 */
public class TestPerformance {
    public static void main(String[] args) throws ParseException, InvalidTokenOffsetsException, IOException {
        SearchEngine se = new SearchEngine();

        JsonParser parser = new JsonParser();
        JsonElement data = parser.parse(new FileReader("data/all.json"));

        JsonArray relevances = data.getAsJsonObject().get("relevances").getAsJsonArray();
        JsonArray queries = data.getAsJsonObject().get("queries").getAsJsonArray();


        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i).getAsJsonObject().get("query").getAsString();
            int query_id = queries.get(i).getAsJsonObject().get("id").getAsInt();
            System.out.println("Query: " + query);

            JsonArray relevance_this_query = get_relevance_array(relevances, query_id);
            int total_relevance_this_query = relevance_this_query.size();

            JsonObject results = se.searchAll(query);
            JsonArray docs = results.get("data").getAsJsonArray();

            int total_system_results = docs.size();
            int total_system_true = get_number_result_true_system(relevance_this_query, docs, query_id, total_relevance_this_query);


            System.out.println("Total relevance this query: " + total_relevance_this_query);
            System.out.println("Total system results: " + total_system_results);
            System.out.println("Total system true: " + total_system_true);

            double precision = total_system_true * 1.0 / total_system_results;
            double recall = total_system_true * 1.0 / total_relevance_this_query;

            System.out.println("Precision: " + precision + " - recall: " + recall);
            System.out.println();

        }
    }

    public static boolean checkRelevance(JsonArray relevances, int doc_id, int query_id) throws FileNotFoundException {
        for (int i = 0; i < relevances.size(); i++) {
            JsonObject relevance = relevances.get(i).getAsJsonObject();
            if (relevance.get("docid").getAsInt() == doc_id && relevance.get("queryid").getAsInt() == query_id) {
                return relevance.get("yes").getAsBoolean();
            }
        }
        return false;
    }

    public static JsonArray get_relevance_array(JsonArray relevances, int query_id) {
        JsonArray relevance_this_query = new JsonArray();
        for (int k = 0; k < relevances.size(); k++) {
            JsonObject relevance = relevances.get(k).getAsJsonObject();
            if (relevance.get("queryid").getAsInt() == query_id && relevance.get("yes").getAsBoolean()) {
                relevance_this_query.add(relevance);
            }
        }
        return relevance_this_query;
    }

    public static int get_number_result_true_system(JsonArray relevances, JsonArray docs, int query_id, int total_relevance_this_query) throws IOException {
        int total_system_true = 0;
        PrintWriter writer = new PrintWriter(new FileWriter("test/query"+query_id + ".txt"));
        for (int j = 0; j < docs.size(); j++) {
            int doc_id = Integer.parseInt(docs.get(j).getAsJsonObject().get("id").getAsString());
//                System.out.println(doc_id);
            boolean relevance = checkRelevance(relevances, doc_id, query_id);
//                System.out.println("Query id: " + query_id + " - doc id: " + doc_id + " - relevance: " + relevance);
            if (relevance) {
                total_system_true += 1;
            }

            double precision_i = total_system_true * 1.0/ (j+1);
            double recall_i = total_system_true *1.0 /total_relevance_this_query;
            writer.println((j+1) + ": " +precision_i+ " , " + recall_i);
        }

        writer.close();

        return total_system_true;
    }
}