import java.net.*;
import java.io.*;

public class SHola {
    public static void main(String args[]) {
        try {
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando cliente ...");
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexion establecida desde " + cl.getInetAddress() + ":" + cl.getPort());
                String mensaje = "Hola mundo";
                DataOutputStream salida = new DataOutputStream(cl.getOutputStream());
                DataInputStream entrada = new DataInputStream(cl.getInputStream());
                // enviar mensaje
                salida.writeUTF(mensaje);
                // recibimos el mensaje del cliente
                String respuesta = entrada.readUTF();
                System.out.println(respuesta);
                salida.close();
                entrada.close();
                cl.close();
            } // for
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }// main
}