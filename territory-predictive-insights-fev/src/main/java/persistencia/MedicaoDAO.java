package persistencia;

import dominio.Medicao;
import dominio.TipoSensor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicaoDAO {

    private ConexaoBanco banco;

    public MedicaoDAO(ConexaoBanco banco) {
        this.banco = banco;
    }

    public Medicao salvar(Medicao medicao) throws SQLException {
        String sql = "INSERT INTO medicao (sensor_id, tipo, valor, data_hora) VALUES (?, ?, ?, ?)";

        try (Connection conexao = banco.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicao.getSensorId());
            ps.setString(2, medicao.getTipo().name());
            ps.setDouble(3, medicao.getValor());
            ps.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            ps.executeUpdate();

            // pega o id que o banco gerou
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    medicao.setId(chaves.getLong(1));
                }
            }
        }
        return medicao;
    }

    // devolve as ultimas N medicoes do sensor, da mais antiga pra mais nova
    public List<Medicao> buscarHistoricoPorSensor(String sensorId, int limite) throws SQLException {
        String sql = "SELECT * FROM medicao WHERE sensor_id = ? ORDER BY id DESC LIMIT ?";
        List<Medicao> lista = new ArrayList<>();

        try (Connection conexao = banco.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, sensorId);
            ps.setInt(2, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Medicao(
                            rs.getLong("id"),
                            rs.getString("sensor_id"),
                            TipoSensor.valueOf(rs.getString("tipo")),
                            rs.getDouble("valor"),
                            rs.getTimestamp("data_hora").toLocalDateTime()));
                }
            }
        }
        // veio da mais nova pra mais antiga, entao inverte
        Collections.reverse(lista);
        return lista;
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicao";
        try (Connection conexao = banco.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
