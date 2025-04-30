package com.example.trello.config;

import com.example.trello.entity.User;
import com.example.trello.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class CurrentUser implements UserDetailsService {
    private final UserRepository userRepostory;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepostory.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }else {
            throw new UsernameNotFoundException(username);
        }
    }
}
