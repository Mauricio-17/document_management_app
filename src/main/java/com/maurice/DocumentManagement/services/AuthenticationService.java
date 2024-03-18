package com.maurice.DocumentManagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maurice.DocumentManagement.dto.AuthenticationRequest;
import com.maurice.DocumentManagement.dto.AuthenticationResponse;
import com.maurice.DocumentManagement.dto.FolderRequest;
import com.maurice.DocumentManagement.dto.UserRequest;
import com.maurice.DocumentManagement.entities.Plan;
import com.maurice.DocumentManagement.entities.Token;
import com.maurice.DocumentManagement.entities.UserEntity;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.CreateStatusException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.PlanRepository;
import com.maurice.DocumentManagement.repository.TokenRepository;
import com.maurice.DocumentManagement.repository.UserRepository;
import com.maurice.DocumentManagement.security.JwtService;
import com.maurice.DocumentManagement.utils.Mappers;
import com.maurice.DocumentManagement.utils.Validators;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {


    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlanRepository planRepository;
    private final FolderService folderService;
    private final StorageService storageService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, PlanRepository planRepository, FolderService folderService, StorageService storageService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.planRepository = planRepository;
        this.folderService = folderService;
        this.storageService = storageService;
    }

    public void registerUser(UserRequest user) {
        Plan planFound = planRepository.findById(user.planId()).orElseThrow(
                () -> CreateStatusException.getThrowableException("Plan related with user email "+ user.email() +" not found", 404)
        );
        Optional<UserEntity> foundUser = userRepository.findUserByEmail(user.email());
        if (foundUser.isPresent()) {
            throw CreateStatusException.getThrowableException("The email "+ user.email() +" already exists.", 400);
        }

        Validators.minimum(user.password(), (short) 9);

        // Mapping the User entity to be saved
        UserEntity userLog = Mappers.dtoToUser.apply(user);
        userLog.setCreatedAt(LocalDateTime.now());
        userLog.setLastModifiedAt(LocalDateTime.now());
        userLog.setPlan(planFound);
        userLog.setPassword(passwordEncoder.encode(userLog.getPassword()));

        UserEntity saved = userRepository.save(userLog);

        // Inserting the first User's folder that will act as a root folder
        folderService.registerFolder(new FolderRequest(
                saved.getEmail(),
                "Root directory",
                saved.getEmail(),
                saved.getId()
        ));
        storageService.createFolderForUser(saved.getEmail());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        System.out.println("Reached ........");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        System.out.println("Reached ........");
        var user = userRepository.findUserByEmail(request.email()).orElseThrow(
                () -> CreateStatusException.getThrowableException("No user found with email "+ request.email(), 404)
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", user.getAuthorities());
        System.out.println("-----"+ user);
        var jwtToken = jwtService.generateToken(claims, user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);

        removeAllTokensOfUser(user);
        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, jwtRefreshToken);
    }

    private void saveUserToken(UserEntity savedUser, String jwtToken) {
        var token = Token.builder().user(savedUser).token(jwtToken).build();
        tokenRepository.save(token);
    }

    private void removeAllTokensOfUser(UserEntity user) {
        var validUserTokens = tokenRepository.findTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        // Deleting the existing tokens of this user
        validUserTokens.forEach(tokenRepository::delete);
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {

        final String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) { // Querying the current user authenticated
            var user = this.userRepository.findUserByEmail(userEmail).
                    orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {

                var accessToken = jwtService.generateToken(user);

                removeAllTokensOfUser(user);
                saveUserToken(user, accessToken);

                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
            }

            if (jwtService.isTokenExpired(refreshToken)){
                // Remove the token from Database
                removeAllTokensOfUser(user);
            }

        }
    }
}
