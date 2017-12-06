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
        model.read(FileUtils.COUNTY_PATH);

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
        // TODO: Unvalidated code, sample code

        // Execute the query and obtain results
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet results = queryExecution.execSelect();

        // Output query results, special fix to display correctly UTF-8 encoded characters
        // The fix create lines shorter that the other and breaks the nice formatting
        String resultTxt = ResultSetFormatter.asText(results);

        try {
            resultTxt = new String(resultTxt.getBytes("iso-8859-1"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Important - free up resources used when running the query
            queryExecution.close();
        }

        return resultTxt;
    }
}
