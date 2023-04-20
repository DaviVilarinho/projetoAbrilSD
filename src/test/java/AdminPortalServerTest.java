import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.ProductNative;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.server.*;
import utils.RandomUtils;

import java.io.IOException;
import java.io.PipedReader;

public class AdminPortalServerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Test
    public void shouldCrudClientOneServer() throws IOException, InterruptedException {
        Client clientThatShouldBeCreated = RandomUtils.generateRandomClient().toClient();
        Client clientThatShouldNotBeCreated = RandomUtils.generateRandomClient().toClient();

        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new AdminPortalServer.AdminPortalImpl()).build().start());

        AdminPortalGrpc.AdminPortalBlockingStub blockingStub = AdminPortalGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        Reply reply = blockingStub.createClient(clientThatShouldBeCreated);
        Thread.sleep(1500);
        Client client = blockingStub.retrieveClient(ID.newBuilder().setID(clientThatShouldBeCreated.getCID()).build());
        Assert.assertEquals(clientThatShouldBeCreated, client);
        Assert.assertNotEquals(clientThatShouldNotBeCreated, client);
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientThatShouldNotBeCreated.getCID()).build());
        Assert.assertNotEquals(clientThatShouldNotBeCreated, client);
        clientThatShouldBeCreated = clientThatShouldNotBeCreated;
        reply = blockingStub.createClient(clientThatShouldBeCreated);
        Assert.assertNotEquals(clientThatShouldBeCreated, client);
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientThatShouldBeCreated.getCID()).build());
        Assert.assertEquals(clientThatShouldBeCreated, client);

        clientThatShouldNotBeCreated = RandomUtils.generateRandomClient().toClient();
        ClientNative clientNativeThatShouldBeUpdated = ClientNative.fromClient(clientThatShouldBeCreated);
        clientNativeThatShouldBeUpdated.setZipCode("326432");
        reply = blockingStub.updateClient(clientNativeThatShouldBeUpdated.toClient());
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getError());
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertEquals(clientNativeThatShouldBeUpdated.toClient(), client);

        reply = blockingStub.deleteClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getError());
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertNotEquals(clientNativeThatShouldBeUpdated.toClient(), client);
    }

    @Test
    public void shouldCrudProductOneServer() throws IOException, InterruptedException {
        Product productThatShouldBeCreated = RandomUtils.generateRandomProduct().toProduct();
        Product productThatShouldNotBeCreated = RandomUtils.generateRandomProduct().toProduct();

        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new AdminPortalServer.AdminPortalImpl()).build().start());

        AdminPortalGrpc.AdminPortalBlockingStub blockingStub = AdminPortalGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        Reply reply = blockingStub.createProduct(productThatShouldBeCreated);
        Thread.sleep(1500);
        Product product = blockingStub.retrieveProduct(ID.newBuilder().setID(productThatShouldBeCreated.getPID()).build());
        Assert.assertEquals(productThatShouldBeCreated, product);
        Assert.assertNotEquals(productThatShouldNotBeCreated, product);
        product = blockingStub.retrieveProduct(ID.newBuilder().setID(productThatShouldNotBeCreated.getPID()).build());
        Assert.assertNotEquals(productThatShouldNotBeCreated, product);
        productThatShouldBeCreated = productThatShouldNotBeCreated;
        reply = blockingStub.createProduct(productThatShouldBeCreated);
        Assert.assertNotEquals(productThatShouldBeCreated, product);
        product = blockingStub.retrieveProduct(ID.newBuilder().setID(productThatShouldBeCreated.getPID()).build());
        Assert.assertEquals(productThatShouldBeCreated, product);

        productThatShouldNotBeCreated = RandomUtils.generateRandomProduct().toProduct();
        ProductNative productNativeThatShouldBeUpdated = ProductNative.fromProduct(productThatShouldBeCreated);
        productNativeThatShouldBeUpdated.setDescription("dkjshabuipokejxm");
        reply = blockingStub.updateProduct(productNativeThatShouldBeUpdated.toProduct());
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getError());
        product = blockingStub.retrieveProduct(ID.newBuilder().setID(productNativeThatShouldBeUpdated.getPID()).build());
        Assert.assertEquals(productNativeThatShouldBeUpdated.toProduct(), product);

        reply = blockingStub.deleteProduct(ID.newBuilder().setID(productNativeThatShouldBeUpdated.getPID()).build());
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getError());
        product = blockingStub.retrieveProduct(ID.newBuilder().setID(productNativeThatShouldBeUpdated.getPID()).build());
        Assert.assertNotEquals(productNativeThatShouldBeUpdated.toProduct(), product);
    }

}
