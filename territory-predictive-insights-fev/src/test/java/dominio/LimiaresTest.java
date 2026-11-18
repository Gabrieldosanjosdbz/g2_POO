package dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimiaresTest {

    @Test
    void deveClassificarNoNivelCerto() {
        // Arrange
        Limiares limiares = new Limiares(30, 50, 80);

        // Act + Assert
        assertNull(limiares.classificar(10));
        assertEquals(NivelAlerta.ATENCAO, limiares.classificar(35));
        assertEquals(NivelAlerta.ALERTA, limiares.classificar(65));
        assertEquals(NivelAlerta.CRITICO, limiares.classificar(80));
    }
}
