package net.springprojectbackend.springboot.service;

public interface DeviceSecretVerifier {

	boolean matches(String plainSecret, String secretHash);
}
