package com.example.trello.controller;

import com.example.trello.entity.Comment;
import com.example.trello.entity.Task;
import com.example.trello.entity.User;
import com.example.trello.repo.CommentRepository;
import com.example.trello.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @PostMapping("/comments/add")
    public String addComment(@RequestParam String comment, @RequestParam int taskId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment savingComment=new Comment();
        savingComment.setComment(comment);
        savingComment.setUser(user);
        commentRepository.save(savingComment);
        Task task = taskRepository.findById(taskId);
        task.getComments().add(savingComment);
        taskRepository.save(task);
        return "redirect:/task/comment/"+task.getId();
    }
}
