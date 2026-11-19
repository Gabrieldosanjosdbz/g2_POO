package servico;

import dominio.Medicao;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Bonus: salva o historico num arquivo CSV (abre no Excel) 
public class ExportadorCsv {

    public void exportar(List<Medicao> medicoes, String arquivo) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(arquivo))) {
            out.println("id;sensor;tipo;valor;unidade;data_hora");
            for (Medicao m : medicoes) {
                // virgula no decimal pra abrir certo no Excel em portugues
                String valor = String.valueOf(m.getValor()).replace('.', ',');
                out.println(m.getId() + ";" + m.getSensorId() + ";" + m.getTipo() + ";" + valor + ";"
                        + m.getTipo().getUnidade() + ";" + m.getDataHora().withNano(0));
            }
        }
    }
}
