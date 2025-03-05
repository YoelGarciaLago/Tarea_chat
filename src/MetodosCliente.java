import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetodosCliente {

    private final ExecutorService hilosLectura = Executors.newFixedThreadPool(Servidor.MAX_CLIENTES);

    public String pedirNickname(Scanner scanner) {
        System.out.println("Dime tu nickname");
        return scanner.nextLine();
    }

    public String pedirIpServidor(Scanner scanner) {
        System.out.println("Dime la ip del servidor");
        return scanner.nextLine();
    }

    public int pedirPuerto(Scanner scanner) {
        try{
            System.out.println("Dime el puerto del servidor");
            return scanner.nextInt();
        } catch (NumberFormatException e) {
            System.out.println("Error, ese puerto no vale");
        }
        return 0;
    }

    public static void cerrarTodo(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar los objetos");
        }
    }


    public void reproduccionDeMensaje(ArrayList<ManejoCliente> listaClientes, String nombreUsuario, String mensaje) {
        if(mensaje.trim().isBlank()){
            return;
        }
        String mensajeFormado = nombreUsuario + ": " + mensaje;
        Servidor.getListaMensajes().add(mensajeFormado);
        for (ManejoCliente clienteRegistrado : listaClientes) {
            if ( nombreUsuario.equals("Servidor")|| !clienteRegistrado.getNombreUsuario().equals(nombreUsuario)) {
                try {
                    clienteRegistrado.getBufferedWriter().write(mensajeFormado);
                    clienteRegistrado.getBufferedWriter().newLine();
                    clienteRegistrado.getBufferedWriter().flush();
                } catch (IOException e) {
                    cerrarTodo(clienteRegistrado.getCliente(), clienteRegistrado.getBufferedWriter(), clienteRegistrado.getBufferedReader());
                }
            }
        }
    }

    public void envioDeMensaje(Cliente cliente, String nombreUsuario) {
        try {
            cliente.getBufferedWriter().write(nombreUsuario);
            cliente.getBufferedWriter().newLine();
            cliente.getBufferedWriter().flush();
            Scanner scanner = new Scanner(System.in);
            while (cliente.getSocket().isConnected()){
                String mensaje = scanner.nextLine();
                if (!cliente.getSocket().isConnected() || cliente.getSocket().isClosed()) {
                    desconexionCliente(cliente);
                }
                if(mensaje.equals("/bye")){
                    despedidaCliente(cliente);
                }
                else if (mensaje.startsWith("/help")) {
                    formarListaComandos();
                    continue;
                }
                cliente.getBufferedWriter().write(mensaje);
                cliente.getBufferedWriter().newLine();
                cliente.getBufferedWriter().flush();
            }
        } catch (IOException e) {
            cerrarTodo(cliente.getSocket(), cliente.getBufferedWriter(), cliente.getBufferedReader());
        }

    }

    private static void formarListaComandos() {
        StringBuilder listaComandos = new StringBuilder("Comandos disponibles:\n");
        for(String comando : Servidor.getListaComandos()){
            listaComandos.append(comando).append("\n");
        }
        System.out.println(listaComandos);
    }

    private static void despedidaCliente(Cliente cliente) throws IOException {
        cliente.getBufferedWriter().write("/bye");
        cliente.getBufferedWriter().newLine();
        cliente.getBufferedWriter().flush();
        System.out.println("🔴 Desconectándose del servidor...");
        cerrarTodo(cliente.getSocket(), cliente.getBufferedWriter(), cliente.getBufferedReader());
        System.exit(0);
    }

    private static void desconexionCliente(Cliente cliente) {
        System.out.println("❌ Servidor desconectado. Cerrando cliente...");
        cerrarTodo(cliente.getSocket(), cliente.getBufferedWriter(), cliente.getBufferedReader());
        try{Thread.sleep(1000);}catch (InterruptedException ignore){}
        System.exit(0);
    }

    public void escucharMensajes(Cliente cliente) {
        hilosLectura.execute(() -> {
            try {
                BufferedReader reader = cliente.getBufferedReader();
                String mensaje;
                while (cliente.getSocket().isConnected()) {
                    mensaje = reader.readLine();
                    if (mensaje == null) {
                        break;
                    }
                    System.out.println(mensaje);
                }
            } catch (IOException e) {
                cerrarTodo(cliente.getSocket(), cliente.getBufferedWriter(), cliente.getBufferedReader());
            }
        });
    }


    public void desconexionCliente(ArrayList<ManejoCliente> listaClientes, ManejoCliente usuario) {
        listaClientes.remove(usuario);
        reproduccionDeMensaje(listaClientes, usuario.getNombreUsuario(), "ha abandonado la sala");
        try {
            usuario.getCliente().setKeepAlive(false);
        } catch (IOException e) {
            System.out.println("Error al cerrar el cliente -->" + e.getMessage());
        }
    }

}