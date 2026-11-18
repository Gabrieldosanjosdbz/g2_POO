package servico;

import dominio.EstrategiaPrevisao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FabricaPrevisaoTest {

    FabricaPrevisao fabrica = new FabricaPrevisao(ConfiguracaoSistema.getInstancia());

    @Test
    void deveCriarEstrategiaPeloNome() throws Exception {
        // Act
        EstrategiaPrevisao estrategia = fabrica.criar("media-movel");

        // Assert
        assertEquals("Media movel", estrategia.getNome());
    }

    @Test
    void nomeDesconhecidoDeveLancarExcecao() {
        assertThrows(DadosInvalidosException.class, () -> fabrica.criar("bola-de-cristal"));
    }
}
