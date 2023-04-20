package ufu.davigabriel.services;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import ufu.davigabriel.server.Product;

import java.util.Arrays;
import java.util.Random;

public class MosquittoUpdaterMiddleware implements IProxyDatabase {
    private static MosquittoUpdaterMiddleware instance;
    final private boolean SHOULD_CONNECT_ONLINE = false;
    final private String RANDOM_ID = Integer.valueOf(new Random().nextInt(100000000)).toString();
    final private String CLIENT_ID = SHOULD_CONNECT_ONLINE ? "publisher-davi-vilarinho-gabriel-amaral-gbc074" : RANDOM_ID;
    final private MemoryPersistence PERSISTENCE = new MemoryPersistence();
    final private DatabaseService databaseService = DatabaseService.getInstance();
    private int QOS = 2;
    private MqttClient mqttClient;

    private MosquittoUpdaterMiddleware(MosquittoPortalContext mosquittoContext) {
        try {
            String BROKER = SHOULD_CONNECT_ONLINE ? "tcp://broker.hivemq.com:1883" : "tcp://localhost:1883";
            this.mqttClient = new MqttClient(BROKER, CLIENT_ID, PERSISTENCE);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(CLIENT_ID);
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            System.out.println("Inicializando conexao com broker MQTT");
            this.mqttClient.setCallback(new MosquittoTopicCallback());
            this.mqttClient.connect();
            System.out.println("Conectado com sucesso");
            this.mqttClient.subscribe(getInterestedTopics(mosquittoContext));
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
        } catch (EnumConstantNotPresentException enumConstantNotPresentException) {
            System.out.println("Contexto inexistente...");
            System.exit(-1);
        }
    }
    
    private String[] getInterestedTopics(MosquittoPortalContext mosquittoPortalContext)  {
        Object[] objectTopicsToSubscribe;
        if (MosquittoPortalContext.admin.equals(mosquittoPortalContext)) {
            objectTopicsToSubscribe = Arrays.stream(MosquittoTopics.values())
                    .map(MosquittoTopics::name)
                    .filter(name -> name.startsWith("CLIENT") || name.startsWith("PRODUCT"))
                    .toArray();
        } else if(MosquittoPortalContext.order.equals(mosquittoPortalContext)) {
            objectTopicsToSubscribe = Arrays.stream(MosquittoTopics.values())
                    .map(MosquittoTopics::name)
                    .filter(name -> name.startsWith("ORDER"))
                    .toArray();
        } else {
            throw new EnumConstantNotPresentException(MosquittoPortalContext.class, "Valor nao esperado de contexto");
        }
        return Arrays.copyOf(objectTopicsToSubscribe, objectTopicsToSubscribe.length, String[].class);
    }

    public static MosquittoUpdaterMiddleware getInstance() {
        return instance;
    }

    public static MosquittoUpdaterMiddleware assignServer(MosquittoPortalContext mosquittoGeneralTopics) {
        if (instance == null) {
            instance = new MosquittoUpdaterMiddleware(mosquittoGeneralTopics);
        }
        return instance;
    }

    public void publishClientChange(Client client, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttClient.publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(client).getBytes()));
    }

    public void publishClientDeletion(ID id) throws MqttException {
        mqttClient.publish(MosquittoTopics.CLIENT_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
    }

    public void publishProductChange(Product product, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttClient.publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(product).getBytes()));
    }

    public void publishProductDeletion(ID id) throws MqttException {
        mqttClient.publish(MosquittoTopics.PRODUCT_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
    }

    public void publishOrderChange(Order order, MosquittoTopics mosquittoTopics) throws MqttException {
        mqttClient.publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(order).getBytes()));
    }

    public void publishOrderDeletion(ID id) throws MqttException {
        mqttClient.publish(MosquittoTopics.ORDER_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
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

    @Override
    public void createClient(Client client) throws DuplicateDatabaseItemException, MqttException {
        if (databaseService.hasClient(client.getCID()))
            throw new DuplicateDatabaseItemException();
        publishClientChange(client, MosquittoTopics.CLIENT_CREATION_TOPIC);
    }

    @Override
    public void updateClient(Client client) throws NotFoundItemInDatabaseException, MqttException {
        if (!databaseService.hasClient(client.getCID()))
            throw new NotFoundItemInDatabaseException();
        publishClientChange(client, MosquittoTopics.CLIENT_UPDATE_TOPIC);
    }

    @Override
    public void deleteClient(ID id) throws NotFoundItemInDatabaseException, MqttException {
        if (!databaseService.hasClient(id.getID()))
            throw new NotFoundItemInDatabaseException();
        publishClientDeletion(id);
    }

    @Override
    public void createProduct(Product product) throws DuplicateDatabaseItemException, MqttException {
        if (databaseService.hasProduct(product.getPID()))
            throw new DuplicateDatabaseItemException();
        publishProductChange(product, MosquittoTopics.PRODUCT_CREATION_TOPIC);
    }

    @Override
    public void updateProduct(Product product) throws NotFoundItemInDatabaseException, MqttException {
        if (!databaseService.hasProduct(product.getPID()))
            throw new NotFoundItemInDatabaseException();
        publishProductChange(product, MosquittoTopics.PRODUCT_UPDATE_TOPIC);
    }

    @Override
    public void deleteProduct(ID id) throws NotFoundItemInDatabaseException, MqttException {
        if (!databaseService.hasProduct(id.getID()))
            throw new NotFoundItemInDatabaseException();
        publishProductDeletion(id);
    }

    @Override
    public void createOrder(Order order) throws DuplicateDatabaseItemException, MqttException {
        if (databaseService.hasOrder(order.getOID()))
            throw new DuplicateDatabaseItemException();
        publishOrderChange(order, MosquittoTopics.ORDER_CREATION_TOPIC);
    }

    @Override
    public void updateOrder(Order order) throws NotFoundItemInDatabaseException, MqttException {
        if (!databaseService.hasOrder(order.getOID()))
            throw new NotFoundItemInDatabaseException();
        publishOrderChange(order, MosquittoTopics.ORDER_UPDATE_TOPIC);
    }

    @Override
    public void deleteOrder(ID id) throws NotFoundItemInDatabaseException, MqttException {
        if (!databaseService.hasOrder(id.getID()))
            throw new NotFoundItemInDatabaseException();
        publishOrderDeletion(id);
    }
}
