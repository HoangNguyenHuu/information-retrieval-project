package performance;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import search.SearchEngine;

import java.io.IOException;

/**
 * Created by hoangnh on 27/11/2017.
 */
public class Test {
    public static void main(String[] args) throws ParseException, InvalidTokenOffsetsException, IOException {
        SearchEngine se = new SearchEngine();
        JsonObject results = se.searchAll("giai ngo");
        JsonArray docs = results.get("data").getAsJsonArray();
        for (int i =0; i< docs.size(); i++){
            System.out.println(docs.get(i).getAsJsonObject().get("doc_id"));
            System.out.println(docs.get(i).getAsJsonObject().get("title"));
            System.out.println();
        }
    }
}
