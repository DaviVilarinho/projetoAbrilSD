package ufu.davigabriel.services;
//todo database validator
import org.json.JSONObject;
import ufu.davigabriel.exceptions.DatabaseException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.Client;
import ufu.davigabriel.models.Order;
import ufu.davigabriel.models.Product;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;
import ufu.davigabriel.server.OrderGRPC;
import ufu.davigabriel.server.ProductGRPC;

import java.util.HashMap;
import java.util.Optional;

public class DatabaseService {
    private static DatabaseService instance;
    private HashMap<String, Product> productsMap;
    private HashMap<String, Client> clientsMap;
    private HashMap<String, Order> ordersMap;

    private DatabaseService() {
        if (instance == null){
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

    public void createClient(ClientGRPC clientGRPC) throws DuplicateDatabaseItemException{
        createClient(Client.fromClientGRPC(clientGRPC));
    }
    public void createClient(Client client) throws DuplicateDatabaseItemException {
        if(clientsMap.containsKey(client.getClientId())) throw new DuplicateDatabaseItemException();

        clientsMap.putIfAbsent(client.getClientId(), client);
    }

    public Client retrieveClient(IDGRPC idGRPC) throws NotFoundItemInDatabaseException {
        return retrieveClient(idGRPC.getIDGRPC());
    }

    public Client retrieveClient(String id) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        return clientsMap.get(id);
    }

    public void updateClient(ClientGRPC clientGRPC) throws NotFoundItemInDatabaseException {
        updateClient(Client.fromClientGRPC(clientGRPC));
    }

    public void updateClient(Client client) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(client.getClientId())) throw new NotFoundItemInDatabaseException();
        clientsMap.put(client.getClientId(), client);
    }

    public void deleteClient(IDGRPC idGRPC) throws NotFoundItemInDatabaseException {
        deleteClient(idGRPC.getIDGRPC());
    }

    public void deleteClient(String id) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        clientsMap.remove(id);
    }

    public void createProduct(ProductGRPC productGRPC) throws DuplicateDatabaseItemException{
        createProduct(Product.fromProductGRPC(productGRPC));
    }
    public void createProduct(Product product) throws DuplicateDatabaseItemException {
        if(productsMap.containsKey(product.getProductId())) throw new DuplicateDatabaseItemException();
        productsMap.putIfAbsent(product.getProductId(), product);
    }

    public Product retrieveProduct(IDGRPC idGRPC) throws NotFoundItemInDatabaseException {
        return retrieveProduct(idGRPC.getIDGRPC());
    }

    public Product retrieveProduct(String id) throws NotFoundItemInDatabaseException {
        if (!productsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        return productsMap.get(id);
    }

    public void updateProduct(ProductGRPC productGRPC) throws NotFoundItemInDatabaseException {
        updateProduct(Product.fromProductGRPC(productGRPC));
    }

    public void updateProduct(Product product) throws NotFoundItemInDatabaseException {
        if (!productsMap.containsKey(product.getProductId())) throw new NotFoundItemInDatabaseException();
        productsMap.put(product.getProductId(), product);
    }

    public void deleteProduct(IDGRPC idGRPC) throws NotFoundItemInDatabaseException {
        deleteProduct(idGRPC.getIDGRPC());
    }

    public void deleteProduct(String id) throws NotFoundItemInDatabaseException {
        if (!productsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        productsMap.remove(id);
    }

    public void createOrder(Order order) throws DuplicateDatabaseItemException {
        if (ordersMap.containsKey(order.getOrderId())) throw new DuplicateDatabaseItemException();
        ordersMap.put(order.getOrderId(), order);
    }

    public void createOrder(OrderGRPC orderGRPC) throws DuplicateDatabaseItemException {
        createOrder(Order.fromOrderGRPC(orderGRPC));
    }

    public Order retrieveOrder(String id) throws NotFoundItemInDatabaseException {
        if (!ordersMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        return ordersMap.get(id);
    }

    public Order retrieveOrder(IDGRPC idGRPC) throws NotFoundItemInDatabaseException {
        return retrieveOrder(idGRPC.getIDGRPC());
    }

    public void updateOrder(Order order) throws NotFoundItemInDatabaseException {
        if(!ordersMap.containsKey(order.getOrderId())) throw new NotFoundItemInDatabaseException();
        ordersMap.put(order.getOrderId(), order);
    }

    public void updateOrder(OrderGRPC orderGRPC) throws NotFoundItemInDatabaseException {
        updateOrder(Order.fromOrderGRPC(orderGRPC));
    }

    public void deleteOrder(String id) throws NotFoundItemInDatabaseException {
        if(!ordersMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        ordersMap.remove(id);
    }

    public void deleteOrder(IDGRPC idGRPC) throws NotFoundItemInDatabaseException {
        deleteOrder(idGRPC.getIDGRPC());
    }
}
