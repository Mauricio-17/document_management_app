package com.maurice.DocumentManagement.controllers;

import com.maurice.DocumentManagement.dto.UserPageResponse;
import com.maurice.DocumentManagement.dto.UserRequest;
import com.maurice.DocumentManagement.dto.UserResponse;
import com.maurice.DocumentManagement.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public UserPageResponse getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ){
        return userService.getAllUsers(pageNo, pageSize);
    }

    @GetMapping("/plan/{planName}")
    public UserPageResponse getUsersByPlanName(
            @PathVariable(name = "planName") String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ){
        return userService.getUsersByPlanName(name, pageNo, pageSize)   ;
    }

    @GetMapping("/share/{shareId}")
    public ResponseEntity<?> getUsersByPlan(
            @PathVariable(name = "shareId") Long shareId
    ){
        List<UserResponse> list = userService.getUsersByShareId(shareId);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest payload, @PathVariable("id") Long userId){
        userService.updateUser(payload, userId);
        return ResponseEntity.status(204).body(null);
    }


}
