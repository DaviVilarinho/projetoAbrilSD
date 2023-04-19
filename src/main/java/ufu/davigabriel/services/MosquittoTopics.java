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
        DatabaseService databaseService = DatabaseService.getInstance();
        Client client = new Gson().fromJson(message.toString(), Client.class);
        try {
            databaseService.createClient(client);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
    }), CLIENT_UPDATE_TOPIC("admin/client/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        Client client = new Gson().fromJson(message.toString(), Client.class);
        try {
            databaseService.updateClient(client);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), CLIENT_DELETION_TOPIC("admin/client/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        ID clientId = ID.newBuilder().setID(message.toString().strip().trim()).build();
        try {
            databaseService.deleteClient(clientId);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), PRODUCT_CREATION_TOPIC("admin/product/creation", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        Product product = new Gson().fromJson(message.toString(), Product.class);
        try {
            databaseService.createProduct(product);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
    }), PRODUCT_UPDATE_TOPIC("admin/product/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        Product product = new Gson().fromJson(message.toString(), Product.class);
        try {
            databaseService.updateProduct(product);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), PRODUCT_DELETION_TOPIC("admin/product/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        ID productId = new Gson().fromJson(message.toString(), ID.class);
        try {
            databaseService.deleteProduct(productId);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), ORDER_CREATION_TOPIC("order/creation", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        Order order = new Gson().fromJson(message.toString(), Order.class);
        try {
            databaseService.createOrder(order);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
    }), ORDER_UPDATE_TOPIC("order/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        Order order = new Gson().fromJson(message.toString(), Order.class);
        try {
            databaseService.updateOrder(order);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }), ORDER_DELETION_TOPIC("order/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        ID orderId = new Gson().fromJson(message.toString(), ID.class);
        try {
            databaseService.deleteOrder(orderId);
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
