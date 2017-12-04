import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.enhanced.Personality;
import org.apache.jena.ontology.*;
import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.rulesys.builtins.Print;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.apache.jena.propertytable.lang.CSV2RDF;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static void download(String url, String file) throws Exception {
        Path path = Paths.get(file);
        URI u = URI.create(url);
        try (InputStream in = u.toURL().openStream()) {
            Files.copy(in, path);
        }
    }

    public static void main(String[] args) {
        String countyURL = "http://data.geohive.ie/dumps/county/default.ttl";
        String schoolURL = "http://airo.maynoothuniversity.ie/files/dDATASTORE/education/csv/primary_schools_2013_2014.csv";

        Path countyPath = Paths.get("temp/county.ttl");
        Path schoolCSVPath = Paths.get("temp/school.csv");
        Path schoolPath = Paths.get("temp/school.ttl");
        if(!Files.exists(countyPath)) {
            try {
                Files.createDirectories(countyPath.getParent());
                download(countyURL, countyPath.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(schoolCSVPath)) {
            try {
                Files.createDirectories(schoolCSVPath.getParent());
                download(schoolURL, schoolCSVPath.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileReader in = new FileReader(schoolCSVPath.toFile());
            for (CSVRecord record : CSVFormat.DEFAULT.parse(in)) {
                System.out.println(record);
//                for (String field : record) {
//                    System.out.print("\"" + field + "\", ");
//                }
//                System.out.println();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    static void createOntology() {
        String baseURI = "http://www.cs7is1.com/assignment2/ireland-schools";
        String ns = baseURI + "#";

        OntModel ontModel = ModelFactory.createOntologyModel();

        // Coordinate Class

        OntClass coordinate = ontModel.createClass(ns + "Coordinate");

        // County Class

        OntClass county = ontModel.createClass(ns + "County");

        DatatypeProperty countyName = ontModel.createDatatypeProperty(ns + "countyName");
        countyName.setDomain(county);
        countyName.setRange(XSD.xstring);

        DatatypeProperty area = ontModel.createDatatypeProperty(ns + "area");
        area.setDomain(county);
        area.setRange(XSD.nonPositiveInteger);


        // School Class

        OntClass school = ontModel.createClass(ns + "School");
        DatatypeProperty boyCount = ontModel.createDatatypeProperty(ns + "boyCount");
        boyCount.setDomain(school);
        boyCount.setRange(XSD.nonNegativeInteger);

        DatatypeProperty girlCount = ontModel.createDatatypeProperty(ns + "girlCount");
        girlCount.setDomain(school);
        girlCount.setRange(XSD.nonNegativeInteger);

        DatatypeProperty studentCount = ontModel.createDatatypeProperty(ns + "studentCount");
        studentCount.setDomain(school);
        studentCount.setRange(XSD.positiveInteger);

        DatatypeProperty inIsland = ontModel.createDatatypeProperty(ns + "inIsland");
        inIsland.setDomain(school);
        inIsland.setRange(XSD.xboolean);

        ObjectProperty hasCoordinate = ontModel.createObjectProperty(ns + "hasCoordinate");
        hasCoordinate.setDomain(school);
        hasCoordinate.setRange(coordinate);

        DatatypeProperty rollNumber = ontModel.createDatatypeProperty(ns + "rollNumber");
        rollNumber.setDomain(school);
        rollNumber.setRange(XSD.xstring);

        DatatypeProperty name = ontModel.createDatatypeProperty(ns + "name");
        name.setDomain(school);
        name.setRange(XSD.xstring);

        ObjectProperty inCounty = ontModel.createObjectProperty(ns + "inCounty");
        inCounty.setDomain(school);
        inCounty.setRange(county);

        ObjectProperty hasSchools = ontModel.createObjectProperty(ns + "Object");
        hasSchools.setDomain(county);
        hasSchools.setRange(school);
        hasSchools.addInverseOf(inCounty);


        DatatypeProperty isDEIS = ontModel.createDatatypeProperty(ns + "isDEIS");
        isDEIS.setDomain(school);
        isDEIS.setRange(XSD.xboolean);

        DatatypeProperty isGaeltacht = ontModel.createDatatypeProperty(ns + "isGaeltacht");
        isGaeltacht.setDomain(school);
        isGaeltacht.setRange(XSD.xboolean);

        OntClass boySchool = ontModel.createClass(ns + "BoySchool");
        OntClass girlSchool = ontModel.createClass(ns + "GirlSchool");
        boySchool.addSuperClass(school);
        girlSchool.addSuperClass(school);

        DatatypeProperty address = ontModel.createDatatypeProperty(ns + "Address");
        address.setDomain(school);
        address.setRange(XSD.xstring);

        RDFList list = ontModel.createList(new RDFNode[] { boySchool, girlSchool });
        ontModel.createUnionClass(ns + "School", list);
        boySchool.addDisjointWith(girlSchool);
        girlSchool.addDisjointWith(boySchool);

        boySchool.addSuperClass(ontModel.createMaxCardinalityRestriction(null, girlCount, 0));
        girlSchool.addSuperClass(ontModel.createMaxCardinalityRestriction(null, boyCount, 0));

        school.addSubClass(boySchool);
        school.addSubClass(girlSchool);

        OntClass catholic = ontModel.createClass(ns + "Catholic");
        OntClass churchOfIreland = ontModel.createClass(ns + "ChurchOfIreland");
        OntClass multiDenominational = ontModel.createClass(ns + "MultiDenominational");

        RDFList ethosList = ontModel.createList(new RDFNode[] { catholic, churchOfIreland, multiDenominational });
        OntClass ethos = ontModel.createEnumeratedClass(ns + "ethos", ethosList);

        DatatypeProperty ethoIs = ontModel.createDatatypeProperty(ns + "ethosIs");
        ethoIs.setDomain(school);
        ethoIs.setRange(ethos);

        school.addSuperClass(ontModel.createCardinalityRestriction(null, ethoIs, 1));
        school.addSuperClass(ontModel.createMinCardinalityRestriction(null, studentCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, rollNumber, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, name, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, inCounty, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, hasCoordinate, 1));

        county.addSuperClass(ontModel.createCardinalityRestriction(null, countyName, 1));
        county.addSuperClass(ontModel.createCardinalityRestriction(null, area, 1));

        rollNumber.addDifferentFrom(rollNumber);

//        OntClass catholicSchool = ontModel.createClass(ns + "CatholicSchool");
//        catholicSchool.addSuperClass(school);
//        catholicSchool.addSuperClass(ontModel.createAllValuesFromRestriction(null, ethoIs, catholic));

        try {
            ontModel.write(new FileOutputStream(new File("temp/ireland-schools.ttl")), "TURTLE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
