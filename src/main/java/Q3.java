import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

public class Q3 {
	
	// path to databases stored as N-TRIPLES (nt) in gzip
	public static String cso_county_age_gender_uri = "data/data.cso.ie/CTY_age-group-gender-population.nt.gz";
	public static String cso_small_area_age_gender_uri = "data/data.cso.ie/SA_age-group-gender-population.nt.gz";
	public static String cso_structure_age_gender_uri = "data/data.cso.ie/structure_age-group-gender-population.nt.gz";
	public static String cso_county_nationality_uri = "data/data.cso.ie/CTY_population-by-nationality.nt";
	public static String cso_area_uri = "data/data.cso.ie/areas.nt";
	public static String cso_nationality_uri = "data/data.cso.ie/nationality.nt";
	
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
		
		// Immigrant population county wise
		String q3Query = ""
				+ "PREFIX csoClassNat: <http://data.cso.ie/census-2011/classification/nationality/> "
				+ "PREFIX csoClassCty: <http://data.cso.ie/census-2011/classification/CTY/> "
				+ "PREFIX csoProp: <http://data.cso.ie/census-2011/property/> "
				+ "PREFIX purlDim: <http://purl.org/linked-data/sdmx/2009/dimension#> "
				+ "PREFIX xmls: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT (STR(?countyLab) AS ?countyLabel) (SUM(xmls:integer(?pop)) AS ?immigrantPopulation) "
				+ "WHERE { "
				+ "		?id csoProp:nationality ?nat . "
				+ "		?id purlDim:refArea ?county . "
				+ "		?id csoProp:usually-resident-population ?pop . "
				+ "		?county rdfs:label ?countyLab . "
				+ "		?nat rdfs:label ?natLabel . "
				+ "		FILTER (?natLabel != \"Irish\" && ?natLabel != \"all\") "
				+ "} "
				+ "GROUP BY ?countyLab";
				
		List<String> model_uris = new ArrayList<String>();
		model_uris.add(cso_county_nationality_uri);
		model_uris.add(cso_area_uri);
		model_uris.add(cso_nationality_uri);
		
		Model model = load_models(model_uris);
				
		query(model, q3Query);
	}
}
