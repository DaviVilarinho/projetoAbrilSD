package ufu.davigabriel.services;
//todo database validator

import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.OrderNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.server.Client;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import ufu.davigabriel.server.Product;

import java.util.HashMap;

public class DatabaseService implements IProxyDatabase {
    private static DatabaseService instance;
    private HashMap<String, ProductNative> productsMap;
    private HashMap<String, ClientNative> clientsMap;
    private HashMap<String, OrderNative> ordersMap;

    private DatabaseService() {
        if (instance == null) {
            productsMap = new HashMap<>();
            clientsMap = new HashMap<>();
            ordersMap = new HashMap<>();
        }
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public void listAll() {
        productsMap.forEach((s, productNative) -> System.out.println(productNative));
        clientsMap.forEach((s, client) -> System.out.println(client));
        ordersMap.forEach((s, orderNative) -> System.out.println(orderNative));
    }

    public void createClient(Client client) throws DuplicateDatabaseItemException {
        createClient(ClientNative.fromClient(client));
    }

    public void createClient(ClientNative clientNative) throws DuplicateDatabaseItemException {
        if (clientsMap.containsKey(clientNative.getClientId()))
            throw new DuplicateDatabaseItemException();

        clientsMap.putIfAbsent(clientNative.getClientId(), clientNative);
    }

    public ClientNative retrieveClient(ID id) throws NotFoundItemInDatabaseException {
        return retrieveClient(id.getID());
    }

    public ClientNative retrieveClient(String id) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        return clientsMap.get(id);
    }

    public boolean hasClient(String id) {
        return clientsMap.containsKey(id);
    }

    public void updateClient(Client client) throws NotFoundItemInDatabaseException {
        updateClient(ClientNative.fromClient(client));
    }

    public void updateClient(ClientNative clientNative) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(clientNative.getClientId())) throw new NotFoundItemInDatabaseException();
        clientsMap.put(clientNative.getClientId(), clientNative);
    }

    public void deleteClient(ID id) throws NotFoundItemInDatabaseException {
        deleteClient(id.getID());
    }

    public void deleteClient(String id) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        clientsMap.remove(id);
    }

    public void createProduct(Product product) throws DuplicateDatabaseItemException {
        createProduct(ProductNative.fromProductGRPC(product));
    }

    public void createProduct(ProductNative productNative) throws DuplicateDatabaseItemException {
        if (hasProduct(productNative.getProductId()))
            throw new DuplicateDatabaseItemException();

        productsMap.putIfAbsent(productNative.getProductId(), productNative);
    }

    public boolean hasProduct(String id) {
        return productsMap.containsKey(id);
    }
    public ProductNative retrieveProduct(ID id) throws NotFoundItemInDatabaseException {
        return retrieveProduct(id.getID());
    }

    public ProductNative retrieveProduct(String id) throws NotFoundItemInDatabaseException {
        if (!hasProduct(id)) throw new NotFoundItemInDatabaseException();
        return productsMap.get(id);
    }

    public void updateProduct(Product product) throws NotFoundItemInDatabaseException {
        updateProduct(ProductNative.fromProductGRPC(product));
    }

    public void updateProduct(ProductNative ProductNative) throws NotFoundItemInDatabaseException {
        if (!hasProduct(ProductNative.getProductId())) throw new NotFoundItemInDatabaseException();
        productsMap.put(ProductNative.getProductId(), ProductNative);
    }

    public void deleteProduct(ID id) throws NotFoundItemInDatabaseException {
        deleteProduct(id.getID());
    }

    public void deleteProduct(String id) throws NotFoundItemInDatabaseException {
        if (!hasProduct(id)) throw new NotFoundItemInDatabaseException();
        productsMap.remove(id);
    }

    public void createOrder(OrderNative orderNative) throws DuplicateDatabaseItemException {
        if (ordersMap.containsKey(orderNative.getOrderId())) throw new DuplicateDatabaseItemException();
        ordersMap.put(orderNative.getOrderId(), orderNative);
    }

    public void createOrder(Order order) throws DuplicateDatabaseItemException {
        createOrder(OrderNative.fromOrderGRPC(order));
    }

    public OrderNative retrieveOrder(String id) throws NotFoundItemInDatabaseException {
        if (!ordersMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        return ordersMap.get(id);
    }

    public OrderNative retrieveOrder(ID id) throws NotFoundItemInDatabaseException {
        return retrieveOrder(id.getID());
    }

    public void updateOrder(OrderNative orderNative) throws NotFoundItemInDatabaseException {
        if(!ordersMap.containsKey(orderNative.getOrderId())) throw new NotFoundItemInDatabaseException();
        ordersMap.put(orderNative.getOrderId(), orderNative);
    }

    public void updateOrder(Order order) throws NotFoundItemInDatabaseException {
        updateOrder(OrderNative.fromOrderGRPC(order));
    }

    public void deleteOrder(String id) throws NotFoundItemInDatabaseException {
        if(!ordersMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        ordersMap.remove(id);
    }

    public void deleteOrder(ID id) throws NotFoundItemInDatabaseException {
        deleteOrder(id.getID());
    }
}
