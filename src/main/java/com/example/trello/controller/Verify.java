package com.example.trello.controller;

import com.example.trello.DTO.UserDTO;
import com.example.trello.entity.Role;
import com.example.trello.entity.Roles;
import com.example.trello.entity.User;
import com.example.trello.repo.UserRepostory;
import com.example.trello.service.EmailSender;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class Verify {
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepostory userRepostory;
    @GetMapping("/verify")
    public String verify(HttpSession session) {
        String randomCode = emailSender.randomCode();
        UserDTO user = (UserDTO)session.getAttribute("user");
        String email = user.getEmail();
        emailSender.sendEmail(email, randomCode);
        user.setRandomCode(randomCode);
        session.setAttribute("user", user);
        return "/verification";
    }
    @PostMapping("/verify/process")
    public String process(@RequestParam String code, HttpSession session) {
        UserDTO user = (UserDTO)session.getAttribute("user");
        if (code.equals(user.getRandomCode())) {
            User regsiteredUser=new User();
            regsiteredUser.setEmail(user.getEmail());
            regsiteredUser.setPassword(passwordEncoder.encode(user.getPassword()));
            regsiteredUser.setUsername(user.getUsername());
            regsiteredUser.set_verified(true);
            List<Role> roles = new ArrayList<>();
            roles.add(new Role(null, Roles.PROGRAMMER.name()));
            regsiteredUser.setRoles(roles);
            userRepostory.save(regsiteredUser);
        }
        return "redirect:/login";
    }

}
