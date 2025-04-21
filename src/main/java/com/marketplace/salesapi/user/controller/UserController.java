package com.marketplace.salesapi.user.controller;

import com.marketplace.salesapi.user.dto.CreateUserRequest;
import com.marketplace.salesapi.user.dto.UserDto;
import com.marketplace.salesapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Criar um novo usuário")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto newUser = userService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um usuário")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        UserDto updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um usuário")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}