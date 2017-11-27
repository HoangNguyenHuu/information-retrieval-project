package main;

import config.Configuration;
import index.IndexJsonFiles;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class CreateIndex {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        String indexPath = configuration.getProperty("indexPath");
        String jsonFilePath = configuration.getProperty("jsonFilePath");
        try{
            IndexJsonFiles ijf = new IndexJsonFiles(indexPath, jsonFilePath);
            ijf.createIndex();

            Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));

            IndexReader indexReader = DirectoryReader.open(indexDirectory);

            int numDocs = indexReader.numDocs();
            System.out.println("NumDocs: " +numDocs);

            for(int i = 0; i<numDocs; i++){
                Document document = indexReader.document(i);
                System.out.println("d="+document.get("doc_id"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
