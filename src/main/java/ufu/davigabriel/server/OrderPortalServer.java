package ufu.davigabriel.server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.ReplyNative;
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
        public void createOrder(Order request, StreamObserver<Reply> responseObserver) {
            try {
                databaseService.createOrder(request);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (DuplicateDatabaseItemException exception) {
                exception.replyError(responseObserver);
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieveOrder(ID request, StreamObserver<Order> responseObserver) {
            try {
                responseObserver.onNext(databaseService.retrieveOrder(request).toOrder());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void updateOrder(Order request, StreamObserver<Reply> responseObserver) {
            try {
                databaseService.updateOrder(request);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void deleteOrder(ID request, StreamObserver<Reply> responseObserver) {
            try {
                databaseService.deleteProduct(request);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieveClientOrders(ID request, StreamObserver<Order> responseObserver) {
            try {
                databaseService.retrieveClientOrders(request).forEach((order) -> {
                    responseObserver.onNext(order.toOrder());
                });
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } finally {
                responseObserver.onCompleted();
            }
        }
    }

}
