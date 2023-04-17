package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.models.ReplyNative;
import ufu.davigabriel.server.Reply;

public abstract class DatabaseException extends Exception {
    public void replyError(StreamObserver responseObserver) {
        this.replyError(responseObserver, ReplyNative.ERRO_DESCONHECIDO);
    }

    public Reply getErrorReply(ReplyNative replyNative) {
        return Reply.newBuilder()
                .setError(replyNative.getError())
                .setDescription(replyNative.getDescription())
                .build();
    }

    public void replyError(StreamObserver responseObserver, ReplyNative replyNative) {
        responseObserver.onNext(getErrorReply(replyNative));
    }
}
