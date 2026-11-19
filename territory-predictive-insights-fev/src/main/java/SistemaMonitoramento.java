import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.ResultadoPrevisao;
import dominio.SensorNivelCorrego;
import dominio.SensorPluviometro;
import dominio.SensorSimulado;
import dominio.SensorUmidadeSolo;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.ExportadorCsv;
import servico.FabricaPrevisao;
import servico.ServicoMonitoramento;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Menu principal do sistema
public class SistemaMonitoramento {

    static Scanner scanner = new Scanner(System.in);
    static ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
    static FabricaPrevisao fabrica = new FabricaPrevisao(config);
    static List<SensorSimulado> sensores = new ArrayList<>();
    static ServicoMonitoramento servico;
    static DadosTerritorio territorio;

    public static void main(String[] args) {
        try {
            ConexaoBanco banco = new ConexaoBanco();
            banco.criarTabelas();
            servico = new ServicoMonitoramento(new MedicaoDAO(banco), new AlertaDAO(banco), config);
            territorio = new DadosTerritorio("Bacia do Corrego Azul", "Zona Leste (ficticia)", 18500,
                    "defesa2026", config);
        } catch (SQLException e) {
            System.out.println("Erro ao abrir o banco de dados: " + e.getMessage());
            return;
        } catch (DadosInvalidosException e) {
            System.out.println("Dados iniciais do territorio invalidos: " + e.getMessage());
            return;
        }

        // sensores ficticios em dois pontos de risco
        sensores.add(new SensorPluviometro("PLV-01", "Pluviometro da varzea", "Baixada Norte"));
        sensores.add(new SensorNivelCorrego("NIV-01", "Regua do corrego", "Baixada Norte"));
        sensores.add(new SensorPluviometro("PLV-02", "Pluviometro da encosta", "Morro Sul"));
        sensores.add(new SensorUmidadeSolo("UMS-01", "Umidade do solo", "Morro Sul"));

        System.out.println("=== Territory Predictive Insights - Risco de Alagamento ===");

        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n1 - Verificar sensores");
            System.out.println("2 - Rodar previsoes");
            System.out.println("3 - Ver historico");
            System.out.println("4 - Gerenciar dados do territorio");
            System.out.println("5 - Status completo");
            System.out.println("0 - Sair");
            opcao = lerInteiro("Escolha: ");

            try {
                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> rodarPrevisoes();
                    case 3 -> verHistorico();
                    case 4 -> gerenciarTerritorio();
                    case 5 -> statusCompleto();
                    case 0 -> System.out.println("Saindo... o historico fica salvo no banco.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (SQLException e) {
                System.out.println("Erro no banco de dados: " + e.getMessage());
            }
        }
    }

    static void verificarSensores() throws SQLException {
        int rodadas = lerInteiro("Quantas rodadas de leitura? ");

        for (int i = 1; i <= rodadas; i++) {
            System.out.println("\n-- Rodada " + i + " --");
            for (SensorSimulado sensor : sensores) {
                try {
                    Medicao medicao = servico.registrarLeitura(sensor);
                    System.out.println(sensor.getId() + ": " + medicao.getValor() + " "
                            + sensor.getTipo().getUnidade() + " (gravado)");
                } catch (DadosInvalidosException e) {
                    System.out.println(sensor.getId() + ": leitura descartada -> " + e.getMessage());
                } catch (FonteIndisponivelException e) {
                    System.out.println(sensor.getId() + ": sem leitura -> " + e.getMessage());
                }
            }
        }
    }

    static void rodarPrevisoes() throws SQLException {
        System.out.println("1 - Media movel");
        System.out.println("2 - Comparacao de periodos");
        System.out.println("3 - As duas");
        int escolha = lerInteiro("Escolha: ");

        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        try {
            if (escolha == 3) {
                estrategias = fabrica.criarTodas();
            } else {
                // a opcao 1 vira "media-movel", a 2 vira "comparacao"
                estrategias.add(fabrica.criar(fabrica.getNomes().get(escolha - 1)));
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Opcao invalida.");
            return;
        } catch (DadosInvalidosException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }

        for (EstrategiaPrevisao estrategia : estrategias) {
            System.out.println("\n>> " + estrategia.descrever());
            for (SensorSimulado sensor : sensores) {
                ResultadoPrevisao resultado = servico.prever(sensor, estrategia);
                System.out.println(resultado);

                Alerta alerta = servico.verificarAlerta(resultado);
                if (alerta != null) {
                    System.out.println("   !!! " + alerta);
                }
            }
        }
    }

    static void verHistorico() throws SQLException {
        System.out.println("1 - Medicoes de um sensor");
        System.out.println("2 - Alertas emitidos");
        System.out.println("3 - Exportar medicoes de um sensor para CSV");
        int escolha = lerInteiro("Escolha: ");

        if (escolha == 1) {
            SensorSimulado sensor = escolherSensor();
            if (sensor == null) return;
            List<Medicao> historico = servico.buscarHistoricoPorSensor(sensor.getId(), 20);
            if (historico.isEmpty()) {
                System.out.println("Nenhuma medicao ainda.");
            }
            for (Medicao m : historico) {
                System.out.println(m);
            }
        } else if (escolha == 2) {
            List<Alerta> alertas = servico.listarAlertas(20);
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta emitido.");
            }
            for (Alerta a : alertas) {
                System.out.println(a);
            }
        } else if (escolha == 3) {
            SensorSimulado sensor = escolherSensor();
            if (sensor == null) return;
            List<Medicao> historico = servico.buscarHistoricoPorSensor(sensor.getId(), 1000);
            String arquivo = "historico-" + sensor.getId() + ".csv";
            try {
                new ExportadorCsv().exportar(historico, arquivo);
                System.out.println(historico.size() + " medicoes salvas em " + arquivo);
            } catch (IOException e) {
                System.out.println("Nao foi possivel salvar o CSV: " + e.getMessage());
            }
        } else {
            System.out.println("Opcao invalida.");
        }
    }

