import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        FileUtils.fetchData();
        OntCreator.createOntology();
        OntCreator.createInstance();

        QueryWindow window = new QueryWindow();
        window.launch();

//        try {
//            FileReader in = new FileReader(schoolCSVPath.toFile());
//            for (CSVRecord record : CSVFormat.DEFAULT.parse(in)) {
//                System.out.println(record);
//                for (String field : record) {
//                    System.out.print("\"" + field + "\", ");
//                }
//                System.out.println();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        CSV2RDF.init() ;
//        Model model = ModelFactory.createDefaultModel();
//        model.read(schoolCSVPath.toString());
//        try {
//            model.write(new FileOutputStream(new File(schoolPath.toString())), "TURTLE");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        createOntology();
    }

    private static void createOntology() {



    }
}
