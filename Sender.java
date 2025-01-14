public class Sender implements Subscriber {
    final private WebsocketServeur websocketServer;
    final private File file;

    @Override
    public void update() {
        websocketServer.broadcast(file.getAdded());
        websocketServer.broadcast(file.getRemoved());
    }
    public Sender(WebsocketServeur websocketServer, File file) {
        this.websocketServer = websocketServer;
        this.file = file;
    }
}