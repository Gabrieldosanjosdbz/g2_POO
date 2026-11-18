package dominio;

import excecao.DadosInvalidosException;

// Cada tipo de sensor tem unidade e uma faixa de valores possiveis
public enum TipoSensor {

    CHUVA("Chuva", "mm/h", 0, 150),
    NIVEL_CORREGO("Nivel do corrego", "m", 0, 8),
    UMIDADE_SOLO("Umidade do solo", "%", 0, 100);

    private final String descricao;
    private final String unidade;
    private final double minimo;
    private final double maximo;

    TipoSensor(String descricao, String unidade, double minimo, double maximo) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public void validar(double valor) throws DadosInvalidosException {
        if (valor < minimo || valor > maximo) {
            throw new DadosInvalidosException(descricao + " fora da faixa: " + valor + " " + unidade
                    + " (aceito de " + minimo + " a " + maximo + ")");
        }
    }

    // nao deixa o valor passar dos limites da faixa
    public double limitarNaFaixa(double valor) {
        if (valor < minimo) return minimo;
        if (valor > maximo) return maximo;
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }
}
