package ufu.davigabriel.exceptions;

import io.grpc.stub.StreamObserver;
import ufu.davigabriel.models.ReplyNative;

public class DuplicateDatabaseItemException extends DatabaseException {
    @Override
    public void replyError(StreamObserver responseObserver) {
        replyError(responseObserver, ReplyNative.DUPLICATA);
    }
}
