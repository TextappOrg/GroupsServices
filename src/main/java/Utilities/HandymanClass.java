package Utilities;

import java.util.UUID;

public class HandymanClass {

    public static String makeUUID(String nameFrom){
        String uuId = UUID.randomUUID().toString();
        String salter = Long.toHexString( System.nanoTime() + System.currentTimeMillis() );
        return uuId + "_" + salter + "_" + nameFrom.hashCode();
    }
}