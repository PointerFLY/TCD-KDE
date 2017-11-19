import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

public class CreateModel {

    public static String ontologiesBase = "http://lab.jena.kdeg.ie/";

    public static String relationshipBase = "http://relationships.lab.jena.kdeg.ie/";

    public static String baseNs;

    public static String ontologyName = "sample.ontology";

    public static OntModel ontology;

    public static void main(String args[]) {

        //ontologyName=args[0];
        baseNs = ontologiesBase + ontologyName + "#";
        ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        OntClass Human = ontology.createClass(baseNs + "Human");
        OntClass Animal = ontology.createClass(baseNs + "Animal");
        OntClass Dog = ontology.createClass(baseNs + "Dog");
        OntClass Cat = ontology.createClass(baseNs + "Cat");
        OntClass Bird = ontology.createClass(baseNs + "Bird");
        Animal.addSubClass(Dog);
        Animal.addSubClass(Cat);        Animal.addSubClass(Bird);

        OntClass Man = ontology.createClass(baseNs + "Man");
        OntClass Woman = ontology.createClass(baseNs + "Woman");

        Human.addSubClass(Man);
        Human.addSubClass(Woman);

        OntClass Burmese = ontology.createClass(baseNs + "Burmese");
        OntClass Siamese = ontology.createClass(baseNs + "Siamese");
        Cat.addSubClass(Burmese);
        Siamese.addSuperClass(Cat);

        Dog.addDisjointWith(Cat);


        OntProperty  chases =  ontology.createObjectProperty(baseNs + "chases");
        chases.addDomain(Dog);
        chases.addRange(Cat);

        OntProperty  chasedby =  ontology.createObjectProperty(baseNs + "chased_by");
        chasedby.addInverseOf(chases);

        OntProperty  runsAfter =  ontology.createObjectProperty(baseNs + "runs_after");
        runsAfter.addEquivalentProperty(chases);

        OntProperty  eats =  ontology.createObjectProperty(baseNs + "eats");
        chases.addSubProperty(eats);



        try {
            writeToFile(ontologyName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try
        {
            ReadModel.loadAllClassesOnt(ontologyName);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String filename)
            throws FileNotFoundException {
        try {
            ontology.write(new FileOutputStream(new File(filename)),
                    "RDF/XML-ABBREV");
            System.out.println("Ontology written to file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
