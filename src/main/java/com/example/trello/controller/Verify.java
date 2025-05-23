package com.example.trello.controller;

import com.example.trello.DTO.UserDTO;
import com.example.trello.entity.Role;
import com.example.trello.entity.Roles;
import com.example.trello.entity.User;
import com.example.trello.repo.RoleRepository;
import com.example.trello.repo.UserRepository;
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
    private final UserRepository userRepostory;
    private final RoleRepository roleRepository;
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
            regsiteredUser.setName(user.getUsername());
            regsiteredUser.set_verified(true);
            Role role=roleRepository.findByRole(Roles.PROGRAMMER.name());
            List<Role> roles=new ArrayList<>();
            roles.add(role);
            regsiteredUser.setRoles(roles);
            userRepostory.save(regsiteredUser);
        }
        return "redirect:/login";
    }

}
