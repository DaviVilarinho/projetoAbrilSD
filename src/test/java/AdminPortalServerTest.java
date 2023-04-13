import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.StreamObservers;
import io.grpc.testing.GrpcCleanupRule;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import ufu.davigabriel.exceptions.DatabaseException;
import ufu.davigabriel.exceptions.NotFoundItemInDatabaseException;
import ufu.davigabriel.models.Client;
import ufu.davigabriel.server.*;
import ufu.davigabriel.services.DatabaseService;
import utils.RandomUtils;

import java.io.IOException;

public class AdminPortalServerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    @Test
    public void shouldCrudClient() throws IOException, NotFoundItemInDatabaseException, InterruptedException {
        ClientGRPC clientThatShouldBeCreated = RandomUtils.generateRandomClient().toClientGRPC();
        ClientGRPC clientThatShouldNotBeCreated = RandomUtils.generateRandomClient().toClientGRPC();

        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new AdminPortalServer.AdminPortalImpl()).build().start());

        AdminPortalGrpc.AdminPortalBlockingStub blockingStub = AdminPortalGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        ReplyGRPC replyGrpc = blockingStub.createClient(clientThatShouldBeCreated);
        ClientOrErrorGRPC clientGrpc = blockingStub.retrieveClient(IDGRPC.newBuilder().setIDGRPC(clientThatShouldBeCreated.getCID()).build());
        Assert.assertEquals(clientThatShouldBeCreated, clientGrpc.getClientGRPC());
        Assert.assertNotEquals(clientThatShouldNotBeCreated, clientGrpc.getClientGRPC());
        clientGrpc = blockingStub.retrieveClient(IDGRPC.newBuilder().setIDGRPC(clientThatShouldNotBeCreated.getCID()).build());
        Assert.assertEquals(404, clientGrpc.getReplyGRPC().getError());
    }
}
