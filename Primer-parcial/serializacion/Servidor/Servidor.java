
/*
 * Instituto Politécnico Nacional
 * Escuela Superior de Cómputo
 * Creado por:
 * Carlos Augusto Santiago Pérez
 * Grupo: 3CV13
 * Materia: Aplicaciones para comunicaciones en Red
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(3000);
            for (;;) {
                Socket cl = server.accept();
                System.out.println("Conexion establecida");

                System.out.println("Recibiendo objeto serializado...");
                // flujo de entrada
                DataInputStream dis = new DataInputStream(cl.getInputStream());

                // se recibe el nombre del archivo
                String nombre = dis.readUTF();
                System.out.println("Recibimos el archivo:" + nombre);
                // se recibe el tamaño del archivo
                long tam = dis.readLong();
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                long recibidos = 0;
                // paquete para el contenido del archivo
                byte[] b = new byte[6 * 1024];
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

                // Deserializando el archivo
                FileInputStream file = new FileInputStream(nombre);
                ObjectInputStream ois = new ObjectInputStream(file);
                System.out.println("Deserializando txt...");
                System.out.println((int) ois.readObject());
                System.out.println((char) ois.readObject());
                System.out.println((boolean) ois.readObject());
                System.out.println((float) ois.readObject());
                System.out.println((String) ois.readObject());
                System.out.println((double) ois.readObject());

                dos.close();
                dis.close();
                ois.close();
                cl.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
