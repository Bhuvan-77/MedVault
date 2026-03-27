package com.medvault.medvault;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.medvault.medvault.entity.User;
import com.medvault.medvault.enums.Role;
import com.medvault.medvault.repository.UserRepository;

@SpringBootApplication
public class MedvaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedvaultApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@medvault.com";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Ensure this fixed email always acts as ADMIN for direct login.
            User existingAdmin = userRepository.findByEmail(adminEmail).orElse(null);
            if (existingAdmin == null) {
                User adminUser = new User();
                adminUser.setName("Admin User");
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode("Admin@123"));
                adminUser.setRole(Role.ADMIN);

                userRepository.save(adminUser);
                System.out.println("✓ Admin user created successfully");
                System.out.println("  Email: " + adminEmail);
                System.out.println("  Password: Admin@123");
            } else {
                existingAdmin.setName("Admin User");
                existingAdmin.setRole(Role.ADMIN);
                existingAdmin.setPassword(passwordEncoder.encode("Admin@123"));
                userRepository.save(existingAdmin);
                System.out.println("✓ Admin user updated and enforced as ADMIN");
            }
        };
    }

}
