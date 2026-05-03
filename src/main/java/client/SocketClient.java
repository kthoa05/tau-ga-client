package client;

import network.common.NetworkConfig;
import network.common.Request;
import network.common.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {

    public Response send(Request request) {
        try (Socket socket = new Socket(NetworkConfig.HOST, NetworkConfig.PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);
            out.flush();

            return (Response) in.readObject();
        } catch (Exception e) {
            return new Response(false, null, "Không thể kết nối tới server: " + e.getMessage());
        }
    }


}
