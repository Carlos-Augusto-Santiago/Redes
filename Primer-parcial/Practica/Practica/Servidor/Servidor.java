package Servidor;

import java.net.*;
import java.io.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000);
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde" + cl.getInetAddress() + ":" + cl.getPort());

                byte[] img = new byte[100];

                // Agregando productos al catalogo
                Catalogo ct = new Catalogo();
                ct.addProduct("Playera azul", 200, "Playera de tela 100% algodon", 10, img);
                ct.addProduct("Playera roja", 200, "Playera de tela 100% algodon", 10, img);
                ct.addProduct("Playera verde", 200, "Playera de tela 100% algodon", 10, img);
                ct.addProduct("Playera amarilla", 200, "Playera de tela 100% algodon", 10, img);
                ct.addProduct("Playera magenta", 200, "Playera de tela 100% algodon", 10, img);
                ct.addProduct("Playera rosa", 200, "Playera de tela 100% algodon", 10, img);

                // Serializacion del catalogo
                // crear el archivo
                File file = new File("Catalogo.txt");
                FileOutputStream fileS = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fileS);

                // serializar el objeto
                System.out.println("\nSerializando...");
                oos.writeObject(ct);
                oos.close();

                // Enviando archivo al servidor
                DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                // nombre del archivo
                dos.writeUTF(file.getName());
                dos.flush();
                // tamaño
                long tam = file.length();
                dos.writeLong(tam);
                dos.flush();
                byte[] b = new byte[6 * 1024];
                long enviados = 0;
                int porcentaje, n;
                while (enviados < tam) {
                    n = dis.read(b);
                    // se escribe el contenido del archivo
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados = enviados + n;
                    porcentaje = (int) (enviados * 100 / tam);
                    System.out.print("Enviado: " + porcentaje + "%\r");
                }
                System.out.print("\nArchivo enviado\n");
                dis.close();
                dos.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }

}