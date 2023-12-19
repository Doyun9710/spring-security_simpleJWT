package com.example.jwt.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.jwt.service.JwtService;
import com.example.jwt.service.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService; 
    @Autowired
    private UserInfoService userDetailsService; 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("필터 시작");
        String authHeader = request.getHeader("Authorization"); 
        System.out.println(authHeader);
        String token = null; 
        String username = null; 

        if (authHeader != null && authHeader.startsWith("Bearer ")) { 
            System.out.println("1 : 토큰 있음 추출");
            token = authHeader.substring(7); 
            username = jwtService.extractUsername(token); 
        } 
  
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { 
            System.out.println("2 : 유저 디테일 설정");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); 
            if (jwtService.validateToken(token, userDetails)) { 
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); 
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 
                SecurityContextHolder.getContext().setAuthentication(authToken); 
            } 
        } 
        System.out.println("필터 끝");
        filterChain.doFilter(request, response);   
        
    }
    
}