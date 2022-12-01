
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Servidor {
    static ArrayList<Producto> catalogo = new ArrayList<Producto>();
    static int num_products_deserialize = 6;

    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000);
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexion establecida desde" + cl.getInetAddress() + ":" + cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                // checar si ya existe un archivo de catalogo
                File ct = new File("Catalogo.txt");
                if (ct.exists()) {
                    // deserializar el archivo
                    deserializeFile(ct.getName());
                } else {
                    // Agregando productos al catalogo
                    addProduct(1, "Playera azul", 200, "Playera de tela 100% algodon", 10);
                    addProduct(2, "Playera roja", 200, "Playera de tela 100% algodon", 10);
                    addProduct(3, "Playera verde", 200, "Playera de tela 100% algodon", 10);
                    addProduct(4, "Playera amarilla", 200, "Playera de tela 100% algodon", 10);
                    addProduct(5, "Playera magenta", 200, "Playera de tela 100% algodon", 10);
                    addProduct(6, "Playera rosa", 200, "Playera de tela 100% algodon", 10);
                }

                sendFiles("amarilla.jpeg", dos);
                sendFiles("azul.jpeg",dos);
                sendFiles("magenta.jpeg", dos);
                sendFiles("roja.jpeg",dos);
                sendFiles("rosa.jpeg", dos);
                sendFiles("verde.jpeg",dos);

                int op;
                do {
                    // Serializar y enviar el catalogo
                    sendCatalogue(dos);
                    op = dis.readInt();
                    // reestablecer o reducir stock
                    String opc = dis.readUTF();
                    switch (opc) {
                        case "u":
                            updateCatalogue(dis);
                            break;
                        default:
                            break;
                    }
                } while (op != 6);

                System.out.println("El cliente termino de realizar su compra");

                dos.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }

    private static void deserializeFile(String nombre) {
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));

            // Deserializando el archivo
            FileInputStream file = new FileInputStream(nombre);
            ObjectInputStream ois = new ObjectInputStream(file);
            System.out.println();
            for (int i = 0; i < num_products_deserialize; i++) {
                Producto aux = (Producto) ois.readObject();
                catalogo.add(aux);
            }
            dos.close();
            ois.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // Funcion para agregar producto
    public static void addProduct(int id, String name, int price, String desc, int cant) {
        catalogo.add(new Producto(id, name, price, desc, cant));
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
            for (int i = 0; i < catalogo.size(); i++) {
                oos.writeObject(catalogo.get(i));
            }
            oos.close();

            // Enviando archivo al servidor
            DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));

            // numero de productos que va a recibir el cliente
            dos.writeInt(catalogo.size());
            num_products_deserialize = catalogo.size();
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
            }
            System.out.println();
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateCatalogue(DataInputStream dis) throws IOException {
        // Recibir el producto que el usuario compro    
        int product = dis.readInt();
        // Recibir la cantidad de productos a quitar
        int numProducts = dis.readInt();
        int stock = catalogo.get(product).getCant();
        catalogo.get(product).setCant(stock - numProducts);
        System.out
                .println(
                        "Se tienen " + catalogo.get(product).getCant() + " del producto " + catalogo.get(product).name);
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