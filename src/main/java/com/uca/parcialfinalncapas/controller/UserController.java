package com.uca.parcialfinalncapas.controller;

import com.uca.parcialfinalncapas.dto.request.UserCreateRequest;
import com.uca.parcialfinalncapas.dto.request.UserUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.GeneralResponse;
import com.uca.parcialfinalncapas.dto.response.UserResponse;
import com.uca.parcialfinalncapas.service.UserService;
import com.uca.parcialfinalncapas.utils.ResponseBuilderUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('TECH')")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<GeneralResponse> getAllUsers() {
        List<UserResponse> users = userService.findAll();
        return ResponseBuilderUtil.buildResponse(
                "Usuarios obtenidos correctamente",
                users.isEmpty() ? org.springframework.http.HttpStatus.NO_CONTENT
                        : org.springframework.http.HttpStatus.OK,
                users
        );
    }

    @GetMapping("/{correo}")
    public ResponseEntity<GeneralResponse> getUserByCorreo(@PathVariable String correo) {
        UserResponse user = userService.findByCorreo(correo);
        return ResponseBuilderUtil.buildResponse("Usuario encontrado",
                org.springframework.http.HttpStatus.OK, user);
    }

    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(
            @Valid @RequestBody UserCreateRequest req) {
        UserResponse created = userService.save(req);
        return ResponseBuilderUtil.buildResponse("Usuario creado",
                org.springframework.http.HttpStatus.CREATED, created);
    }

    @PutMapping
    public ResponseEntity<GeneralResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest req) {
        UserResponse updated = userService.update(req);
        return ResponseBuilderUtil.buildResponse("Usuario actualizado",
                org.springframework.http.HttpStatus.OK, updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseBuilderUtil.buildResponse("Usuario eliminado",
                org.springframework.http.HttpStatus.OK, null);
    }
}
