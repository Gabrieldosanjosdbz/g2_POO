package dominio;

// Do mais leve pro mais grave. A ordem importa (Limiares usa).
public enum NivelAlerta {

    ATENCAO("Acompanhar de perto e checar os pontos de drenagem."),
    ALERTA("Avisar moradores dos pontos baixos e preparar abrigo."),
    CRITICO("Acionar Defesa Civil e orientar saida das areas de risco.");

    private final String recomendacao;

    NivelAlerta(String recomendacao) {
        this.recomendacao = recomendacao;
    }

    public String getRecomendacao() {
        return recomendacao;
    }
}
