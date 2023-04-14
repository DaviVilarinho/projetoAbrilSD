package ufu.davigabriel.services;

import org.eclipse.paho.client.mqttv3.MqttException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.Client;
import ufu.davigabriel.models.Product;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;
import ufu.davigabriel.server.ProductGRPC;

public interface IProxyDatabase {
    void createClient(ClientGRPC client) throws DuplicateDatabaseItemException, MqttException;

    void updateClient(ClientGRPC clientGRPC) throws NotFoundItemInDatabaseException;
    void deleteClient(IDGRPC idgrpc) throws NotFoundItemInDatabaseException;
    void createProduct(ProductGRPC product) throws DuplicateDatabaseItemException;

    void updateProduct(ProductGRPC ProductGRPC) throws NotFoundItemInDatabaseException;
    void deleteProduct(IDGRPC idgrpc) throws NotFoundItemInDatabaseException;
}
