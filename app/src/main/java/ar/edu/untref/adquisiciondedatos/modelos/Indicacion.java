package ar.edu.untref.adquisiciondedatos.modelos;

import java.io.Serializable;

/**
 * Created by garispe on 9/13/16.
 */
public class Indicacion implements Serializable {

    private float angulos;
    private int segundos;

    public float getAngulos() {
        return angulos;
    }

    public void setAngulos(float angulos) {
        this.angulos = angulos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }
}
