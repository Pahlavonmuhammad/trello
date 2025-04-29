package com.example.trello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Cabinet {
    @GetMapping("/cabinet")
    public String cabinet(){
        return "cabinet";
    }
}
