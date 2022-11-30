
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Cliente {

    static int numProducts;
    static String nombre;
    static String op;
    static long tam;
    static ArrayList<Producto> catalogo = new ArrayList<Producto>();
    static ArrayList<Producto> carrito = new ArrayList<Producto>();

    public static void main(String[] args) {
        try {
            // Conectando con el servidor
            // Scanner scanner = new Scanner(System.in);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la direccion del servidor: ");
            String host = br.readLine();
            System.out.printf("\nEscriba el puerto: ");
            int pto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(cl.getInputStream());

            do {
                // Recibir el archivo serializado
                // recibir la cantidad de productos
                numProducts = dis.readInt();
                // se recibe el nombre del archivo
                nombre = dis.readUTF();
                // se recibe el tamaño del archivo
                tam = dis.readLong();
                // Recibir el archivo serializado
                recieveFile(nombre, tam, dis);
                // Menu de opciones
                System.out.println("¿Que deseas realizar?");
                System.out.println("1. Comprar productos");
                System.out.println("2. Checar el carrito de compras");
                System.out.println("3. Eliminar productos del carrito");
                System.out.println("4. Cancelar todo el carrito");
                System.out.println("5. Comprar lo que hay en el carrito");
                System.out.println("6. Salir");
                int opc = Integer.parseInt(br.readLine());

                switch (opc) {
                    case 1:
                        // Enviando el producto que se esta comprando
                        buying(br, dos, dis);
                        break;
                    case 2:
                        // Checando productos del carrito
                        // Si el carrito esta vacio devolver que no hay productos
                        if (carrito.size() == 0) {
                            System.out.println("El carrito esta vacio");
                        } else {
                            for (int i = 0; i < carrito.size(); i++) {
                                System.out.println("Productos del carrito");
                                System.out.println("Nombre: " + carrito.get(i).name);
                                System.out.println("Precio: $" + carrito.get(i).price);
                                System.out.println("Descripcion: " + carrito.get(i).desc);
                                System.out.println("Stock: " + carrito.get(i).stock);
                                System.out.println();
                            }
                        }
                        break;
                    case 3:
                        // Borrar productos del carrito
                        if (carrito.size() == 0) {
                            System.out.println("El carrito esta vacio");
                        } else {
                            System.out.println("¿Que produto quieres borrar del carrito");
                            
                        }
                        break;
                }
                System.out.printf("¿Quiere continuar comprando? \nTipea no para salir: ");
                op = br.readLine();
                // Enviar al servidor la respuesta
                dos.writeUTF(op);
                // aux--;
            } while (!op.equals("no"));

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
            }
            System.out.print("\nCatalogo\n");

            // Deserializando el archivo
            FileInputStream file = new FileInputStream(nombre);
            ObjectInputStream ois = new ObjectInputStream(file);
            System.out.println();
            System.out.println("El catalogo es: ");
            for (int i = 0; i < numProducts; i++) {
                Producto aux = (Producto) ois.readObject();
                System.out.println("Num Producto: " + (i + 1));
                System.out.println("Nombre: " + aux.name);
                System.out.println("Precio: $" + aux.price);
                System.out.println("Descripcion: " + aux.desc);
                System.out.println("Stock: " + aux.stock);
                System.out.println();
                catalogo.add(aux);
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
            carrito.add(catalogo.get(num - 1));
            System.out.println("Producto agregado al carrito");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}