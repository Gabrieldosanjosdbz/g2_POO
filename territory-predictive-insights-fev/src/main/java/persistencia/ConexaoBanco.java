package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Banco H2 salvo em arquivo na pasta ./dados (os dados continuam la depois de fechar)
public class ConexaoBanco {

    private static final String URL = "jdbc:h2:./dados/territorio";

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    public void criarTabelas() throws SQLException {
        String tabelaMedicao = "CREATE TABLE IF NOT EXISTS medicao ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "sensor_id VARCHAR(20) NOT NULL, "
                + "tipo VARCHAR(30) NOT NULL, "
                + "valor DOUBLE NOT NULL, "
                + "data_hora TIMESTAMP NOT NULL)";

        String tabelaAlerta = "CREATE TABLE IF NOT EXISTS alerta ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "sensor_id VARCHAR(20) NOT NULL, "
                + "tipo VARCHAR(30) NOT NULL, "
                + "nivel VARCHAR(10) NOT NULL, "
                + "valor_previsto DOUBLE NOT NULL, "
                + "limite DOUBLE NOT NULL, "
                + "estrategia VARCHAR(40) NOT NULL, "
                + "data_hora TIMESTAMP NOT NULL)";

        try (Connection conexao = abrir(); Statement st = conexao.createStatement()) {
            st.execute(tabelaMedicao);
            st.execute(tabelaAlerta);
        }
    }
}
