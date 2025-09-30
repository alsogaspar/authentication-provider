package eu.europa.ec.simpl.authenticationprovider.utils;

import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import eu.europa.ec.simpl.common.utils.PemUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class PemUtils {

    public static byte[] decodePemFormat(String pem) {
        var inputStream = new ByteArrayInputStream(pem.getBytes());
        try {
            return PemUtil.loadPemObjects(inputStream).getFirst().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeWrapperException(e);
        }
    }
}
