package servico;

import dominio.Limiares;
import dominio.TipoSensor;

import java.util.HashMap;
import java.util.Map;

// Singleton: so existe uma configuracao no sistema todo.
// Assim, quando o limiar muda no menu, muda pra todo mundo que usa.
public class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    private Map<TipoSensor, Limiares> limiares = new HashMap<>();
    private int janelaMediaMovel = 5;
    private int janelaComparacao = 3;

    // construtor privado: ninguem de fora consegue dar new
    private ConfiguracaoSistema() {
        restaurarPadroes();
    }

    public static ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public void restaurarPadroes() {
        limiares.put(TipoSensor.CHUVA, new Limiares(30, 50, 80));
        limiares.put(TipoSensor.NIVEL_CORREGO, new Limiares(2.5, 3.5, 4.5));
        limiares.put(TipoSensor.UMIDADE_SOLO, new Limiares(75, 85, 95));
    }

    public Limiares getLimiares(TipoSensor tipo) {
        return limiares.get(tipo);
    }

    public void setLimiares(TipoSensor tipo, Limiares novos) {
        limiares.put(tipo, novos);
    }

    public int getJanelaMediaMovel() {
        return janelaMediaMovel;
    }

    public int getJanelaComparacao() {
        return janelaComparacao;
    }
}
