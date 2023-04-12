package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.client.AdminPortalReply;

public class NotFoundItemInDatabaseException extends DatabaseException{
    @Override
    public void replyError(StreamObserver responseObserver) {
        replyError(responseObserver, AdminPortalReply.INEXISTENTE);
    }
}
