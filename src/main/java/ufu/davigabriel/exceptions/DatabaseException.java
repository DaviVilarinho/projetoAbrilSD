package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.client.AdminPortalReply;
import ufu.davigabriel.server.ReplyGRPC;

public abstract class DatabaseException extends Exception{
    public void replyError(StreamObserver responseObserver) {
        this.replyError(responseObserver, AdminPortalReply.ERRO_DESCONHECIDO);
    }

    public void replyError(StreamObserver responseObserver, AdminPortalReply adminPortalReply) {
        responseObserver.onNext(ReplyGRPC.newBuilder()
                .setError(adminPortalReply.getError())
                .setDescription(adminPortalReply.getDescription())
                .build());
    }
}
