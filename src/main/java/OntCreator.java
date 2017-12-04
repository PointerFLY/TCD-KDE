import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.XSD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class OntCreator {

    public static void createOntology() {
        String baseURI = "http://www.cs7is1.com/assignment2/ireland-schools";
        String ns = baseURI + "#";

        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.setNsPrefix("vocab","http://myweb.in/vocab#");

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

        SymmetricProperty adjacentTo = ontModel.createSymmetricProperty(ns + "adjacentTo");
        adjacentTo.setDomain(county);
        adjacentTo.setRange(county);

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

        ObjectProperty hasSchools = ontModel.createObjectProperty(ns + "hasSchools");
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

        DatatypeProperty address = ontModel.createDatatypeProperty(ns + "address");
        address.setDomain(school);
        address.setRange(XSD.xstring);

        OntClass maleSchool = ontModel.createClass(ns + "MaleSchool");
        maleSchool.addEquivalentClass(boySchool);
        OntClass femaleSchool = ontModel.createClass(ns + "FemaleSchool");
        femaleSchool.addEquivalentClass(girlSchool);
        OntClass mixedSchool = ontModel.createClass(ns + "MixedSchool");
        mixedSchool.addSuperClass(school);

        mixedSchool.addSuperClass(ontModel.createMinCardinalityRestriction(null, boyCount, 1));
        mixedSchool.addSuperClass(ontModel.createMinCardinalityRestriction(null, girlCount, 1));

        RDFList list = ontModel.createList(new RDFNode[] { boySchool, girlSchool, mixedSchool });
        ontModel.createUnionClass(ns + "School", list);
        boySchool.addDisjointWith(girlSchool);
        girlSchool.addDisjointWith(boySchool);
        girlSchool.addDisjointWith(mixedSchool);
        boySchool.addDisjointWith(mixedSchool);
        mixedSchool.addDisjointWith(boySchool);
        mixedSchool.addDisjointWith(girlSchool);


        boySchool.addSuperClass(ontModel.createMaxCardinalityRestriction(null, girlCount, 0));
        girlSchool.addSuperClass(ontModel.createMaxCardinalityRestriction(null, boyCount, 0));

        school.addSubClass(boySchool);
        school.addSubClass(girlSchool);


        OntClass catholic = ontModel.createClass(ns + "Catholic");
        OntClass churchOfIreland = ontModel.createClass(ns + "ChurchOfIreland");
        OntClass multiDenominational = ontModel.createClass(ns + "MultiDenominational");

        RDFList ethosList = ontModel.createList(new RDFNode[] { catholic, churchOfIreland, multiDenominational });
        OntClass ethos = ontModel.createEnumeratedClass(ns + "Ethos", ethosList);

        DatatypeProperty withEthos = ontModel.createDatatypeProperty(ns + "withEthos");
        withEthos.setDomain(school);
        withEthos.setRange(ethos);

        school.addSuperClass(ontModel.createCardinalityRestriction(null, withEthos, 1));
        school.addSuperClass(ontModel.createMinCardinalityRestriction(null, studentCount, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, rollNumber, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, name, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, inCounty, 1));
        school.addSuperClass(ontModel.createCardinalityRestriction(null, hasCoordinate, 1));

        county.addSuperClass(ontModel.createCardinalityRestriction(null, countyName, 1));
        county.addSuperClass(ontModel.createCardinalityRestriction(null, area, 1));

        try {
            ontModel.write(new FileOutputStream(new File("temp/ireland-schools.ttl")), "TURTLE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void createInstance() {

    }
}
