import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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

            filesRecibidos("Playera_1.jpeg", dis);
            filesRecibidos("Playera_2.jpeg", dis);

            int opc;
            do {
                System.out.println("\tBienvenido a la tienda de playeras");
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
                System.out.println("3. Eliminar un producto del carrito");
                System.out.println("4. Agregar mas productos de lo que ya tienes en el carrito");
                System.out.println("5. Comprar lo que hay en el carrito");
                System.out.println("6. Salir");
                System.out.printf("Opcion: ");
                opc = Integer.parseInt(br.readLine());
                dos.writeInt(opc);

                switch (opc) {
                    case 1:
                        // Enviando el producto que se esta comprando
                        //dos.writeUTF("b");
                        buying(br, dos);
                        break;
                    case 2:
                        // Checando productos del carrito
                        // Si el carrito esta vacio devolver que no hay productos
                        dos.writeUTF("nada");
                        if (carrito.isEmpty()) {
                            System.out.println("El carrito esta vacio");
                        } else {
                            System.out.println("Productos del carrito");
                            printCarrito();
                        }
                        System.out.println("Press Any Key To Continue...");
                        new java.util.Scanner(System.in).nextLine();
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        break;
                    case 3:
                        // Borrar productos del carrito
                        if (carrito.isEmpty()) {
                            dos.writeUTF("nada");
                            System.out.println("El carrito esta vacio");
                        } else {
                            dos.writeUTF("r");
                            System.out.println("¿Que producto quieres borrar del carrito?");
                            System.out.println("Productos del carrito");
                            printCarrito();
                            System.out.printf("Ingresa el numero del producto: ");
                            int numProduct = Integer.parseInt(br.readLine());
                            deleteProduct(numProduct, dos, br);
                            System.out.println("Carrito actualizado:");
                            printCarrito();
                        }
                        System.out.println("Press Any Key To Continue...");
                        new java.util.Scanner(System.in).nextLine();
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        break;
                    case 4:
                        if (carrito.isEmpty()) {
                            dos.writeUTF("nada");
                            System.out.println("El carrito esta vacio");
                        } else {
                            dos.writeUTF("u");
                            System.out.println("En tu carrito tienes: ");
                            printCarrito();
                            System.out.printf("¿De que producto quiere mas articulos?: ");
                            int numProduct = Integer.parseInt(br.readLine());
                            modifyCarrito(numProduct, br, dos);
                        }
                        System.out.println("Press Any Key To Continue...");
                        new java.util.Scanner(System.in).nextLine();
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        break;
                    case 5:
                        dos.writeUTF("nada");
                        if (carrito.isEmpty()) {
                            System.out.println("El carrito esta vacio");
                        } else {
                            crearPDF();
                            for(int i = 0; i < carrito.size(); i++)
                            {
                                carrito.remove(i);
                            }
                        }
                        System.out.println("Press Any Key To Continue...");
                        new java.util.Scanner(System.in).nextLine();
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        break;
                    case 6:
                        System.out.println("Vuelva pronto!!");
                        dos.writeInt(opc);
                        break;
                    default:
                        System.out.println("Opcion incorrecta");
                        break;
                }
            } while (opc != 6);

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
            System.out.print("\nNuestro catalogo es\n");
            // Deserializando el archivo
            FileInputStream file = new FileInputStream(nombre);
            ObjectInputStream ois = new ObjectInputStream(file);
            System.out.println();
            for (int i = 0; i < numProducts; i++) {
                Producto aux = (Producto) ois.readObject();
                System.out.println("Num Producto: " + aux.id);
                System.out.println("Nombre: " + aux.name);
                System.out.println("Precio: $" + aux.price);
                System.out.println("Descripcion: " + aux.desc);
                System.out.println("Stock: " + aux.cant);
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
    public static void buying(BufferedReader br, DataOutputStream dos) {
        try {
            System.out.println("¿Que producto desea comprar?");
            System.out.printf("Ingresa el numero del producto: ");
            int num = Integer.parseInt(br.readLine());
            System.out.printf("¿Cuantos productos vas a comprar?: ");
            int numProducts = Integer.parseInt(br.readLine());
            System.out.println("Se estan comprobando existencias de su producto, por favor espere");
            if (numProducts > catalogo.get(num - 1).getCant()) {
                System.out.println("No puedes comprar esta cantidad de productos.");
                dos.writeInt(num);
                dos.writeInt(0);
            } else {
                dos.writeInt(num);
                dos.writeInt(numProducts);
                carrito.add(catalogo.get(num - 1));
                for (int i = 0; i < carrito.size(); i++) {
                    System.out.println("id " + carrito.get(i).getId());
                    if (carrito.get(i).getId() == num) {
                        carrito.get(i).setCant(numProducts);
                        int aux = carrito.get(i).getCant();
                        System.out.println("Se agregaron " + aux + " productos al carrito");
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // Imprimir el catalogo
    public static void printCarrito() {
        for (int i = 0; i < carrito.size(); i++) {
            System.out.println("Num Producto: " + carrito.get(i).getId());
            System.out.println("Nombre: " + carrito.get(i).getName());
            System.out.println("Precio: $" + carrito.get(i).getPrice());
            System.out.println("Descripcion: " + carrito.get(i).getDesc());
            System.out.println("Cantidad: " + carrito.get(i).getCant());
            System.out.println();
        }
    }

    public static void modifyCarrito(int numProduct, BufferedReader br, DataOutputStream dos) throws IOException {
        for (int i = 0; i < carrito.size(); i++) {
            if (carrito.get(i).getId() == numProduct) {
                System.out.println("Se tienen " + catalogo.get(numProduct - 1).getCant() + " productos de "
                        + catalogo.get(numProduct - 1).getName());
                System.out.printf("¿Cuantos productos vas a agregar?: ");
                int cant = Integer.parseInt(br.readLine());
                if (cant > catalogo.get(numProduct - 1).getCant()) {
                    System.out.println("No puedes agregar mas productos de lo que hay en stock");
                    dos.writeInt(numProduct - 1);
                    dos.writeInt(0);
                } else {
                    dos.writeInt(numProduct - 1);
                    dos.writeInt(cant);
                    int aux = carrito.get(i).getCant();
                    carrito.get(i).setCant(aux + cant);
                    System.out.println("Se agregaron " + cant + " del producto " + carrito.get(i).getName());
                }
            }

        }
    }

    public static void deleteProduct(int numProduct, DataOutputStream dos, BufferedReader br) throws IOException {
        for (int i = 0; i < carrito.size(); i++) {
            if (carrito.get(i).getId() == numProduct) {
                // Enviar el numero de producto a quitar
                dos.writeInt(numProduct - 1);
                if (carrito.get(i).getCant() > 1) {
                    System.out.printf("Ingresa la cantidad de productos a cancelar: ");
                    int cant = Integer.parseInt(br.readLine());
                    if (cant > carrito.get(i).getCant()) {
                        System.out.println("No puedes eliminar esta cantidad de productos.");
                        dos.writeInt(0);
                    } else if (cant == carrito.get(i).getCant()) {
                        // Enviar la cantidad de productos que eran
                        dos.writeInt(cant);
                        // Se elimina el producto del carrito
                        carrito.remove(i);
                    } else if (cant < carrito.get(i).getCant()) {
                        // Enviar la cantidad de productos que eran
                        dos.writeInt(cant);
                        // obtener cantidad que tiene
                        int aux = carrito.get(i).getCant();
                        carrito.get(i).setCant(aux - cant);
                    }
                } else {
                    // Enviar la cantidad de productos que eran
                    dos.writeInt(1);
                    // Se elimina el producto del carrito
                    carrito.remove(i);
                }
            }
        }
    }

    public static void filesRecibidos(String fileName, DataInputStream dis) throws Exception {
        int bytes = 0;
        FileOutputStream fo = new FileOutputStream(fileName);
        long size = dis.readLong();
        byte[] buffer = new byte[4 * 1024];

        while (size > 0 && (bytes = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fo.write(buffer, 0, bytes);
            size -= bytes;
        }
        fo.close();
    }

    public static void crearPDF() throws Exception {

        int total = 0;
        int cantidad = 0;
        Date fecha = new Date();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        // OutputStream outdoc = new OutputStream(new File("test.pdf"));
        // Crea la instancia de pdf
        PdfWriter.getInstance(doc, new FileOutputStream("test.pdf"));
        doc.open();

        // Crear el ticket de compra
        for (int i = 0; i < carrito.size(); i++) {
            doc.add(new Paragraph("Ticket de Compra"));
            doc.add(new Paragraph("Num Producto: " + carrito.get(i).getId()));
            doc.add(new Paragraph("Nombre: " + carrito.get(i).getName()));
            doc.add(new Paragraph("Precio: $" + carrito.get(i).getPrice()));
            doc.add(new Paragraph("Descripcion: " + carrito.get(i).getDesc()));
            doc.add(new Paragraph("Cantidad: " + carrito.get(i).getCant()));
            total = carrito.get(i).getPrice() + total;
            cantidad = carrito.get(i).getCant() + cantidad;

        }
        doc.add(new Paragraph("El total de la compra fue de: " + total));
        doc.add(new Paragraph("El total de productos fue: " + cantidad));
        doc.add(new Paragraph("Emitido: " + fecha));
        doc.close();

    }
}