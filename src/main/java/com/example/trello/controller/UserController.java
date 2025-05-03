package com.example.trello.controller;

import com.example.trello.entity.Attachment;
import com.example.trello.entity.Role;
import com.example.trello.entity.Task;
import com.example.trello.entity.User;
import com.example.trello.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttachmentRepository attachmentRepository;
    private final RoleRepository roleRepository;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public String user(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/add")
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.set_verified(true);

        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                Attachment attachment = new Attachment();
                attachment.setFile_type(attachmentFile.getContentType());
                attachment.setContent(attachmentFile.getBytes());

                attachmentRepository.save(attachment);
                user.setAttachment(attachment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            user.setAttachment(null);
        }

        userRepository.save(user);
        return "redirect:/user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        Optional<User> user = userRepository.findById(id);
        List<Role> allRoles = roleRepository.findAll();
        user.ifPresent(value -> model.addAttribute("user", value));
        model.addAttribute("allRoles", allRoles);
        return "updateUser";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable Integer id,
                             @ModelAttribute("user") User userForm,
                             @RequestParam(value = "attachmentFile", required = false) MultipartFile file,
                             @RequestParam(value = "roleIds", required = false) List<Integer> roleIds) throws IOException {

        User user = userRepository.findById(id).orElseThrow();

        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());

        if (userForm.getPassword() != null && !userForm.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }

        if (roleIds != null) {
            List<Role> selectedRoles = roleRepository.findAllById(roleIds);
            user.setRoles(selectedRoles);
        }

        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFile_type(file.getContentType());
            attachment.setContent(file.getBytes());
            attachmentRepository.save(attachment);
            user.setAttachment(attachment);
        }

        userRepository.save(user);
        return "redirect:/user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            commentRepository.deleteByUser(user.get());
            List<Task> tasks = taskRepository.findByUser(user.get());
            for (Task task : tasks) {
                task.setUser(null);
            }
            taskRepository.saveAll(tasks);
            userRepository.deleteById(id);
        }
        return "redirect:/user";
    }
    @GetMapping("/user/edit")
    public String editUser(Model model) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        return "editUser";
    }
    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable Integer id,
                             @ModelAttribute("user") User userForm,
                             @RequestParam(value = "attachmentFile", required = false) MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User user = userRepository.findByEmail(currentUser.getEmail()).orElseThrow();

        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());

        if (userForm.getPassword() != null && !userForm.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }

        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFile_type(file.getContentType());
            attachment.setContent(file.getBytes());
            attachmentRepository.save(attachment);
            user.setAttachment(attachment);
        }
        Authentication authentication1 = new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication1);

        userRepository.save(user);
        return "redirect:/cabinet";
    }

}
