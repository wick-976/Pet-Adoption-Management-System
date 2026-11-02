package com.examly.springapp.config;

import com.examly.springapp.model.*;
import com.examly.springapp.repository.PetImageRepository;
import com.examly.springapp.repository.PetRepository;
import com.examly.springapp.repository.ShelterRepository;
import com.examly.springapp.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final StaffRepository staffRepository;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            StaffRepository staffRepository,
            ShelterRepository shelterRepository,
            PetRepository petRepository,
            PetImageRepository petImageRepository,
            PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
        this.petImageRepository = petImageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed default Admin if none exists
        if (staffRepository.count() == 0) {
            Staff admin = Staff.builder()
                    .username("admin")
                    .email("admin@petadoption.org")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            staffRepository.save(admin);

            Staff staff = Staff.builder()
                    .username("staff")
                    .email("staff@petadoption.org")
                    .passwordHash(passwordEncoder.encode("Staff@123"))
                    .role(Role.ROLE_STAFF)
                    .build();
            staffRepository.save(staff);

            log.info("Default staff accounts created (admin/Admin@123, staff/Staff@123)");
        }

        // Seed default Shelter profile if none exists
        if (shelterRepository.count() == 0) {
            Shelter shelter = Shelter.builder()
                    .name("Happy Paws Animal Shelter")
                    .address("123 Rescue Way, Springfield, IL 62701")
                    .phone("+1 (555) 123-4567")
                    .email("contact@happypaws.org")
                    .operatingHours("Mon - Sat: 9:00 AM - 6:00 PM, Sun: 10:00 AM - 4:00 PM")
                    .description("Dedicated to rescuing, rehabilitating, and finding forever homes for abandoned and homeless pets.")
                    .website("https://www.happypawsshelter.org")
                    .build();
            shelterRepository.save(shelter);
            log.info("Default shelter profile initialized");
        }

        // Seed initial sample pets if none exist
        if (petRepository.count() == 0) {
            Pet dog1 = petRepository.save(Pet.builder()
                    .name("Max")
                    .species("Dog")
                    .breed("Golden Retriever")
                    .age(3)
                    .description("Friendly, energetic golden retriever who loves fetch and is great with kids.")
                    .adoptionStatus(PetStatus.AVAILABLE)
                    .viewCount(25L)
                    .vaccinationStatus("Up to date")
                    .spayNeuterStatus("Neutered")
                    .specialMedicalNeeds("None")
                    .build());

            petImageRepository.save(PetImage.builder()
                    .pet(dog1)
                    .imageUrl("https://images.unsplash.com/photo-1552053831-71594a27632d")
                    .isPrimary(true)
                    .build());

            Pet cat1 = petRepository.save(Pet.builder()
                    .name("Luna")
                    .species("Cat")
                    .breed("Siamese")
                    .age(2)
                    .description("Gentle and affectionate indoor cat who loves sunbeams and lap cuddles.")
                    .adoptionStatus(PetStatus.AVAILABLE)
                    .viewCount(18L)
                    .vaccinationStatus("Up to date")
                    .spayNeuterStatus("Spayed")
                    .specialMedicalNeeds("Mild seasonal allergies")
                    .build());

            petImageRepository.save(PetImage.builder()
                    .pet(cat1)
                    .imageUrl("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba")
                    .isPrimary(true)
                    .build());

            Pet dog2 = petRepository.save(Pet.builder()
                    .name("Bella")
                    .species("Dog")
                    .breed("Labrador")
                    .age(4)
                    .description("Calm, loyal, and fully house-trained lab looking for a peaceful home.")
                    .adoptionStatus(PetStatus.AVAILABLE)
                    .viewCount(32L)
                    .vaccinationStatus("Up to date")
                    .spayNeuterStatus("Spayed")
                    .specialMedicalNeeds("None")
                    .build());

            petImageRepository.save(PetImage.builder()
                    .pet(dog2)
                    .imageUrl("https://images.unsplash.com/photo-1587300003388-59208cc962cb")
                    .isPrimary(true)
                    .build());

            log.info("Default sample pets initialized");
        }
    }
}
