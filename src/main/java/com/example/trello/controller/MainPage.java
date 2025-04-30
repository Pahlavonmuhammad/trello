package com.example.trello.controller;

import com.example.trello.entity.Status;
import com.example.trello.entity.Task;
import com.example.trello.entity.User;
import com.example.trello.repo.StatusRepository;
import com.example.trello.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainPage {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    @GetMapping("/task")
    public String task(Model model) {
        List<Status> statusList = statusRepository.findByIs_active(true);
        List<Task> taskList = taskRepository.findAll();
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("taskList", taskList);
        model.addAttribute("user", user);
        model.addAttribute("statusList", statusList);
        return "task";
    }
}
