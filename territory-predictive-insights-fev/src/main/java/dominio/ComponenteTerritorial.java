package dominio;

// Classe base de tudo que fica no territorio (no nosso caso, os sensores)
public abstract class ComponenteTerritorial {

    protected String id;
    protected String nome;
    protected String regiao;
    protected StatusComponente status;

    public ComponenteTerritorial(String id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = StatusComponente.OPERANTE;
    }

    // cada componente diz como esta do seu jeito
    public abstract String verificarStatus();

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public StatusComponente getStatus() {
        return status;
    }
}
