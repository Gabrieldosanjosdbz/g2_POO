package dominio;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrevisaoComparacaoTest {

    @Test
    void deveProjetarTendenciaDeSubida() {
        // Arrange: media anterior 2.0 e recente 3.0, entao a previsao deve ser 4.0
        List<Medicao> historico = HistoricoFake.criar("NIV-01", TipoSensor.NIVEL_CORREGO,
                1.8, 2.0, 2.2, 2.8, 3.0, 3.2);
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act
        ResultadoPrevisao resultado = previsao.prever("NIV-01", TipoSensor.NIVEL_CORREGO, historico);

        // Assert
        assertEquals(4.0, resultado.getValorPrevisto(), 0.001);
    }

    @Test
    void naoDevePreverChuvaNegativa() {
        // Arrange: queda forte daria previsao negativa
        List<Medicao> historico = HistoricoFake.criar("PLV-01", TipoSensor.CHUVA, 60, 60, 60, 5, 5, 5);
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act
        ResultadoPrevisao resultado = previsao.prever("PLV-01", TipoSensor.CHUVA, historico);

        // Assert
        assertEquals(0.0, resultado.getValorPrevisto(), 0.001);
    }
}
