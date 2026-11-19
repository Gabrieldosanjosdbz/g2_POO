# Territory Predictive Insights — Risco de Alagamento

Global Solution 2º/2026 — POO (Turma de Fevereiro) · Tema **CitizenTech**

Recorte escolhido: **🌊 Clima local — chuva intensa e risco de alagamento em pontos vulneráveis.**

O sistema lê sensores (simulados) de chuva, nível de córrego e umidade do solo, grava tudo num banco H2, faz previsões simples em cima do histórico e emite alertas explicados (ATENÇÃO, ALERTA, CRÍTICO) quando a previsão passa dos limiares configurados.

---

## 👥 Público beneficiado

**Quem sofre o problema:** moradores das partes baixas de bairros cortados por córregos e de encostas ocupadas — gente que perde móveis, documentos e às vezes a casa quando a água sobe ou a terra desce.

**Quem usa o sistema:** a associação de bairro / núcleo de Defesa Civil comunitária (NUPDEC) que acompanha esses pontos.

**Qual decisão ele apoia:** *"preciso agir agora?"* — avisar moradores dos pontos baixos, abrir o abrigo, desobstruir bocas de lobo, acionar a Defesa Civil. O sistema mostra o número previsto, o limite que foi estourado e uma recomendação; a decisão continua sendo da pessoa.

> Território fictício ("Bacia do Córrego Azul"), descrito só por região e equipamento. Nenhum dado pessoal.

---

## ▶️ Como rodar

**Pré-requisito:** Java 17 ou superior. Não precisa instalar banco (H2 vem como dependência) nem Maven (o projeto traz o wrapper `mvnw`).

```bash
# Linux / Mac / Git Bash
./mvnw compile exec:java

# Windows (cmd / PowerShell)
mvnw.cmd compile exec:java
```

Rodar os testes:

```bash
./mvnw test          # Windows: mvnw.cmd test
```

Se tiver Maven instalado, pode trocar `./mvnw` por `mvn`. Também roda direto pela IDE (classe `SistemaMonitoramento`, pacote padrão).

**Banco:** H2 em arquivo, criado automaticamente em `./dados/territorio.mv.db` na primeira execução (tabelas criadas pelo `ConexaoBanco.criarTabelas()`). Fechou e abriu de novo → o histórico continua lá. Para zerar, é só apagar a pasta `dados/`.

**Código de acesso para alterar limiares:** `defesa2026`

### Roteiro rápido para a correção

1. Opção **1** → digite `12` rodadas (as previsões precisam de pelo menos 5–6 leituras por sensor). Repare nas leituras **descartadas** (valor inválido) e **sem leitura** (sensor falhou) — são as exceções em ação.
2. Opção **2** → `3` (todas as estratégias). Veja as previsões e os alertas com o porquê.
3. Opção **3** → histórico por sensor, alertas emitidos e exportação CSV.
4. Opção **4** → tente alterar limiares com código errado, com valores fora de ordem, população negativa; desligue um sensor e volte na opção 1.
5. Opção **5** → status completo.
6. Saia (`0`), rode de novo e confira que o histórico continua lá.

---

## 🗂 Estrutura

```
territory-predictive-insights-fev/
├── src/main/java/
│   ├── dominio/        ComponenteTerritorial, StatusComponente, Sensor, SensorSimulado,
│   │                   SensorPluviometro, SensorNivelCorrego, SensorUmidadeSolo, TipoSensor,
│   │                   DadosTerritorio, Limiares, Medicao, Alerta, NivelAlerta,
│   │                   EstrategiaPrevisao, PrevisaoMediaMovel, PrevisaoComparacao, ResultadoPrevisao
│   ├── excecao/        DadosInvalidosException, FonteIndisponivelException
│   ├── persistencia/   ConexaoBanco, MedicaoDAO, AlertaDAO  (JDBC)
│   ├── servico/        ServicoMonitoramento, FabricaPrevisao, ConfiguracaoSistema, ExportadorCsv
│   └── SistemaMonitoramento.java   (menu principal)
└── src/test/java/
    ├── dominio/        MedicaoTest, PrevisaoMediaMovelTest, PrevisaoComparacaoTest,
    │                   LimiaresTest, DadosTerritorioTest, HistoricoFake (apoio)
    └── servico/        ServicoMonitoramentoTest (Mockito), FabricaPrevisaoTest
```

---

## 📚 Onde está cada conceito pedido

