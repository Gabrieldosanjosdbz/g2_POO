package dominio;

import excecao.FonteIndisponivelException;

public interface Sensor {

    String getId();

    double lerValor() throws FonteIndisponivelException;

    boolean verificarFuncionamento();

    TipoSensor getTipo();
}
