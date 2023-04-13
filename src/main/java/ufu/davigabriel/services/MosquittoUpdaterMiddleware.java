package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ufu.davigabriel.server.ClientGRPC;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Random;

public class MosquittoUpdaterMiddleware {
    private static final MosquittoUpdaterMiddleware instance = new MosquittoUpdaterMiddleware();
    private String CLIENT_TOPIC = "notifier/client/1";
    private String ORDER_TOPIC = "notifier/order/1";
    private String PRODUCT_TOPIC = "notifier/product/1";
    private int QOS = 2;
    final private boolean SHOULD_CONNECT_ONLINE = false;
    final private String BROKER = SHOULD_CONNECT_ONLINE ? "tcp://broker.hivemq.com:1883" : "tcp://localhost:1883";
    final private String RANDOM_ID = Integer.valueOf(new Random().nextInt(100000000)).toString();
    final private String CLIENT_ID = SHOULD_CONNECT_ONLINE ? "publisher-davi-vilarinho-gabriel-amaral-gbc074" : RANDOM_ID;
    final private MemoryPersistence PERSISTENCE = new MemoryPersistence();
    private MqttClient mqttClient;

    private MosquittoUpdaterMiddleware() {
        try {
            this.mqttClient = new MqttClient(BROKER, CLIENT_ID, PERSISTENCE);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Inicializando conexao com broker MQTT");
            this.mqttClient.connect(connOpts);
            System.out.println("Conectado com sucesso");
            this.mqttClient.subscribe(CLIENT_TOPIC, QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString()); //((ClientGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject()).toString());
            });
            this.mqttClient.subscribe(ORDER_TOPIC, QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
            });
            this.mqttClient.subscribe(PRODUCT_TOPIC, QOS, (topic, message) -> {
                System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
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

    public void publishClientChange(ClientGRPC clientGRPC) throws MqttException {
        mqttClient.publish(CLIENT_TOPIC, new MqttMessage(clientGRPC.toByteArray()));
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
