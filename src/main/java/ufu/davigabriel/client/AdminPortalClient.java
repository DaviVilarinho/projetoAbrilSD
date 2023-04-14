package ufu.davigabriel.client;

import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.ProductNative;

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
            System.out.println("Opcoes:");
            Arrays.stream(AdminPortalOption.values()).forEach(System.out::println);

            System.out.print("Escolha: ");
            try {
                adminPortalOption = AdminPortalOption.valueOf(scanner.nextLine());
            } catch (NullPointerException | IllegalArgumentException exception) {
                System.out.println("Por favor, escolha outra opcao.");
                adminPortalOption = AdminPortalOption.NOOP;
            }

            switch (adminPortalOption) {
                case NOOP -> System.out.println("Nada a ser feito.");
                case CRIAR_CLIENTE -> {
                    System.out.print("Escreva o ID do novo cliente: ");
                    String userId = scanner.nextLine();
                    System.out.print("Escreva o novo nome do cliente: ");
                    String name = scanner.nextLine();
                    System.out.print("Escreva o Novo zipCode do cliente: ");
                    String zipCode = scanner.nextLine();

                    AdminPortalReply response = createClient(ClientNative.builder().clientId(userId).name(name).zipCode(zipCode).build());
                    if (response.getError() != 0)
                        System.out.println("ERRO: " + response.getDescription());
                    else
                        System.out.println("CLIENTE INSERIDO");
                }
                case BUSCAR_CLIENTE -> {
                    System.out.print("Escreva o ID do cliente: ");
                    Optional<ClientNative> foundClient = retrieveClient(scanner.nextLine());
                    foundClient.ifPresentOrElse(client -> {
                        System.out.println("CLIENTE ENCONTRADO");
                        System.out.println(client);
                    }, () -> System.out.println("CLIENTE NAO ENCONTRADO"));
                }
                case MUDAR_CLIENTE -> {
                    System.out.print("Novo nome do cliente: ");
                    String name = scanner.nextLine();
                    System.out.print("Novo zipCode do cliente: ");
                    String zipCode = scanner.nextLine();
                    System.out.print("Escreva o ID do cliente: ");

                    AdminPortalReply response = updateClient(ClientNative.builder().clientId(scanner.nextLine()).name(name).zipCode(zipCode).build());
                    if (response.getError() != 0)
                        System.out.println("ERRO: " + response.getDescription());
                    else
                        System.out.println("CLIENTE ALTERADO");
                }
                case REMOVER_CLIENTE -> {
                    System.out.print("Escreva o ID do cliente: ");
                    AdminPortalReply response = removeClient(scanner.nextLine());
                    if (response.getError() != 0)
                        System.out.println("ERRO: " + response.getDescription());
                    else
                        System.out.println("CLIENTE REMOVIDO");
                }
                case CRIAR_PRODUTO -> {
                    System.out.print("Escreva o ID do novo produto: ");
                    String productId = scanner.nextLine();
                    System.out.print("Escreva o nome do novo produto: ");
                    String name = scanner.nextLine();
                    System.out.print("Escreva uma descricao do produto: ");
                    String description = scanner.nextLine();
                    try {
                        System.out.print("Escreva o preco do produto");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Escreva a quantidade do produto");
                        int quantity = Integer.parseInt(scanner.nextLine());
                        AdminPortalReply response = createProduct(ProductNative.builder()
                                .productId(productId)
                                .name(name)
                                .description(description)
                                .price(price)
                                .quantity(quantity)
                                .build());
                        if (response.getError() != 0)
                            System.out.println("ERRO: " + response.getDescription());
                        else
                            System.out.println("CLIENTE INSERIDO");
                    } catch (NullPointerException | NumberFormatException formatException) {
                        System.out.println("Este produto e invalido e nao sera inserido");
                    }
                }
                case BUSCAR_PRODUTO -> {
                    System.out.print("Escreva o ID do produto: ");
                    Optional<ProductNative> foundProduct = retrieveProduct(scanner.nextLine());
                    foundProduct.ifPresentOrElse(productNative -> {
                        System.out.println("PRODUTO ENCONTRADO");
                        System.out.println(productNative);
                    }, () -> System.out.println("PRODUTO NAO ENCONTRADO"));
                }
                case MUDAR_PRODUTO -> {
                    System.out.print("Escreva o ID do produto a ser alterado: ");
                    String targetProductId = scanner.nextLine();
                    System.out.print("Escreva o nome do novo produto: ");
                    String name = scanner.nextLine();
                    System.out.print("Escreva uma descricao do produto: ");
                    String description = scanner.nextLine();
                    try {
                        System.out.print("Escreva o preco do produto");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Escreva a quantidade do produto");
                        int quantity = Integer.parseInt(scanner.nextLine());
                        AdminPortalReply response = updateProduct(ProductNative.builder()
                                .productId(targetProductId)
                                .name(name)
                                .description(description)
                                .price(price)
                                .quantity(quantity)
                                .build());
                        if (response.getError() != 0)
                            System.out.println("ERRO: " + response.getDescription());
                        else
                            System.out.println("PRODUTO ATUALIZADO");
                    } catch (NullPointerException | NumberFormatException formatException) {
                        System.out.println("Este produto e invalido e nao sera atualizado");
                    }
                }
                case REMOVER_PRODUTO -> {
                    System.out.print("Escreva o ID do produto: ");
                    AdminPortalReply response = removeProduct(scanner.nextLine());
                    if (response.getError() != 0)
                        System.out.println("ERRO: " + response.getDescription());
                    else
                        System.out.println("PRODUTO REMOVIDO");
                }
                default -> {
                    System.out.println("Encerrando o portal administrativo.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    adminPortalOption = AdminPortalOption.SAIR;
                }
            }
        }
    }

    static private AdminPortalReply createClient(ClientNative clientNative) {
        if (!true) { //substituir true por uma operacao que verifica se o cliente existe no banco
            //incluir operacao que inclui um cliente no banco de dados e na cache
            System.out.println("Inseriu um ID " + clientNative.getClientId());
            return AdminPortalReply.SUCESSO;
        }

        return AdminPortalReply.DUPLICATA;
    }

    static private Optional<ClientNative> retrieveClient(String clientId) {
        Optional optClient = Optional.empty();
        //incluir operacao que tenta recuperar o cliente da cache
        //se nao encontrar, incluir operacao que tenta recuperar o cliente do banco de dados e incluir operacao que atualiza a cache

        System.out.println("Buscou um ID " + clientId);
        return optClient;
    }

    static private AdminPortalReply updateClient(ClientNative clientNative) {
        if (true) { //substituir true por uma operacao que verifica se o cliente existe no banco
            //incluir operacao que atualiza o cliente no banco e na cache
            System.out.println("Atualizou um ID " + clientNative.getClientId());
            return AdminPortalReply.SUCESSO;
        }

        return AdminPortalReply.INEXISTENTE;
    }

    static private AdminPortalReply removeClient(String clientId) {
        if (true) { //substituir true por uma operacao que verifica se o cliente existe no banco
            //incluir operacao que remove o cliente do banco e da cache (se estiver la)
            System.out.println("Removeu um ID " + clientId);
            return AdminPortalReply.SUCESSO;
        }
        return AdminPortalReply.INEXISTENTE;
    }

    static private AdminPortalReply createProduct(ProductNative productNative) {
        if (!true) {
            System.out.println("Criou produto " + productNative.getProductId());
            return AdminPortalReply.SUCESSO;
        }

        return AdminPortalReply.DUPLICATA;
    }

    static private Optional<ProductNative> retrieveProduct(String productId) {
        Optional<ProductNative> optionalProduct = Optional.empty();

        System.out.println("Buscou um ID " + productId);
        return optionalProduct;
    }

    static private AdminPortalReply updateProduct(ProductNative productNative) {
        if (!true) {
            System.out.println("Atualizou produto " + productNative.getProductId());
            return AdminPortalReply.SUCESSO;
        }

        return AdminPortalReply.DUPLICATA;
    }

    static private AdminPortalReply removeProduct(String productId) {
        if (true) { //substituir true por uma operacao que verifica se o cliente existe no banco
            //incluir operacao que remove o cliente do banco e da cache (se estiver la)
            System.out.println("Removeu um ID " + productId);
            return AdminPortalReply.SUCESSO;
        }
        return AdminPortalReply.INEXISTENTE;
    }
}
