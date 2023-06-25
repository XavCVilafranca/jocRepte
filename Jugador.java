package joc;

public class Jugador {
    private String nombre;
    private String contrasena;

    public Jugador(String nombre, String contrasena) {
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    
    public boolean validarContrasena(String contrasena) {
        return this.contrasena.equals(new String(contrasena));
    }

}
