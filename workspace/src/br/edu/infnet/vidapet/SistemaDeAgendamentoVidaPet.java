package br.edu.infnet.vidapet;

import java.util.Scanner;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class SistemaDeAgendamentoVidaPet {
	
	static int MAX_REGISTROS = 100;
    static String STATUS_AGENDADA = "agendada";
    static String STATUS_REALIZADA = "realizada";
    static String STATUS_CANCELADA = "cancelada";

    static DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    static DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    // Armazenamento em memória via vetores paralelos — Animais (RF01 / RNF03)
    static String[] nomesAnimais = new String[MAX_REGISTROS];
    static String[] especiesAnimais = new String[MAX_REGISTROS];
    static int[] idadesAnimais = new int[MAX_REGISTROS];
    static String[] tutoresAnimais = new String[MAX_REGISTROS];
    static String[] telefonesTutores = new String[MAX_REGISTROS];
    static int totalAnimais = 0;

    // Armazenamento em memória via vetores paralelos — Veterinários (RF02 / RNF03)
    static String[] nomesVeterinarios = new String[MAX_REGISTROS];
    static String[] crmvsVeterinarios = new String[MAX_REGISTROS];
    static int totalVeterinarios = 0;

    // Armazenamento em memória via vetores paralelos — Consultas (RF03–RF06 / RNF03)
    static int[] idsConsultas = new int[MAX_REGISTROS];
    static String[] animaisConsultas = new String[MAX_REGISTROS];
    static String[] crmvsVeterinariosConsultas = new String[MAX_REGISTROS];
    static LocalDate[] datasConsultas = new LocalDate[MAX_REGISTROS];
    static LocalTime[] horariosConsultas = new LocalTime[MAX_REGISTROS];
    static String[] situacoesConsultas = new String[MAX_REGISTROS];
    static String[] observacoesConsultas = new String[MAX_REGISTROS];
    static int totalConsultas = 0;
    static int proximoIdConsulta = 1;

	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        executarMenuPrincipal(scanner);
        scanner.close();
    }

    // =========================================================================
    // NAVEGAÇÃO E MENUS (RNF04 / RNF05)
    // =========================================================================

    static void executarMenuPrincipal(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\n==========================================");
            System.out.println("       CLÍNICA VETERINÁRIA VIDAPET        ");
            System.out.println("==========================================");
            System.out.println("1 - Acesso Recepcionista");
            System.out.println("2 - Acesso Veterinário");
            System.out.println("0 - Sair do sistema");
            System.out.println("------------------------------------------");

            opcao = lerOpcaoInteira(scanner, "Escolha o perfil de acesso: ");

            switch (opcao) {
                case 1:
                    executarMenuRecepcionista(scanner);
                    break;
                case 2:
                    executarMenuVeterinario(scanner);
                    break;
                case 0:
                    System.out.println("Encerrando o sistema VidaPet. Até logo!");
                    break;
                default:
                    System.out.println("[ERRO] Opção inválida. Digite 1, 2 ou 0.");
                    break;
            }
        } while (opcao != 0);
    }

    static void executarMenuRecepcionista(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\n--- MENU RECEPCIONISTA ---");
            System.out.println("1 - Cadastrar animal (UC01)");
            System.out.println("2 - Cadastrar veterinário (UC02)");
            System.out.println("3 - Agendar consulta (UC03)");
            System.out.println("4 - Cancelar consulta (UC04)");
            System.out.println("0 - Voltar ao menu principal");

            opcao = lerOpcaoInteira(scanner, "Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    cadastrarAnimal(scanner);
                    break;
                case 2:
                    cadastrarVeterinario(scanner);
                    break;
                case 3:
                    agendarConsulta(scanner);
                    break;
                case 4:
                    cancelarConsulta(scanner);
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("[ERRO] Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
    }

    static void executarMenuVeterinario(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\n--- MENU VETERINÁRIO ---");
            System.out.println("1 - Consultar agenda do dia (UC05)");
            System.out.println("2 - Registrar consulta como realizada (UC06)");
            System.out.println("0 - Voltar ao menu principal");

            opcao = lerOpcaoInteira(scanner, "Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    consultarAgendaDoDia(scanner);
                    break;
                case 2:
                    registrarConsultaRealizada(scanner);
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("[ERRO] Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
    }

    // =========================================================================
    // CASOS DE USO — RECEPCIONISTA (UC01 a UC04)
    // =========================================================================

    /**
     * UC01 — Cadastrar animal (RF01)
     */
    static void cadastrarAnimal(Scanner scanner) {
        System.out.println("\n[UC01 - Cadastrar Animal]");
        if (totalAnimais >= MAX_REGISTROS) {
            System.out.println("[ERRO] Limite máximo de cadastros atingido.");
            return;
        }

        String nome = lerTextoObrigatorio(scanner, "Nome do animal: ");
        String especieEntrada = lerTextoObrigatorio(scanner, "Espécie (cão ou gato): ").toLowerCase();

        // Fluxo Alternativo A2: validação de espécie permitida (RF01)
        String especieNormalizada;
        if (especieEntrada.equals("cão") || especieEntrada.equals("cao")) {
            especieNormalizada = "cão";
        } else if (especieEntrada.equals("gato")) {
            especieNormalizada = "gato";
        } else {
            System.out.println("[ERRO] Espécie inválida. O sistema aceita apenas 'cão' ou 'gato'.");
            return;
        }

        int idade = lerInteiroNaoNegativo(scanner, "Idade do animal (em anos): ");
        String nomeTutor = lerTextoObrigatorio(scanner, "Nome do tutor: ");
        String telefoneTutor = lerTextoObrigatorio(scanner, "Telefone do tutor: ");

        // Fluxo Alternativo A1: verifica duplicidade (mesmo animal e mesmo tutor)
        if (buscarIndiceAnimal(nome, nomeTutor) != -1) {
            System.out.println("[ERRO] Este animal já está cadastrado para o tutor informado.");
            return;
        }

        nomesAnimais[totalAnimais] = nome;
        especiesAnimais[totalAnimais] = especieNormalizada;
        idadesAnimais[totalAnimais] = idade;
        tutoresAnimais[totalAnimais] = nomeTutor;
        telefonesTutores[totalAnimais] = telefoneTutor;
        totalAnimais++;

        System.out.println("[SUCESSO] Animal '" + nome + "' (" + especieNormalizada + ") cadastrado!");
    }

    /**
     * UC02 — Cadastrar veterinário (RF02)
     */
    static void cadastrarVeterinario(Scanner scanner) {
        System.out.println("\n[UC02 - Cadastrar Veterinário]");
        if (totalVeterinarios >= MAX_REGISTROS) {
            System.out.println("[ERRO] Limite máximo de veterinários atingido.");
            return;
        }

        String nome = lerTextoObrigatorio(scanner, "Nome do veterinário: ");
        String crmv = lerTextoObrigatorio(scanner, "CRMV (ex: CRMV-SP 12345): ").toUpperCase();

        // Fluxo Alternativo A2: valida formato padrão do CRMV
        if (!crmv.matches("^CRMV-[A-Z]{2}\\s\\d{4,6}$")) {
            System.out.println("[ERRO] Formato de CRMV inválido. Utilize o padrão: CRMV-UF 12345 (ex: CRMV-SP 12345).");
            return;
        }

        // Fluxo Alternativo A1: verifica se CRMV já está cadastrado
        if (buscarIndiceVeterinarioPorCrmv(crmv) != -1) {
            System.out.println("[ERRO] Já existe um veterinário cadastrado com o " + crmv + ".");
            return;
        }

        nomesVeterinarios[totalVeterinarios] = nome;
        crmvsVeterinarios[totalVeterinarios] = crmv;
        totalVeterinarios++;

        System.out.println("[SUCESSO] Veterinário(a) " + nome + " (" + crmv + ") cadastrado(a)!");
    }

    /**
     * UC03 — Agendar consulta (RF03, RN01, RN02, RN03)
     */
    static void agendarConsulta(Scanner scanner) {
        System.out.println("\n[UC03 - Agendar Consulta]");
        if (totalConsultas >= MAX_REGISTROS) {
            System.out.println("[ERRO] Limite máximo de consultas atingido.");
            return;
        }

        String nomeAnimal = lerTextoObrigatorio(scanner, "Nome do animal: ");
        String crmv = lerTextoObrigatorio(scanner, "CRMV do veterinário (ex: CRMV-SP 12345): ").toUpperCase();

        // Fluxo Alternativo A1: pré-condição de cadastros existentes
        if (!existeAnimalPorNome(nomeAnimal)) {
            System.out.println("[ERRO] Animal '" + nomeAnimal + "' não encontrado nos cadastros.");
            return;
        }
        int indiceVet = buscarIndiceVeterinarioPorCrmv(crmv);
        if (indiceVet == -1) {
            System.out.println("[ERRO] Veterinário com " + crmv + " não encontrado nos cadastros.");
            return;
        }

        LocalDate data = lerData(scanner, "Data da consulta (dd/MM/aaaa): ");
        LocalTime horario = lerHorario(scanner, "Horário da consulta (HH:mm): ");

        // Fluxo Alternativo A2: valida RN03 (passado) e RN01 (seg-sex, 08:00–17:30, blocos de 30 min)
        if (!isDataHoraFutura(data, horario)) {
            System.out.println("[ERRO - RN03] Não é permitido agendar consultas em datas ou horários que já passaram.");
            return;
        }
        if (!isHorarioComercialValido(data, horario)) {
            System.out.println("[ERRO - RN01] Agendamentos permitidos apenas de segunda a sexta, das 08:00 às 17:30, em intervalos de 30 minutos (ex: 08:00, 08:30).");
            return;
        }

        // Fluxo Alternativo A3: valida RN02 (choque de horário para o mesmo veterinário)
        if (possuiConflitoAgenda(crmv, data, horario)) {
            System.out.println("[ERRO - RN02] O(A) veterinário(a) " + nomesVeterinarios[indiceVet]
                    + " já possui uma consulta agendada para este dia e horário.");
            return;
        }

        int idGerado = proximoIdConsulta++;
        idsConsultas[totalConsultas] = idGerado;
        animaisConsultas[totalConsultas] = nomeAnimal;
        crmvsVeterinariosConsultas[totalConsultas] = crmv;
        datasConsultas[totalConsultas] = data;
        horariosConsultas[totalConsultas] = horario;
        situacoesConsultas[totalConsultas] = STATUS_AGENDADA;
        observacoesConsultas[totalConsultas] = "";
        totalConsultas++;

        System.out.println("[SUCESSO] Consulta #" + idGerado + " agendada para " + nomeAnimal
                + " com " + nomesVeterinarios[indiceVet] + " em "
                + data.format(FORMATO_DATA) + " às " + horario.format(FORMATO_HORA) + ".");
    }

    /**
     * UC04 — Cancelar consulta (RF04, RN04)
     */
    static void cancelarConsulta(Scanner scanner) {
        System.out.println("\n[UC04 - Cancelar Consulta]");
        int idConsulta = lerOpcaoInteira(scanner, "Informe o número (ID) da consulta: ");

        // Fluxo Alternativo A1: consulta inexistente
        int indice = buscarIndiceConsultaPorId(idConsulta);
        if (indice == -1) {
            System.out.println("[ERRO] Consulta #" + idConsulta + " não encontrada.");
            return;
        }

        // Fluxo Alternativo A2: valida RN04 (somente consultas 'agendada' podem ser canceladas)
        if (!situacoesConsultas[indice].equals(STATUS_AGENDADA)) {
            System.out.println("[ERRO - RN04] Apenas consultas com situação 'agendada' podem ser canceladas. "
                    + "Situação atual: " + situacoesConsultas[indice] + ".");
            return;
        }

        situacoesConsultas[indice] = STATUS_CANCELADA;
        System.out.println("[SUCESSO] Consulta #" + idConsulta + " cancelada.");
    }

    // =========================================================================
    // CASOS DE USO — VETERINÁRIO (UC05 e UC06)
    // =========================================================================

    /**
     * UC05 — Consultar agenda do dia (RF05)
     */
    static void consultarAgendaDoDia(Scanner scanner) {
        System.out.println("\n[UC05 - Consultar Agenda do Dia]");
        String crmv = lerTextoObrigatorio(scanner, "Informe seu CRMV (ex: CRMV-SP 12345): ").toUpperCase();

        // Fluxo Alternativo A1: veterinário não cadastrado
        int indiceVet = buscarIndiceVeterinarioPorCrmv(crmv);
        if (indiceVet == -1) {
            System.out.println("[ERRO] Veterinário com " + crmv + " não cadastrado.");
            return;
        }

        LocalDate dataConsulta = lerData(scanner, "Informe a data desejada (dd/MM/aaaa): ");

        System.out.println("\n--- Agenda de " + nomesVeterinarios[indiceVet]
                + " (" + crmv + ") em " + dataConsulta.format(FORMATO_DATA) + " ---");

        boolean encontrouAlguma = false;
        for (int i = 0; i < totalConsultas; i++) {
            if (crmvsVeterinariosConsultas[i].equalsIgnoreCase(crmv)
                    && datasConsultas[i].equals(dataConsulta)) {
                encontrouAlguma = true;
                System.out.printf("ID #%d | Horário: %s | Animal: %s | Situação: %s%n",
                        idsConsultas[i],
                        horariosConsultas[i].format(FORMATO_HORA),
                        animaisConsultas[i],
                        situacoesConsultas[i]);
                if (!observacoesConsultas[i].isEmpty()) {
                    System.out.println("   -> Observações: " + observacoesConsultas[i]);
                }
            }
        }

        // Fluxo Alternativo A2: agenda vazia na data consultada
        if (!encontrouAlguma) {
            System.out.println("Nenhuma consulta encontrada para esta data.");
        }
    }

    /**
     * UC06 — Registrar consulta como realizada (RF06, RN05)
     */
    static void registrarConsultaRealizada(Scanner scanner) {
        System.out.println("\n[UC06 - Registrar Consulta como Realizada]");
        String crmv = lerTextoObrigatorio(scanner, "Informe seu CRMV (ex: CRMV-SP 12345): ").toUpperCase();

        if (buscarIndiceVeterinarioPorCrmv(crmv) == -1) {
            System.out.println("[ERRO] Veterinário com " + crmv + " não cadastrado.");
            return;
        }

        int idConsulta = lerOpcaoInteira(scanner, "Informe o número (ID) da consulta: ");
        int indice = buscarIndiceConsultaPorId(idConsulta);

        if (indice == -1) {
            System.out.println("[ERRO] Consulta #" + idConsulta + " não encontrada.");
            return;
        }

        // Fluxo Alternativo A2 (RN05): apenas o veterinário responsável pela consulta pode concluí-la
        if (!crmvsVeterinariosConsultas[indice].equalsIgnoreCase(crmv)) {
            System.out.println("[ERRO - RN05] Acesso negado: esta consulta pertence a outro veterinário.");
            return;
        }

        // Fluxo Alternativo A1 (RN05): apenas consultas 'agendada' podem passar para 'realizada'
        if (!situacoesConsultas[indice].equals(STATUS_AGENDADA)) {
            System.out.println("[ERRO - RN05] Não é possível concluir esta consulta. Situação atual: "
                    + situacoesConsultas[indice] + ".");
            return;
        }

        String observacoes = lerTextoObrigatorio(scanner, "Observações do atendimento veterinário: ");

        situacoesConsultas[indice] = STATUS_REALIZADA;
        observacoesConsultas[indice] = observacoes;

        System.out.println("[SUCESSO] Consulta #" + idConsulta + " registrada como REALIZADA!");
    }

    // =========================================================================
    // REGRAS DE NEGÓCIO E BUSCAS AUXILIARES (RN01, RN02, RN03)
    // =========================================================================

    static boolean isDataHoraFutura(LocalDate data, LocalTime horario) {
        LocalDateTime momentoConsulta = LocalDateTime.of(data, horario);
        return momentoConsulta.isAfter(LocalDateTime.now());
    }

    static boolean isHorarioComercialValido(LocalDate data, LocalTime horario) {
        DayOfWeek diaSemana = data.getDayOfWeek();
        boolean diaUtil = diaSemana != DayOfWeek.SATURDAY && diaSemana != DayOfWeek.SUNDAY;

        LocalTime abertura = LocalTime.of(8, 0);
        LocalTime ultimoHorario = LocalTime.of(17, 30);
        boolean dentroDaJanela = !horario.isBefore(abertura) && !horario.isAfter(ultimoHorario);
        boolean blocoDeTrintaMinutos = horario.getMinute() == 0 || horario.getMinute() == 30;

        return diaUtil && dentroDaJanela && blocoDeTrintaMinutos;
    }

    static boolean possuiConflitoAgenda(String crmv, LocalDate data, LocalTime horario) {
        for (int i = 0; i < totalConsultas; i++) {
            boolean mesmoVet = crmvsVeterinariosConsultas[i].equalsIgnoreCase(crmv);
            boolean mesmaData = datasConsultas[i].equals(data);
            boolean mesmoHorario = horariosConsultas[i].equals(horario);
            boolean estaAgendada = situacoesConsultas[i].equals(STATUS_AGENDADA);

            if (mesmoVet && mesmaData && mesmoHorario && estaAgendada) {
                return true;
            }
        }
        return false;
    }

    static int buscarIndiceAnimal(String nomeAnimal, String nomeTutor) {
        for (int i = 0; i < totalAnimais; i++) {
            if (nomesAnimais[i].equalsIgnoreCase(nomeAnimal)
                    && tutoresAnimais[i].equalsIgnoreCase(nomeTutor)) {
                return i;
            }
        }
        return -1;
    }

    static boolean existeAnimalPorNome(String nomeAnimal) {
        for (int i = 0; i < totalAnimais; i++) {
            if (nomesAnimais[i].equalsIgnoreCase(nomeAnimal)) {
                return true;
            }
        }
        return false;
    }

    static int buscarIndiceVeterinarioPorCrmv(String crmv) {
        for (int i = 0; i < totalVeterinarios; i++) {
            if (crmvsVeterinarios[i].equalsIgnoreCase(crmv)) {
                return i;
            }
        }
        return -1;
    }

    static int buscarIndiceConsultaPorId(int id) {
        for (int i = 0; i < totalConsultas; i++) {
            if (idsConsultas[i] == id) {
                return i;
            }
        }
        return -1;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE LEITURA SEGURA (RNF05 — Confiabilidade)
    // =========================================================================

    static int lerOpcaoInteira(Scanner scanner, String mensagem) {
        int valor = 0;
        boolean valido = false;

        do {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                valor = Integer.parseInt(entrada);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Entrada inválida. Digite apenas números inteiros.");
            }
        } while (!valido);

        return valor;
    }

    static int lerInteiroNaoNegativo(Scanner scanner, String mensagem) {
        int valor;

        do {
            valor = lerOpcaoInteira(scanner, mensagem);
            if (valor < 0) {
                System.out.println("[ERRO] O valor não pode ser negativo.");
            }
        } while (valor < 0);

        return valor;
    }

    static String lerTextoObrigatorio(Scanner scanner, String mensagem) {
        String entrada;

        do {
            System.out.print(mensagem);
            entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.println("[ERRO] Este campo é obrigatório e não pode ficar em branco.");
            }
        } while (entrada.isEmpty());

        return entrada;
    }

    static LocalDate lerData(Scanner scanner, String mensagem) {
        LocalDate data = null;
        boolean valido = false;

        do {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                data = LocalDate.parse(entrada, FORMATO_DATA);
                valido = true;
            } catch (DateTimeParseException e) {
                System.out.println("[ERRO] Data inválida. Utilize o formato dd/MM/aaaa (ex: 25/11/2026).");
            }
        } while (!valido);

        return data;
    }

    static LocalTime lerHorario(Scanner scanner, String mensagem) {
        LocalTime horario = null;
        boolean valido = false;

        do {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                horario = LocalTime.parse(entrada, FORMATO_HORA);
                valido = true;
            } catch (DateTimeParseException e) {
                System.out.println("[ERRO] Horário inválido. Utilize o formato HH:mm (ex: 09:30).");
            }
        } while (!valido);

        return horario;
    }
}
