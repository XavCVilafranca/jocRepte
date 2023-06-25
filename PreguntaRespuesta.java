package joc;

public class PreguntaRespuesta {
    private String pregunta;
    private String respuesta;
    private int puntuacion;

    public PreguntaRespuesta(String pregunta, String respuesta, int puntuacion) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.puntuacion = puntuacion;
    }

    public String getPregunta() {
        return pregunta;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void restarPuntuacion() {
        this.puntuacion -= 5;
    }

    public boolean validarRespuesta(String respuesta) {
        return this.respuesta.equals(respuesta);
    }

}