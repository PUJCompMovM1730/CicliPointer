package entities;

/**
 * Created by Felipe on 19/10/2017.
 */

public class Usuario {

    private int peso;// kg
    private int edad; // años
    private double altura; //metros
    private String RH;

    public Usuario() {
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public String getRH() {
        return RH;
    }

    public void setRH(String RH) {
        this.RH = RH;
    }
}
