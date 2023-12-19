package com.example.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.entity.AuthRequest;
import com.example.jwt.entity.UserInfo;
import com.example.jwt.service.JwtService;
import com.example.jwt.service.UserInfoService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class UserContoller {
    @Autowired
    private UserInfoService service; 
  
    @Autowired
    private JwtService jwtService; 
  
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome") 
    public String welcome() { 
        return "Welcome this endpoint is not secure"; 
    } 
  
    @PostMapping("/addNewUser") 
    public String addNewUser(@RequestBody UserInfo userInfo) { 
        return service.addUser(userInfo); 
    } 
  
    @GetMapping("/user/userProfile") 
    @PreAuthorize("hasAuthority('ROLE_USER')") 
    public String userProfile() { 
        return "Welcome to User Profile"; 
    } 
  
    @GetMapping("/admin/adminProfile") 
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") 
    public String adminProfile() { 
        return "Welcome to Admin Profile"; 
    } 

    @PostMapping("/generateToken") 
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) { 
        System.out.println("/generateToken");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())); 
        if (authentication.isAuthenticated()) { 
            String token = jwtService.generateToken(authRequest.getUsername()); 
            System.out.println("token : "+token);
            response.addHeader("Authorization", "Bearer " + token);
            return ResponseEntity.ok("Token generated successfully"); 
        } else { 
            throw new UsernameNotFoundException("invalid user request !"); 
        } 
    } 
}
