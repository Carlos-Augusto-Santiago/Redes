
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Cliente {

    static int numProducts;
    static String nombre;
    static long tam;
    static ArrayList<Producto> carrito = new ArrayList<Producto>();

    public static void main(String[] args) {
        try {
            // Conectando con el servidor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la direccion del servidor: ");
            String host = br.readLine();
            System.out.printf("\nEscriba el puerto: ");
            int pto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(cl.getInputStream());

            int aux = 3;
            
            while (aux > 0) {
                // Recibir el archivo serializado
                // recibir la cantidad de productos
                numProducts = dis.readInt();
                // se recibe el nombre del archivo
                nombre = dis.readUTF();
                System.out.println("Recibimos el archivo:" + nombre);
                // se recibe el tamaño del archivo
                tam = dis.readLong();
                // Recibir el archivo serializado
                recieveFile(nombre, tam, dis);

                // Enviando el producto que se esta comprando
                buying(br, dos, dis);
                aux--;
            }

            dis.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recieveFile(String nombre, long tam, DataInputStream dis) {
        try {
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
            }
            System.out.print("\nArchivo recibido.\n");

            // Deserializando el archivo
            FileInputStream file = new FileInputStream(nombre);
            ObjectInputStream ois = new ObjectInputStream(file);
            System.out.println("Deserializando txt...");
            System.out.println("El catalogo es: ");
            for (int i = 0; i < numProducts; i++) {
                Producto aux = (Producto) ois.readObject();
                System.out.println("Num Producto: " + (i + 1));
                System.out.println("Nombre: " + aux.name);
                System.out.println("Precio: $" + aux.price);
                System.out.println("Descripcion: " + aux.desc);
                System.out.println("Stock: " + aux.stock);
                System.out.println();
            }
            dos.close();
            ois.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // Comprando un producto
    public static void buying(BufferedReader br, DataOutputStream dos, DataInputStream dis) {
        try {
            System.out.println("¿Que producto desea comprar?");
            System.out.print("Ingresa el numero del producto: ");
            int num = Integer.parseInt(br.readLine());
            System.out.println("Se estan comprobando existencias de su producto, por favor espere");
            dos.writeInt(num);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}