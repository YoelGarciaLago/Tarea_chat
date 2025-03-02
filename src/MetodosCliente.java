import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MetodosCliente {

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

    public void cerrarTodo(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (socket != null) {
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
        for (ManejoCliente clienteRegistrado : listaClientes) {
            if (!clienteRegistrado.getNombreUsuario().equals(nombreUsuario)) {
                try {
                    clienteRegistrado.getBufferedWriter().write(mensaje);
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
                cliente.getBufferedWriter().write(nombreUsuario);
                cliente.getBufferedWriter().newLine();
                cliente.getBufferedWriter().flush();
            }
        } catch (IOException e) {
            cerrarTodo(cliente.getSocket(), cliente.getBufferedWriter(), cliente.getBufferedReader());
        }

    }




    public void desconexionCliente(ArrayList<ManejoCliente> listaClientes, ManejoCliente usuario) {
        listaClientes.remove(usuario);
        reproduccionDeMensaje(listaClientes, usuario.getNombreUsuario(), " ha abandonado la sala");
    }

}