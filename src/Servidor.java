import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private static ArrayList <String> listaMensajes = new ArrayList<>();

    public static ArrayList<String> getListaMensajes() {
        return listaMensajes;
    }

    public static void setListaMensajes(ArrayList<String> listaMensajes) {
        Servidor.listaMensajes = listaMensajes;
    }
    public static void enviarHistorialMensajes(ArrayList<String> historialMensajes, ManejoCliente cliente) {
        try {
            BufferedWriter writer = cliente.getBufferedWriter();
            for (String mensaje : historialMensajes) {
                writer.write(mensaje);
                writer.newLine();
            }
            writer.flush(); // Asegura que los mensajes se envíen
        } catch (IOException e) {
            MetodosCliente.cerrarTodo(cliente.getCliente(), cliente.getBufferedWriter(), cliente.getBufferedReader());
        }
    }
    public static void main(String[]args){

        MetodosConexionServer metodosConexionServer = new MetodosConexionServer();
        ServerSocket serverSocket = metodosConexionServer.crearServer();
        metodosConexionServer.bindAlPuerto(6666,serverSocket);


        ExecutorService hilosCliente = Executors.newFixedThreadPool(3);
        while (true){
            try {
                Socket cliente = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                String nombreUsuario = reader.readLine(); // 🔴 Leer el nickname enviado por el cliente

                System.out.println("Nuevo cliente conectado");
                ManejoCliente nuevoCliente = new ManejoCliente(cliente, nombreUsuario);
                Servidor.enviarHistorialMensajes(Servidor.getListaMensajes(),nuevoCliente);
                hilosCliente.execute(nuevoCliente);

            } catch (IOException e) {
                metodosConexionServer.closeServer(serverSocket);
                break;
            }
        }
        metodosConexionServer.closeServer(serverSocket);
    }


}
