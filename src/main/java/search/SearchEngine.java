package search;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class SearchEngine {

    public JsonObject searchInformation(String queryStr, int page) throws IOException, ParseException, InvalidTokenOffsetsException {

        JsonObject totalObj = new JsonObject();
        if (page <= 0) {
            throw new NumberFormatException();
        }
        JsonArray data = new JsonArray();
        if (queryStr.trim().equals("")) {
            return totalObj;
        }

        Configuration configuration = new Configuration();
        String index = configuration.getProperty("indexPath");
        String field = configuration.getProperty("field");
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);

        TopDocs topDocs = searcher.search(query, 1000);
        ScoreDoc[] hits = topDocs.scoreDocs;
        int quantity = Math.toIntExact(topDocs.totalHits);

        int hitsPerPage = 10;
        int start = 0;

        if ((page - 1) * hitsPerPage < quantity) {
            start = (page - 1) * hitsPerPage;
        } else {
            totalObj.addProperty("quantity", quantity);
            totalObj.addProperty("error", "no such page");
            return totalObj;
        }

        int end = Math.min(quantity, start + hitsPerPage);

        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

//        int numHit = hits.length;
        for (int i = start; i < end; i++) {
            int id = hits[i].doc;
            Document doc = searcher.doc(id);
            JsonObject object = new JsonObject();
            object.addProperty("url", doc.get("url"));
            object.addProperty("title", doc.get("title"));

            String text = doc.get("content");
            TokenStream tokenStream = TokenSources.getTokenStream("default", text, analyzer);
            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 4);
            StringBuilder content = new StringBuilder();
            for (int j = 0; j < frag.length; j++) {
                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
                    content.append(frag[j].toString());
                }
            }
            object.addProperty("snippet", content.toString());
            //object.addProperty("content", doc.get("content"));
            //object.addProperty("relevance", doc.get("relevance"));
            data.add(object);
        }

        totalObj.addProperty("quantity", quantity);
        totalObj.add("data", data);
        return totalObj;
    }

    public JsonObject searchAll(String queryStr) throws IOException, InvalidTokenOffsetsException, ParseException {
        JsonObject totalObj = new JsonObject();

        JsonArray data = new JsonArray();
        if (queryStr.trim().equals("")) {
            return totalObj;
        }

        Configuration configuration = new Configuration();
        String index = configuration.getProperty("indexPath");
        String field = configuration.getProperty("field");
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);

        TopDocs topDocs = searcher.search(query, 10000);
        ScoreDoc[] hits = topDocs.scoreDocs;
        int quantity = Math.toIntExact(topDocs.totalHits);


        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

        int numHit = hits.length;
        for (int i = 0; i < numHit; i++) {
            int id = hits[i].doc;
            Document doc = searcher.doc(id);
            JsonObject object = new JsonObject();
            object.addProperty("url", doc.get("url"));
            object.addProperty("title", doc.get("title"));
            object.addProperty("id", doc.get("id"));

            String text = doc.get("content");
            TokenStream tokenStream = TokenSources.getTokenStream("default", text, analyzer);
            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 4);
            StringBuilder content = new StringBuilder();
            for (int j = 0; j < frag.length; j++) {
                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
                    content.append(frag[j].toString());
                }
            }
            object.addProperty("snippet", content.toString());
            //object.addProperty("content", doc.get("content"));
            //object.addProperty("relevance", doc.get("relevance"));
            data.add(object);
        }

        totalObj.addProperty("quantity", quantity);
        totalObj.add("data", data);
        return totalObj;
    }
}
