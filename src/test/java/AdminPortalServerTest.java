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
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getError());
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertEquals(clientNativeThatShouldBeUpdated.toClient(), client);

        reply = blockingStub.deleteClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertEquals(reply.getError(), ReplyNative.SUCESSO.getError());
        client = blockingStub.retrieveClient(ID.newBuilder().setID(clientNativeThatShouldBeUpdated.getCID()).build());
        Assert.assertNotEquals(clientNativeThatShouldBeUpdated.toClient(), client);
    }

}
