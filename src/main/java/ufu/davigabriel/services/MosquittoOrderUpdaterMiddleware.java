package ufu.davigabriel.services;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;

import java.util.Arrays;

public class MosquittoOrderUpdaterMiddleware extends MosquittoUpdaterMiddleware implements IOrderProxyDatabase {
    private static MosquittoOrderUpdaterMiddleware instance;
    final private OrderDatabaseService orderDatabaseService = OrderDatabaseService.getInstance();

    public MosquittoOrderUpdaterMiddleware() {
        super();
    }

    @Override
    public String[] getInterestedTopics() {
        Object[] objectTopicsToSubscribe = Arrays.stream(MosquittoTopics.values())
                .map(MosquittoTopics::name)
                .filter(name -> name.startsWith("ORDER"))
                .toArray();

        return Arrays.copyOf(objectTopicsToSubscribe, objectTopicsToSubscribe.length, String[].class);
    }

    public static MosquittoOrderUpdaterMiddleware getInstance() {
        if (instance == null)
            instance = new MosquittoOrderUpdaterMiddleware();

        return instance;
    }

    public void publishOrderChange(Order order, MosquittoTopics mosquittoTopics) throws MqttException {
        super.getMqttClient().publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(order).getBytes()));
    }

    public void publishOrderDeletion(ID id) throws MqttException {
        super.getMqttClient().publish(MosquittoTopics.ORDER_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
    }

    @Override
    public void createOrder(Order order) throws DuplicateDatabaseItemException, MqttException {
        if (orderDatabaseService.hasOrder(order.getOID()))
            throw new DuplicateDatabaseItemException();
        publishOrderChange(order, MosquittoTopics.ORDER_CREATION_TOPIC);
    }

    @Override
    public void updateOrder(Order order) throws NotFoundItemInDatabaseException, MqttException {
        if (!orderDatabaseService.hasOrder(order.getOID()))
            throw new NotFoundItemInDatabaseException();
        publishOrderChange(order, MosquittoTopics.ORDER_UPDATE_TOPIC);
    }

    @Override
    public void deleteOrder(ID id) throws NotFoundItemInDatabaseException, MqttException {
        if (!orderDatabaseService.hasOrder(id.getID()))
            throw new NotFoundItemInDatabaseException();
        publishOrderDeletion(id);
    }
}
