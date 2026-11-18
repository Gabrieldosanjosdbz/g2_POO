package dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Cria uma lista de medicoes pros testes, sem precisar do banco
public class HistoricoFake {

    public static List<Medicao> criar(String sensorId, TipoSensor tipo, double... valores) {
        List<Medicao> lista = new ArrayList<>();
        for (int i = 0; i < valores.length; i++) {
            lista.add(new Medicao(i + 1, sensorId, tipo, valores[i], LocalDateTime.now()));
        }
        return lista;
    }
}
