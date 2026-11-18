package dominio;

import excecao.FonteIndisponivelException;

import java.util.Random;

// Parte comum dos sensores: sorteia a leitura e de vez em quando falha
public abstract class SensorSimulado extends ComponenteTerritorial implements Sensor {

    protected Random random = new Random();
    protected TipoSensor tipo;
    protected double valorAtual;
    private boolean ligado = true;

    public SensorSimulado(String id, String nome, String regiao, TipoSensor tipo, double valorInicial) {
        super(id, nome, regiao);
        this.tipo = tipo;
        this.valorAtual = valorInicial;
    }

    // cada sensor decide como o valor muda de uma leitura pra outra
    protected abstract double proximoValor(double anterior);

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!ligado) {
            throw new FonteIndisponivelException(id, "sensor desligado");
        }
        // 5% de chance do sensor nao responder
        if (random.nextDouble() < 0.05) {
            status = StatusComponente.INSTAVEL;
            throw new FonteIndisponivelException(id, "sensor nao respondeu");
        }
        status = StatusComponente.OPERANTE;

        // 5% de chance de vir um valor com defeito (negativo)
        if (random.nextDouble() < 0.05) {
            return -1 - random.nextInt(20);
        }

        valorAtual = tipo.limitarNaFaixa(proximoValor(valorAtual));
        return Math.round(valorAtual * 10) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return ligado;
    }

    @Override
    public String verificarStatus() {
        return id + " - " + nome + " (" + regiao + "): " + status;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }

    public void ligar() {
        ligado = true;
        status = StatusComponente.OPERANTE;
    }

    public void desligar() {
        ligado = false;
        status = StatusComponente.DESLIGADO;
    }

    public boolean estaLigado() {
        return ligado;
    }
}
