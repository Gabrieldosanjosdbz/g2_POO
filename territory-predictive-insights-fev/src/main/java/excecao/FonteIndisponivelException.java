package excecao;

// Quando um sensor (ou outra fonte de dados) nao consegue entregar a leitura
public class FonteIndisponivelException extends Exception {

    private String fonteId;

    public FonteIndisponivelException(String fonteId, String motivo) {
        super("Sensor " + fonteId + " indisponivel: " + motivo);
        this.fonteId = fonteId;
    }

    public String getFonteId() {
        return fonteId;
    }
}
