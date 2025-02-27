import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private String nombreUsuario;
    private String ipServidor;
    private int puerto;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private MetodosCliente metodosCliente;

    public Cliente(Socket socket, String nombreUsuario) {
        this.socket = socket;
        this.nombreUsuario = nombreUsuario;
        this.metodosCliente = new MetodosCliente();
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            metodosCliente.cerrarTodo(this.socket, this.bufferedWriter, this.bufferedReader);
        }
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public static void main(String[]args){
    }



}
