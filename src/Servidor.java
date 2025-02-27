import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    public static void main(String[]args){
        ArrayList <Socket> listaDeUsuarios = new ArrayList<>();
        ArrayList <String> listaMensajes = new ArrayList<>();

        MetodosConexionServer metodosConexionServer = new MetodosConexionServer();
        ServerSocket serverSocket = metodosConexionServer.crearServer();
        metodosConexionServer.bindAlPuerto(6666,serverSocket);


        ExecutorService hilosCliente = Executors.newFixedThreadPool(3);

        while (true){
            try {
                if(serverSocket == null){
                    throw new IOException();
                }
                Socket cliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");
                ManejoCliente nuevoCliente = new ManejoCliente(cliente);
                hilosCliente.execute(nuevoCliente);

            } catch (IOException e) {
                metodosConexionServer.closeServer(serverSocket);
                break;
            }
        }
        metodosConexionServer.closeServer(serverSocket);
    }
}
