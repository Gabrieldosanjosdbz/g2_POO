package servico;

import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.ResultadoPrevisao;
import dominio.Sensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

// Regras do sistema: ler e gravar medicoes, prever e gerar alertas
public class ServicoMonitoramento {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private ConfiguracaoSistema config;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO, ConfiguracaoSistema config) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.config = config;
    }

    // le o sensor, valida (no construtor da Medicao) e grava
    public Medicao registrarLeitura(Sensor sensor)
            throws FonteIndisponivelException, DadosInvalidosException, SQLException {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor, LocalDateTime.now());
        return medicaoDAO.salvar(medicao);
    }

    // roda a previsao em cima das ultimas 30 leituras do banco
    public ResultadoPrevisao prever(Sensor sensor, EstrategiaPrevisao estrategia) throws SQLException {
        List<Medicao> historico = medicaoDAO.buscarHistoricoPorSensor(sensor.getId(), 30);
        return estrategia.prever(sensor.getId(), sensor.getTipo(), historico);
    }

    // compara a previsao com os limiares; se passou, grava e devolve o alerta (senao devolve null)
    public Alerta verificarAlerta(ResultadoPrevisao resultado) throws SQLException {
        if (!resultado.isConfiavel()) {
            return null; // sem dados suficientes o sistema fica quieto
        }
        Limiares limiares = config.getLimiares(resultado.getTipo());
        NivelAlerta nivel = limiares.classificar(resultado.getValorPrevisto());
        if (nivel == null) {
            return null;
        }
        Alerta alerta = new Alerta(0, resultado.getSensorId(), resultado.getTipo(), nivel,
                resultado.getValorPrevisto(), limiares.getLimite(nivel), resultado.getEstrategia(),
                LocalDateTime.now());
        return alertaDAO.salvar(alerta);
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorId, int limite) throws SQLException {
        return medicaoDAO.buscarHistoricoPorSensor(sensorId, limite);
    }

    public List<Alerta> listarAlertas(int limite) throws SQLException {
        return alertaDAO.listarUltimos(limite);
    }

    public int contarMedicoes() throws SQLException {
        return medicaoDAO.contar();
    }

    public int contarAlertas() throws SQLException {
        return alertaDAO.contar();
    }
}
