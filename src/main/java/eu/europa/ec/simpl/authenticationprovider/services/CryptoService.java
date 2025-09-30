package eu.europa.ec.simpl.authenticationprovider.services;

public interface CryptoService {

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
