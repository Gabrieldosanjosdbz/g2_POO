package dominio;

import java.util.List;

// Molde das previsoes. Cada filha calcula de um jeito diferente.
public abstract class EstrategiaPrevisao {

    protected String nome;
    protected int minimoLeituras;

    public EstrategiaPrevisao(String nome, int minimoLeituras) {
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
    }

    // o historico vem da leitura mais antiga pra mais nova
    public abstract ResultadoPrevisao prever(String sensorId, TipoSensor tipo, List<Medicao> historico);

    public String descrever() {
        return nome + " (precisa de " + minimoLeituras + " leituras)";
    }

    // com poucas leituras a previsao nao e confiavel, entao nao arrisca
    protected ResultadoPrevisao semLeiturasSuficientes(String sensorId, TipoSensor tipo, int quantidade) {
        return new ResultadoPrevisao(sensorId, tipo, nome, 0, false,
                "so " + quantidade + " leitura(s) no banco, precisa de " + minimoLeituras + ". Sem previsao.");
    }

    // media das leituras da posicao inicio ate fim (sem incluir o fim)
    protected double media(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += historico.get(i).getValor();
        }
        return soma / (fim - inicio);
    }

    public String getNome() {
        return nome;
    }
}
