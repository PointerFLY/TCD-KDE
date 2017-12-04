import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.query.Query;
import org.apache.jena.reasoner.rulesys.builtins.Print;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class QueryHub {

    private OntModel model;
    private ArrayList<String> queries;

    QueryHub() {
        model = ModelFactory.createOntologyModel();
        model.read(FileUtils.COUNTY_PATH);
        String queriesDir = getClass().getResource("Queries").getFile();

        // TODO: Initialize queries with file from resources/queries
    }

    public void executeQuery(int number) {
        // TODO: A callback for GUI
        try {
            query(model, queries.get(number));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void query(Model model, String queryString) throws IOException {
        // TODO: Unvalidated code, sample code

        // Execute the query and obtain results
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet results = queryExecution.execSelect();

        // Output query results, special fix to display correctly UTF-8 encoded characters
        // The fix create lines shorter that the other and breaks the nice formatting
        String tmp;
        tmp = ResultSetFormatter.asText(results);
        tmp = new String(tmp.getBytes("iso-8859-1"), "utf-8");
        System.out.print(tmp);

        // Important - free up resources used when running the query
        queryExecution.close();
    }
}
