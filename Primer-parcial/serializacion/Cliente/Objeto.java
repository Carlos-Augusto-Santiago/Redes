import java.io.Serializable;

public class Objeto implements Serializable {
    // Atributos
    int num = 10;
    char c = 'c';
    boolean bool = true;
    // Se coloca la palabra transient para no serializarlos
    transient float f = 2.5f;
    transient String s = "hola mundo";
    transient double d = 5.1;

    // Getters y Setters
    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public char getC() {
        return this.c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public boolean isValue() {
        return this.bool;
    }

    public void setValue(boolean value) {
        this.bool = value;
    }

    public float getF() {
        return this.f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public String getS() {
        return this.s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public double getD() {
        return this.d;
    }

    public void setD(double d) {
        this.d = d;
    }
}