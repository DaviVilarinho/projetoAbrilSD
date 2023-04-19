package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import ufu.davigabriel.server.Product;

public interface IProxyDatabase {
    void createClient(Client client) throws DuplicateDatabaseItemException, MqttException;
    void updateClient(Client client) throws NotFoundItemInDatabaseException, MqttException;
    void deleteClient(ID id) throws NotFoundItemInDatabaseException, MqttException;

    void createProduct(Product product) throws DuplicateDatabaseItemException, MqttException;
    void updateProduct(Product Product) throws NotFoundItemInDatabaseException, MqttException;
    void deleteProduct(ID id) throws NotFoundItemInDatabaseException, MqttException;
    void createOrder(Order order) throws DuplicateDatabaseItemException, MqttException;
    void updateOrder(Order order) throws NotFoundItemInDatabaseException, MqttException;
    void deleteOrder(ID id) throws NotFoundItemInDatabaseException, MqttException;

}
