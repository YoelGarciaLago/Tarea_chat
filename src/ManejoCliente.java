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

    @Override
    public void run() {
        try {
            metodosCliente.reproduccionDeMensaje(listaClientes,"Servidor", "¡" + nombreUsuario + " se ha conectado! - Usuarios conectados: " + Servidor.clientesActivos.get());
            bufferedReader.readLine();
            while (cliente.isConnected()){
                mensajeAEnviar = bufferedReader.readLine();
                if (verificarMensajeSalida()){
                    expulsarCliente();
                    break;
                }
                else if (mensajeAEnviar.startsWith("/nickname")) {
                    String[] mensajeDiv = mensajeAEnviar.split(" ", 2);

                    if (mensajeDiv.length < 2 || mensajeDiv[1].trim().isEmpty()) {
                        cambioNombreInvalido();
                    } else {
                        cambioDeNombre(mensajeDiv);
                        break;
                    }
                }
                else if (mensajeAEnviar.startsWith("/all")){
                    envioDeNombresUsuarios();
                }
                metodosCliente.reproduccionDeMensaje(listaClientes, this.nombreUsuario, this.mensajeAEnviar);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            desconexionCliente();
        }
    }

    private void desconexionCliente() {
        MetodosCliente.cerrarTodo(this.cliente,this.bufferedWriter,this.bufferedReader);
        metodosCliente.desconexionCliente(listaClientes, this);
    }

    private void cambioNombreInvalido() {
        // Si el nombre de usuario no es válido o está vacío
        try {
        bufferedWriter.write("❌ Error: Debes proporcionar un nuevo nombre de usuario.");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void envioDeNombresUsuarios() throws IOException {
        StringBuilder listaUsuarios = new StringBuilder("Usuarios conectados:\n");
        for(ManejoCliente manejoCliente : listaClientes){
            listaUsuarios.append(manejoCliente.nombreUsuario).append("\n");
        }
        bufferedWriter.write(listaUsuarios.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void cambioDeNombre(String[] mensajeDiv) {
        // Cambiar el nombre de usuario
        String nuevoNombre = mensajeDiv[1].trim();
        System.out.println("El cliente ha cambiado su nombre de usuario a: " + nuevoNombre);
        nombreUsuario = nuevoNombre; // Cambiar el nombre del usuario

        // Notificar a todos los demás clientes sobre el cambio
        metodosCliente.reproduccionDeMensaje(listaClientes, "Servidor", "El usuario " + nombreUsuario + " ha cambiado su nombre.");
    }

    private void expulsarCliente() {
        System.out.println("🔴 Cliente " + nombreUsuario + " se ha desconectado.");
        Servidor.decrementarClientes(); // 🔥 Asegurar que el cliente se elimina correctamente
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
