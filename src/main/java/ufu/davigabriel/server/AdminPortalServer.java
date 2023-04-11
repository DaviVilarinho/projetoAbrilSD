package ufu.davigabriel.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.stub.StreamObserver;
import ufu.davigabriel.client.AdminPortalReply;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.services.DatabaseService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AdminPortalServer {
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new AdminPortalImpl())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    AdminPortalServer.this.stop();
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

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        /*
        final AdminPortalServer server = new AdminPortalServer();
        server.start();
        server.blockUntilShutdown();
         */
        System.out.println("AQUI CONSEGUIMOS FINALMENTE COLOCAR O GRPC");
    }
    static class AdminPortalImpl extends AdminPortalGrpc.AdminPortalImplBase {
        private DatabaseService databaseService = DatabaseService.getInstance();

        @Override
        public void createClient(ClientGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                databaseService.createClient(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(0)
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (DuplicateDatabaseItemException exception) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(1)
                        .setDescription(exception.getMessage())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
            super.createClient(request, responseObserver);
        }

        @Override
        public void retrieveClient(IDGRPC request, StreamObserver<ClientGRPC> responseObserver) {
            super.retrieveClient(request, responseObserver);
        }

        @Override
        public void updateClient(ClientGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            super.updateClient(request, responseObserver);
        }

        @Override
        public void deleteClient(IDGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            super.deleteClient(request, responseObserver);
        }

        @Override
        public void createProduct(ProductGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            super.createProduct(request, responseObserver);
        }

        @Override
        public void retrieveProduct(IDGRPC request, StreamObserver<ProductGRPC> responseObserver) {
            super.retrieveProduct(request, responseObserver);
        }

        @Override
        public void updateProduct(ProductGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            super.updateProduct(request, responseObserver);
        }

        @Override
        public void deleteProduct(IDGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            super.deleteProduct(request, responseObserver);
        }
    }
    static class OrderPortalImpl extends OrderPortalGrpc.OrderPortalImplBase {
        private DatabaseService databaseService = DatabaseService.getInstance();

    }
}
