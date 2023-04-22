package ufu.davigabriel.services;
//todo database validator

import ufu.davigabriel.exceptions.DuplicateDatabaseItemException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.OrderNative;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderDatabaseService implements IOrderProxyDatabase {
    private static OrderDatabaseService instance;
    private HashMap<String, OrderNative> ordersMap;

    private OrderDatabaseService() {
        if (instance == null) {
            ordersMap = new HashMap<>();
        }
    }

    public static OrderDatabaseService getInstance() {
        if (instance == null) {
            instance = new OrderDatabaseService();
        }
        return instance;
    }

    public void listAll() {
        ordersMap.forEach((s, orderNative) -> System.out.println(orderNative));
    }

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

    public ArrayList<OrderNative> retrieveClientOrders(ID id) {
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