import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.query.Query;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class QueryHub {

    private OntModel model;
    private String[] queries;

    QueryHub() {
        model = ModelFactory.createOntologyModel();
        model.read(FileUtils.ONTOLOGY_PATH);

        try {
            File dir = new File(ClassLoader.getSystemResources("sparqls").nextElement().getFile());
            File[] files = dir.listFiles();
            Arrays.sort(files);

            queries = new String[files.length];

            for (int i = 0; i < files.length; i++) {
                Path path = Paths.get(files[i].getPath());
                byte[] bytes = Files.readAllBytes(path);
                String queryTxt = new String(bytes);
                queries[i] = queryTxt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String executeQuery(int idx) {
        String questionTxt = queries[idx];
        String resultTxt = queryWithTxt(questionTxt);

        return resultTxt;
    }

    private String queryWithTxt(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet results = queryExecution.execSelect();

        String resultTxt = ResultSetFormatter.asText(results);
        queryExecution.close();

        return resultTxt;
    }
}
