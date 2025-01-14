import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class main {
    public static void main(String[] args) throws UnknownHostException {
         // Récupérer l'adresse InetAddress de l'IP
        InetAddress ip = InetAddress.getByName("172.20.164.24");

        // Démarre les deux serveurs dans des threads distincts
        WebServeur https = new WebServeur( ip,9999);
        WebsocketServeur websocket = new WebsocketServeur( ip,8080);
        

        Thread websocketThread = new Thread(websocket);
        Thread httpsThread = new Thread(https);

        
        //websocketThread.start();
        httpsThread.start();
    }
}
