package index;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class IndexJsonFiles {

    String indexPath;
    String jsonFilePath;

    IndexWriter indexWriter = null;

    public IndexJsonFiles(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.jsonFilePath = jsonFilePath;
    }

    public void createIndex() throws FileNotFoundException {
        JsonArray collection = parseJsonFile();
        openIndex();
        addDocument(collection);
        finish();
    }

    public boolean openIndex() {
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);


            indexWriter = new IndexWriter(dir, iwc);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addDocument(JsonArray collection) {
        System.out.println("collection docs: " + collection.size());
        for (JsonElement element : collection) {
            JsonObject object = element.getAsJsonObject();
//            System.out.println(object.get("title"));
            Document doc = new Document();
//            System.out.println("------------------------");
            for (String field : object.keySet()) {
                if (field.equals("id")) {
                    long id = object.get(field).getAsLong();
                    doc.add(new StoredField(field, id));
//                    System.out.println("id: " + id);
                } else if (field.equals("url")) {
                    Field stringField = new StringField(field, object.get(field).getAsString(), Field.Store.YES);
//                    System.out.println("1: " + field);
                    doc.add(stringField);
                } else if (field.equals("title") || field.equals("content")) {
                    Field textField = new TextField(field, object.get(field).getAsString(), Field.Store.YES);
                    doc.add(textField);
                }
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonArray parseJsonFile() throws FileNotFoundException {
//        JsonArray result = new JsonArray();
        JsonParser parser = new JsonParser();
        System.out.println(jsonFilePath);
        JsonElement data = parser.parse(new FileReader(jsonFilePath));
//        JsonObject jsonObject = data.getAsJsonObject();
//        System.out.println(jsonObject.get("authors"));
        JsonArray documents = data.getAsJsonObject().get("documents").getAsJsonArray();
//        for (int i = 0; i < collection.size(); i++) {
//            JsonObject query = collection.get(i).getAsJsonObject();
//            JsonArray sites = query.get("sites").getAsJsonArray();
//            for (int j = 0; j < sites.size(); j++) {
//                JsonObject site = sites.get(j).getAsJsonObject();
//                result.add(site);
//            }
//        }
//
//        for (int i = 0; i < result.size(); i++) {
//            System.out.println(result.get(i).getAsJsonObject().get("title"));
//        }

        return documents;
    }

    public static void main(String[] args) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        System.out.println("data/all.json");
        JsonElement data = parser.parse(new FileReader("data/all.json"));
//        JsonObject jsonObject = data.getAsJsonObject();
//        System.out.println(jsonObject.get("authors"));
        JsonArray documents = data.getAsJsonObject().get("documents").getAsJsonArray();

        for (int i = 0; i < documents.size(); i++) {
            System.out.println(documents.get(i).getAsJsonObject().get("title"));
        }
        System.out.println(documents.size());
    }
}
