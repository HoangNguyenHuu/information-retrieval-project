package performance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import search.SearchEngine;

import java.io.*;
import java.text.DecimalFormat;

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

        PrintWriter writer = new PrintWriter("evaluate/result.txt");

        int all_query_system_true = 0;
        int all_query_system = 0;
        int all_query_relevance = 0;
        double precision_sum = 0;
        double recall_sum = 0;

        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i).getAsJsonObject().get("query").getAsString();
            int query_id = queries.get(i).getAsJsonObject().get("id").getAsInt();
            System.out.println("Query: " + query);

            JsonArray relevance_this_query = get_relevance_array(relevances, query_id);
            int total_relevance_this_query = relevance_this_query.size();

            JsonObject results = se.searchAll(query);
            JsonArray docs = results.get("data").getAsJsonArray();

            int total_system_results = docs.size();
            int total_system_true = get_number_result_true_system(relevance_this_query, docs, query_id, total_relevance_this_query, query);


            System.out.println("Total relevance this query: " + total_relevance_this_query);
            System.out.println("Total system results: " + total_system_results);
            System.out.println("Total system true: " + total_system_true);

            double precision = total_system_true * 1.0 / total_system_results;
            double recall = total_system_true * 1.0 / total_relevance_this_query;
            double f1 = 0;
            if (precision != 0 && recall != 0) {
                f1 = 2 * precision * recall / (precision + recall);
            }

            writer.println("Truy vấn: " + query);
            writer.println("Số kết quả hệ thống trả về đúng: " + total_system_true);
            writer.println("Số kết quả hệ thống trả về: " + total_system_results);
            writer.println("Số kết quả chính xác theo người đánh giá (relevance): " + total_relevance_this_query);
            writer.println("Precision: " + precision);
            writer.println("Recall: " + recall);
            writer.println("F1 measure: " + f1);
            writer.println();

            all_query_system_true += total_system_true;
            all_query_system += total_system_results;
            all_query_relevance += total_relevance_this_query;
            precision_sum += precision;
            recall_sum += recall;

            System.out.println("Precision: " + precision + " - recall: " + recall);
            System.out.println();

        }

        double precision_micro = all_query_system_true * 1.0/all_query_system;
        double recall_micro = all_query_system_true * 1.0/all_query_relevance;
        double f1_micro = 0;
        if (precision_micro != 0 && recall_micro != 0) {
            f1_micro = 2 * precision_micro * recall_micro / (precision_micro + recall_micro);
        }

        double precision_macro = precision_sum/queries.size();
        double recall_macro = recall_sum/queries.size();
        double f1_macro = 0;
        if (precision_macro != 0 && recall_macro != 0) {
            f1_macro = 2 * precision_macro * recall_macro / (precision_macro + recall_macro);
        }

        writer.println("---------------------------------------------------------------------------------------");
        writer.println("Micro");
        writer.println("Precision micro: " + precision_micro);
        writer.println("Recall micro: " + recall_micro);
        writer.println("F1 micro: " + f1_micro);
        writer.println();

        writer.println("Macro");
        writer.println("Precision macro: " + precision_macro);
        writer.println("Recall marco: " + recall_macro);
        writer.println("F1 macro: " + f1_macro);

        writer.close();
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

    public static int get_number_result_true_system(JsonArray relevances, JsonArray docs, int query_id, int total_relevance_this_query, String query) throws IOException {
        int total_system_true = 0;
        PrintWriter writer = new PrintWriter(new FileWriter("evaluate/query" + query_id + ".txt"));
        writer.println("Query: " + query);
        writer.println();

        for (int j = 0; j < docs.size(); j++) {
            int doc_id = Integer.parseInt(docs.get(j).getAsJsonObject().get("id").getAsString());
//                System.out.println(doc_id);
            boolean relevance = checkRelevance(relevances, doc_id, query_id);
//                System.out.println("Query id: " + query_id + " - doc id: " + doc_id + " - relevance: " + relevance);
            if (relevance) {
                total_system_true += 1;
            }

            double precision_i = total_system_true * 1.0 / (j + 1);
            double recall_i = total_system_true * 1.0 / total_relevance_this_query;
            double f1 = 0;
            if (precision_i != 0 && recall_i != 0) {
                f1 = 2 * precision_i * recall_i / (precision_i + recall_i);
            }
            writer.println("Số kết quả trả về: " + String.format("%4d", j + 1) + " - precision: " + String.format("%.3f", precision_i) + " - recall: " + String.format("%.3f", recall_i) + " - f1 measure:  " + String.format("%.3f", f1));
            if (j == docs.size() - 1) {
                writer.println();
                writer.println("Result");
                writer.println("Precision: " + precision_i);
                writer.println("Recall: " + recall_i);
                writer.println("F1 measure: ");
            }
        }


        writer.close();

        return total_system_true;
    }
}