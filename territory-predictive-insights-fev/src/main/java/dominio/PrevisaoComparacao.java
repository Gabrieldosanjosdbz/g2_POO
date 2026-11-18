package dominio;

import java.util.List;

// Compara as ultimas N leituras com as N anteriores e projeta a tendencia.
// Ex: se a media subiu 10, aposta que vai subir mais 10.
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private int janela;

    public PrevisaoComparacao(int janela) {
        super("Comparacao de periodos", janela * 2);
        this.janela = janela;
    }

    @Override
    public ResultadoPrevisao prever(String sensorId, TipoSensor tipo, List<Medicao> historico) {
        if (historico.size() < minimoLeituras) {
            return semLeiturasSuficientes(sensorId, tipo, historico.size());
        }
        int fim = historico.size();
        double recente = media(historico, fim - janela, fim);
        double anterior = media(historico, fim - 2 * janela, fim - janela);
        double variacao = recente - anterior;
        double previsto = tipo.limitarNaFaixa(recente + variacao);

        String tendencia = variacao > 0 ? "SUBINDO" : "caindo ou estavel";
        String explicacao = String.format("tendencia %s (media anterior %.1f -> recente %.1f)",
                tendencia, anterior, recente);

        return new ResultadoPrevisao(sensorId, tipo, nome, previsto, true, explicacao);
    }

    @Override
    public String descrever() {
        return super.descrever() + " - antecipa subidas";
    }
}
