
import java.net.*;
import java.util.ArrayList;
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

            int opc;
            String ok = "";
            filesRecibidos("Playera_1.jpeg", dis);
            filesRecibidos("Playera_2.jpeg", dis);
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
                opc = Integer.parseInt(br.readLine());

                switch (opc) {
                    case 1:
                        // Enviando el producto que se esta comprando
                        buying(br, dos, dis);
                        break;
                    case 2:
                        // Checando productos del carrito
                        // Si el carrito esta vacio devolver que no hay productos
                        if (carrito.isEmpty()) {
                            System.out.println("El carrito esta vacio");
                            ok = "no";
                        } else {
                            printCarrito();
                        }
                        break;
                    case 3:
                        // Borrar productos del carrito
                        if (carrito.isEmpty()) {
                            System.out.println("El carrito esta vacio");
                            ok = "no";
                        } else {
                            System.out.println("¿Que produto quieres borrar del carrito");
                            System.out.printf("Ingresa el numero del producto: ");
                            int numProduct = Integer.parseInt(br.readLine());
                            catalogo.remove(numProduct);
                            System.out.println("Carrito actualizado:");
                            printCarrito();
                        }
                        break;
                    case 4:
                        if (carrito.isEmpty()) {
                            System.out.println("El carrito esta vacio");
                            ok = "no";
                        } else {
                            for (int i = 0; i < carrito.size(); i++) {
                                carrito.remove(i);
                            }
                            System.out.println("El carrito esta vacio");
                        }
                        break;
                    case 5:
                        if (carrito.isEmpty()) {
                            System.out.println("El carrito esta vacio");
                            ok = "no";
                        } else {
                            // Generar el archivo pdf con los articulos del carrito
                            System.out.println("El carrito fue comprador");
                            crearPDF();
                        }
                        break;
                    default:
                        System.out.println("Opcion no valida");
                }

            } while (opc != 6);
            dos.writeUTF(ok);
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

    // Imprimir el catalogo
    public static void printCarrito() {
        for (int i = 0; i < carrito.size(); i++) {
            System.out.println("Productos del carrito");
            System.out.println("Num Producto: " + (i + 1));
            System.out.println("Nombre: " + carrito.get(i).name);
            System.out.println("Precio: $" + carrito.get(i).price);
            System.out.println("Descripcion: " + carrito.get(i).desc);
            System.out.println("Stock: " + carrito.get(i).stock);
            System.out.println();
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

        // 1. Create document
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        // 2. Create PdfWriter
        PdfWriter.getInstance(document, new FileOutputStream("result.pdf"));

        // 3. Open document
        document.open();

        // 4. Add content
        document.add(new Paragraph("Create Pdf Document with iText in Java"));

        // 5. Close document
        document.close();
        // Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        // // OutputStream outdoc = new OutputStream(new File("test.pdf"));
        // // Crea la instancia de pdf
        // PdfWriter.getInstance(doc, new FileOutputStream("test.pdf"));
        // doc.open();

        // Paragraph paragraph = new Paragraph();
        // // Crear el ticket de compra
        // for (int i = 0; i < carrito.size(); i++) {
        // paragraph.add("Ticket de Compra");
        // paragraph.add("Num Producto: " + (i + 1));
        // paragraph.add("Nombre: " + carrito.get(i).name);
        // paragraph.add("Precio: $" + carrito.get(i).price);
        // paragraph.add("Descripcion: " + carrito.get(i).desc);
        // paragraph.add("Stock: " + carrito.get(i).stock);

        // }

        // doc.close();

    }
}