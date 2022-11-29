package Cliente;

import java.net.*;
import java.io.*;

public class Cliente {
    public static void main(String[] args) {
        try {
            // Conectando con el servidor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la direccion del servidor: ");
            String host = br.readLine();
            System.out.printf("\nEscriba el puerto: ");
            int pto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, pto);

            // Recibir el archivo serializado
            // se recibe el nombre del archivo
            DataInputStream dis = new DataInputStream(cl.getInputStream());
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
            System.out.print("\nArchivo recibido.\n");

            // Deserializando el archivo
            FileInputStream file = new FileInputStream(nombre);
            ObjectInputStream ois = new ObjectInputStream(file);
            System.out.println("Deserializando txt...");
            System.out.println("Puedes consultar el catalogo en el archivo txt, al igual que las imagenes que se te mandaron");

            dos.close();
            dis.close();
            ois.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}