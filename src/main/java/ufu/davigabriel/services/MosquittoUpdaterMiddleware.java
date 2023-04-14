package ufu.davigabriel.services;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.Client;
import ufu.davigabriel.models.Product;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;
import ufu.davigabriel.server.ProductGRPC;

import java.util.Arrays;
import java.util.Random;

public class MosquittoUpdaterMiddleware implements IProxyDatabase {
    private static final MosquittoUpdaterMiddleware instance = new MosquittoUpdaterMiddleware();
    final private boolean SHOULD_CONNECT_ONLINE = false;
    final private String RANDOM_ID = Integer.valueOf(new Random().nextInt(100000000)).toString();
    final private String CLIENT_ID = SHOULD_CONNECT_ONLINE ? "publisher-davi-vilarinho-gabriel-amaral-gbc074" : RANDOM_ID;
    final private MemoryPersistence PERSISTENCE = new MemoryPersistence();
    final private DatabaseService databaseService = DatabaseService.getInstance();
    private int QOS = 2;
    private MqttAsyncClient mqttAsyncClient;

    private MosquittoUpdaterMiddleware() {
        try {
            String BROKER = SHOULD_CONNECT_ONLINE ? "tcp://broker.hivemq.com:1883" : "tcp://localhost:1883";
            this.mqttAsyncClient = new MqttAsyncClient(BROKER, CLIENT_ID, PERSISTENCE);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(CLIENT_ID);
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            System.out.println("Inicializando conexao com broker MQTT");
            this.mqttAsyncClient.setCallback(new MosquittoTopicCallback());
            IMqttToken iMqttToken = this.mqttAsyncClient.connect(connOpts);
            iMqttToken.waitForCompletion();
            System.out.println("Conectado com sucesso");
            this.mqttAsyncClient.subscribe("admin/#", 0);
            System.out.println("Subscrito...");
        } catch (MqttException me) {
            System.out.println("Nao foi possivel inicializar o client MQTT, encerrando");
            System.out.println("reason: " + me.getReasonCode());
            System.out.println("msg: " + me.getMessage());
            System.out.println("loc: " + me.getLocalizedMessage());
            System.out.println("cause: " + me.getCause());
            System.out.println("exception: " + me);
            me.printStackTrace();
            System.exit(-1);
        }
    }

    public static MosquittoUpdaterMiddleware getInstance() {
        return instance;
    }

    public void publishClientChange(ClientGRPC clientGRPC, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttAsyncClient.publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(clientGRPC).getBytes()));
    }

    public void publishClientDeletion(IDGRPC idgrpc) throws MqttException {
        mqttAsyncClient.publish(MosquittoTopics.CLIENT_DELETION_TOPIC.name(), new MqttMessage(idgrpc.toByteArray()));
    }

    public void publishProductChange(ProductGRPC productGRPC, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttAsyncClient.publish(mosquittoTopics.name(), new MqttMessage(productGRPC.toByteArray()));
    }

    public void publishProductDeletion(IDGRPC idgrpc) throws MqttException {
        mqttAsyncClient.publish(MosquittoTopics.PRODUCT_DELETION_TOPIC.name(), new MqttMessage(idgrpc.toByteArray()));
    }

    public void disconnect() {
        System.out.println("Desconectando...");
        try {
            this.mqttAsyncClient.disconnect();
            System.out.println("Desconectado com sucesso");
        } catch (MqttException e) {
            System.out.println("Nao foi possivel desconectar do broker... Conexao estagnada");
        }
    }

    @Override
    public void createClient(ClientGRPC clientGRPC) throws DuplicateDatabaseItemException, MqttException {
        if (databaseService.hasClient(clientGRPC.getCID()))
            throw new DuplicateDatabaseItemException();
        publishClientChange(clientGRPC, MosquittoTopics.CLIENT_CREATION_TOPIC);
    }

    @Override
    public void updateClient(ClientGRPC clientGRPC) throws NotFoundItemInDatabaseException {

    }

    @Override
    public void deleteClient(IDGRPC idgrpc) throws NotFoundItemInDatabaseException {

    }

    @Override
    public void createProduct(ProductGRPC product) throws DuplicateDatabaseItemException {

    }

    @Override
    public void updateProduct(ProductGRPC ProductGRPC) throws NotFoundItemInDatabaseException {

    }

    @Override
    public void deleteProduct(IDGRPC idgrpc) throws NotFoundItemInDatabaseException {

    }
}
