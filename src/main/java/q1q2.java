import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

public class q1q2 {

    // path to databases stored as N-TRIPLES (nt) in gzip
    public static String cso_county_age_gender_uri = "data/data.cso.ie/CTY_age-group-gender-population.nt.gz";
    public static String cso_small_area_age_gender_uri = "data/data.cso.ie/SA_age-group-gender-population.nt";
    public static String cso_structure_age_gender_uri = "data/data.cso.ie/structure_age-group-gender-population.nt.gz";
    public static String cso_area_uri = "data/data.cso.ie/areas.nt.gz";

    private static void query(Model model, String queryString) throws IOException {
        // Execute the query and obtain results
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        // Output query results, special fix to display correctly UTF-8 encoded characters
        // The fix create lines shorter that the other and breaks the nice formatting
        String tmp;
        tmp = ResultSetFormatter.asText(results);
        tmp = new String(tmp.getBytes("iso-8859-1"), "utf-8");

        System.out.print(tmp);
        // Important - free up resources used when running the query
        qe.close();
    }

    private static Model load_models(List<String> model_uris) throws IOException {

        Model model = ModelFactory.createMemModelMaker().createDefaultModel();

        // Load all the models, add them up.
        for (String uri: model_uris){
            Model model2 = RDFDataMgr.loadModel(uri);
            model.add(model2);
        }

        return model;
    }

    public static void main(String[] args) throws IOException {

        //Query to get the list of Counties and their labels

        String queryString = ""+
                "prefix skos: <http://www.w3.org/2004/02/skos/core#>" +
                "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "" +
                "SELECT ?countyLabel ?conceptLabel ?county " +
                "WHERE { " +
                "?concept a skos:ConceptScheme . " +
                "?concept rdfs:label ?conceptLabel ." +
                "FILTER (?conceptLabel = \"Counties of Ireland\"@en)" +
                "?county skos:inScheme ?concept ." +
                "?county rdfs:label ?countyLabel ." +
                "}";

        //Query to get the total population per county
        String queryString2 = ""+
                "prefix skos: <http://www.w3.org/2004/02/skos/core#>" +
                "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "prefix qb: <http://purl.org/linked-data/cube#>" +
                "prefix sdmxdim: <http://purl.org/linked-data/sdmx/2009/dimension#>" +
                "prefix csoProp: <http://data.cso.ie/census-2011/property/>" +
                "SELECT ?countyLabel (SUM(?pop) AS ?popsum) " +
                "WHERE { " +
                "?concept a skos:ConceptScheme . " +
                "?concept rdfs:label ?conceptLabel ." +
                "FILTER (?conceptLabel = \"Counties of Ireland\"@en)" +
                "?county skos:inScheme ?concept ." +
                "?county rdfs:label ?countyLabel ." +
                "?obs a qb:Observation ." +
                "?obs sdmxdim:refArea ?county ." +
                "?obs csoProp:population ?pop" +
                "}" +
                "GROUP BY ?countyLabel";

        List<String> model_uris = new ArrayList<String>();
        model_uris.add(cso_county_age_gender_uri);
//        model_uris.add(cso_small_area_age_gender_uri);

        Model model = load_models(model_uris);

        query(model, queryString2);
    }
}
