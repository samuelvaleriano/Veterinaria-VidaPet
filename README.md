# Clínica Veterinária VidaPet (TP1)

Aplicação standalone em console desenvolvida em **Java 21** (paradigma estruturado, sem Orientação a Objetos) para gerenciamento de cadastros e agendamentos de consultas veterinárias.

## Documentação e Modelagem
* **[Documentação Técnica Completa (PDF)](docs/Documentacao_VidaPet.pdf)** — Contém objetivo, requisitos funcionais (`RF01`–`RF06`), regras de negócio (`RN01`–`RN05`), requisitos não funcionais (`RNF01`–`RNF05`), especificação dos casos de uso (`UC01`–`UC06`) e justificativa de escopo.

### Diagrama de Casos de Uso
![Diagrama de Casos de Uso - Veterinária VidaPet](docs/diagrama-casos-de-uso.png)

## Funcionalidades Implementadas
* **Perfil Recepcionista:** Cadastrar animal (`UC01`), cadastrar veterinário (`UC02`), agendar consulta (`UC03`) e cancelar consulta (`UC04`).
* **Perfil Veterinário:** Consultar agenda do dia (`UC05`) e registrar consulta como realizada com observações (`UC06`).

## Como Executar (Java 21)

No terminal, dentro da pasta raiz do projeto, execute:

```bash
javac src/VidaPetApp.java
java -cp src VidaPetApp
