import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Cliente {
    private static final String IP_REGEX =
            "^((25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)\\.){3}" +
                    "(25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
    private String nombreUsuario;
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
            MetodosCliente.cerrarTodo(this.socket, this.bufferedWriter, this.bufferedReader);
        }
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public static boolean esIPValida(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }

    public static void main(String[]args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        MetodosCliente metodosCliente1 = new MetodosCliente();
        String nickname = metodosCliente1.pedirNickname(scanner);
        String ip = metodosCliente1.pedirIpServidor(scanner);
        if (!ip.equals("localhost") && !esIPValida(ip)) {
            System.out.println("❌ IP inválida, intenta de nuevo.");
            System.exit(1);
        }
        int puerto = 0;
        try{
            puerto = metodosCliente1.pedirPuerto(scanner);
            if(puerto == 0){
                System.exit(1);
            }
        }catch (InputMismatchException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if(Servidor.clientesActivos.get() >= Servidor.MAX_CLIENTES){
            System.out.println("Servidor lleno");
            System.exit(1);

        }

        Socket socket1 = null;
        try {
            socket1 = new Socket(ip,puerto);
            try{Thread.sleep(500);}catch (InterruptedException ignored){}
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            if(socket1.isClosed()){
                System.out.println(bufferedReader1.readLine());
                System.exit(1);
            }
        }catch (ConnectException e){
            System.out.println("Servidor inactivo o en mantenimiento --> " + e.getMessage());
            System.exit(1);
        }catch (SocketException e){
            System.out.println("Error al conectarse al servidor, revise la ip y el puerto --> " + e.getMessage());
        }


        // 🔴 Enviar el nombre de usuario al servidor antes de cualquier mensaje
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
        writer.write(nickname);
        writer.newLine();
        writer.flush();

        Cliente cliente = new Cliente(socket1,nickname);
        cliente.getMetodosCliente().escucharMensajes(cliente);
        cliente.getMetodosCliente().envioDeMensaje(cliente, nickname);
    }



}
