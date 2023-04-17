package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.models.ReplyNative;

public class NotFoundItemInDatabaseException extends DatabaseException {
    @Override
    public void replyError(StreamObserver responseObserver) {
        replyError(responseObserver, ReplyNative.INEXISTENTE);
    }
}
