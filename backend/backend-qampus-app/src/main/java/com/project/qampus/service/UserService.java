package com.project.qampus.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.qampus.dto.UserDTO;
import com.project.qampus.model.User;
import com.project.qampus.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUND = "Usuário não encontrado";

    private final UserRepository userRepository;

    public User update(String userId, UserDTO body, User user) {

        User userFinal = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND
                ));

        if (!userFinal.getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esse usuário pertence a outro usuário"
            );
        }

        userFinal.setName(body.name());
        userFinal.setEmail(body.email());
        userFinal.setPassword(body.password());

        return userRepository.save(userFinal);
    }

    public void delete(String userId, User user) {

        User userFinal = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND
                ));

        if (!userFinal.getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esse usuário pertence a outro usuário"
            );
        }

        userRepository.delete(userFinal);
    }

    public User findById(String userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND
                ));
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        USER_NOT_FOUND
                ));
    }
}