    static void gerenciarTerritorio() {
        System.out.println("1 - Ver dados do territorio");
        System.out.println("2 - Alterar populacao");
        System.out.println("3 - Alterar limiares (precisa do codigo de acesso)");
        System.out.println("4 - Ligar/desligar sensor");
        int escolha = lerInteiro("Escolha: ");

        try {
            if (escolha == 1) {
                System.out.println("Territorio: " + territorio.getNome() + " - " + territorio.getRegiao());
                System.out.println("Populacao: " + territorio.getPopulacao());
                for (TipoSensor tipo : TipoSensor.values()) {
                    System.out.println("Limiares " + tipo.getDescricao() + " (" + tipo.getUnidade() + "): "
                            + territorio.getLimiares(tipo));
                }
            } else if (escolha == 2) {
                territorio.setPopulacao(lerInteiro("Nova populacao: "));
                System.out.println("Populacao alterada.");
            } else if (escolha == 3) {
                alterarLimiares();
            } else if (escolha == 4) {
                SensorSimulado sensor = escolherSensor();
                if (sensor == null) return;
                if (sensor.estaLigado()) {
                    sensor.desligar();
                } else {
                    sensor.ligar();
                }
                System.out.println(sensor.verificarStatus());
            } else {
                System.out.println("Opcao invalida.");
            }
        } catch (DadosInvalidosException e) {
            System.out.println("Nao foi possivel alterar: " + e.getMessage());
        }
    }

    static void alterarLimiares() throws DadosInvalidosException {
        String codigo = lerTexto("Codigo de acesso: ");
        TipoSensor[] tipos = TipoSensor.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + " - " + tipos[i].getDescricao() + " (atual: "
                    + config.getLimiares(tipos[i]) + ")");
        }
        int escolha = lerInteiro("Tipo: ");
        if (escolha < 1 || escolha > tipos.length) {
            System.out.println("Tipo invalido.");
            return;
        }
        double atencao = lerDecimal("Limiar ATENCAO: ");
        double alerta = lerDecimal("Limiar ALERTA: ");
        double critico = lerDecimal("Limiar CRITICO: ");

        territorio.alterarLimiares(codigo, tipos[escolha - 1], atencao, alerta, critico);
        System.out.println("Limiares alterados.");
    }

    static void statusCompleto() throws SQLException {
        System.out.println("\n===== STATUS COMPLETO =====");
        System.out.println("Territorio: " + territorio.getNome() + " | Populacao: " + territorio.getPopulacao());

        System.out.println("\nSensores:");
        for (SensorSimulado sensor : sensores) {
            System.out.println("  " + sensor.verificarStatus());
        }

        System.out.println("\nLimiares:");
        for (TipoSensor tipo : TipoSensor.values()) {
            System.out.println("  " + tipo.getDescricao() + ": " + config.getLimiares(tipo));
        }

        System.out.println("\nNo banco: " + servico.contarMedicoes() + " medicoes e "
                + servico.contarAlertas() + " alertas");

        System.out.println("\nUltimos alertas:");
        List<Alerta> alertas = servico.listarAlertas(5);
        if (alertas.isEmpty()) {
            System.out.println("  nenhum");
        }
        for (Alerta a : alertas) {
            System.out.println("  " + a);
        }
    }

    static SensorSimulado escolherSensor() {
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println((i + 1) + " - " + sensores.get(i).getId() + " (" + sensores.get(i).getNome() + ")");
        }
        int escolha = lerInteiro("Sensor: ");
        if (escolha < 1 || escolha > sensores.size()) {
            System.out.println("Sensor invalido.");
            return null;
        }
        return sensores.get(escolha - 1);
    }

    static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    static int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero inteiro.");
            }
        }
    }

    static double lerDecimal(String mensagem) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(mensagem).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero (ex: 42.5).");
            }
        }
    }
}
