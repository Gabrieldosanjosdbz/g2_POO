package dominio;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrevisaoMediaMovelTest {

    @Test
    void deveCalcularMediaDasUltimasCincoLeituras() {
        // Arrange: os dois 100 do comeco nao podem entrar na conta
        List<Medicao> historico = HistoricoFake.criar("PLV-01", TipoSensor.CHUVA, 100, 100, 10, 20, 30, 40, 50);
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5);

        // Act
        ResultadoPrevisao resultado = previsao.prever("PLV-01", TipoSensor.CHUVA, historico);

        // Assert
        assertTrue(resultado.isConfiavel());
        assertEquals(30.0, resultado.getValorPrevisto(), 0.001);
    }

    @Test
    void comPoucasLeiturasNaoDevePrever() {
        // Arrange
        List<Medicao> historico = HistoricoFake.criar("PLV-01", TipoSensor.CHUVA, 90, 95, 99);
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5);

        // Act
        ResultadoPrevisao resultado = previsao.prever("PLV-01", TipoSensor.CHUVA, historico);

        // Assert
        assertFalse(resultado.isConfiavel());
    }
}
