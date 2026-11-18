package excecao;

// Quando um dado nao faz sentido: medicao fora da faixa, populacao negativa, limiar invalido
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
