package com.example.trello.controller;

import com.example.trello.entity.Attachment;
import com.example.trello.entity.Task;
import com.example.trello.entity.User;
import com.example.trello.repo.AttachmentRepository;
import com.example.trello.repo.TaskRepository;
import com.example.trello.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/task/update/{id}")
    public String updateTask(@PathVariable int id, Model model) {
        Task task = taskRepository.findById(id);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("task", task);
        return "TaskUpdate";
    }
    @PostMapping("/task/update/process/{id}")
    public String processTask(
            @PathVariable int id,
            @RequestParam String title,
            @RequestParam(required = false) Integer userId,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            Model model) throws IOException {
        Task task = taskRepository.findById(id);
        task.setTitle(title);
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                task.setUser(user.get());
            }
        }else {
            task.setUser(null);
        }
        if (attachment != null) {
            Attachment setAttachment=new Attachment();
            setAttachment.setFile_type(attachment.getContentType());
            setAttachment.setContent(attachment.getBytes());
            attachmentRepository.save(setAttachment);
            task.setAttachment(setAttachment);
        }else {
            task.setAttachment(null);
        }
        taskRepository.save(task);
        return "redirect:/task"; // Or wherever you want to redirect
    }


}
