

public class Main {

    public static void main(String[] args) {
        FileUtils.fetchData();

        OntCreator.createOntology();

        QueryWindow window = new QueryWindow();
        window.launch();
    }
}
