package utils;

import net.bytebuddy.utility.RandomString;
import ufu.davigabriel.models.ClientNative;

public class RandomUtils {
    public static ClientNative generateRandomClient() {
        return ClientNative.builder()
                .name(RandomString.make(16))
                .zipCode(RandomString.make(8))
                .CID(RandomString.make(32).strip().trim())
                .build();
    }
}
