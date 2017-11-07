package main;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class Search {
    public static void main(String[] args) throws IOException {
        String INDEX_PATH = "index";

        String field = "content";

        Directory indexDirectory = FSDirectory.open(Paths.get(INDEX_PATH));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new StandardAnalyzer();

        Term t = new Term("content", "sách");

        QueryParser parser = new QueryParser(field, analyzer);
        Query query = new TermQuery(t);
        TopDocs topDocs = indexSearcher.search(query, 10);

        ScoreDoc[] hits = topDocs.scoreDocs;
//        assertEquals(1, topDocs.totalHits);
        System.out.println(topDocs.totalHits);

        int number = 10;
        if (hits.length < 10) {
            number = hits.length;
        }
        for (int i = 0; i < number; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            System.out.println(doc.get("title"));
        }

    }
}
