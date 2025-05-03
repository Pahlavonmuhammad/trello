package com.example.trello.controller;

import com.example.trello.DTO.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Register {
    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @PostMapping("/register/process")
    public String register(@RequestParam String name, @RequestParam String email, @RequestParam String password, HttpSession session){
        UserDTO userDTO = new UserDTO(name,email,password);
        session.setAttribute("user",userDTO);
        return "redirect:/verify"; // bu yerdan MuhammadQodir boshlaydi va buni filterga tiqishni unutmang
    }
}
