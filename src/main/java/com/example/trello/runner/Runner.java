package com.example.trello.runner;

import com.example.trello.entity.Role;
import com.example.trello.entity.Roles;
import com.example.trello.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class Runner implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Role> all = roleRepository.findAll();
        if (all.isEmpty()) {
            all.add(new Role(null, Roles.PROGRAMMER.name()));
            all.add(new Role(null, Roles.ADMIN.name()));
            all.add(new Role(null, Roles.MAINTAINER.name()));
            roleRepository.saveAll(all);
        }
    }
}