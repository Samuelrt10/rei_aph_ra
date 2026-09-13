package com.rei.aph;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class SimulationRecord {

    @ServerTimestamp
    private Date fechaSimulacion;

    private String nombreEvaluador; // NUEVO CAMPO AGREGADO AL CÓDIGO
    private String nivelDeEstudios;
    private String toxidromeReal;
    private String toxidromeElegido;
    private boolean acierto;
    private long tiempoDecisionSegundos;
    private int cantidadSintomas;
    private List<Integer> ordenSintomas;

    public SimulationRecord() {
    }

    public SimulationRecord(String nombreEvaluador, String nivelDeEstudios, String toxidromeReal, String toxidromeElegido, 
                            boolean acierto, long tiempoDecisionSegundos, int cantidadSintomas, 
                            List<Integer> ordenSintomas) {
        this.nombreEvaluador = nombreEvaluador;
        this.nivelDeEstudios = nivelDeEstudios;
        this.toxidromeReal = toxidromeReal;
        this.toxidromeElegido = toxidromeElegido;
        this.acierto = acierto;
        this.tiempoDecisionSegundos = tiempoDecisionSegundos;
        this.cantidadSintomas = cantidadSintomas;
        this.ordenSintomas = ordenSintomas;
    }

    public Date getFechaSimulacion() { return fechaSimulacion; }
    public String getNombreEvaluador() { return nombreEvaluador; }
    public String getNivelDeEstudios() { return nivelDeEstudios; }
    public String getToxidromeReal() { return toxidromeReal; }
    public String getToxidromeElegido() { return toxidromeElegido; }
    public boolean isAcierto() { return acierto; }
    public long getTiempoDecisionSegundos() { return tiempoDecisionSegundos; }
    public int getCantidadSintomas() { return cantidadSintomas; }
    public List<Integer> getOrdenSintomas() { return ordenSintomas; }
}
