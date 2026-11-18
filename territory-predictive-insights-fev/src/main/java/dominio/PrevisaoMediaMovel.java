package dominio;

import java.util.List;

// Previsao = media das ultimas N leituras
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Media movel", janela);
        this.janela = janela;
    }

    @Override
    public ResultadoPrevisao prever(String sensorId, TipoSensor tipo, List<Medicao> historico) {
        if (historico.size() < minimoLeituras) {
            return semLeiturasSuficientes(sensorId, tipo, historico.size());
        }
        int fim = historico.size();
        double previsto = media(historico, fim - janela, fim);

        return new ResultadoPrevisao(sensorId, tipo, nome, previsto, true,
                "media das ultimas " + janela + " leituras, o proximo valor deve ficar perto disso");
    }

    @Override
    public String descrever() {
        return super.descrever() + " - mostra o patamar atual";
    }
}
