package ufu.davigabriel.server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.services.AdminDatabaseService;
import ufu.davigabriel.services.MosquittoTopics;
import ufu.davigabriel.services.MosquittoAdminUpdaterMiddleware;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AdminPortalServer {
    private Server server;
    public static int BASE_PORTAL_SERVER_PORT = 25506;

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = BASE_PORTAL_SERVER_PORT;
        if (args.length > 0) {
            port = BASE_PORTAL_SERVER_PORT + Integer.parseInt(args[0]);
        }
        AdminPortalServer server = new AdminPortalServer();
        server.start(port);
        System.out.println("Admin portal running...");
        server.blockUntilShutdown();
    }

    private void start(int port) throws IOException {
        /* The port on which the server should run */
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
        private AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        private MosquittoAdminUpdaterMiddleware mosquittoAdminUpdaterMiddleware = MosquittoAdminUpdaterMiddleware.getInstance();

        @Override
        public void createClient(Client request, StreamObserver<Reply> responseObserver) {
            try {
                mosquittoAdminUpdaterMiddleware.createClient(request);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (DuplicateDatabaseItemException exception) {
                exception.replyError(responseObserver);
            } catch (MqttException mqttException) {
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.ERRO_MQTT.getError())
                        .setDescription(ReplyNative.ERRO_MQTT.getDescription())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieveClient(ID request, StreamObserver<Client> responseObserver) {
            try {
                responseObserver.onNext(adminDatabaseService.retrieveClient(request).toClient());
            } catch (NotFoundItemInDatabaseException exception) {
                responseObserver.onNext(ClientNative.generateEmptyClientNative().toClient());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void updateClient(Client request, StreamObserver<Reply> responseObserver) {
            try {
                mosquittoAdminUpdaterMiddleware.publishClientChange(request, MosquittoTopics.CLIENT_UPDATE_TOPIC);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (MqttException mqttException) {
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.ERRO_MQTT.getError())
                        .setDescription(ReplyNative.ERRO_MQTT.getDescription())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void deleteClient(ID request, StreamObserver<Reply> responseObserver) {
            try {
                mosquittoAdminUpdaterMiddleware.publishClientDeletion(request);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (MqttException mqttException) {
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.ERRO_MQTT.getError())
                        .setDescription(ReplyNative.ERRO_MQTT.getDescription())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void createProduct(Product request, StreamObserver<Reply> responseObserver) {
            try {
                mosquittoAdminUpdaterMiddleware.publishProductChange(request, MosquittoTopics.PRODUCT_CREATION_TOPIC);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (MqttException mqttException) {
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.ERRO_MQTT.getError())
                        .setDescription(ReplyNative.ERRO_MQTT.getDescription())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieveProduct(ID request, StreamObserver<Product> responseObserver) {
            try {
                responseObserver.onNext(adminDatabaseService.retrieveProduct(request).toProduct());
            } catch (NotFoundItemInDatabaseException exception) {
                responseObserver.onNext(ProductNative.generateEmptyProductNative().toProduct());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void updateProduct(Product request, StreamObserver<Reply> responseObserver) {
            try {
                mosquittoAdminUpdaterMiddleware.publishProductChange(request, MosquittoTopics.PRODUCT_UPDATE_TOPIC);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (MqttException mqttException) {
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.ERRO_MQTT.getError())
                        .setDescription(ReplyNative.ERRO_MQTT.getDescription())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void deleteProduct(ID request, StreamObserver<Reply> responseObserver) {
            try {
                mosquittoAdminUpdaterMiddleware.publishProductDeletion(request);
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.SUCESSO.getError())
                        .setDescription(ReplyNative.SUCESSO.getDescription())
                        .build());
            } catch (MqttException mqttException) {
                responseObserver.onNext(Reply.newBuilder()
                        .setError(ReplyNative.ERRO_MQTT.getError())
                        .setDescription(ReplyNative.ERRO_MQTT.getDescription())
                        .build());
            } finally {
                responseObserver.onCompleted();
            }
        }
    }
}
