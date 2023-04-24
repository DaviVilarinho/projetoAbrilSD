import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import ufu.davigabriel.client.AdminPortalClient;
import ufu.davigabriel.client.OrderPortalClient;
import ufu.davigabriel.models.OrderItemNative;
import ufu.davigabriel.models.OrderNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.server.AdminPortalGrpc;
import ufu.davigabriel.server.ID;
import ufu.davigabriel.server.OrderPortalGrpc;
import ufu.davigabriel.server.Product;
import utils.RandomOrderTriple;
import utils.RandomUtils;

import java.io.IOException;
import java.util.Optional;

public class OrderPortalServerTest {

    public static int TOLERANCE_MS = 1000;
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    public OrderPortalGrpc.OrderPortalBlockingStub getOrderBlockingStub() {
        return OrderPortalGrpc.newBlockingStub(Grpc.newChannelBuilder(OrderPortalClient.TARGET_SERVER, InsecureChannelCredentials.create()).build());
    }

    public AdminPortalGrpc.AdminPortalBlockingStub getAdminBlockingStub() {
        return AdminPortalGrpc.newBlockingStub(Grpc.newChannelBuilder(AdminPortalClient.TARGET_SERVER, InsecureChannelCredentials.create()).build());
    }

    @Test
    public void shouldCreateRandomValidOrders() throws IOException, InterruptedException {
        AdminPortalGrpc.AdminPortalBlockingStub adminPortalBlockingStub = getAdminBlockingStub();
        Thread.sleep(TOLERANCE_MS);
        OrderPortalGrpc.OrderPortalBlockingStub orderPortalBlockingStub = getOrderBlockingStub();
        Thread.sleep(TOLERANCE_MS);

        RandomOrderTriple randomOrderTriple = RandomUtils.generateRandomValidOrder();

        Assert.assertEquals(adminPortalBlockingStub.createClient(randomOrderTriple.getRandomClientNative().toClient()).getError(), ReplyNative.SUCESSO.getError());
        Thread.sleep(TOLERANCE_MS);
        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Assert.assertEquals(adminPortalBlockingStub.createProduct(productNative.toProduct()).getError(), ReplyNative.SUCESSO.getError());
            Thread.sleep(TOLERANCE_MS);
        }
        Assert.assertEquals(orderPortalBlockingStub.createOrder(randomOrderTriple.getRandomOrderNative().toOrder()).getError(), ReplyNative.SUCESSO.getError());
    }

    @Test
    public void shouldNotAllowUnauthorized() throws IOException, InterruptedException {
        AdminPortalGrpc.AdminPortalBlockingStub adminPortalBlockingStub = getAdminBlockingStub();
        Thread.sleep(TOLERANCE_MS);
        OrderPortalGrpc.OrderPortalBlockingStub orderPortalBlockingStub = getOrderBlockingStub();
        Thread.sleep(TOLERANCE_MS);

        RandomOrderTriple randomOrderTriple = RandomUtils.generateRandomValidOrder();

        Assert.assertEquals(adminPortalBlockingStub.createClient(randomOrderTriple.getRandomClientNative().toClient()).getError(), ReplyNative.SUCESSO.getError());
        Thread.sleep(TOLERANCE_MS);
        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Assert.assertEquals(adminPortalBlockingStub.createProduct(productNative.toProduct()).getError(), ReplyNative.SUCESSO.getError());
            Thread.sleep(TOLERANCE_MS);
        }
        Assert.assertEquals(orderPortalBlockingStub.createOrder(randomOrderTriple.getRandomOrderNative().toOrder()).getError(), ReplyNative.SUCESSO.getError());

        OrderNative orderNativeThatUserIsNotAuthenticated = randomOrderTriple.getRandomOrderNative();
        orderNativeThatUserIsNotAuthenticated.setCID("naoexiste".repeat(5));
        orderNativeThatUserIsNotAuthenticated.setOID(orderNativeThatUserIsNotAuthenticated.getOID().toLowerCase().repeat(3));
        Assert.assertEquals(orderPortalBlockingStub.createOrder(orderNativeThatUserIsNotAuthenticated.toOrder()).getError(), ReplyNative.NAO_LOGADO.getError());
        Thread.sleep(TOLERANCE_MS);
    }

    @Test
    public void shouldNotAllowDuplicates() throws IOException, InterruptedException {
        AdminPortalGrpc.AdminPortalBlockingStub adminPortalBlockingStub = getAdminBlockingStub();
        Thread.sleep(TOLERANCE_MS);
        OrderPortalGrpc.OrderPortalBlockingStub orderPortalBlockingStub = getOrderBlockingStub();
        Thread.sleep(TOLERANCE_MS);

        RandomOrderTriple randomOrderTriple = RandomUtils.generateRandomValidOrder();

        Assert.assertEquals(adminPortalBlockingStub.createClient(randomOrderTriple.getRandomClientNative().toClient()).getError(), ReplyNative.SUCESSO.getError());
        Thread.sleep(TOLERANCE_MS);
        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Assert.assertEquals(adminPortalBlockingStub.createProduct(productNative.toProduct()).getError(), ReplyNative.SUCESSO.getError());
            Thread.sleep(TOLERANCE_MS);
        }
        Assert.assertEquals(orderPortalBlockingStub.createOrder(randomOrderTriple.getRandomOrderNative().toOrder()).getError(), ReplyNative.SUCESSO.getError());

        // OID que ja existe deve dar DUPLICATA
        OrderNative orderNativeThatOIDIsRepeated = randomOrderTriple.getRandomOrderNative();
        Assert.assertEquals(orderPortalBlockingStub.createOrder(orderNativeThatOIDIsRepeated.toOrder()).getError(), ReplyNative.DUPLICATA.getError());
        Thread.sleep(TOLERANCE_MS);
    }

    @Test
    public void shouldDecreaseProductsQuantity() throws IOException, InterruptedException {
        AdminPortalGrpc.AdminPortalBlockingStub adminPortalBlockingStub = getAdminBlockingStub();
        Thread.sleep(TOLERANCE_MS);
        OrderPortalGrpc.OrderPortalBlockingStub orderPortalBlockingStub = getOrderBlockingStub();
        Thread.sleep(TOLERANCE_MS);

        RandomOrderTriple randomOrderTriple = RandomUtils.generateRandomValidOrder();

        Assert.assertEquals(adminPortalBlockingStub.createClient(randomOrderTriple.getRandomClientNative().toClient()).getError(), ReplyNative.SUCESSO.getError());
        Thread.sleep(TOLERANCE_MS);
        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Assert.assertEquals(adminPortalBlockingStub.createProduct(productNative.toProduct()).getError(), ReplyNative.SUCESSO.getError());
            Thread.sleep(TOLERANCE_MS);
        }
        Assert.assertEquals(orderPortalBlockingStub.createOrder(randomOrderTriple.getRandomOrderNative().toOrder()).getError(), ReplyNative.SUCESSO.getError());

        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Product productAfterUpdate = adminPortalBlockingStub.retrieveProduct(ID.newBuilder().setID(productNative.getPID()).build());
            Assert.assertNotEquals(productAfterUpdate.getPID(), "0");

            Optional<OrderItemNative> optionalOrderItemNativeThatMatchesProductNative = randomOrderTriple.getRandomOrderNative().getProducts().stream().filter(productsInOrder -> productsInOrder.getPID() == productNative.getPID()).findFirst();

            Assert.assertTrue(optionalOrderItemNativeThatMatchesProductNative.isPresent());

            OrderItemNative orderItemNativeThatMatchesProductNative = optionalOrderItemNativeThatMatchesProductNative.get();

            Assert.assertEquals(ProductNative.fromProduct(productAfterUpdate).getQuantity() + orderItemNativeThatMatchesProductNative.getQuantity(), productNative.getQuantity());
            Thread.sleep(TOLERANCE_MS);
        }
    }

    @Test
    public void shouldIncreaseProductsQuantityOnDeletion() throws IOException,
            InterruptedException {
        AdminPortalGrpc.AdminPortalBlockingStub adminPortalBlockingStub = getAdminBlockingStub();
        Thread.sleep(TOLERANCE_MS);
        OrderPortalGrpc.OrderPortalBlockingStub orderPortalBlockingStub = getOrderBlockingStub();
        Thread.sleep(TOLERANCE_MS);

        RandomOrderTriple randomOrderTriple = RandomUtils.generateRandomValidOrder();

        Assert.assertEquals(adminPortalBlockingStub.createClient(randomOrderTriple.getRandomClientNative().toClient()).getError(), ReplyNative.SUCESSO.getError());
        Thread.sleep(TOLERANCE_MS);
        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Assert.assertEquals(adminPortalBlockingStub.createProduct(productNative.toProduct()).getError(), ReplyNative.SUCESSO.getError());
            Thread.sleep(TOLERANCE_MS);
        }
        Assert.assertEquals(orderPortalBlockingStub.createOrder(randomOrderTriple.getRandomOrderNative().toOrder()).getError(), ReplyNative.SUCESSO.getError());

        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Product productAfterUpdate = adminPortalBlockingStub.retrieveProduct(ID.newBuilder().setID(productNative.getPID()).build());
            Assert.assertNotEquals(productAfterUpdate.getPID(), "0");

            Optional<OrderItemNative> optionalOrderItemNativeThatMatchesProductNative = randomOrderTriple.getRandomOrderNative().getProducts().stream().filter(productsInOrder -> productsInOrder.getPID() == productNative.getPID()).findFirst();

            Assert.assertTrue(optionalOrderItemNativeThatMatchesProductNative.isPresent());

            OrderItemNative orderItemNativeThatMatchesProductNative = optionalOrderItemNativeThatMatchesProductNative.get();

            Assert.assertEquals(ProductNative.fromProduct(productAfterUpdate).getQuantity() + orderItemNativeThatMatchesProductNative.getQuantity(), productNative.getQuantity());
            Thread.sleep(TOLERANCE_MS);
        }

        Assert.assertEquals(orderPortalBlockingStub.deleteOrder(ID.newBuilder()
                .setID(randomOrderTriple.getRandomOrderNative().getOID()).build())
                .getError(), ReplyNative.SUCESSO.getError());

        for (ProductNative productNative : randomOrderTriple.getRandomProductsNative()) {
            Product productAfterUpdate = adminPortalBlockingStub.retrieveProduct(ID.newBuilder().setID(productNative.getPID()).build());
            Assert.assertNotEquals(productAfterUpdate.getPID(), "0");

            Optional<OrderItemNative> optionalOrderItemNativeThatMatchesProductNative = randomOrderTriple.getRandomOrderNative().getProducts().stream().filter(productsInOrder -> productsInOrder.getPID() == productNative.getPID()).findFirst();

            Assert.assertTrue(optionalOrderItemNativeThatMatchesProductNative.isPresent());

            OrderItemNative orderItemNativeThatMatchesProductNative = optionalOrderItemNativeThatMatchesProductNative.get();

            Assert.assertEquals(ProductNative.fromProduct(productAfterUpdate).getQuantity(), productNative.getQuantity());
            Thread.sleep(TOLERANCE_MS);
        }
    }
}
