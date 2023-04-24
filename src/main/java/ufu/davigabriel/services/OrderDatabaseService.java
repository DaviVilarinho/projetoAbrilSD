package ufu.davigabriel.services;
//todo database validator

import ufu.davigabriel.exceptions.DuplicatePortalItemException;
import ufu.davigabriel.exceptions.NotFoundItemInPortalException;
import ufu.davigabriel.models.OrderNative;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.Order;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * As mudancas de pedidos devem ser realizadas aqui, bem como
 * o armazenamento das tabelas.
 *
 * Esta classe nao tem responsabilidade de sincronia ou atualizacao,
 * apenas realiza mudancas na tabela e nao permite estados invalidos.
 */
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

    public void createOrder(OrderNative orderNative) throws DuplicatePortalItemException {
        if (hasOrder(orderNative.getOID())) throw new DuplicatePortalItemException();
        ordersMap.put(orderNative.getOID(), orderNative);
    }

    public void createOrder(Order order) throws DuplicatePortalItemException {
        createOrder(OrderNative.fromOrder(order));
    }

    public OrderNative retrieveOrder(String id) throws NotFoundItemInPortalException {
        if (!hasOrder(id)) throw new NotFoundItemInPortalException();
        return ordersMap.get(id);
    }

    public OrderNative retrieveOrder(ID id) throws NotFoundItemInPortalException {
        return retrieveOrder(id.getID());
    }

    public void updateOrder(OrderNative orderNative) throws NotFoundItemInPortalException {
        if(!hasOrder(orderNative.getOID())) throw new NotFoundItemInPortalException();
        ordersMap.put(orderNative.getOID(), orderNative);
    }

    public void updateOrder(Order order) throws NotFoundItemInPortalException {
        updateOrder(OrderNative.fromOrder(order));
    }

    public void deleteOrder(String id) throws NotFoundItemInPortalException {
        if(!hasOrder(id)) throw new NotFoundItemInPortalException();
        ordersMap.remove(id);
    }

    public void deleteOrder(ID id) throws NotFoundItemInPortalException {
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
