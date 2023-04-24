package ufu.davigabriel.client;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import ufu.davigabriel.Main;
import ufu.davigabriel.models.OrderItemNative;
import ufu.davigabriel.models.OrderNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.server.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class OrderPortalClient {
    private static final String HOST = "localhost";
    private static final int SERVER_PORT = OrderPortalServer.BASE_PORTAL_SERVER_PORT + new Random().nextInt(Main.PORTAL_SERVERS);
    private static final String TARGET_SERVER = String.format("%s:%d", HOST, SERVER_PORT);
    private final OrderPortalGrpc.OrderPortalBlockingStub blockingStub;

    public OrderPortalClient(Channel channel) {
        this.blockingStub = OrderPortalGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("----------------------------------");
        System.out.println("Bem vindo ao Portal de Pedidos");
        System.out.println("----------------------------------");

        ManagedChannel channel = Grpc.newChannelBuilder(TARGET_SERVER, InsecureChannelCredentials.create()).build();

        String CONNECTION_SERVER = String.format("%s:%d", "localhost", AdminPortalServer.BASE_PORTAL_SERVER_PORT + new Random().nextInt(Main.PORTAL_SERVERS));
        ManagedChannel connectionChannel = Grpc.newChannelBuilder(CONNECTION_SERVER, InsecureChannelCredentials.create()).build();
        AdminPortalGrpc.AdminPortalBlockingStub connectionBlockingStub = AdminPortalGrpc.newBlockingStub(connectionChannel);

        try {
            OrderPortalClient orderPortalClient = new OrderPortalClient(channel);
            System.out.println("Conectado com server " + TARGET_SERVER);

            OrderPortalOption orderPortalOption = OrderPortalOption.NOOP;
            Scanner scanner = new Scanner(System.in);
            String loggedClientId = orderPortalClient.login(connectionBlockingStub, scanner);
            if (loggedClientId == null) {
                System.out.println("Tentativas esgotadas!");
                orderPortalOption = OrderPortalOption.SAIR;
            }

            while (!OrderPortalOption.SAIR.equals(orderPortalOption)) {
                System.out.println("^^--__");
                System.out.println("Opcoes:");
                Arrays.stream(OrderPortalOption.values()).forEach(System.out::println);

                System.out.print("Escolha: ");
                try {
                    orderPortalOption = OrderPortalOption.valueOf(scanner.nextLine());
                } catch (NullPointerException | IllegalArgumentException exception) {
                    System.out.println("Por favor, escolha outra opcao.");
                    orderPortalOption = OrderPortalOption.NOOP;
                }

                switch (orderPortalOption) {
                    case NOOP -> System.out.println("Nada a ser feito.");
                    case CRIAR_PEDIDO -> {
                        System.out.print("Escreva a ID desejada para o pedido: ");
                        String orderId = scanner.nextLine();

                        ArrayList<OrderItemNative> addedProducts = new ArrayList<>();
                        String option = "z";
                        while (!"n".equals(option)){
                            System.out.print("Escreva se deseja adicionar um produto ao pedido (y/n): ");
                            option = scanner.nextLine().strip().toLowerCase();
                            if("y".equals(option))
                                addedProducts.add(orderPortalClient.addProductToOrder(connectionBlockingStub, scanner));
                        }

                        ReplyNative response = createOrder(orderPortalClient.blockingStub, OrderNative.builder().OID(orderId).CID(loggedClientId).products(addedProducts).build());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("PEDIDO INSERIDO");
                    }
                    case BUSCAR_PEDIDO -> {
                        System.out.print("Escreva o ID do pedido: ");
                        Optional<OrderNative> foundOrder = retrieveOrder(orderPortalClient.blockingStub, scanner.nextLine());
                        foundOrder.ifPresentOrElse(orderNative -> {
                            System.out.println("PEDIDO ENCONTRADO");
                            double totalPrice = 0;
                            for(OrderItemNative item : orderNative.getProducts()){
                                System.out.println(item);
                                totalPrice += item.getPrice();
                            }
                            System.out.println("Preco final do pedido: " + totalPrice);
                        }, () -> System.out.println("PEDIDO NAO ENCONTRADO"));
                    }
                    case MUDAR_PEDIDO -> {
                        System.out.print("Escreva a ID do pedido a mudar: ");
                        String oidAMudar = scanner.nextLine();

                        ArrayList<OrderItemNative> addedProducts = new ArrayList<>();
                        char option;
                        do {
                            System.out.print("Escreva se deseja adicionar novo produto ao pedido (y/n): ");
                            option = scanner.next().toLowerCase().charAt(0);
                            if(option == 'y')
                                addedProducts.add(orderPortalClient.addProductToOrder(connectionBlockingStub, scanner));
                        }while (option != 'n');

                        ReplyNative response = createOrder(orderPortalClient.blockingStub, OrderNative.builder().OID(oidAMudar).CID(loggedClientId).products(addedProducts).build());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("PEDIDO ATUALIZADO");
                    }
                    case REMOVER_PEDIDO -> {
                        System.out.print("Escreva o ID do pedido a ser removido: ");
                        ReplyNative response = removeOrder(orderPortalClient.blockingStub, scanner.nextLine());
                        if (response.getError() != 0) System.out.println("ERRO: " + response.getDescription());
                        else System.out.println("PEDIDO REMOVIDO");
                    }
                    case BUSCAR_PEDIDOS_POR_CLIENTE -> {
                        ArrayList<OrderNative> clientOrders = retrieveClientOrders(orderPortalClient.blockingStub, loggedClientId);
                        System.out.println("PEDIDOS ASSOCIADOS AO CLIENTE:");
                        clientOrders.forEach(orderNative -> {
                            System.out.println(orderNative.getOID());
                        });

                    }
                    default -> {
                        System.out.println("Encerrando o Portal de Pedidos.");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                        orderPortalOption = OrderPortalOption.SAIR;
                    }
                }
            }
        } finally {
            connectionChannel.shutdownNow();
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    static private String geraId(String nome) {
        return Base64.getEncoder().encodeToString(nome.getBytes());
    }

    static private ReplyNative createOrder(OrderPortalGrpc.OrderPortalBlockingStub blockingStub, OrderNative orderNative) {
        return ReplyNative.fromReply(blockingStub.createOrder(orderNative.toOrder()));
    }

    static private Optional<OrderNative> retrieveOrder(OrderPortalGrpc.OrderPortalBlockingStub blockingStub, String OrderId) {
        Order order = blockingStub.retrieveOrder(ID.newBuilder().setID(OrderId).build());
        Optional<OrderNative> optOrder = Optional.empty();
        if (!"0".equals(order.getCID())) {
            optOrder = Optional.of(OrderNative.fromOrder(order));
        }
        return optOrder;
    }

    static private ReplyNative updateOrder(OrderPortalGrpc.OrderPortalBlockingStub blockingStub, OrderNative orderNative) {
        return ReplyNative.fromReply(blockingStub.updateOrder(orderNative.toOrder()));
    }

    static private ReplyNative removeOrder(OrderPortalGrpc.OrderPortalBlockingStub blockingStub, String orderId) {
        return ReplyNative.fromReply(blockingStub.deleteOrder(ID.newBuilder().setID(orderId).build()));
    }


    static private ArrayList<OrderNative> retrieveClientOrders(OrderPortalGrpc.OrderPortalBlockingStub blockingStub, String clientId){
        ArrayList<OrderNative> clientOrders = new ArrayList<>();
        blockingStub.retrieveClientOrders(ID.newBuilder().setID(clientId).build()).forEachRemaining(clientOrder -> clientOrders.add(OrderNative.fromOrder(clientOrder)));
        return clientOrders;
    }

    private String login(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, Scanner scanner) {
        System.out.println("Por favor, autentique o cliente antes de prosseguir.");

        int attempts = 5;
        do {
            System.out.print("Escreva o ID do cliente: ");
            String clientId = scanner.nextLine();
            if (!"0".equals(blockingStub.retrieveClient(ID.newBuilder().setID(clientId).build()).getCID()))
                return clientId;

            System.out.println("ID invalido. " + --attempts + " tentativas restantes.");
        } while (attempts > 0);

        return null;
    }

    private OrderItemNative addProductToOrder(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, Scanner scanner) {
        OrderItemNative orderItemNative = OrderItemNative.builder().build();
        System.out.print("Escreva o ID do produto que deseja adicionar: ");
        orderItemNative.setPID(scanner.nextLine());

        System.out.print("Escreva a quantidade desejada do mesmo: ");
        orderItemNative.setQuantity(scanner.nextInt());

        ProductNative retrievedProduct = ProductNative.fromProduct(blockingStub.retrieveProduct(ID.newBuilder().setID(orderItemNative.getPID()).build()));
        orderItemNative.setName(retrievedProduct.getName());
        orderItemNative.setPrice(retrievedProduct.getPrice());

        return orderItemNative;
    }
}