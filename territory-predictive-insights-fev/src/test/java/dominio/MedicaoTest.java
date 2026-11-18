package dominio;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MedicaoTest {

    @Test
    void deveCriarMedicaoValida() throws Exception {
        // Arrange
        double chuva = 42.5;

        // Act
        Medicao medicao = new Medicao("PLV-01", TipoSensor.CHUVA, chuva, LocalDateTime.now());

        // Assert
        assertEquals("PLV-01", medicao.getSensorId());
        assertEquals(42.5, medicao.getValor());
    }

    @Test
    void chuvaNegativaDeveLancarExcecao() {
        // Arrange
        double chuvaNegativa = -3;

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLV-01", TipoSensor.CHUVA, chuvaNegativa, LocalDateTime.now()));
    }

    @Test
    void umidadeAcimaDe100DeveLancarExcecao() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("UMS-01", TipoSensor.UMIDADE_SOLO, 120, LocalDateTime.now()));
    }
}
