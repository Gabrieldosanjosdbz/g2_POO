package dominio;

// O que a previsao devolve: o valor e uma explicacao do que ele significa
public class ResultadoPrevisao {

    private String sensorId;
    private TipoSensor tipo;
    private String estrategia;
    private double valorPrevisto;
    private boolean confiavel;
    private String explicacao;

    public ResultadoPrevisao(String sensorId, TipoSensor tipo, String estrategia, double valorPrevisto,
                             boolean confiavel, String explicacao) {
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.estrategia = estrategia;
        this.valorPrevisto = valorPrevisto;
        this.confiavel = confiavel;
        this.explicacao = explicacao;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public boolean isConfiavel() {
        return confiavel;
    }

    public String getExplicacao() {
        return explicacao;
    }

    @Override
    public String toString() {
        if (!confiavel) {
            return sensorId + ": " + explicacao;
        }
        return String.format("%s: previsto %.1f %s - %s", sensorId, valorPrevisto, tipo.getUnidade(), explicacao);
    }
}
