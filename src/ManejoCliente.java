import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ManejoCliente implements Runnable{

    private static ArrayList<ManejoCliente> listaClientes = new ArrayList<>();
    private String nombreUsuario;
    private String mensajeAEnviar;
    private Socket cliente;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private MetodosCliente metodosCliente;

    public ManejoCliente(Socket cliente, String nombreUsuario){
        this.cliente = cliente;
        this.nombreUsuario = nombreUsuario;
        this.metodosCliente = new MetodosCliente();
        listaClientes.add(this);
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));

        } catch (IOException e) {
            System.out.println("Error al crear los objetos --> " + e.getMessage());
        }
    }



    @Override
    public void run() {
        try {
            metodosCliente.reproduccionDeMensaje(listaClientes,"Servidor", "¡" + nombreUsuario + " se ha conectado!");
            bufferedReader.readLine();
            while (cliente.isConnected()){
                mensajeAEnviar = bufferedReader.readLine();
                metodosCliente.reproduccionDeMensaje(listaClientes, this.nombreUsuario, this.mensajeAEnviar);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            metodosCliente.desconexionCliente(listaClientes, this);
        }

    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getMensajeAEnviar() {
        return mensajeAEnviar;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public Socket getCliente() {
        return cliente;
    }
}
