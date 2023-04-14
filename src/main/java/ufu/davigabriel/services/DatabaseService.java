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

    public void listAll() {
        productsMap.forEach((s, product) -> System.out.println(product));
        clientsMap.forEach((s, client) -> System.out.println(client));
        ordersMap.forEach((s, order) -> System.out.println(order));
    }

    public void createClient(ClientGRPC client) throws DuplicateDatabaseItemException{
        createClient(Client.fromClientGRPC(client));
    }
    public void createClient(Client client) throws DuplicateDatabaseItemException {
        if(clientsMap.containsKey(client.getClientId()))
            throw new DuplicateDatabaseItemException();

        clientsMap.putIfAbsent(client.getClientId(), client);
    }

    public Client retrieveClient(IDGRPC idgrpc) throws NotFoundItemInDatabaseException {
        return retrieveClient(idgrpc.getIDGRPC());
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

    public void deleteClient(IDGRPC idgrpc) throws NotFoundItemInDatabaseException {
        deleteClient(idgrpc.getIDGRPC());
    }

    public void deleteClient(String id) throws NotFoundItemInDatabaseException {
        if (!clientsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        clientsMap.remove(id);
    }

    public void createProduct(ProductGRPC product) throws DuplicateDatabaseItemException{
        createProduct(Product.fromProductGRPC(product));
    }
    public void createProduct(Product product) throws DuplicateDatabaseItemException {
        if(productsMap.containsKey(product.getProductId()))
            throw new DuplicateDatabaseItemException();

        productsMap.putIfAbsent(product.getProductId(), product);
    }

    public Product retrieveProduct(IDGRPC idgrpc) throws NotFoundItemInDatabaseException {
        return retrieveProduct(idgrpc.getIDGRPC());
    }

    public Product retrieveProduct(String id) throws NotFoundItemInDatabaseException {
        if (!productsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        return productsMap.get(id);
    }

    public void updateProduct(ProductGRPC ProductGRPC) throws NotFoundItemInDatabaseException {
        updateProduct(Product.fromProductGRPC(ProductGRPC));
    }

    public void updateProduct(Product Product) throws NotFoundItemInDatabaseException {
        if (!productsMap.containsKey(Product.getProductId())) throw new NotFoundItemInDatabaseException();
        productsMap.put(Product.getProductId(), Product);
    }

    public void deleteProduct(IDGRPC idgrpc) throws NotFoundItemInDatabaseException {
        deleteProduct(idgrpc.getIDGRPC());
    }

    public void deleteProduct(String id) throws NotFoundItemInDatabaseException {
        if (!productsMap.containsKey(id)) throw new NotFoundItemInDatabaseException();
        productsMap.remove(id);
    }
}
