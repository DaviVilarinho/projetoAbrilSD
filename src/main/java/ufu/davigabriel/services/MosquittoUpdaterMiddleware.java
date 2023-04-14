package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ufu.davigabriel.models.Order;
import ufu.davigabriel.models.Product;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;
import ufu.davigabriel.server.OrderGRPC;
import ufu.davigabriel.server.ProductGRPC;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Random;

public class MosquittoUpdaterMiddleware {
    private static final MosquittoUpdaterMiddleware instance = new MosquittoUpdaterMiddleware();
    private int QOS = 2;
    final private boolean SHOULD_CONNECT_ONLINE = false;
    final private String RANDOM_ID = Integer.valueOf(new Random().nextInt(100000000)).toString();
    final private String CLIENT_ID = SHOULD_CONNECT_ONLINE ? "publisher-davi-vilarinho-gabriel-amaral-gbc074" : RANDOM_ID;
    final private MemoryPersistence PERSISTENCE = new MemoryPersistence();
    final private DatabaseService databaseService = DatabaseService.getInstance();
    private MqttClient mqttClient;

    private MosquittoUpdaterMiddleware() {
        try {
            String BROKER = SHOULD_CONNECT_ONLINE ? "tcp://broker.hivemq.com:1883" : "tcp://localhost:1883";
            this.mqttClient = new MqttClient(BROKER, CLIENT_ID, PERSISTENCE);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Inicializando conexao com broker MQTT");
            this.mqttClient.connect(connOpts);
            System.out.println("Conectado com sucesso");
            this.mqttClient.subscribe(MosquittoTopics.CLIENT_CREATION_TOPIC.name(), QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
                ClientGRPC clientGRPC = (ClientGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
                databaseService.createClient(clientGRPC);
                databaseService.listAll();
            });
            this.mqttClient.subscribe(MosquittoTopics.CLIENT_UPDATE_TOPIC.name(), QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
                ClientGRPC clientGRPC = (ClientGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
                databaseService.updateClient(clientGRPC);
                databaseService.listAll();
            });
            this.mqttClient.subscribe(MosquittoTopics.CLIENT_DELETION_TOPIC.name(), QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
                IDGRPC idgrpc = (IDGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
                databaseService.deleteClient(idgrpc);
                databaseService.listAll();
            });
            this.mqttClient.subscribe(MosquittoTopics.PRODUCT_CREATION_TOPIC.name(), QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
                ProductGRPC productGRPC = (ProductGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
                databaseService.createProduct(productGRPC);
                databaseService.listAll();
            });
            this.mqttClient.subscribe(MosquittoTopics.PRODUCT_UPDATE_TOPIC.name(), QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
                ProductGRPC productGRPC = (ProductGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
                databaseService.updateProduct(productGRPC);
                databaseService.listAll();
            });
            this.mqttClient.subscribe(MosquittoTopics.PRODUCT_DELETION_TOPIC.name(), QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
                IDGRPC idgrpc = (IDGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
                databaseService.deleteProduct(idgrpc);
                databaseService.listAll();
            });
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

    public static MosquittoUpdaterMiddleware getInstance() {
        return instance;
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
