package dominio;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servico.ConfiguracaoSistema;

import static org.junit.jupiter.api.Assertions.*;

class DadosTerritorioTest {

    ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
    DadosTerritorio territorio;

    @BeforeEach
    void preparar() throws Exception {
        territorio = new DadosTerritorio("Bacia Teste", "Regiao Teste", 1000, "1234", config);
    }

    @AfterEach
    void limpar() {
        // a configuracao e um singleton, entao volta ao padrao pra nao atrapalhar outros testes
        config.restaurarPadroes();
    }

    @Test
    void codigoErradoNaoDeveAlterarLimiares() {
        assertThrows(DadosInvalidosException.class,
                () -> territorio.alterarLimiares("errado", TipoSensor.CHUVA, 10, 20, 30));
        assertEquals(50, config.getLimiares(TipoSensor.CHUVA).getAlerta());
    }

    @Test
    void codigoCertoDeveAlterarLimiares() throws Exception {
        // Act
        territorio.alterarLimiares("1234", TipoSensor.CHUVA, 10, 20, 30);

        // Assert
        assertEquals(20, config.getLimiares(TipoSensor.CHUVA).getAlerta());
    }

    @Test
    void limiaresForaDeOrdemDevemSerRecusados() {
        assertThrows(DadosInvalidosException.class,
                () -> territorio.alterarLimiares("1234", TipoSensor.CHUVA, 50, 30, 80));
    }

    @Test
    void populacaoNegativaDeveLancarExcecao() {
        assertThrows(DadosInvalidosException.class, () -> territorio.setPopulacao(-5));
    }
}
