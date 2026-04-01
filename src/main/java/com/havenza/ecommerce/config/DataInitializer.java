package com.havenza.ecommerce.config;

import com.havenza.ecommerce.auth.Role;
import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.product.CategoryEntity;
import com.havenza.ecommerce.product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        createDefaultCategories();
    }

    private void createDefaultAdmin() {
        String adminEmail = "admin@havenza.com";
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping creation.");
            return;
        }

        UserEntity admin = UserEntity.builder()
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .fullName("Havenza Admin")
                .phone("9999999999")
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("✅ Default admin user created: {} / Admin@123", adminEmail);
    }

    private void createDefaultCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping seeding.");
            return;
        }

        List<CategoryEntity> categories = List.of(
                CategoryEntity.builder().name("Electronics").slug("electronics").description("Gadgets & devices").build(),
                CategoryEntity.builder().name("Fashion").slug("fashion").description("Clothing & accessories").build(),
                CategoryEntity.builder().name("Home & Kitchen").slug("home-kitchen").description("Home appliances & kitchen essentials").build(),
                CategoryEntity.builder().name("Beauty & Health").slug("beauty-health").description("Skincare, grooming & wellness").build(),
                CategoryEntity.builder().name("Sports & Outdoors").slug("sports-outdoors").description("Fitness gear & outdoor equipment").build()
        );

        categoryRepository.saveAll(categories);
        log.info("✅ Seeded {} default categories", categories.size());
    }
}
