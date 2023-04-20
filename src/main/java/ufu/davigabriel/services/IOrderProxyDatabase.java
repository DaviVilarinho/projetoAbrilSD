package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import ufu.davigabriel.server.Product;

public interface IOrderProxyDatabase {
    void createOrder(Order order) throws DuplicateDatabaseItemException, MqttException;
    void updateOrder(Order order) throws NotFoundItemInDatabaseException, MqttException;
    void deleteOrder(ID id) throws NotFoundItemInDatabaseException, MqttException;
}
