package com.havenza.ecommerce.auth;

import com.havenza.ecommerce.auth.dto.AuthResponse;
import com.havenza.ecommerce.auth.dto.LoginRequest;
import com.havenza.ecommerce.auth.dto.RegisterRequest;
import com.havenza.ecommerce.auth.dto.UserDto;
import com.havenza.ecommerce.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(authService.getMe(), "User details fetched"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> updateMe(@Valid @RequestBody com.havenza.ecommerce.auth.dto.UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.updateMe(request), "User profile updated"));
    }
}
