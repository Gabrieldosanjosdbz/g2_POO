package dominio;

// Mede a chuva. De vez em quando vem uma pancada e o valor sobe bastante.
public class SensorPluviometro extends SensorSimulado {

    public SensorPluviometro(String id, String nome, String regiao) {
        super(id, nome, regiao, TipoSensor.CHUVA, 15);
    }

    @Override
    protected double proximoValor(double anterior) {
        double variacao = random.nextGaussian() * 8;
        if (random.nextDouble() < 0.10) {
            variacao += 25; // pancada de chuva
        }
        return anterior + variacao;
    }
}
