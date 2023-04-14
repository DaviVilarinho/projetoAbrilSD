package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;
import ufu.davigabriel.server.ProductGRPC;

import java.util.Arrays;
import java.util.Random;

public class MosquittoUpdaterMiddleware {
    private static final MosquittoUpdaterMiddleware instance = new MosquittoUpdaterMiddleware();
    final private boolean SHOULD_CONNECT_ONLINE = false;
    final private String RANDOM_ID = Integer.valueOf(new Random().nextInt(100000000)).toString();
    final private String CLIENT_ID = SHOULD_CONNECT_ONLINE ? "publisher-davi-vilarinho-gabriel-amaral-gbc074" : RANDOM_ID;
    final private MemoryPersistence PERSISTENCE = new MemoryPersistence();
    final private DatabaseService databaseService = DatabaseService.getInstance();
    private int QOS = 2;
    private MqttClient mqttClient;

    private MosquittoUpdaterMiddleware() {
        try {
            String BROKER = SHOULD_CONNECT_ONLINE ? "tcp://broker.hivemq.com:1883" : "tcp://localhost:1883";
            this.mqttClient = new MqttClient(BROKER, CLIENT_ID, PERSISTENCE);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Inicializando conexao com broker MQTT");
            this.mqttClient.setCallback(new MosquittoTopicCallback());
            this.mqttClient.connect(connOpts);
            System.out.println("Conectado com sucesso");
            /*
            Object[] topics = Arrays.stream(MosquittoTopics.values()).map(MosquittoTopics::getTopic).toArray();
            this.mqttClient.subscribe(Arrays.copyOf(topics, topics.length, String[].class));
            */
            this.mqttClient.subscribe(MosquittoTopics.CLIENT_CREATION_TOPIC.getTopic());
            this.mqttClient.wait();
            System.out.println("Subscrevendo...");
        } catch (MqttException me) {
            System.out.println("Nao foi possivel inicializar o client MQTT, encerrando");
            System.out.println("reason: " + me.getReasonCode());
            System.out.println("msg: " + me.getMessage());
            System.out.println("loc: " + me.getLocalizedMessage());
            System.out.println("cause: " + me.getCause());
            System.out.println("exception: " + me);
            me.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static MosquittoUpdaterMiddleware getInstance() {
        return instance;
    }

    public void publishClientChange(ClientGRPC clientGRPC, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttClient.publish(mosquittoTopics.name(), new MqttMessage(clientGRPC.toByteArray()));
    }

    public void publishClientDeletion(IDGRPC idgrpc) throws MqttException {
        mqttClient.publish(MosquittoTopics.CLIENT_DELETION_TOPIC.name(), new MqttMessage(idgrpc.toByteArray()));
    }

    public void publishProductChange(ProductGRPC productGRPC, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttClient.publish(mosquittoTopics.name(), new MqttMessage(productGRPC.toByteArray()));
    }

    public void publishProductDeletion(IDGRPC idgrpc) throws MqttException {
        mqttClient.publish(MosquittoTopics.PRODUCT_DELETION_TOPIC.name(), new MqttMessage(idgrpc.toByteArray()));
    }

    public void disconnect() {
        System.out.println("Desconectando...");
        try {
            this.mqttClient.disconnect();
            System.out.println("Desconectado com sucesso");
        } catch (MqttException e) {
            System.out.println("Nao foi possivel desconectar do broker... Conexao estagnada");
        }
    }
}
