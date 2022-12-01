import java.io.Serializable;

public class Producto implements Serializable {
    // Atributos
    int id;
    String name;
    int price;
    String desc;
    int cant;

    // Clase constructora
    public Producto(int id, String name, int price, String desc, int cant) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.cant = cant;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCant() {
        return this.cant;
    }

    public void setCant(int stock) {
        this.cant = stock;
    }

}