package org.splitmoneybot.service;

import lombok.RequiredArgsConstructor;
import org.splitmoneybot.entity.AppUser;
import org.splitmoneybot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void registerOrUpdateUser(Long chatId, String username, String firstName, String lastName) {
        Optional<AppUser> existingUser = userRepository.findByChatId(chatId);
        if (existingUser.isPresent()) {
            // Update existing appUser
            AppUser appUser = existingUser.get();
            appUser.setUsername(username);
            appUser.setFirstName(firstName);
            appUser.setLastName(lastName);
            userRepository.save(appUser);
        } else {

            AppUser newAppUser = AppUser.builder()
                    .chatId(chatId)
                    .username(username)
                    .firstName(firstName)
                    .lastName(lastName)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(newAppUser);
        }
    }
}