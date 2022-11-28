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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        try {
            // Conexion al servidor
            Socket socket = new Socket("localhost", 3000);
            System.out.println("Conectado al servidor...");

            // Instancia de la clase Objeto
            Objeto obj = new Objeto();
            // Impresion de los atributos en consola.
            System.out.println("\nValores de los atributos: ");
            System.out.println("Entero: " + obj.num);
            System.out.println("Caracter: " + obj.c);
            System.out.println("Boleano: " + obj.bool);
            System.out.println("Flotante: " + obj.f);
            System.out.println("Cadena de texto: " + obj.s);
            System.out.println("Doble: " + obj.d);

            // crear el archivo
            File file = new File("serializado.txt");
            FileOutputStream fileS = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fileS);

            // serializar el objeto
            System.out.println("\nSerializando...");
            oos.writeObject(obj.num);
            oos.writeObject(obj.c);
            oos.writeObject(obj.bool);

            // Imprimendo los atributos ya serializados
            System.out.println("\nAtributos serializados:");
            oos.close();

            // Enviando archivo al servidor
            DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
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
            System.out.print("\n\nArchivo enviado\n");
            dis.close();
            dos.close();

            socket.close();
        } catch (Exception e) {
        }

    }

}