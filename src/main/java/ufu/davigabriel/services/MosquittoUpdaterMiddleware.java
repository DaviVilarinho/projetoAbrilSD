package ufu.davigabriel.services;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Product;

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
            this.mqttAsyncClient.subscribe("#", 0);
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

    public void publishClientChange(Client client, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttAsyncClient.publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(client).getBytes()));
    }

    public void publishClientDeletion(ID id) throws MqttException {
        mqttAsyncClient.publish(MosquittoTopics.CLIENT_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
    }

    public void publishProductChange(Product product, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttAsyncClient.publish(mosquittoTopics.name(), new MqttMessage(product.toByteArray()));
    }

    public void publishProductDeletion(ID id) throws MqttException {
        mqttAsyncClient.publish(MosquittoTopics.PRODUCT_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
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
    public void createClient(Client client) throws DuplicateDatabaseItemException, MqttException {
        if (databaseService.hasClient(client.getCID()))
            throw new DuplicateDatabaseItemException();
        publishClientChange(client, MosquittoTopics.CLIENT_CREATION_TOPIC);
    }

    @Override
    public void updateClient(Client client) throws NotFoundItemInDatabaseException {

    }

    @Override
    public void deleteClient(ID id) throws NotFoundItemInDatabaseException {

    }

    @Override
    public void createProduct(Product product) throws DuplicateDatabaseItemException {

    }

    @Override
    public void updateProduct(Product Product) throws NotFoundItemInDatabaseException {

    }

    @Override
    public void deleteProduct(ID id) throws NotFoundItemInDatabaseException {

    }
}
