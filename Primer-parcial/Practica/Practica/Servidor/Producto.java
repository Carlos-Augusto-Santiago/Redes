package Servidor;

public class Producto {
    // Atributos
    String name;
    int price;
    String desc;
    int stock;

    byte[] img = new byte[1024 * 8];

    // Clase constructora
    public Producto(String name, int price, String desc, int stock, byte[] img) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.stock = stock;
        this.img = img;
    }

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}