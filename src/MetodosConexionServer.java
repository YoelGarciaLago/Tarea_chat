import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class MetodosConexionServer {
    public ServerSocket crearServer(){
        ServerSocket serverSocket = null;
        try {
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

    public void bindAlPuerto(int puerto, String ip, ServerSocket serverSocket){
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, puerto);
        try {
            serverSocket.bind(inetSocketAddress);
        } catch (IOException e) {
            System.out.println("Error al hacer el bind -> " + e.getMessage());
        }
    }

    public static int pedirPuertoServidor(Scanner scanner) {
        int puerto;

        while (true) {
            System.out.println("Introduce un puerto (1024 - 65535):");

            if (scanner.hasNextInt()) {
                puerto = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer

                if (puerto >= 1024 && puerto <= 65535) {
                    return puerto; // ✅ Puerto válido
                } else {
                    System.out.println("❌ El puerto debe estar entre 1024 y 65535.");
                }
            } else {
                System.out.println("❌ Entrada no válida. Introduce un número.");
                scanner.nextLine(); // Limpiar la entrada incorrecta
            }
        }
    }


}
