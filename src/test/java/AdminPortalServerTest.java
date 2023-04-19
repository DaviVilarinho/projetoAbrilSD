import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.server.*;
import utils.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AdminPortalServerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Test
    public void shouldCrudClientOneServer() throws IOException, NotFoundItemInDatabaseException, InterruptedException {
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
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getCode());
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertEquals(clientNativeThatShouldBeUpdated.toClient(), client);

        reply = blockingStub.deleteClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getCode());
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertNotEquals(clientNativeThatShouldBeUpdated.toClient(), client);
    }

    @Test
    public void shouldCrudClientMultipleServer() throws InterruptedException {
        List<String> serverNames = IntStream.range(0, 6).mapToObj(i -> InProcessServerBuilder.generateName()).toList();
        List<AdminPortalGrpc.AdminPortalBlockingStub> adminPortalBlockingStubs = new ArrayList<>();
        serverNames.forEach(serverName -> {
            try {
                grpcCleanup.register(InProcessServerBuilder
                        .forName(serverName).directExecutor().addService(new AdminPortalServer.AdminPortalImpl()).build().start());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            adminPortalBlockingStubs.add(
                    AdminPortalGrpc.newBlockingStub(
                            grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()))
            );
        });

        Client clientThatShouldBeCreated = RandomUtils.generateRandomClient().toClient();
        Client clientThatShouldNotBeCreated = RandomUtils.generateRandomClient().toClient();

        Reply reply = adminPortalBlockingStubs.get(0).createClient(clientThatShouldBeCreated);
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getCode());
        Thread.sleep(500);
        adminPortalBlockingStubs.forEach(blockingStub -> {
            Client client = blockingStub.retrieveClient(ID.newBuilder().setID(clientThatShouldBeCreated.getCID()).build());
            Assert.assertEquals(clientThatShouldBeCreated, client);
        });
        ClientNative anotherClientNativeThatShouldBeCreated = RandomUtils.generateRandomClient();
        Client anotherClientThatShouldBeCreated = anotherClientNativeThatShouldBeCreated.toClient();
        reply = adminPortalBlockingStubs.get(0).createClient(anotherClientThatShouldBeCreated);
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getCode());
        Thread.sleep(500);
        adminPortalBlockingStubs.forEach(blockingStub -> {
            Client client = blockingStub.retrieveClient(ID.newBuilder().setID(anotherClientThatShouldBeCreated.getCID()).build());
            Assert.assertEquals(anotherClientThatShouldBeCreated, client);
        });
        Client anotherClientThatShouldBeUpdated = anotherClientNativeThatShouldBeCreated.toBuilder()
                .zipCode("zipMUDADO1222")
                .build()
                .toClient();
        reply = adminPortalBlockingStubs.get(0).updateClient(anotherClientThatShouldBeUpdated);
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getCode());
        Thread.sleep(500);
        adminPortalBlockingStubs.forEach(blockingStub -> {
            Client client = blockingStub.retrieveClient(ID.newBuilder().setID(anotherClientThatShouldBeUpdated.getCID()).build());
            Assert.assertEquals(anotherClientThatShouldBeUpdated, client);
        });
    }
}
