package ufu.davigabriel.services;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.BiConsumer;

@Getter
@ToString
public enum MosquittoTopics {
    CLIENT_CREATION_TOPIC("admin/client/creation", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        Client client = new Gson().fromJson(message.toString(), Client.class);
        try {
            databaseService.createClient(client);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), CLIENT_UPDATE_TOPIC("admin/client/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        Client client = new Gson().fromJson(message.toString(), Client.class);
        try {
            databaseService.updateClient(client);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), CLIENT_DELETION_TOPIC("admin/client/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        ID id = null;
        try {
            id = (ID) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.deleteClient(id);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), PRODUCT_CREATION_TOPIC("admin/product/creation", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        Product product = null;
        try {
            product = (Product) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.createProduct(product);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), PRODUCT_UPDATE_TOPIC("admin/product/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        Product product = null;
        try {
            product = (Product) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.updateProduct(product);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), PRODUCT_DELETION_TOPIC("admin/product/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        ID id = null;
        try {
            id = (ID) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.deleteProduct(id);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), ORDER_CREATION_TOPIC("order/creation", (topic, message) -> {
        //TODO
    }), ORDER_UPDATE_TOPIC("order/update", (topic, message) -> {
        //TODO
    }), ORDER_DELETION_TOPIC("order/deletion", (topic, message) -> {
        //TODO
    });

    private String topic;
    private BiConsumer<String, MqttMessage> iMqttMessageListener;

    MosquittoTopics(String topic, BiConsumer<String, MqttMessage> iMqttMessageListener) {
        this.topic = topic;
        this.iMqttMessageListener = iMqttMessageListener;
    }
}
