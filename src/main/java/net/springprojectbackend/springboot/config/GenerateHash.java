package net.springprojectbackend.springboot.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secret = "mySecret333"; // put your desired device secret here
        String hash = encoder.encode(secret);
        System.out.println("Device secret hash: " + hash);
    }
}
