package com.auth.authservice.controller;

import com.auth.authservice.dto.AuthResponse;
import com.auth.authservice.dto.LoginRequest;
import com.auth.authservice.model.App;
import com.auth.authservice.model.MagicToken;
import com.auth.authservice.repository.AppRepository;
import com.auth.authservice.repository.MagicTokenRepository;
import com.auth.authservice.service.EmailService;
import com.auth.authservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.auth.authservice.dto.AuthRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private MagicTokenRepository magicTokenRepo;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/request")
    public String requestLogin(@RequestBody AuthRequest request) {

        String token = UUID.randomUUID().toString();

        MagicToken mt = new MagicToken();
        mt.setEmail(request.getEmail());
        mt.setToken(token);
        mt.setExpiry(LocalDateTime.now().plusMinutes(10));
        mt.setRedirectUrl(request.getRedirectUrl());
        magicTokenRepo.save(mt);

        String link = "http://localhost:8081/auth/verify?token=" + mt.getToken();

        System.out.println("Magic Link: " + link);
        emailService.sendMagicLink(request.getEmail(), link);
        return "Magic link generated";
    }

    /*@PostMapping("/request")
    public String requestLogin(@RequestBody LoginRequest request) {

        String token = UUID.randomUUID().toString();

        MagicToken mt = new MagicToken();
        mt.setEmail(request.getEmail());
        mt.setToken(token);
        mt.setExpiry(LocalDateTime.now().plusMinutes(10));

        magicTokenRepo.save(mt);

        String link = "http://localhost:8081/auth/verify?token=" + token;

        System.out.println("Magic Link: " + link);

        return "Magic link generated. Check console.";
    }*/
    /*
    @GetMapping("/verify")
    public Object verify(@RequestParam String token) {

        MagicToken mt = magicTokenRepo.findByToken(token);

        if (mt == null || mt.getExpiry().isBefore(LocalDateTime.now())) {
            return "Invalid or expired token";
        }

        String jwt = JwtUtil.generateToken(mt.getEmail());

        return new AuthResponse(jwt);
    }
    */
    @GetMapping("/verify")
    public void verify(@RequestParam String token, HttpServletResponse response) throws IOException {

        MagicToken mt = magicTokenRepo.findByToken(token);

        if (mt == null || mt.getExpiry().isBefore(LocalDateTime.now())) {
            response.getWriter().write("Invalid or expired token");
            return;
        }

        String jwt = JwtUtil.generateToken(mt.getEmail());

        // 🔥 TEMP TEST (we'll improve later)
        String redirectUrl = "http://localhost:8081/index.html";

        response.sendRedirect(redirectUrl + "?token=" + jwt);
    }
    @GetMapping("/app/create")
    public App createApp(@RequestParam String name) {
        App app = new App();
        app.setName(name);
        app.setApiKey(UUID.randomUUID().toString());
        return appRepository.save(app);
    }
}
