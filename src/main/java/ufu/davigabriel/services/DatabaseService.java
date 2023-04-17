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

import java.util.ArrayList;
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
        if (hasClient(clientNative.getCID()))
            throw new DuplicateDatabaseItemException();

        clientsMap.putIfAbsent(clientNative.getCID(), clientNative);
    }

    public ClientNative retrieveClient(ID id) throws NotFoundItemInDatabaseException {
        return retrieveClient(id.getID());
    }

    public ClientNative retrieveClient(String id) throws NotFoundItemInDatabaseException {
        if (!hasClient(id)) throw new NotFoundItemInDatabaseException();
        return clientsMap.get(id);
    }

    public void updateClient(Client client) throws NotFoundItemInDatabaseException {
        updateClient(ClientNative.fromClient(client));
    }

    public void updateClient(ClientNative clientNative) throws NotFoundItemInDatabaseException {
        if (!hasClient(clientNative.getCID())) throw new NotFoundItemInDatabaseException();
        clientsMap.put(clientNative.getCID(), clientNative);
    }

    public void deleteClient(ID id) throws NotFoundItemInDatabaseException {
        deleteClient(id.getID());
    }

    public void deleteClient(String id) throws NotFoundItemInDatabaseException {
        if (!hasClient(id)) throw new NotFoundItemInDatabaseException();
        clientsMap.remove(id);
    }

    public boolean hasClient(String id) { return clientsMap.containsKey(id); }

    public void createProduct(Product product) throws DuplicateDatabaseItemException {
        createProduct(ProductNative.fromProduct(product));
    }

    public void createProduct(ProductNative productNative) throws DuplicateDatabaseItemException {
        if (hasProduct(productNative.getPID()))
            throw new DuplicateDatabaseItemException();

        productsMap.putIfAbsent(productNative.getPID(), productNative);
    }

    public ProductNative retrieveProduct(ID id) throws NotFoundItemInDatabaseException {
        return retrieveProduct(id.getID());
    }

    public ProductNative retrieveProduct(String id) throws NotFoundItemInDatabaseException {
        if (!hasProduct(id)) throw new NotFoundItemInDatabaseException();
        return productsMap.get(id);
    }

    public void updateProduct(Product product) throws NotFoundItemInDatabaseException {
        updateProduct(ProductNative.fromProduct(product));
    }

    public void updateProduct(ProductNative ProductNative) throws NotFoundItemInDatabaseException {
        if (!hasProduct(ProductNative.getPID())) throw new NotFoundItemInDatabaseException();
        productsMap.put(ProductNative.getPID(), ProductNative);
    }

    public void deleteProduct(ID id) throws NotFoundItemInDatabaseException {
        deleteProduct(id.getID());
    }

    public void deleteProduct(String id) throws NotFoundItemInDatabaseException {
        if (!hasProduct(id)) throw new NotFoundItemInDatabaseException();
        productsMap.remove(id);
    }

    public boolean hasProduct(String id) { return productsMap.containsKey(id); }

    public void createOrder(OrderNative orderNative) throws DuplicateDatabaseItemException {
        if (hasOrder(orderNative.getOID())) throw new DuplicateDatabaseItemException();
        ordersMap.put(orderNative.getOID(), orderNative);
    }

    public void createOrder(Order order) throws DuplicateDatabaseItemException {
        createOrder(OrderNative.fromOrder(order));
    }

    public OrderNative retrieveOrder(String id) throws NotFoundItemInDatabaseException {
        if (!hasOrder(id)) throw new NotFoundItemInDatabaseException();
        return ordersMap.get(id);
    }

    public OrderNative retrieveOrder(ID id) throws NotFoundItemInDatabaseException {
        return retrieveOrder(id.getID());
    }

    public void updateOrder(OrderNative orderNative) throws NotFoundItemInDatabaseException {
        if(!hasOrder(orderNative.getOID())) throw new NotFoundItemInDatabaseException();
        ordersMap.put(orderNative.getOID(), orderNative);
    }

    public void updateOrder(Order order) throws NotFoundItemInDatabaseException {
        updateOrder(OrderNative.fromOrder(order));
    }

    public void deleteOrder(String id) throws NotFoundItemInDatabaseException {
        if(!hasOrder(id)) throw new NotFoundItemInDatabaseException();
        ordersMap.remove(id);
    }

    public void deleteOrder(ID id) throws NotFoundItemInDatabaseException {
        deleteOrder(id.getID());
    }

    public ArrayList<OrderNative> retrieveClientOrders(ID id) throws NotFoundItemInDatabaseException {
        if (!hasClient(id.getID())) throw new NotFoundItemInDatabaseException();

        ArrayList<OrderNative> arrayList = new ArrayList<>();
        ordersMap.forEach((key, order) -> {
            if(order.getCID().equals(id.getID()))
                arrayList.add(order);
        });
        return arrayList;
    }

    public boolean hasOrder(String id) {
        return ordersMap.containsKey(id);
    }
}
