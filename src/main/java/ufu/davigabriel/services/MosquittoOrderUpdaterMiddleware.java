package ufu.davigabriel.services;

import com.google.gson.Gson;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import ufu.davigabriel.Main;
import ufu.davigabriel.exceptions.BadRequestException;
import ufu.davigabriel.exceptions.DuplicatePortalItemException;
import ufu.davigabriel.exceptions.NotFoundItemInPortalException;
import ufu.davigabriel.exceptions.UnauthorizedUserException;
import ufu.davigabriel.models.OrderItemNative;
import ufu.davigabriel.models.OrderNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.server.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
Diferentemente do 'MosquitoAdminUpdaterMiddleware', esta classe não pode atualizar na resposta de uma subscrição, no 'AdminPortalServer',
via requests, a quantidade de produtos. Isso se deve ao fato de que existem multiplas instâncias e todas executarão o callback para
mudar o estado local. No entanto, esta sería uma mudança no estado global.
 */
public class MosquittoOrderUpdaterMiddleware extends MosquittoUpdaterMiddleware implements IOrderProxyDatabase {
    private static MosquittoOrderUpdaterMiddleware instance;
    private AdminPortalGrpc.AdminPortalBlockingStub connectionBlockingStub;
    final private OrderDatabaseService orderDatabaseService = OrderDatabaseService.getInstance();

    public MosquittoOrderUpdaterMiddleware() {
        super();

        String CONNECTION_SERVER = String.format("%s:%d", "localhost", AdminPortalServer.BASE_PORTAL_SERVER_PORT + new Random().nextInt(Main.PORTAL_SERVERS));
        ManagedChannel connectionChannel = Grpc.newChannelBuilder(CONNECTION_SERVER, InsecureChannelCredentials.create()).build();
        this.connectionBlockingStub = AdminPortalGrpc.newBlockingStub(connectionChannel);
    }

    public void authenticateClient(String CID) throws UnauthorizedUserException {
        Client client = connectionBlockingStub.retrieveClient(ID.newBuilder().setID(CID).build());

        if("0".equals(client.getCID())) throw new UnauthorizedUserException();
    }

    @Override
    public String[] getInterestedTopics() {
        Object[] objectTopicsToSubscribe = Arrays.stream(MosquittoTopics.values())
                .map(MosquittoTopics::name)
                .filter(name -> name.startsWith("ORDER"))
                .toArray();

        return Arrays.copyOf(objectTopicsToSubscribe, objectTopicsToSubscribe.length, String[].class);
    }

    public static MosquittoOrderUpdaterMiddleware getInstance() {
        if (instance == null)
            instance = new MosquittoOrderUpdaterMiddleware();

        return instance;
    }

    public void publishOrderChange(Order order, MosquittoTopics mosquittoTopics) throws MqttException {
        super.getMqttClient().publish(mosquittoTopics.name(), new MqttMessage(new Gson().toJson(order).getBytes()));
    }

    public void publishOrderDeletion(ID id) throws MqttException {
        super.getMqttClient().publish(MosquittoTopics.ORDER_DELETION_TOPIC.name(), new MqttMessage(id.toByteArray()));
    }

    public void validateProduct(String id, int quantityRequest) throws NotFoundItemInPortalException, BadRequestException {
        ProductNative productNative = ProductNative.fromProduct(connectionBlockingStub.retrieveProduct(ID.newBuilder().setID(id).build()));
        if("0".equals(productNative.getPID()))
            throw new NotFoundItemInPortalException();

        if(productNative.getQuantity() <= 0 || productNative.getQuantity() < quantityRequest)
            throw new BadRequestException();
    }

    public void validateOrderProducts(ArrayList<OrderItemNative> products) throws NotFoundItemInPortalException, BadRequestException {
        for (OrderItemNative product : products) {
            validateProduct(product.getPID(), product.getQuantity());
        }
    }

    public OrderNative validateOrder(Order order) throws NotFoundItemInPortalException, BadRequestException {
        OrderNative orderNative = OrderNative.fromOrder(order);
        validateOrderProducts(orderNative.getProducts());
        return orderNative;
    }

    public void throwIfDuplicatedOrder(String id) throws DuplicatePortalItemException {
        if(orderDatabaseService.hasOrder(id))
            throw new DuplicatePortalItemException();
    }

    @Override
    public void createOrder(Order order) throws DuplicatePortalItemException, MqttException, UnauthorizedUserException, NotFoundItemInPortalException, BadRequestException {
        authenticateClient(order.getCID());
        throwIfDuplicatedOrder(order.getOID());
        OrderNative orderNative = validateOrder(order);
        for (OrderItemNative product : orderNative.getProducts()) {
            if (product.getQuantity() == 0)
                throw new BadRequestException();
        }
        publishOrderChange(order, MosquittoTopics.ORDER_CREATION_TOPIC);
        orderNative.getProducts().forEach(item -> {
            increaseGlobalProductQuantity(connectionBlockingStub, item.getPID(), -item.getQuantity());
        });
    }

    @Override
    public void updateOrder(Order order) throws NotFoundItemInPortalException, MqttException, UnauthorizedUserException, BadRequestException {
        authenticateClient(order.getCID());
        if (!orderDatabaseService.hasOrder(order.getOID()))
            throw new NotFoundItemInPortalException();

        OrderNative oldOrderNative = orderDatabaseService.retrieveOrder(order.getOID());
        OrderNative newOrderNative = validateOrder(order);
        newOrderNative.getProducts().removeIf(product -> product.getQuantity() == 0);
        publishOrderChange(order, MosquittoTopics.ORDER_UPDATE_TOPIC);
        oldOrderNative.getProducts().forEach(oldItem -> {
            if (newOrderNative.getProducts().stream().filter(newItem -> newItem.getPID().equals(oldItem.getPID())).toList().size() == 0)
                increaseGlobalProductQuantity(connectionBlockingStub, oldItem.getPID(), oldItem.getQuantity());
        });
        newOrderNative.getProducts().forEach(item -> {
            increaseGlobalProductQuantity(connectionBlockingStub, item.getPID(), getProductQuantityIncrease(item, oldOrderNative.getProducts()));
        });
    }

    @Override
    public void deleteOrder(ID id) throws NotFoundItemInPortalException, MqttException {
        OrderNative orderNative = orderDatabaseService.retrieveOrder(id);
        if (!orderDatabaseService.hasOrder(id.getID()))
            throw new NotFoundItemInPortalException();
        publishOrderDeletion(id);
        orderNative.getProducts().forEach(item -> {
            increaseGlobalProductQuantity(connectionBlockingStub, item.getPID(), item.getQuantity());
        });
    }

    private int getProductQuantityIncrease(OrderItemNative orderItem, ArrayList<OrderItemNative> oldOrderItems){
        for (OrderItemNative oldOrderItem : oldOrderItems) {
            if (orderItem.getPID().equals(oldOrderItem.getPID()))
                return oldOrderItem.getQuantity() - orderItem.getQuantity();
        }
        return -orderItem.getQuantity();
    }

    private void increaseGlobalProductQuantity(AdminPortalGrpc.AdminPortalBlockingStub blockingStub, String productId, int variation){
        ProductNative productToBeAdjusted = ProductNative.fromProduct(blockingStub.retrieveProduct(ID.newBuilder().setID(productId).build()));
        productToBeAdjusted.setQuantity(productToBeAdjusted.getQuantity()+variation);
        blockingStub.updateProduct(productToBeAdjusted.toProduct());
    }
}
