package servico;

import dominio.Alerta;
import dominio.HistoricoFake;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.PrevisaoMediaMovel;
import dominio.ResultadoPrevisao;
import dominio.Sensor;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testa a logica do servico sem banco: os DAOs e o sensor sao mocks
@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    MedicaoDAO medicaoDAO;
    @Mock
    AlertaDAO alertaDAO;
    @Mock
    Sensor sensor;

    ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        config.restaurarPadroes(); // chuva: 30 / 50 / 80
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, config);
    }

    @Test
    void chuvaMuitoAltaDeveGerarAlertaCritico() throws Exception {
        // Arrange
        when(sensor.getId()).thenReturn("PLV-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.CHUVA);
        when(medicaoDAO.buscarHistoricoPorSensor("PLV-01", 30))
                .thenReturn(HistoricoFake.criar("PLV-01", TipoSensor.CHUVA, 85, 90, 95, 100, 105));
        when(alertaDAO.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        // Act
        ResultadoPrevisao resultado = servico.prever(sensor, new PrevisaoMediaMovel(5));
        Alerta alerta = servico.verificarAlerta(resultado);

        // Assert
        assertEquals(NivelAlerta.CRITICO, alerta.getNivel());
        assertEquals(95.0, alerta.getValorPrevisto(), 0.001);
        assertEquals(80.0, alerta.getLimite(), 0.001);
        verify(alertaDAO).salvar(any());
    }

    @Test
    void chuvaFracaNaoDeveGerarAlerta() throws Exception {
        // Arrange
        when(sensor.getId()).thenReturn("PLV-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.CHUVA);
        when(medicaoDAO.buscarHistoricoPorSensor("PLV-01", 30))
                .thenReturn(HistoricoFake.criar("PLV-01", TipoSensor.CHUVA, 5, 8, 10, 6, 7));

        // Act
        ResultadoPrevisao resultado = servico.prever(sensor, new PrevisaoMediaMovel(5));
        Alerta alerta = servico.verificarAlerta(resultado);

        // Assert
        assertNull(alerta);
        verify(alertaDAO, never()).salvar(any());
    }

    @Test
    void comPoucasLeiturasNaoDeveGerarAlerta() throws Exception {
        // Arrange: so 2 leituras, mesmo sendo altas
        when(sensor.getId()).thenReturn("PLV-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.CHUVA);
        when(medicaoDAO.buscarHistoricoPorSensor("PLV-01", 30))
                .thenReturn(HistoricoFake.criar("PLV-01", TipoSensor.CHUVA, 140, 145));

        // Act
        ResultadoPrevisao resultado = servico.prever(sensor, new PrevisaoMediaMovel(5));
        Alerta alerta = servico.verificarAlerta(resultado);

        // Assert
        assertNull(alerta);
        verify(alertaDAO, never()).salvar(any());
    }

    @Test
    void leituraValidaDeveSerGravada() throws Exception {
        // Arrange
        when(sensor.getId()).thenReturn("NIV-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.NIVEL_CORREGO);
        when(sensor.lerValor()).thenReturn(2.3);
        when(medicaoDAO.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        // Act
        Medicao medicao = servico.registrarLeitura(sensor);

        // Assert
        assertEquals(2.3, medicao.getValor());
        verify(medicaoDAO).salvar(any());
    }

    @Test
    void leituraInvalidaNaoDeveSerGravada() throws Exception {
        // Arrange
        when(sensor.getId()).thenReturn("PLV-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.CHUVA);
        when(sensor.lerValor()).thenReturn(-7.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> servico.registrarLeitura(sensor));
        verify(medicaoDAO, never()).salvar(any());
    }

    @Test
    void sensorDesligadoDeveLancarFonteIndisponivel() throws Exception {
        // Arrange
        when(sensor.lerValor()).thenThrow(new FonteIndisponivelException("PLV-01", "sensor desligado"));

        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.registrarLeitura(sensor));
        verify(medicaoDAO, never()).salvar(any());
    }
}
