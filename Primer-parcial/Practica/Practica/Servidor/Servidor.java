
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Servidor {
    static ArrayList<Producto> catalogo = new ArrayList<Producto>();
    static String op;

    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000);
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde" + cl.getInetAddress() + ":" + cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                byte[] img = new byte[100];

                // Agregando productos al catalogo
                addProduct("Playera azul", 200, "Playera de tela 100% algodon", 10, img);
                addProduct("Playera roja", 200, "Playera de tela 100% algodon", 10, img);
                addProduct("Playera verde", 200, "Playera de tela 100% algodon", 10, img);
                addProduct("Playera amarilla", 200, "Playera de tela 100% algodon", 10, img);
                addProduct("Playera magenta", 200, "Playera de tela 100% algodon", 10, img);
                addProduct("Playera rosa", 200, "Playera de tela 100% algodon", 10, img);

                // sendFiles("/home/spike/Documents/REDES_2/Redes/Primer-parcial/Practica/Practica/Servidor/imgs/awwww.jpeg",
                // dos);
                // sendFiles("/home/spike/Documents/REDES_2/Redes/Primer-parcial/Practica/Practica/Servidor/imgs/makima.jpeg",
                // dos);

                do {
                    // Serializar y enviar el catalogo
                    sendCatalogue(dos);
                    // Recibir que producto pidio el cliente
                    updateCatalogue(dis);
                    op = dis.readUTF();

                } while (!op.equals("no"));

                System.out.println("El cliente termino de realiar su compra");

                dos.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }

    // Funcion para agregar producto
    public static void addProduct(String name, int price, String desc, int stock, byte[] img) {
        catalogo.add(new Producto(name, price, desc, stock, img));
    }

    // Funcion para reducir el stock
    public static void reduceStock(int pos) {
        int stock = catalogo.get(pos).getStock();
        catalogo.get(pos).setStock(stock - 1);
    }

    // funcion para borrar productos
    public static void deleteProduct(int pos) {
        catalogo.remove(pos);
    }

    // Serializar y enviar catalogo
    public static void sendCatalogue(DataOutputStream dos) {
        try {
            // Serializacion del catalogo
            // crear el archivo
            File file = new File("Catalogo.txt");
            FileOutputStream fileS = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fileS);

            // serializar los productos
            System.out.println("\nSerializando...");
            for (int i = 0; i < catalogo.size(); i++) {
                oos.writeObject(catalogo.get(i));
            }
            oos.close();

            // Enviando archivo al servidor
            DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));

            // numero de productos que va a recibir el cliente
            dos.writeInt(catalogo.size());
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
            System.out.println();
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCatalogue(DataInputStream dis) throws IOException {
        // Recibir el producto que el usuario quiere comprar
        int numProduct = dis.readInt() - 1;
        // Comprobacion de exitencias
        int exist = catalogo.get(numProduct).getStock();
        if (exist >= 0) {
            System.out.println("Producto en existencia");
            System.out.println("Se tienen: " + exist);
            System.out.println("Reduciendo el stock de exitencias");
            catalogo.get(numProduct).setStock(exist - 1);
            int newStock = catalogo.get(numProduct).getStock();
            System.out.println("Quedan " + newStock + " productos");
        }
    }

    public static void sendFiles(String path, DataOutputStream dos) throws Exception {
        int bytes = 0;
        File img = new File(path);
        FileInputStream fs = new FileInputStream(img);

        // Mandando el tamaño del archivo
        dos.writeLong(img.length());
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fs.read(buffer)) != -1) {
            dos.write(buffer, 0, bytes);
            dos.flush();
        }
        fs.close();
    }
}