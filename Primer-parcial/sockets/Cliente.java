import java.net.*;
import java.io.*;

public class Cliente {
    public static void main(String[] args) {
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la direccion del servidor: ");
            String host = br1.readLine();
            System.out.printf("\n\nEscriba el puerto:");
            int pto = Integer.parseInt(br1.readLine());
            Socket cl = new Socket(host, pto);
            DataOutputStream salida = new DataOutputStream(cl.getOutputStream());
            DataInputStream entrada = new DataInputStream(cl.getInputStream());
            System.out.println("Recibimos un mensaje desde el servidor");
            String mensaje = entrada.readUTF();
            System.out.println("Mensaje:" + mensaje);
            // se envia de vuelta el mensaje al servidor
            salida.writeUTF(mensaje);
            br1.close();
            salida.close();
            entrada.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
