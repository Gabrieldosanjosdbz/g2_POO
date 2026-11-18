package dominio;

import excecao.DadosInvalidosException;

import java.time.LocalDateTime;

public class Medicao {

    private long id;
    private String sensorId;
    private TipoSensor tipo;
    private double valor;
    private LocalDateTime dataHora;

    // medicao nova: valida antes de criar, entao nada invalido vai pro banco
    public Medicao(String sensorId, TipoSensor tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("medicao sem sensor");
        }
        tipo.validar(valor);
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    // usado pelo DAO quando le do banco (ja foi validada na hora de gravar)
    public Medicao(long id, String sensorId, TipoSensor tipo, double valor, LocalDateTime dataHora) {
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
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

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "#" + id + " " + dataHora.withNano(0) + " " + sensorId + " = " + valor + " " + tipo.getUnidade();
    }
}
