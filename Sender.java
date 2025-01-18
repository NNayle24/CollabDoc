public class Sender implements Subscriber {
    private WebsocketServeur websocketServer;
    final private File file;

    @Override
    public void update() {
        websocketServer.broadcast(file.getAdded(),"NEW_LINK:");
        websocketServer.broadcast(file.getRemoved(),"REMOVE_LINK:");
    }
    public Sender(WebsocketServeur websocketServer, File file) {
        this.websocketServer = websocketServer;
        this.file = file;
    }
    public Sender(File file) {
        this.websocketServer = null;
        this.file = file;
    }
    public void setWebsocketServer(WebsocketServeur websocketServer) {
        this.websocketServer = websocketServer;
    }
}