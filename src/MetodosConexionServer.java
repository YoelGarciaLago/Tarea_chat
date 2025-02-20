import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class MetodosConexionServer {

    public ServerSocket crearServer(){
        ServerSocket serverSocket = null;
        try {
        System.out.println("Creando servidor");
        serverSocket = new ServerSocket();

        } catch (IOException e) {
            System.out.println("Error al crear el servidor --> " + e.getMessage());;
        }
        return serverSocket;
    }

    public void closeServer(ServerSocket server){
        try{
            if(server != null)
                server.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar el servidor --> " + e.getMessage());;
        }
    }

    public void bindAlPuerto(int puerto, ServerSocket serverSocket){
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", puerto);
        System.out.println("Realizando el bind");
        try {
            serverSocket.bind(inetSocketAddress);
        } catch (IOException e) {
            System.out.println("Error al hacer el bind -> " + e.getMessage());
        }
    }
}
