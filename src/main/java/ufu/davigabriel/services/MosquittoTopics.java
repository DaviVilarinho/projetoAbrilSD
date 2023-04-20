package ufu.davigabriel.services;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import ufu.davigabriel.server.Product;

import java.util.function.BiConsumer;

@Getter
@ToString
public enum MosquittoTopics {
    CLIENT_CREATION_TOPIC("admin/client/creation", (topic, message) -> {
        AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        Client client = new Gson().fromJson(message.toString(), Client.class);
        try {
            adminDatabaseService.createClient(client);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
    }), CLIENT_UPDATE_TOPIC("admin/client/update", (topic, message) -> {
        AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        Client client = new Gson().fromJson(message.toString(), Client.class);
        try {
            adminDatabaseService.updateClient(client);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), CLIENT_DELETION_TOPIC("admin/client/deletion", (topic, message) -> {
        AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        ID clientId = ID.newBuilder().setID(message.toString().strip().trim()).build();
        try {
            adminDatabaseService.deleteClient(clientId);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), PRODUCT_CREATION_TOPIC("admin/product/creation", (topic, message) -> {
        AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        Product product = new Gson().fromJson(message.toString(), Product.class);
        try {
            adminDatabaseService.createProduct(product);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
    }), PRODUCT_UPDATE_TOPIC("admin/product/update", (topic, message) -> {
        AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        Product product = new Gson().fromJson(message.toString(), Product.class);
        try {
            adminDatabaseService.updateProduct(product);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), PRODUCT_DELETION_TOPIC("admin/product/deletion", (topic, message) -> {
        AdminDatabaseService adminDatabaseService = AdminDatabaseService.getInstance();
        ID productId = ID.newBuilder().setID(message.toString().strip().trim()).build();
        try {
            adminDatabaseService.deleteProduct(productId);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), ORDER_CREATION_TOPIC("order/creation", (topic, message) -> {
        OrderDatabaseService orderDatabaseService = OrderDatabaseService.getInstance();
        Order order = new Gson().fromJson(message.toString(), Order.class);
        try {
            orderDatabaseService.createOrder(order);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
    }), ORDER_UPDATE_TOPIC("order/update", (topic, message) -> {
        OrderDatabaseService orderDatabaseService = OrderDatabaseService.getInstance();
        Order order = new Gson().fromJson(message.toString(), Order.class);
        try {
            orderDatabaseService.updateOrder(order);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), ORDER_DELETION_TOPIC("order/deletion", (topic, message) -> {
        OrderDatabaseService orderDatabaseService = OrderDatabaseService.getInstance();
        ID orderId = new Gson().fromJson(message.toString(), ID.class);
        try {
            orderDatabaseService.deleteOrder(orderId);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    });

    private final String topic;
    private final BiConsumer<String, MqttMessage> iMqttMessageListener;

    MosquittoTopics(String topic, BiConsumer<String, MqttMessage> iMqttMessageListener) {
        this.topic = topic;
        this.iMqttMessageListener = iMqttMessageListener;
    }
}
