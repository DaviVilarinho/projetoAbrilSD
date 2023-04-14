package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.client.AdminPortalReply;
import ufu.davigabriel.server.Reply;

public class DuplicateDatabaseItemException extends DatabaseException{
    @Override
    public void replyError(StreamObserver responseObserver) {
        replyError(responseObserver, AdminPortalReply.DUPLICATA);
    }
}
