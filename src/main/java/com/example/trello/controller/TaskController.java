package com.example.trello.controller;

import com.example.trello.entity.Attachment;
import com.example.trello.entity.Status;
import com.example.trello.entity.Task;
import com.example.trello.entity.User;
import com.example.trello.repo.AttachmentRepository;
import com.example.trello.repo.StatusRepository;
import com.example.trello.repo.TaskRepository;
import com.example.trello.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private final StatusRepository statusRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @GetMapping("/task/update/{id}")
    public String updateTask(@PathVariable int id, Model model) {
        Task task = taskRepository.findById(id);
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("task", task);
        return "TaskUpdate";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
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
            user.ifPresent(task::setUser);
        } else {
            task.setUser(null);
        }
        if (attachment != null && !attachment.isEmpty()) {
            Attachment setAttachment = new Attachment();
            setAttachment.setFile_type(attachment.getContentType());
            setAttachment.setContent(attachment.getBytes());
            attachmentRepository.save(setAttachment);
            task.setAttachment(setAttachment);
        } else if (task.getAttachment() == null) {
            Optional<Attachment> defaultAttachment = attachmentRepository.findById(task.getAttachment().getId());
            defaultAttachment.ifPresent(task::setAttachment);
        }

        taskRepository.save(task);
        return "redirect:/task";
    }

    @GetMapping("/task/comment/{id}")
    public String commentTask(@PathVariable int id, Model model) {
        List<User> allUsers = userRepository.findAll();
        Task task = taskRepository.findById(id);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("task", task);
        return "comments";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @GetMapping("/task/add")
    public String addTask(Model model) {
        return "addTask";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @PostMapping("/task/add/process")
    public String addTask(@RequestParam String title,
                          @RequestParam(value = "file", required = false) MultipartFile file,
                          Model model) throws IOException {
        Attachment setAttachment = null;
        if (file != null && !file.isEmpty()) {
            setAttachment = new Attachment();
            setAttachment.setFile_type(file.getContentType());
            setAttachment.setContent(file.getBytes());
            attachmentRepository.save(setAttachment);
        }
        Task task = new Task();
        task.setTitle(title);
        Status defaultStatus = statusRepository.findByPosition_number(1);
        task.setStatus(defaultStatus);
        task.setAttachment(setAttachment);
        taskRepository.save(task);
        return "redirect:/task";
    }

    @PostMapping("/task/moveRight/{id}")
    public String shiftTaskRight(@PathVariable int id, Model model) {
        Task task = taskRepository.findById(id);
        if (task == null) {
            model.addAttribute("message", "Task topilmadi!");
            return "error";
        }

        int currentPos = task.getStatus().getPosition_number();
        List<Status> statuses = statusRepository.findByIs_active(true);
        Status nextStatus = null;
        int minHigherPos = Integer.MAX_VALUE;

        for (Status status : statuses) {
            int pos = status.getPosition_number();
            if (pos > currentPos && pos < minHigherPos) {
                minHigherPos = pos;
                nextStatus = status;
            }
        }

        if (nextStatus != null) {
            task.setStatus(nextStatus);
            taskRepository.save(task);
        }

        return "redirect:/task";
    }

    @PostMapping("/task/moveLeft/{id}")
    public String shiftTaskLeft(@PathVariable int id, Model model) {
        Task task = taskRepository.findById(id);
        if (task == null) {
            model.addAttribute("message", "Task topilmadi!");
            return "error";
        }

        int currentPos = task.getStatus().getPosition_number();
        List<Status> statuses = statusRepository.findByIs_active(true);
        Status prevStatus = null;
        int maxLowerPos = Integer.MIN_VALUE;

        for (Status status : statuses) {
            int pos = status.getPosition_number();
            if (pos < currentPos && pos > maxLowerPos) {
                maxLowerPos = pos;
                prevStatus = status;
            }
        }

        if (prevStatus != null) {
            task.setStatus(prevStatus);
            taskRepository.save(task);
        }

        return "redirect:/task";
    }
}
