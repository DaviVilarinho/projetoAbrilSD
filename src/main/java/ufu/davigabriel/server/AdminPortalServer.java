package ufu.davigabriel.server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.client.AdminPortalReply;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.services.DatabaseService;
import ufu.davigabriel.services.MosquittoTopics;
import ufu.davigabriel.services.MosquittoUpdaterMiddleware;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AdminPortalServer {
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final AdminPortalServer server = new AdminPortalServer();
        MosquittoUpdaterMiddleware.getInstance();
        server.start();
        System.out.println("AQUI CONSEGUIMOS FINALMENTE COLOCAR O GRPC");
        server.blockUntilShutdown();
    }

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

    static public class AdminPortalImpl extends AdminPortalGrpc.AdminPortalImplBase {
        private DatabaseService databaseService = DatabaseService.getInstance();
        private MosquittoUpdaterMiddleware mosquittoUpdaterMiddleware = MosquittoUpdaterMiddleware.getInstance();

        @Override
        public void createClient(ClientGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                mosquittoUpdaterMiddleware.publishClientChange(request, MosquittoTopics.CLIENT_CREATION_TOPIC);
                databaseService.createClient(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(AdminPortalReply.SUCESSO.getError())
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (DuplicateDatabaseItemException exception) {
                exception.replyError(responseObserver);
            } catch (MqttException mqttException) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(-10)
                        .setDescription("Erro MQTT").build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieveClient(IDGRPC request, StreamObserver<ClientOrErrorGRPC> responseObserver) {
            try {
                responseObserver.onNext(databaseService.retrieveClient(request).toClientOrErrorGRPC());
            } catch (NotFoundItemInDatabaseException exception) {
                responseObserver.onNext(ClientOrErrorGRPC.newBuilder().setReplyGRPC(exception.getErrorReply(AdminPortalReply.INEXISTENTE)).build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void updateClient(ClientGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                mosquittoUpdaterMiddleware.publishClientChange(request, MosquittoTopics.CLIENT_UPDATE_TOPIC);
                databaseService.updateClient(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(AdminPortalReply.SUCESSO.getError())
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
                responseObserver.onError(exception);
            } catch (MqttException mqttException) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(-10)
                        .setDescription("Erro MQTT").build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void deleteClient(IDGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                mosquittoUpdaterMiddleware.publishClientDeletion(request);
                databaseService.deleteClient(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(AdminPortalReply.SUCESSO.getError())
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } catch (MqttException mqttException) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(-10)
                        .setDescription("Erro MQTT").build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void createProduct(ProductGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                mosquittoUpdaterMiddleware.publishProductChange(request, MosquittoTopics.PRODUCT_CREATION_TOPIC);
                databaseService.createProduct(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(AdminPortalReply.SUCESSO.getError())
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (DuplicateDatabaseItemException exception) {
                exception.replyError(responseObserver);
            } catch (MqttException mqttException) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(-10)
                        .setDescription("Erro MQTT").build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieveProduct(IDGRPC request, StreamObserver<ProductGRPC> responseObserver) {
            try {
                responseObserver.onNext(databaseService.retrieveProduct(request).toProductGRPC());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void updateProduct(ProductGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                mosquittoUpdaterMiddleware.publishProductChange(request, MosquittoTopics.CLIENT_UPDATE_TOPIC);
                databaseService.updateProduct(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(AdminPortalReply.SUCESSO.getError())
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } catch (MqttException mqttException) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(-10)
                        .setDescription("Erro MQTT").build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void deleteProduct(IDGRPC request, StreamObserver<ReplyGRPC> responseObserver) {
            try {
                mosquittoUpdaterMiddleware.publishProductDeletion(request);
                databaseService.deleteProduct(request);
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(AdminPortalReply.SUCESSO.getError())
                        .setDescription(AdminPortalReply.SUCESSO.getDescription())
                        .build());
            } catch (NotFoundItemInDatabaseException exception) {
                exception.replyError(responseObserver);
            } catch (MqttException mqttException) {
                responseObserver.onNext(ReplyGRPC.newBuilder()
                        .setError(-10)
                        .setDescription("Erro MQTT").build());
            } finally {
                responseObserver.onCompleted();
            }
        }
    }
}
