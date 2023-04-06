package ufu.davigabriel.client;

import ufu.davigabriel.models.Client;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class AdminPortalClient {
    public static void main(String[] args) {
        System.out.println("----------------------------------");
        System.out.println("Bem vindo ao Portal Administrativo");
        System.out.println("----------------------------------");

        AdminPortalOption adminPortalOption = AdminPortalOption.NOOP;
        Scanner scanner = new Scanner(System.in);
        while (!AdminPortalOption.SAIR.equals(adminPortalOption)) {
            System.out.println("Escolha o que quer fazer:");
            Arrays.stream(AdminPortalOption.values()).forEach(System.out::println);

            System.out.print("Escolha: ");
            try {
                adminPortalOption = AdminPortalOption.valueOf(scanner.nextLine());
            } catch (NullPointerException | IllegalArgumentException exception) {
                System.out.println("Por favor, escolha outra opcao.");
                adminPortalOption = AdminPortalOption.NOOP;
            }

            switch (adminPortalOption) {
                case NOOP -> System.out.println("Nada a ser Feito");
                case BUSCAR_CLIENTE -> {
                    System.out.print("Escreva o ID do cliente: ");
                    Optional<Client> foundClient = searchClient(scanner.nextLine());
                    foundClient.ifPresentOrElse(client -> {
                        System.out.println("CLIENTE ENCONTRADO");
                        System.out.println(client);
                    }, () -> System.out.println("CLIENTE NAO ENCONTRADO"));
                }
            }

        }
    }

    static private Optional<Client> searchClient(String clientId) {
        System.out.println("Buscou um ID " + clientId);
        return Optional.empty();
    }
}
