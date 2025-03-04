import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {
    private static ArrayList <String> listaMensajes = new ArrayList<>();
    static final AtomicInteger clientesActivos = new AtomicInteger(0);
    private static String[] listaComandos = {"bye: El usuario sale del servidor", "all: muestra a todos los nombres de usuario de los presentes en la sala", "help: muestra todos los comandos"};

    public static void incrementarClientes() {
        int activos = clientesActivos.incrementAndGet();
        System.out.println("Clientes conectados: " + activos);
    }

    public static void decrementarClientes() {
        int activos = clientesActivos.decrementAndGet();
        System.out.println("Clientes conectados: " + activos);
    }

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

        final int MAX_CLIENTES = 2;
        MetodosConexionServer metodosConexionServer = new MetodosConexionServer();
        ServerSocket serverSocket = metodosConexionServer.crearServer();
        int puertoServer = MetodosConexionServer.pedirPuertoServidor(new Scanner(System.in));
        metodosConexionServer.bindAlPuerto(puertoServer,serverSocket);

        ThreadPoolExecutor hilosCliente = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_CLIENTES);
        System.out.println("Aceptando conexiones");
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
