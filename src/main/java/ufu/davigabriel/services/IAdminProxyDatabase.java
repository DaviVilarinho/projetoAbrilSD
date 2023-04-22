package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import ufu.davigabriel.server.Product;

/**
 * O Middleware e a Database fazem uso desta interface para redirecionar as atribuicoes de cada
 * Middleware: falar para todos que houve mudancas
 * Database: realizar mudanca
 */
public interface IAdminProxyDatabase {
    void createClient(Client client) throws DuplicateDatabaseItemException, MqttException;
    void updateClient(Client client) throws NotFoundItemInDatabaseException, MqttException;
    void deleteClient(ID id) throws NotFoundItemInDatabaseException, MqttException;

    void createProduct(Product product) throws DuplicateDatabaseItemException, MqttException;
    void updateProduct(Product Product) throws NotFoundItemInDatabaseException, MqttException;
    void deleteProduct(ID id) throws NotFoundItemInDatabaseException, MqttException;
}
