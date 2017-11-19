import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.enhanced.Personality;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.XSD;

public class Main {

    public static void main(String[] args) {
        rdf();
        owl();
    }

    static void rdf() {
        Model model = ModelFactory.createDefaultModel();
        String ns = "http://www.example.com/example#";
        Resource john = model.createResource(ns + "John");
        Resource jane = model.createResource(ns + "Jane");
        // Create the 'hasBrother' Property declaration
        Property hasBrother = model.createProperty(ns, "hasBrother");
        // Associate jane to john through 'hasBrother'
        jane.addProperty(hasBrother, john);
        // Create the 'hasSister' Property declaration
        Property hasSister = model.createProperty(ns, "hasSister");
        // Associate john and jane through 'hasSister' with a Statement
        Statement sisterStmt = model.createStatement(john, hasSister, jane);
        model.add(sisterStmt);
        model.write(System.out, "RDF/XML");
    }

    static void owl() {
        OntModel ontModel = ModelFactory.createOntologyModel();
        String ns = "http://www.example.com/onto1#";
        String baseURI = "http://www.example.com/onto1";
        Ontology onto = ontModel.createOntology(baseURI);
        // Create ‘Person’, ‘MalePerson’ and ‘FemalePerson’ classes
        OntClass person = ontModel.createClass(ns + "Person");
        OntClass malePerson = ontModel.createClass(ns + "MalePerson");
        OntClass femalePerson = ontModel.createClass(ns + "FemalePerson");
        // FemalePerson and MalePerson are subclasses of Person
        person.addSubClass(malePerson);
        person.addSubClass(femalePerson);
        // FemalePerson and MalePerson are disjoint
        malePerson.addDisjointWith(femalePerson);
        femalePerson.addDisjointWith(malePerson);

        DatatypeProperty hasAge =  ontModel.createDatatypeProperty(ns + "hasAge");
        // 'hasAge' takes integer values, so its range is 'integer'
        // Basic datatypes are defined in the ‘vocabulary’ package
        hasAge.setDomain(person); hasAge.setRange(XSD.integer);
        // com.hp.hpl.jena.vocabulary.XSD
        // Create individuals
        Individual john = malePerson.createIndividual(ns + "John");
        Individual jane = femalePerson.createIndividual(ns + "Jane");
        Individual bob = malePerson.createIndividual(ns + "Bob");
        // Create statement 'John hasAge 20'
        Literal age20 =  ontModel.createTypedLiteral("20", XSDDatatype.XSDint);
        Statement johnIs20 =  ontModel.createStatement(john, hasAge, age20);
        ontModel.add(johnIs20);

        ontModel.write(System.out, "RDF/XML");
    }
}
