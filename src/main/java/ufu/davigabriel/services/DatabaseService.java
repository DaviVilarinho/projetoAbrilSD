package ufu.davigabriel.services;
//todo database validator
import ufu.davigabriel.exceptions.DatabaseException;
import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.Client;
import ufu.davigabriel.models.Order;
import ufu.davigabriel.models.Product;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.IDGRPC;

import java.util.HashMap;
import java.util.Optional;

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
}
