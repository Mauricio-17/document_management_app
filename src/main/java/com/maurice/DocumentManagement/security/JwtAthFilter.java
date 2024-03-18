package com.maurice.DocumentManagement.security;

import com.maurice.DocumentManagement.entities.UserEntity;
import com.maurice.DocumentManagement.exceptions.CreateStatusException;
import com.maurice.DocumentManagement.repository.TokenRepository;
import com.maurice.DocumentManagement.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain filterChain
    )
            throws ServletException, IOException {

        final String authHeader = req.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(req, res);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println(jwt);
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("Auth context "+SecurityContextHolder.getContext().getAuthentication());

        // Querying the current user authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            UserEntity userEntity = this.userRepository.findUserByEmail(userEmail).orElseThrow(
                    () -> CreateStatusException.getThrowableException("No user found with email "+ userEmail, 404)
            );
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(req)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
            if (jwtService.isTokenExpired(jwt)){
                // Remove the token from Database
                removeAllTokensOfUser(userEntity);
            }
        }
        filterChain.doFilter(req, res); // Calling the next filter on the chain
    }

    private void removeAllTokensOfUser(UserEntity user) {
        var validUserTokens = tokenRepository.findTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        // Deleting the existing tokens of this user
        validUserTokens.forEach(tokenRepository::delete);
        tokenRepository.saveAll(validUserTokens);
    }
}
