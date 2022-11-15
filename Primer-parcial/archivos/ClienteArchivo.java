import javax.swing.JFileChooser;
import java.net.*;
import java.io.*;

public class ClienteArchivo {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la dirección del servidor:");
            String host = br.readLine();
            System.out.printf("\n\nEscribael puerto:");
            int pto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, pto);
            JFileChooser jf = new JFileChooser();
            jf.setMultiSelectionEnabled(true);
            int r = jf.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File[] f = jf.getSelectedFiles(); // Manejador
                // cuantos archivos se van a enviar
                int numFiles = f.length;
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                // enviamos el numFiles al servidor
                dos.writeInt(numFiles);
                // ciclo para obtener los datos de cada archivo
                for (int i = 0; i < numFiles; i++) {
                    String archivo = f[i].getAbsolutePath(); // Dirección
                    String nombre = f[i].getName(); // Nombre
                    long tam = f[i].length(); // Tamaño
                    DataInputStream dis = new DataInputStream(new FileInputStream(archivo));
                    // se envia el nombre del archivo
                    dos.writeUTF(nombre);
                    dos.flush();
                    // se envia el tamaño del archivo
                    dos.writeLong(tam);
                    dos.flush();
                    // se lee el archivo en paquetes
                    byte[] b = new byte[1024];
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
                    } // While
                    System.out.print("\n\nArchivo enviado\n");
                    dis.close();
                }
                cl.close();
                dos.close();
            } // if
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}