| Requisito | Onde |
|---|---|
| Classe abstrata `ComponenteTerritorial` (id, nome, região, status) com `verificarStatus()` abstrato | `dominio/ComponenteTerritorial`, herdada por `SensorSimulado` e pelos 3 sensores |
| Interface `Sensor` + 3 implementações | `SensorPluviometro`, `SensorNivelCorrego`, `SensorUmidadeSolo` |
| `DadosTerritorio` com atributos privados e código de acesso pros limiares | `DadosTerritorio.alterarLimiares(codigo, ...)` |
| `EstrategiaPrevisao` + 2 filhas sobrescrevendo `prever()` e usando `super` | `PrevisaoMediaMovel` e `PrevisaoComparacao` chamam `super(nome, minimo)` e `super.descrever()` |
| Escolha da estratégia sem `if tipo ==` / `instanceof` | `FabricaPrevisao` usa um mapa nome → estratégia; o serviço só chama `estrategia.prever(...)` |
| `DadosInvalidosException` | medição fora da faixa, população negativa, limiar inválido, código de acesso errado, estratégia desconhecida |
| `FonteIndisponivelException` | sensor desligado ou que não respondeu (`SensorSimulado.lerValor`) |
| Capturas com mensagem útil, sem `catch` vazio | no menu (`SistemaMonitoramento`) |
| JDBC com `PreparedStatement` e try-with-resources | `MedicaoDAO`, `AlertaDAO` |
| Histórico por sensor e alertas no menu | opção 3 |
| Níveis ATENÇÃO / ALERTA / CRÍTICO com explicação | `NivelAlerta`, `Limiares.classificar`, `Alerta.getMotivo()` |
| Limiares vindos da configuração | `ConfiguracaoSistema` (Singleton) |

---

## 🏭 Padrões criacionais (e por que ali)

**Factory — `FabricaPrevisao`**
Sem ela, o menu teria um `if (opcao == 1) new PrevisaoMediaMovel(5) else if ...`, e cada estratégia nova obrigaria mexer no menu. A fábrica guarda num mapa o nome de cada estratégia e como criá-la (já com a janela que vem da configuração). Quem usa só pede pelo nome. Para adicionar uma estratégia nova basta uma linha no construtor da fábrica.
Não usamos Factory nos sensores porque eles são criados uma vez só, no início, com valores fixos — não teria ganho.

**Singleton — `ConfiguracaoSistema`**
Os limiares precisam ser os mesmos para quem altera (menu → `DadosTerritorio`) e para quem usa (`ServicoMonitoramento`). Com uma instância única e construtor privado, mudar um limiar vale na hora para o sistema inteiro. Não usamos Singleton nos DAOs nem no serviço para poder testar com Mockito: eles recebem as dependências pelo construtor.

---

## 🔮 Como as previsões funcionam

Nada de estatística avançada (como o enunciado pede):

- **Média móvel (janela 5):** média das últimas 5 leituras do banco. Suaviza picos; mostra o patamar atual.
- **Comparação de períodos (janela 3):** compara a média das 3 últimas com as 3 anteriores e projeta a mesma variação para frente (`previsto = recente + (recente − anterior)`). Antecipa subidas, mas exagera quando o sensor oscila. A previsão nunca sai da faixa física do sensor (chuva não fica negativa).

Se não houver leituras suficientes, a estratégia **não chuta**: devolve "sem previsão por enquanto" e nenhum alerta é gerado.

---

## 🧪 Testes

20 testes JUnit 5 no padrão AAA, sem banco e sem console:

- `MedicaoTest` — medição válida e `assertThrows` para chuva negativa e umidade acima de 100%.
- `PrevisaoMediaMovelTest` — confere que a média usa **só** as 5 últimas leituras e que com 3 leituras não há previsão.
- `PrevisaoComparacaoTest` — tendência de subida calculada certo; queda forte não gera chuva negativa.
- `LimiaresTest` — classificação em cada nível.
- `DadosTerritorioTest` — código errado, limiares fora de ordem e população negativa são recusados; código certo altera.
- `FabricaPrevisaoTest` — cria pelo nome; nome desconhecido lança exceção.
- `ServicoMonitoramentoTest` (**Mockito**) — `MedicaoDAO`, `AlertaDAO` e `Sensor` mockados: chuva alta gera alerta CRÍTICO; chuva fraca e poucas leituras não geram alerta (`verify(never())`); leitura inválida e sensor desligado não chegam ao banco.

---

## ⭐ Bônus escolhido

**Exportar o histórico em CSV** (menu 3 → 3). Gera `historico-<SENSOR>.csv` com `;` e vírgula decimal, que abre direto no Excel.

---

## 🧭 Decisões da equipe

