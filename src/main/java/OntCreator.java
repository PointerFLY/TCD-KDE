import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OntCreator {

    private final static String BASE_URI = "http://www.cs7is1.com/assignment2/ireland-schools";
    private final static String NAMESPACE = BASE_URI +"#";

    public static void createOntology() {
        OntModel ontModel = ModelFactory.createOntologyModel();

        // GeoLocation Class

        OntClass geoLocation = ontModel.createClass(NAMESPACE + "GeoLocation");

        DatatypeProperty latitude = ontModel.createDatatypeProperty(NAMESPACE + "latitude");
        DatatypeProperty longitude = ontModel.createDatatypeProperty(NAMESPACE + "longitude");
        latitude.setDomain(geoLocation);
        latitude.setRange(XSD.xfloat);
        longitude.setDomain(geoLocation);
        longitude.setRange(XSD.xfloat);

        geoLocation.addSuperClass(ontModel.createCardinalityRestriction(null, latitude, 1));
        geoLocation.addSuperClass(ontModel.createCardinalityRestriction(null, longitude, 1));


        /****************** County Class *******************/

        OntClass county = ontModel.createClass(NAMESPACE + "County");

        // RDFS: label for name

        DatatypeProperty area = ontModel.createDatatypeProperty(NAMESPACE + "area");
        area.setDomain(county);
        area.setRange(XSD.xfloat);

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

        school.addSuperClass(ontModel.createCardinalityRestriction(null, address, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, boyCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, girlCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, studentCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, inIsland, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, location, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, inCounty, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, withEthos, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, isDEIS, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, isGaeltacht, 1));




        /* ---------------------   Split Line  ---------------------------- */
        /********************** creator individuals *************************/

        Model countyRDF = RDFDataMgr.loadModel(FileUtils.COUNTY_PATH);

        ArrayList<ArrayList<Object>> countyInfoList = new ArrayList<>();
        ResIterator countyResIter = countyRDF.listResourcesWithProperty(RDFS.label);

        Property hasGeometry = countyRDF.getProperty("http://www.opengis.net/ont/geosparql#hasGeometry");
        Property asWKT = countyRDF.getProperty("http://www.opengis.net/ont/geosparql#asWKT");

        while (countyResIter.hasNext()) {
            Resource res = countyResIter.next();

            // labels
            NodeIterator labelsIter = countyRDF.listObjectsOfProperty(res, RDFS.label);
            List<RDFNode> labels = labelsIter.toList();
            String idLabel = "";
            String gaLabel = "";
            String enLabel = "";

            for (RDFNode label : labels) {
                String labelText = label.toString();
                if (labelText.contains("@ga")) {
                    gaLabel = labelText;
                } else if (labelText.contains("@en")) {
                    enLabel = labelText;
                } else {
                    idLabel = labelText;
                }
            }

            // WKT
            Resource geometry = countyRDF.listObjectsOfProperty(res, hasGeometry).next().asResource();
            String wkt = countyRDF.listObjectsOfProperty(geometry, asWKT).next().toString();

            ArrayList<Object> info = new ArrayList<>();
            info.add(idLabel);
            info.add(enLabel);
            info.add(gaLabel);
            info.add(wkt);
            countyInfoList.add(info);
        }

        for (ArrayList<Object> info : countyInfoList) {
            Individual aCounty = county.createIndividual(NAMESPACE + info.get(0));
            aCounty.addLiteral(RDFS.label, info.get(0));
            aCounty.addLiteral(RDFS.label, info.get(1));
            aCounty.addLiteral(RDFS.label, info.get(2));
            aCounty.addLiteral(area, Float.parseFloat("100.21"));
            aCounty.addProperty(adjacentTo, aCounty);
            aCounty.addProperty(biggerThan, aCounty);
            aCounty.addProperty(hasSchools, school.createIndividual());
        }

        try {
            FileReader in = new FileReader(FileUtils.SCHOOL_CSV_PATH);
            CSVParser schoolCSV = CSVFormat.DEFAULT.parse(in);
            List<CSVRecord> records = schoolCSV.getRecords();
            records.remove(0);

            for (CSVRecord record : records) {
                String aRollNumber = record.get(1);
                String aLabel = record.get(2);
                String aAddress = record.get(3) + ", " + record.get(4) + ", " + record.get(5) + ", " + record.get(6);
                int aBoyCount = Integer.parseInt(record.get(12));
                int aGirlCount = Integer.parseInt(record.get(13));
                int aStudentCount = Integer.parseInt(record.get(14));
                boolean aInIsland = record.get(9) == "N";
                boolean aIsDeis = record.get(10) == "N";
                boolean aIsGaeltacht = record.get(11) == "N";

                Individual aLocation = geoLocation.createIndividual();
                aLocation.addLiteral(latitude, Float.parseFloat(record.get(18)));
                aLocation.addLiteral(longitude, Float.parseFloat(record.get(17)));

                Individual aCounty = county.createIndividual(NAMESPACE + "CAVAN");

                String ethosString = record.get(8);
                Individual aEthosType;
                if (ethosString == "CATHOLIC") {
                    aEthosType = catholic.createIndividual();
                } else if (ethosString == "CHURCH OF IRELAND") {
                    aEthosType = churchOfIreland.createIndividual();
                } else {
                    aEthosType = multiDenominational.createIndividual();
                }

                // Create school classes

                Individual aSchool;
                if (aBoyCount == 0) {
                    aSchool = girlSchool.createIndividual(NAMESPACE + aRollNumber);
                } else if (aGirlCount == 0) {
                    aSchool = boySchool.createIndividual(NAMESPACE + aRollNumber);
                } else {
                    aSchool = mixedSchool.createIndividual(NAMESPACE + aRollNumber);
                }
                aSchool.addOntClass(school);
                if (ethosString == "CATHOLIC") {
                    aSchool.addOntClass(catholicSchool);
                }

                aSchool.addLiteral(RDFS.label, aLabel);
                aSchool.addLiteral(address, aAddress);
                aSchool.addLiteral(boyCount, ontModel.createTypedLiteral(aBoyCount, XSDDatatype.XSDnonNegativeInteger));
                aSchool.addLiteral(girlCount, ontModel.createTypedLiteral(aGirlCount, XSDDatatype.XSDnonNegativeInteger));
                aSchool.addLiteral(studentCount, ontModel.createTypedLiteral(aStudentCount, XSDDatatype.XSDpositiveInteger));
                aSchool.addLiteral(inIsland, aInIsland);
                aSchool.addLiteral(isDEIS, aIsDeis);
                aSchool.addLiteral(isGaeltacht, aIsGaeltacht);
                aSchool.addProperty(withEthos, aEthosType);
                aSchool.addProperty(location, aLocation);
                aSchool.addProperty(inCounty, aCounty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        /* ---------------------   Split Line  ---------------------------- */
        /************************** Persistence **********************/

        writeToFile(ontModel);
    }

    public static void writeToFile(OntModel ontModel) {
        try {
            ontModel.write(new FileWriter(FileUtils.ONTOLOGY_PATH), "TURTLE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
