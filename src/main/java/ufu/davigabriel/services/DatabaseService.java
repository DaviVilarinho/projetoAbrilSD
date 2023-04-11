package ufu.davigabriel.services;
//todo database validator
import ufu.davigabriel.exceptions.DatabaseException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.models.Client;
import ufu.davigabriel.models.Order;
import ufu.davigabriel.models.Product;
import ufu.davigabriel.server.ClientGRPC;

import java.util.HashMap;

public class DatabaseService {
    private static DatabaseService instance;
    private HashMap<String, Product> productsMap;
    private HashMap<String, Client> clientsMap;
    private HashMap<String, Order> ordersMap;

    public DatabaseService() {
        if (instance != null){
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

    //Client operations
    public Client parseClient(ClientGRPC client){
        return Client.builder()
                .clientId(client.getCID())
                .name(client.getName())
                .zipCode(client.getZipCode()).build();
    }
    public void createClient(ClientGRPC client) throws DuplicateDatabaseItemException{
        createClient(parseClient(client));
    }
    public void createClient(Client client) throws DuplicateDatabaseItemException {
        if(clientsMap.containsKey(client.getClientId()))
            throw new DuplicateDatabaseItemException();

        clientsMap.putIfAbsent(client.getClientId(), client);
    }
    //
}
