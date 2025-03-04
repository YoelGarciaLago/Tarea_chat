import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ManejoCliente implements Runnable{

    public static ArrayList<ManejoCliente> listaClientes = new ArrayList<>();
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
        Servidor.incrementarClientes();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));

        } catch (IOException e) {
            System.out.println("Error al crear los objetos --> " + e.getMessage());
        }
    }
    public void ponerNombreUsuario(String newNickname){
        this.setNombreUsuario(newNickname);
    }


    @Override
    public void run() {
        try {
            metodosCliente.reproduccionDeMensaje(listaClientes,"Servidor", "¡" + nombreUsuario + " se ha conectado! - Usuarios conectados: " + Servidor.clientesActivos.get());
            bufferedReader.readLine();
            while (cliente.isConnected()){
                mensajeAEnviar = bufferedReader.readLine();
                if (verificarMensajeSalida()){
                    System.out.println("🔴 Cliente " + nombreUsuario + " se ha desconectado.");
                    Servidor.decrementarClientes(); // 🔥 Asegurar que el cliente se elimina correctamente
                    MetodosCliente.cerrarTodo(cliente, bufferedWriter, bufferedReader);
                    break;
                }
                else if(mensajeAEnviar.startsWith("/nickname")){
                    String[] mensajeDiv = mensajeAEnviar.split(" ", 2);

                }
                metodosCliente.reproduccionDeMensaje(listaClientes, this.nombreUsuario, this.mensajeAEnviar);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            metodosCliente.desconexionCliente(listaClientes, this);
            //Servidor.decrementarClientes();
        }

    }

    private boolean verificarMensajeSalida() {
        return mensajeAEnviar == null || mensajeAEnviar.equals("/bye".trim());
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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
