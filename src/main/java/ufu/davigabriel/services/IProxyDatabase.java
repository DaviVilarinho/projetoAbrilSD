package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Product;

public interface IProxyDatabase {
    void createClient(Client client) throws DuplicateDatabaseItemException, MqttException;
    void updateClient(Client client) throws NotFoundItemInDatabaseException;
    void deleteClient(ID id) throws NotFoundItemInDatabaseException;

    void createProduct(Product product) throws DuplicateDatabaseItemException;
    void updateProduct(Product Product) throws NotFoundItemInDatabaseException;
    void deleteProduct(ID id) throws NotFoundItemInDatabaseException;

}
