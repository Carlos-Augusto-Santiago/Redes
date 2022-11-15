import java.net.*;
import java.io.*;

public class ServidorArchivo {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000);
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde" + cl.getInetAddress() + ":" + cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                // se reciben cuantos archivos nos van a enviar
                int numFiles = dis.readInt();
                // se crea un ciclo para recibir todos los archivos
                for (int i = 0; i < numFiles; i++) {
                    // se recibe el nombre del archivo
                    String nombre = dis.readUTF();
                    System.out.println("Recibimos el archivo:" + nombre);
                    // se recibe el tamaño del archivo
                    long tam = dis.readLong();
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                    long recibidos = 0;
                    // paquete para el contenido del archivo
                    byte[] b = new byte[1024];
                    int n, porcentaje;
                    while (recibidos < tam) {
                        n = dis.read(b);
                        // se recibe el contenido del archivo
                        dos.write(b, 0, n);
                        dos.flush();
                        recibidos = recibidos + n;
                        porcentaje = (int) (recibidos * 100 / tam);
                        System.out.print("Recibido: " + porcentaje + "%\r");
                    } // While
                    System.out.print("\n\nArchivo recibido.\n");
                    dos.close();
                }
                dis.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }
}