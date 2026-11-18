package dominio;

// Umidade do solo na encosta (solo encharcado = risco de deslizamento)
public class SensorUmidadeSolo extends SensorSimulado {

    public SensorUmidadeSolo(String id, String nome, String regiao) {
        super(id, nome, regiao, TipoSensor.UMIDADE_SOLO, 60);
    }

    @Override
    protected double proximoValor(double anterior) {
        return anterior + 0.5 + random.nextGaussian() * 4;
    }
}
