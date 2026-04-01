package com.havenza.ecommerce.admin;

import com.havenza.ecommerce.admin.dto.DashboardDto;
import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;
import com.havenza.ecommerce.auth.Role;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserDto>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers(page, size, sort)));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PagedResponse<com.havenza.ecommerce.order.dto.OrderDto>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getOrders(page, size, sort)));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<com.havenza.ecommerce.order.dto.OrderDto>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getOrderById(id)));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        adminService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Order deleted successfully"));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<com.havenza.ecommerce.order.dto.OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        com.havenza.ecommerce.order.OrderStatus newStatus = com.havenza.ecommerce.order.OrderStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(ApiResponse.success(adminService.updateOrderStatus(id, newStatus), "Order status updated"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Role newRole = Role.valueOf(body.get("role"));
        return ResponseEntity.ok(ApiResponse.success(adminService.updateUserRole(id, newRole), "User role updated successfully"));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateUser(id, body), "User updated successfully"));
    }
}
