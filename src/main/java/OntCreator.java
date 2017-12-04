import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class OntCreator {

    public static void createOntology() {
        String baseURI = "http://www.cs7is1.com/assignment2/ireland-schools";
        String ns = baseURI + "#";

        OntModel ontModel = ModelFactory.createOntologyModel();

        // Coordinate Class

        OntClass geoLocation = ontModel.createClass(ns + "GeoLocation");

        DatatypeProperty latitude = ontModel.createDatatypeProperty(ns + "latitude");
        DatatypeProperty longitude = ontModel.createDatatypeProperty(ns + "longitude");
        latitude.setDomain(geoLocation);
        latitude.setRange(XSD.decimal);
        longitude.setDomain(geoLocation);
        longitude.setRange(XSD.decimal);

        geoLocation.addSuperClass(ontModel.createCardinalityRestriction(null, latitude, 1));
        geoLocation.addSuperClass(ontModel.createCardinalityRestriction(null, longitude, 1));


        /****************** County Class *******************/

        OntClass county = ontModel.createClass(ns + "County");

        // RDFS: label for name

        DatatypeProperty area = ontModel.createDatatypeProperty(ns + "area");
        area.setDomain(county);
        area.setRange(XSD.nonPositiveInteger);

        SymmetricProperty adjacentTo = ontModel.createSymmetricProperty(ns + "adjacentTo");
        adjacentTo.setDomain(county);
        adjacentTo.setRange(county);

        TransitiveProperty biggerThan = ontModel.createTransitiveProperty(ns + "biggerThan");
        biggerThan.setDomain(county);
        biggerThan.setRange(county);

        county.addSuperClass(ontModel.createCardinalityRestriction(null, area, 1));

        /****************** Ethos Class *********************/

        OntClass catholic = ontModel.createClass(ns + "Catholic");
        OntClass churchOfIreland = ontModel.createClass(ns + "ChurchOfIreland");
        OntClass multiDenominational = ontModel.createClass(ns + "MultiDenominational");

        RDFList ethosList = ontModel.createList(new RDFNode[] { catholic, churchOfIreland, multiDenominational });
        OntClass ethos = ontModel.createEnumeratedClass(ns + "Ethos", ethosList);

        catholic.addSuperClass(ethos);
        churchOfIreland.addSuperClass(ethos);
        multiDenominational.addSuperClass(ethos);

        /*********** subclasses of School **********/

        OntClass boySchool = ontModel.createClass(ns + "BoySchool");
        OntClass girlSchool = ontModel.createClass(ns + "GirlSchool");
        OntClass mixedSchool = ontModel.createClass(ns + "MixedSchool");

        RDFList list = ontModel.createList(new RDFNode[] { boySchool, girlSchool, mixedSchool });
        boySchool.addDisjointWith(girlSchool);
        mixedSchool.addDisjointWith(boySchool);
        mixedSchool.addDisjointWith(girlSchool);

        /***************** School Class *******************/

        OntClass school = ontModel.createUnionClass(ns + "School", list);

        InverseFunctionalProperty rollNumber = ontModel.createInverseFunctionalProperty(ns + "rollNumber", true);
        rollNumber.setDomain(school);
        rollNumber.setRange(XSD.xstring);

        // RDFS:label for name

        DatatypeProperty address = ontModel.createDatatypeProperty(ns + "address");
        address.setDomain(school);
        address.setRange(XSD.xstring);

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

        ObjectProperty hasGeoLocation = ontModel.createObjectProperty(ns + "hasGeoLocation");
        hasGeoLocation.setDomain(school);
        hasGeoLocation.setRange(geoLocation);

        ObjectProperty inCounty = ontModel.createObjectProperty(ns + "inCounty");
        inCounty.setDomain(school);
        inCounty.setRange(county);

        DatatypeProperty isDEIS = ontModel.createDatatypeProperty(ns + "isDEIS");
        isDEIS.setDomain(school);
        isDEIS.setRange(XSD.xboolean);

        DatatypeProperty isGaeltacht = ontModel.createDatatypeProperty(ns + "isGaeltacht");
        isGaeltacht.setDomain(school);
        isGaeltacht.setRange(XSD.xboolean);

        DatatypeProperty withEthos = ontModel.createDatatypeProperty(ns + "withEthos");
        withEthos.setDomain(school);
        withEthos.setRange(ethos);

        // Subclasses setup

        boySchool.addSuperClass(school);
        girlSchool.addSuperClass(school);
        mixedSchool.addSuperClass(school);

        OntClass catholicSchool = ontModel.createClass(ns + "CatholicSchool");
        catholicSchool.addSuperClass(school);
        catholicSchool.addSuperClass(ontModel.createHasValueRestriction(null, withEthos, catholic));

        Literal zero =  ontModel.createTypedLiteral(0);
        boySchool.addSuperClass(ontModel.createHasValueRestriction(null, girlCount, zero));
        girlSchool.addSuperClass(ontModel.createHasValueRestriction(null, girlCount, zero));

        // County property

        ObjectProperty hasSchools = ontModel.createObjectProperty(ns + "hasSchools");
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
            ontModel.write(new FileOutputStream(new File("temp/ireland-schools.ttl")), "TURTLE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void createInstance() {
        // TODO: Instance creation
    }
}
