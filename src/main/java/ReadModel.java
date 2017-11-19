import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Properties;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

public class ReadModel {

    //public static String webSource = "http://www.outriot.com/mecon/itunesCat.owl";
    //   public static String localSource =  "itunesCat.owl";

    public static OntModel Ontologymodel = null;

    public static void loadAllClassesOnt(String localSource) throws FileNotFoundException {

        //Not needed when reading locally
        setProxy();

        //FROM WEB
        //OntModel m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, null);
        //m.read(webSource);

        //LOCALLY (On Hard Drive or USB key)
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM, null);
        m.read(new FileInputStream(localSource),null);

        Iterator RootClasses = m.listHierarchyRootClasses();

        while (RootClasses.hasNext()) {
            String RootclassSTR = RootClasses.next().toString();
            System.out.println("ROOTCLASS: " + RootclassSTR);
            OntClass query = m.getOntClass(RootclassSTR);

            for (Iterator i = query.listSubClasses(); i.hasNext();) {
                OntClass c = (OntClass) i.next();
                System.out.println("                SubClass: " + c);
            }

        }
    }

    private static void setProxy() {

        Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", "www-proxy.cs.tcd.ie");
        systemSettings.put("http.proxyPort", "8080");

    }
}
