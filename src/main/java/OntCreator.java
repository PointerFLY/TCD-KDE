import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.XSD;

import java.io.*;
import java.util.Iterator;

public class OntCreator {

    private final static String BASE_URI = "http://www.cs7is1.com/assignment2/ireland-schools";
    private final static String NAMESPACE = BASE_URI +"#";

    public static void createOntology() {
        OntModel ontModel = ModelFactory.createOntologyModel();

        // Coordinate Class

        OntClass geoLocation = ontModel.createClass(NAMESPACE + "GeoLocation");

        DatatypeProperty latitude = ontModel.createDatatypeProperty(NAMESPACE + "latitude");
        DatatypeProperty longitude = ontModel.createDatatypeProperty(NAMESPACE + "longitude");
        latitude.setDomain(geoLocation);
        latitude.setRange(XSD.decimal);
        longitude.setDomain(geoLocation);
        longitude.setRange(XSD.decimal);

        geoLocation.addSuperClass(ontModel.createCardinalityRestriction(null, latitude, 1));
        geoLocation.addSuperClass(ontModel.createCardinalityRestriction(null, longitude, 1));


        /****************** County Class *******************/

        OntClass county = ontModel.createClass(NAMESPACE + "County");

        // RDFS: label for name

        DatatypeProperty area = ontModel.createDatatypeProperty(NAMESPACE + "area");
        area.setDomain(county);
        area.setRange(XSD.nonPositiveInteger);

        SymmetricProperty adjacentTo = ontModel.createSymmetricProperty(NAMESPACE + "adjacentTo");
        adjacentTo.setDomain(county);
        adjacentTo.setRange(county);

        TransitiveProperty biggerThan = ontModel.createTransitiveProperty(NAMESPACE + "biggerThan");
        biggerThan.setDomain(county);
        biggerThan.setRange(county);

        county.addSuperClass(ontModel.createCardinalityRestriction(null, area, 1));

        /****************** Ethos Class *********************/

        OntClass catholic = ontModel.createClass(NAMESPACE + "Catholic");
        OntClass churchOfIreland = ontModel.createClass(NAMESPACE + "ChurchOfIreland");
        OntClass multiDenominational = ontModel.createClass(NAMESPACE + "MultiDenominational");

        RDFList ethosList = ontModel.createList(new RDFNode[]{catholic, churchOfIreland, multiDenominational});
        OntClass ethos = ontModel.createEnumeratedClass(NAMESPACE + "Ethos", ethosList);

        catholic.addSuperClass(ethos);
        churchOfIreland.addSuperClass(ethos);
        multiDenominational.addSuperClass(ethos);

        /*********** subclasses of School **********/

        OntClass boySchool = ontModel.createClass(NAMESPACE + "BoySchool");
        OntClass girlSchool = ontModel.createClass(NAMESPACE + "GirlSchool");
        OntClass mixedSchool = ontModel.createClass(NAMESPACE + "MixedSchool");

        RDFList list = ontModel.createList(new RDFNode[]{boySchool, girlSchool, mixedSchool});
        boySchool.addDisjointWith(girlSchool);
        mixedSchool.addDisjointWith(boySchool);
        mixedSchool.addDisjointWith(girlSchool);

        /***************** School Class *******************/

        OntClass school = ontModel.createUnionClass(NAMESPACE + "School", list);

        // RDFS:label for name

        DatatypeProperty address = ontModel.createDatatypeProperty(NAMESPACE + "address");
        address.setDomain(school);
        address.setRange(XSD.xstring);

        DatatypeProperty boyCount = ontModel.createDatatypeProperty(NAMESPACE + "boyCount");
        boyCount.setDomain(school);
        boyCount.setRange(XSD.nonNegativeInteger);

        DatatypeProperty girlCount = ontModel.createDatatypeProperty(NAMESPACE + "girlCount");
        girlCount.setDomain(school);
        girlCount.setRange(XSD.nonNegativeInteger);

        DatatypeProperty studentCount = ontModel.createDatatypeProperty(NAMESPACE + "studentCount");
        studentCount.setDomain(school);
        studentCount.setRange(XSD.positiveInteger);

        DatatypeProperty inIsland = ontModel.createDatatypeProperty(NAMESPACE + "inIsland");
        inIsland.setDomain(school);
        inIsland.setRange(XSD.xboolean);

        ObjectProperty location = ontModel.createObjectProperty(NAMESPACE + "location");
        location.setDomain(school);
        location.setRange(geoLocation);

        ObjectProperty inCounty = ontModel.createObjectProperty(NAMESPACE + "inCounty");
        inCounty.setDomain(school);
        inCounty.setRange(county);

        DatatypeProperty isDEIS = ontModel.createDatatypeProperty(NAMESPACE + "isDEIS");
        isDEIS.setDomain(school);
        isDEIS.setRange(XSD.xboolean);

        DatatypeProperty isGaeltacht = ontModel.createDatatypeProperty(NAMESPACE + "isGaeltacht");
        isGaeltacht.setDomain(school);
        isGaeltacht.setRange(XSD.xboolean);

        DatatypeProperty withEthos = ontModel.createDatatypeProperty(NAMESPACE + "withEthos");
        withEthos.setDomain(school);
        withEthos.setRange(ethos);

        // Subclasses setup

        boySchool.addSuperClass(school);
        girlSchool.addSuperClass(school);
        mixedSchool.addSuperClass(school);

        OntClass catholicSchool = ontModel.createClass(NAMESPACE + "CatholicSchool");
        catholicSchool.addSuperClass(school);
        catholicSchool.addSuperClass(ontModel.createHasValueRestriction(null, withEthos, catholic));

        Literal zero = ontModel.createTypedLiteral(0);
        boySchool.addSuperClass(ontModel.createHasValueRestriction(null, girlCount, zero));
        girlSchool.addSuperClass(ontModel.createHasValueRestriction(null, girlCount, zero));

        // County property

        ObjectProperty hasSchools = ontModel.createObjectProperty(NAMESPACE + "hasSchools");
        hasSchools.setDomain(county);
        hasSchools.setRange(school);
        hasSchools.addInverseOf(inCounty);


        /************** School Cardinality Restrictions **************/

        school.addSuperClass(ontModel.createCardinalityRestriction(null, rollNumber, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, address, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, boyCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, girlCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, studentCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, inIsland, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, hasGeoLocation, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, inCounty, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, withEthos, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, isDEIS, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, isGaeltacht, 1));


        try {
            ontModel.write(new FileWriter(FileUtils.ONTOLOGY_PATH), "TURTLE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createIndividuals() {
        try {
            FileReader in = new FileReader(FileUtils.SCHOOL_CSV_PATH);
            CSVParser schoolCSV = CSVFormat.DEFAULT.parse(in);

            Model countyRDF = RDFDataMgr.loadModel(FileUtils.COUNTY_PATH);
            Iterator<Statement> statements = countyRDF.listStatements();
            FileWriter writer = new FileWriter("temp/1.txt");
            while (statements.hasNext()) {
                writer.write(statements.next().toString() + "\n");
                System.out.println(statements.next());
            }

            OntModel ontModel = ModelFactory.createOntologyModel();
            ontModel.read(FileUtils.ONTOLOGY_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}