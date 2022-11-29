package Servidor;

import java.io.Serializable;
import java.util.ArrayList;

public class Catalogo implements Serializable {
    // Creacion de arreglo de productos
    private ArrayList<Producto> catalogo = new ArrayList<Producto>();

    // Funcion para agregar producto
    public void addProduct(String name, int price, String desc, int stock, byte[] img) {
        catalogo.add(new Producto(name, price, desc, stock, img));
    }

    // Funcion para reducir el stock
    public void reduceStock(int pos) {
        int stock = catalogo.get(pos).getStock();
        catalogo.get(pos).setStock(stock - 1);
    }

    // funcion para borrar productos
    public void deleteProduct(int pos) {
        catalogo.remove(pos);
    }

    

}
