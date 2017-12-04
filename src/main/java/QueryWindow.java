public class QueryWindow {

    private QueryHub hub;

    QueryWindow() {
        hub = new QueryHub();
    }

    public void launch() {

    }

    // TODO: GUI design and coding

    // TODO: GUI Action callbacks
    private void buttonAction() {
        hub.executeQuery(1);
    }
}
