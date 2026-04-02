package com.havenza.ecommerce.admin;

import com.havenza.ecommerce.admin.dto.DashboardDto;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.order.OrderEntity;
import com.havenza.ecommerce.order.OrderRepository;
import com.havenza.ecommerce.order.OrderStatus;
import com.havenza.ecommerce.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public DashboardDto getDashboardStats() {
        long totalUsers = userRepository.countByRoleNot(com.havenza.ecommerce.auth.Role.ADMIN);
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        // Calculate total revenue from DELIVERED orders only.
        // totalAmount in DB is already the net value (after discount).
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(OrderEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> salesByStatus = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

        Map<String, BigDecimal> monthlySales = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().getMonth().name().substring(0, 3) + " " + o.getCreatedAt().getYear(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                OrderEntity::getTotalAmount,
                                BigDecimal::add
                        )
                ));

        return DashboardDto.builder()
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
                .salesByStatus(salesByStatus)
                .monthlySales(monthlySales)
                .build();
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserDto> getUsers(int page, int size, String sortParam) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("name_asc".equals(sortParam)) {
            sort = Sort.by(Sort.Direction.ASC, "fullName");
        } else if ("email_asc".equals(sortParam)) {
            sort = Sort.by(Sort.Direction.ASC, "email");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserEntity> userPage = userRepository.findAll(pageable);

        Page<UserDto> dtoPage = userPage.map(UserDto::fromEntity);
        return PagedResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public PagedResponse<com.havenza.ecommerce.order.dto.OrderDto> getOrders(int page, int size, String sortParam) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("date_asc".equals(sortParam)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<com.havenza.ecommerce.order.OrderEntity> orderPage = orderRepository.findAll(pageable);
        return PagedResponse.of(orderPage.map(com.havenza.ecommerce.order.dto.OrderDto::fromEntity));
    }

    @Transactional(readOnly = true)
    public com.havenza.ecommerce.order.dto.OrderDto getOrderById(Long orderId) {
        com.havenza.ecommerce.order.OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return com.havenza.ecommerce.order.dto.OrderDto.fromEntity(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        com.havenza.ecommerce.order.OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    @Transactional
    public com.havenza.ecommerce.order.dto.OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        com.havenza.ecommerce.order.OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(newStatus);
        com.havenza.ecommerce.order.OrderEntity saved = orderRepository.save(order);
        return com.havenza.ecommerce.order.dto.OrderDto.fromEntity(saved);
    }

    @Transactional
    public UserDto updateUserRole(Long id, com.havenza.ecommerce.auth.Role newRole) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(newRole);
        UserEntity saved = userRepository.save(user);
        return UserDto.fromEntity(saved);
    }

    @Transactional
    public UserDto updateUser(Long id, java.util.Map<String, String> body) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (body.containsKey("fullName")) {
            user.setFullName(body.get("fullName"));
        }
        if (body.containsKey("phone")) {
            user.setPhone(body.get("phone"));
        }
        if (body.containsKey("role")) {
            user.setRole(com.havenza.ecommerce.auth.Role.valueOf(body.get("role")));
        }
        UserEntity saved = userRepository.save(user);
        return UserDto.fromEntity(saved);
    }
}
