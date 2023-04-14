package ufu.davigabriel.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;
import ufu.davigabriel.server.ProductGRPC;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@ToString
public enum MosquittoTopics {
    CLIENT_CREATION_TOPIC("admin/client/creation", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        ClientGRPC clientGRPC = new Gson().fromJson(message.toString(), ClientGRPC.class);
        try {
            databaseService.createClient(clientGRPC);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), CLIENT_UPDATE_TOPIC("admin/client/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        ClientGRPC clientGRPC = new Gson().fromJson(message.toString(), ClientGRPC.class);
        try {
            databaseService.updateClient(clientGRPC);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), CLIENT_DELETION_TOPIC("admin/client/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        IDGRPC idgrpc = null;
        try {
            idgrpc = (IDGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.deleteClient(idgrpc);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), ORDER_CREATION_TOPIC("order/creation", (topic, message) -> {//TODO
    }), ORDER_UPDATE_TOPIC("order/update", (topic, message) -> {
        //TODO
    }), ORDER_DELETION_TOPIC("order/deletion", (topic, message) -> {
        //TODO
    }), PRODUCT_CREATION_TOPIC("admin/product/creation", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        ProductGRPC productGRPC = null;
        try {
            productGRPC = (ProductGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.createProduct(productGRPC);
        } catch (DuplicateDatabaseItemException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }), PRODUCT_UPDATE_TOPIC("admin/product/update", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        ProductGRPC productGRPC = null;
        try {
            productGRPC = (ProductGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.updateProduct(productGRPC);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    }
    ), PRODUCT_DELETION_TOPIC("admin/product/deletion", (topic, message) -> {
        DatabaseService databaseService = DatabaseService.getInstance();
        System.out.println("Mensagem recebida de " + topic + ": " + message.toString());
        IDGRPC idgrpc = null;
        try {
            idgrpc = (IDGRPC) new ObjectInputStream(new ByteArrayInputStream(message.getPayload())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            databaseService.deleteProduct(idgrpc);
        } catch (NotFoundItemInDatabaseException e) {
            throw new RuntimeException(e);
        }
        databaseService.listAll();
    });

    private String topic;
    private BiConsumer<String, MqttMessage> iMqttMessageListener;

    MosquittoTopics(String topic, BiConsumer<String, MqttMessage> iMqttMessageListener) {
        this.topic = topic;
        this.iMqttMessageListener = iMqttMessageListener;
    }
}
