package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// Factory: cria a estrategia de previsao pelo nome.
// Em vez de um monte de if, cada nome aponta pra quem sabe criar a estrategia.
public class FabricaPrevisao {

    private Map<String, Supplier<EstrategiaPrevisao>> estrategias = new LinkedHashMap<>();

    public FabricaPrevisao(ConfiguracaoSistema config) {
        estrategias.put("media-movel", () -> new PrevisaoMediaMovel(config.getJanelaMediaMovel()));
        estrategias.put("comparacao", () -> new PrevisaoComparacao(config.getJanelaComparacao()));
    }

    public EstrategiaPrevisao criar(String nome) throws DadosInvalidosException {
        Supplier<EstrategiaPrevisao> criador = estrategias.get(nome);
        if (criador == null) {
            throw new DadosInvalidosException("estrategia desconhecida: " + nome);
        }
        return criador.get();
    }

    public List<EstrategiaPrevisao> criarTodas() {
        List<EstrategiaPrevisao> todas = new ArrayList<>();
        for (Supplier<EstrategiaPrevisao> criador : estrategias.values()) {
            todas.add(criador.get());
        }
        return todas;
    }

    public List<String> getNomes() {
        return new ArrayList<>(estrategias.keySet());
    }
}
