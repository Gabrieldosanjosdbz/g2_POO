package dominio;

import excecao.DadosInvalidosException;
import servico.ConfiguracaoSistema;

// Dados do territorio. Pra mudar os limiares precisa do codigo de acesso.
public class DadosTerritorio {

    private String nome;
    private String regiao;
    private int populacao;
    private String codigoAcesso;
    private ConfiguracaoSistema config;

    public DadosTerritorio(String nome, String regiao, int populacao, String codigoAcesso,
                           ConfiguracaoSistema config) throws DadosInvalidosException {
        this.nome = nome;
        this.regiao = regiao;
        this.codigoAcesso = codigoAcesso;
        this.config = config;
        setPopulacao(populacao);
    }

    public void alterarLimiares(String codigo, TipoSensor tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (!codigoAcesso.equals(codigo)) {
            throw new DadosInvalidosException("codigo de acesso incorreto, limiares nao alterados");
        }
        if (atencao <= 0) {
            throw new DadosInvalidosException("o limiar de ATENCAO precisa ser maior que zero");
        }
        if (atencao >= alerta || alerta >= critico) {
            throw new DadosInvalidosException("os limiares precisam seguir ATENCAO < ALERTA < CRITICO");
        }
        config.setLimiares(tipo, new Limiares(atencao, alerta, critico));
    }

    public Limiares getLimiares(TipoSensor tipo) {
        return config.getLimiares(tipo);
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public int getPopulacao() {
        return populacao;
    }

    public void setPopulacao(int populacao) throws DadosInvalidosException {
        if (populacao < 0) {
            throw new DadosInvalidosException("populacao nao pode ser negativa: " + populacao);
        }
        this.populacao = populacao;
    }
}