- **JDBC + H2 em arquivo em vez de Spring Boot/JPA:** o projeto é um menu de console; subir Spring só para isso pesaria mais do que ajuda, e com JDBC fica explícito o que vai pro banco. H2 em arquivo atende "os dados sobrevivem ao reiniciar" sem pedir instalação de nada na correção.
- **Exceções checadas (`extends Exception`):** obriga quem chama a tratar o erro — o compilador não deixa esquecer.
- **Validação no construtor da `Medicao`:** é impossível existir uma medição inválida em memória, então nada inválido chega ao banco.
- **Sensores com falhas de propósito:** 5% das leituras falham e 5% vêm com valor absurdo, para as exceções aparecerem durante o uso normal (não só nos testes).
- **Limiares de referência:** chuva 30/50/80 mm/h, nível do córrego 2,5/3,5/4,5 m, umidade do solo 75/85/95%. São valores didáticos — numa implantação real viriam da Defesa Civil local. Eles ficam em memória: ao reiniciar voltam ao padrão (só medições e alertas são persistidos, como o enunciado pede).

---

## 🤔 Reflexão crítica

**Quando o sistema deve se calar?** Quando não tem histórico suficiente. Com 2 ou 3 leituras, uma pancada de chuva de 5 minutos vira "tendência" e o sistema gritaria CRÍTICO à toa. Por isso cada estratégia tem um mínimo (5 para média móvel, 6 para comparação) e, abaixo disso, responde "sem previsão por enquanto" e não emite alerta — temos teste garantindo isso. Também ignora leituras absurdas: elas são descartadas em vez de entrar na média. Alarme falso tem custo: a comunidade que recebe muitos alertas sem motivo para de levar a sério o alerta que importa.

**O que os dados não representam?** Um pluviômetro mede a chuva *naquele ponto*; a 500 m pode estar chovendo o dobro. Um sensor de nível não sabe se a boca de lobo está entupida de lixo, que é muitas vezes o que de fato alaga a rua. Os dados também não dizem quem mora no ponto baixo, quem tem mobilidade reduzida, quem não tem para onde ir. E aqui os valores são simulados: servem para exercitar o sistema, não para descrever um território real.

**Por que o alerta apoia a decisão em vez de decidir?** Porque a pessoa no território sabe coisas que o sensor não sabe: que a obra da esquina mudou o escoamento, que a escola já está servindo de abrigo, que aquela rua alaga com bem menos chuva. Por isso o alerta mostra o *porquê* (sensor, valor previsto, limite estourado, estratégia usada) e uma recomendação — não uma ordem. Previsão simples erra, e quem responde pela decisão (evacuar, abrir abrigo) precisa poder conferir o raciocínio e discordar dele.

---

## 🤖 Uso de IA

Usamos o **Claude (Anthropic)** como apoio no projeto:

- **Código:** a IA gerou uma primeira versão da estrutura e das classes a partir do enunciado. Cada integrante ficou responsável por revisar, rodar e entender a sua parte (ver divisão abaixo), e o que não fazia sentido para o nosso recorte foi ajustado.
- **Dúvidas técnicas:** explicações sobre `PreparedStatement`, try-with-resources, H2 em arquivo e como usar Mockito (`@Mock`, `when`, `verify`).
- **README:** a IA escreveu o README a partir das nossas decisões, e nós revisamos o texto.

**Como revisamos:** rodamos o sistema seguindo o roteiro de correção acima (inclusive fechando e abrindo de novo para conferir o histórico no banco), rodamos os testes e quebramos o cálculo de propósito para ver o teste ficar vermelho. Cada integrante leu linha a linha os arquivos da sua parte para conseguir explicá-los na conversa de correção.

---

## 🧑‍🤝‍🧑 Divisão de tarefas

| Integrante | RM | Responsabilidade |
|---|---|---|
| **Gabriel dos Anjos** | 565532 | **Núcleo do sistema:** `ServicoMonitoramento` (ler → validar → gravar → prever → alertar), previsões (`EstrategiaPrevisao`, `PrevisaoMediaMovel`, `PrevisaoComparacao`, `ResultadoPrevisao`), alertas (`Alerta`, `NivelAlerta`, `Limiares`), padrões criacionais (`FabricaPrevisao`, `ConfiguracaoSistema`) e testes com Mockito (`ServicoMonitoramentoTest`, `FabricaPrevisaoTest`, testes das previsões) |
| Eduardo Abreu | 566460 | **Domínio e exceções:** `ComponenteTerritorial`, `StatusComponente`, interface `Sensor` e os sensores (`SensorSimulado`, `SensorPluviometro`, `SensorNivelCorrego`, `SensorUmidadeSolo`), `TipoSensor`, `Medicao`, `DadosTerritorio`, as duas exceções e os testes de domínio (`MedicaoTest`, `DadosTerritorioTest`, `LimiaresTest`, `HistoricoFake`) |
| João Pedro Ferreira | 563869 | **Persistência e menu:** `ConexaoBanco`, `MedicaoDAO`, `AlertaDAO` (JDBC + H2), menu `SistemaMonitoramento` com tratamento de erros, bônus CSV (`ExportadorCsv`), configuração do projeto (`pom.xml`, wrapper) e revisão do README |
