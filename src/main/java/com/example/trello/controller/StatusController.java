package com.example.trello.controller;

import com.example.trello.entity.Status;
import com.example.trello.repo.StatusRepository;
import com.example.trello.service.UpdateStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatusController {

    private final StatusRepository statusRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @GetMapping("/status/manage")
    public String manage(Model model) {
        List<Status> statuses = statusRepository.findAll();
        model.addAttribute("statuses", statuses);
        return "updateStatus";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @PostMapping("/status/update")
    public String updateAll(@ModelAttribute UpdateStatus updateStatus, Model model) {
        if (updateStatus.getStatuses() != null && !updateStatus.getStatuses().isEmpty()) {
            statusRepository.saveAll(updateStatus.getStatuses());
        }
        return "redirect:/task";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @GetMapping("/status/add")
    public String add(Model model) {
        return "addStatus";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @PostMapping("/status/add/process")
    public String addProcess(@RequestParam String name) {
        List<Status> statuses = statusRepository.findAll();
        Status status = new Status();
        status.setName(name.toUpperCase());
        status.setIs_active(true);
        status.setPosition_number(statuses.size() + 1);
        statusRepository.save(status);
        return "redirect:/task";
    }
}
