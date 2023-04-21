package ufu.davigabriel.client;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.server.AdminPortalGrpc;
import ufu.davigabriel.server.AdminPortalServer;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AdminPortalClient {
    private static String HOST = "localhost";
    private static int SERVER_PORT = AdminPortalServer.BASE_PORTAL_SERVER_PORT;
    private static String TARGET_SERVER = String.format("%s:%d", HOST, SERVER_PORT);

    private final AdminPortalGrpc.AdminPortalBlockingStub blockingStub;

    public AdminPortalClient(Channel channel) {
        this.blockingStub = AdminPortalGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("----------------------------------");
        System.out.println("Bem vindo ao Portal Administrativo");
        System.out.println("----------------------------------");

        ManagedChannel channel = Grpc.newChannelBuilder(TARGET_SERVER, InsecureChannelCredentials.create()).build();

        try {
            AdminPortalClient adminPortalClient = new AdminPortalClient(channel);

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
                        System.out.print("Escreva o novo nome do cliente: ");
                        String name = scanner.nextLine();
                        System.out.print("Escreva o Novo zipCode do cliente: ");
                        String zipCode = scanner.nextLine();

                        String cid = geraId(name);
                        System.out.println("Escolhendo ID: " + cid);
                        ReplyNative response = createClient(adminPortalClient.blockingStub, ClientNative.builder().CID(cid).name(name).zipCode(zipCode).build());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("CLIENTE INSERIDO");
                    }
                    case BUSCAR_CLIENTE -> {
                        System.out.print("Escreva o ID do cliente: ");
                        Optional<ClientNative> foundClient = retrieveClient(adminPortalClient.blockingStub, scanner.nextLine());
                        foundClient.ifPresentOrElse(client -> {
                            System.out.println("CLIENTE ENCONTRADO");
                            System.out.println(client);
                        }, () -> System.out.println("CLIENTE NAO ENCONTRADO"));
                    }
                    case MUDAR_CLIENTE -> {
                        System.out.print("Escreva o ID do cliente a mudar: ");
                        String cidAMudar = scanner.nextLine();

                        System.out.print("Novo nome do cliente: ");
                        String name = scanner.nextLine();
                        System.out.print("Novo zipCode do cliente: ");
                        String zipCode = scanner.nextLine();

                        ReplyNative response = updateClient(adminPortalClient.blockingStub, ClientNative.builder().CID(cidAMudar).name(name).zipCode(zipCode).build());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("CLIENTE ALTERADO");
                    }
                    case REMOVER_CLIENTE -> {
                        System.out.print("Escreva o ID do cliente: ");

                        ReplyNative response = removeClient(adminPortalClient.blockingStub, scanner.nextLine());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("CLIENTE REMOVIDO");
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

                            ReplyNative response = createProduct(ProductNative.builder().PID(productId).name(name).description(description).price(price).quantity(quantity).build());
                            if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                            else System.out.println("CLIENTE INSERIDO");
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

                            ReplyNative response = updateProduct(ProductNative.builder().PID(targetProductId).name(name).description(description).price(price).quantity(quantity).build());
                            if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                            else System.out.println("PRODUTO ATUALIZADO");
                        } catch (NullPointerException | NumberFormatException formatException) {
                            System.out.println("Este produto e invalido e nao sera atualizado");
                        }
                    }
                    case REMOVER_PRODUTO -> {
                        System.out.print("Escreva o ID do produto: ");

                        ReplyNative response = removeProduct(scanner.nextLine());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("PRODUTO REMOVIDO");
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
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    static private String geraId(String nome) {
        return Base64.getEncoder().encodeToString(nome.getBytes());
    }

    static private ReplyNative createClient(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, ClientNative clientNative) {
        return ReplyNative.fromReply(blockingStub.createClient(clientNative.toClient()));
    }

    static private Optional<ClientNative> retrieveClient(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, String clientId) {
        Client client = blockingStub.retrieveClient(ID.newBuilder().setID(clientId).build());
        Optional<ClientNative> optClient = Optional.empty();
        if (!"0".equals(client.getCID())) {
            optClient = Optional.of(ClientNative.fromClient(client));
        }
        return optClient;
    }

    static private ReplyNative updateClient(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, ClientNative clientNative) {
        return ReplyNative.fromReply(blockingStub.updateClient(clientNative.toClient()));
    }

    static private ReplyNative removeClient(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, String clientId) {
        return ReplyNative.fromReply(blockingStub.deleteClient(ID.newBuilder().setID(clientId).build()));
    }

    static private ReplyNative createProduct(ProductNative productNative) {
        if (!true) {
            System.out.println("Criou produto " + productNative.getPID());
            return ReplyNative.SUCESSO;
        }

        return ReplyNative.DUPLICATA;
    }

    static private Optional<ProductNative> retrieveProduct(String productId) {
        Optional<ProductNative> optionalProduct = Optional.empty();

        System.out.println("Buscou um ID " + productId);
        return optionalProduct;
    }

    static private ReplyNative updateProduct(ProductNative productNative) {
        if (!true) {
            System.out.println("Atualizou produto " + productNative.getPID());
            return ReplyNative.SUCESSO;
        }

        return ReplyNative.DUPLICATA;
    }

    static private ReplyNative removeProduct(String productId) {
        if (true) { //substituir true por uma operacao que verifica se o cliente existe no banco
            //incluir operacao que remove o cliente do banco e da cache (se estiver la)
            System.out.println("Removeu um ID " + productId);
            return ReplyNative.SUCESSO;
        }
        return ReplyNative.INEXISTENTE;
    }
}
