package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import dominio.TipoSensor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AlertaDAO {

    private ConexaoBanco banco;

    public AlertaDAO(ConexaoBanco banco) {
        this.banco = banco;
    }

    public Alerta salvar(Alerta alerta) throws SQLException {
        String sql = "INSERT INTO alerta (sensor_id, tipo, nivel, valor_previsto, limite, estrategia, data_hora) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = banco.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alerta.getSensorId());
            ps.setString(2, alerta.getTipo().name());
            ps.setString(3, alerta.getNivel().name());
            ps.setDouble(4, alerta.getValorPrevisto());
            ps.setDouble(5, alerta.getLimite());
            ps.setString(6, alerta.getEstrategia());
            ps.setTimestamp(7, Timestamp.valueOf(alerta.getDataHora()));
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    alerta.setId(chaves.getLong(1));
                }
            }
        }
        return alerta;
    }

    // ultimos alertas, do mais novo pro mais antigo
    public List<Alerta> listarUltimos(int limite) throws SQLException {
        String sql = "SELECT * FROM alerta ORDER BY id DESC LIMIT ?";
        List<Alerta> lista = new ArrayList<>();

        try (Connection conexao = banco.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Alerta(
                            rs.getLong("id"),
                            rs.getString("sensor_id"),
                            TipoSensor.valueOf(rs.getString("tipo")),
                            NivelAlerta.valueOf(rs.getString("nivel")),
                            rs.getDouble("valor_previsto"),
                            rs.getDouble("limite"),
                            rs.getString("estrategia"),
                            rs.getTimestamp("data_hora").toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM alerta";
        try (Connection conexao = banco.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
