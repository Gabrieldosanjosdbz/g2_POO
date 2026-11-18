package dominio;

// Os tres limites de alerta de um tipo de sensor
public class Limiares {

    private double atencao;
    private double alerta;
    private double critico;

    public Limiares(double atencao, double alerta, double critico) {
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    // devolve o nivel mais grave que o valor passou, ou null se esta tudo ok
    public NivelAlerta classificar(double valor) {
        if (valor >= critico) return NivelAlerta.CRITICO;
        if (valor >= alerta) return NivelAlerta.ALERTA;
        if (valor >= atencao) return NivelAlerta.ATENCAO;
        return null;
    }

    public double getLimite(NivelAlerta nivel) {
        if (nivel == NivelAlerta.CRITICO) return critico;
        if (nivel == NivelAlerta.ALERTA) return alerta;
        return atencao;
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }

    @Override
    public String toString() {
        return "atencao " + atencao + " | alerta " + alerta + " | critico " + critico;
    }
}
