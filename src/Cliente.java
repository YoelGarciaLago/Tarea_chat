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

    public MetodosCliente getMetodosCliente() {
        return metodosCliente;
    }

    public void setMetodosCliente(MetodosCliente metodosCliente) {
        this.metodosCliente = metodosCliente;
    }


    public static void main(String[]args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        MetodosCliente metodosCliente1 = new MetodosCliente();
        String nickname = metodosCliente1.pedirNickname(scanner);
        String ip = metodosCliente1.pedirIpServidor(scanner);
        int puerto = metodosCliente1.pedirPuerto(scanner);
        if(puerto == 0){
            System.exit(130);
        }
        Socket socket1 = null;
        try {
            socket1 = new Socket(ip,puerto);
        }catch (ConnectException e){
            System.out.println("Servidor inactivo o en mantenimiento --> " + e.getMessage());
            System.exit(130);
        }

        // 🔴 Enviar el nombre de usuario al servidor antes de cualquier mensaje
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
        writer.write(nickname);
        writer.newLine();
        writer.flush();

        Cliente cliente = new Cliente(socket1,nickname);
        //cliente.listenMessages();
        cliente.getMetodosCliente().escucharMensajes(cliente);
        cliente.getMetodosCliente().envioDeMensaje(cliente, nickname);
    }



}
