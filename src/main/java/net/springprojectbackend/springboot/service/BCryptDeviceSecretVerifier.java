package net.springprojectbackend.springboot.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class BCryptDeviceSecretVerifier implements DeviceSecretVerifier {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean matches(String plainSecret, String secretHash) {
        if (plainSecret == null || secretHash == null) {
            return false;
        }
        return encoder.matches(plainSecret, secretHash);
    }
}

