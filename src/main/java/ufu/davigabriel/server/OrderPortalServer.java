package ufu.davigabriel.server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import ufu.davigabriel.services.DatabaseService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OrderPortalServer {
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new OrderPortalServer.OrderPortalImpl())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    OrderPortalServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        final OrderPortalServer server = new OrderPortalServer();
        server.start();
        System.out.println("Order Portal running...");
        server.blockUntilShutdown();
    }

    static public class OrderPortalImpl extends OrderPortalGrpc.OrderPortalImplBase {

        private DatabaseService databaseService = DatabaseService.getInstance();

        @Override
        public void createOrder(OrderGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
        }

        @Override
        public void retrieveOrder(IDGRPC request, StreamObserver<OrderGRPC> responseObserver) {
        }

        @Override
        public void updateOrder(OrderGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
        }

        @Override
        public void deleteOrder(IDGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
        }

        @Override
        public void retrieveClientOrders(IDGRPC request, StreamObserver<OrderGRPC> responseObserver) {
        }
    }

}
