package dominio;

// Mede o nivel da agua do corrego, que muda devagar
public class SensorNivelCorrego extends SensorSimulado {

    public SensorNivelCorrego(String id, String nome, String regiao) {
        super(id, nome, regiao, TipoSensor.NIVEL_CORREGO, 1.5);
    }

    @Override
    protected double proximoValor(double anterior) {
        return anterior + 0.05 + random.nextGaussian() * 0.25;
    }
}
