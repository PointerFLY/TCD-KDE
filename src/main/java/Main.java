

public class Main {

    public static void main(String[] args) {
        FileUtils.fetchData();

        OntCreator.createOntology();
        OntCreator.createIndividuals();

        QueryWindow window = new QueryWindow();
        window.launch();
    }
}
