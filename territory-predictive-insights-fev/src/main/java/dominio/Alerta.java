package dominio;

import java.time.LocalDateTime;

public class Alerta {

    private long id;
    private String sensorId;
    private TipoSensor tipo;
    private NivelAlerta nivel;
    private double valorPrevisto;
    private double limite;
    private String estrategia;
    private LocalDateTime dataHora;

    public Alerta(long id, String sensorId, TipoSensor tipo, NivelAlerta nivel, double valorPrevisto,
                  double limite, String estrategia, LocalDateTime dataHora) {
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.estrategia = estrategia;
        this.dataHora = dataHora;
    }

    // explica o porque do alerta: sensor, valor previsto e limite que passou
    public String getMotivo() {
        return String.format("%s (%s): previsao de %.1f %s (%s) passou o limite de %s (%.1f %s)",
                sensorId, tipo.getDescricao(), valorPrevisto, tipo.getUnidade(), estrategia,
                nivel, limite, tipo.getUnidade());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "[" + nivel + "] " + dataHora.withNano(0) + " - " + getMotivo()
                + "\n      O que fazer: " + nivel.getRecomendacao();
    }
}
