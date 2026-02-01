package com.sda.ticketing.web;

import com.sda.ticketing.domain.Category;
import com.sda.ticketing.domain.SlaPolicy;
import com.sda.ticketing.domain.User;
import com.sda.ticketing.repository.CategoryRepository;
import com.sda.ticketing.repository.SlaPolicyRepository;
import com.sda.ticketing.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SlaPolicyRepository slaPolicyRepository;

    public AdminController(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           SlaPolicyRepository slaPolicyRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.slaPolicyRepository = slaPolicyRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> listCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @GetMapping("/sla")
    public ResponseEntity<List<SlaPolicy>> listSla() {
        return ResponseEntity.ok(slaPolicyRepository.findAll());
    }

    @PostMapping("/sla")
    public ResponseEntity<SlaPolicy> createSla(@Valid @RequestBody SlaPolicy policy) {
        return ResponseEntity.ok(slaPolicyRepository.save(policy));
    }
}

