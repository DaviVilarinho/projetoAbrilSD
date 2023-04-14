package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.client.AdminPortalReply;
import ufu.davigabriel.server.Reply;

public abstract class DatabaseException extends Exception{
    public void replyError(StreamObserver responseObserver) {
        this.replyError(responseObserver, AdminPortalReply.ERRO_DESCONHECIDO);
    }

    public Reply getErrorReply(AdminPortalReply adminPortalReply) {
        return Reply.newBuilder()
                .setError(adminPortalReply.getError())
                .setDescription(adminPortalReply.getDescription())
                .build();
    }

    public void replyError(StreamObserver responseObserver, AdminPortalReply adminPortalReply) {
        responseObserver.onNext(getErrorReply(adminPortalReply));
    }
}
