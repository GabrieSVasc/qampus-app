package com.project.qampus.service;

import com.project.qampus.dto.UserDTO;
import com.project.qampus.model.User;
import com.project.qampus.repositories.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @InjectMocks
    private UserService userService;

    // ---------- update ----------

    @Test
    void shouldUpdateUserSuccessfully() {

        UserDTO dto = new UserDTO(
                "Novo nome",
                "novoemail@example.com",
                "novaSenha123");

        when(user.getId())
                .thenReturn("user-1");

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.update("user-1", dto, user);

        assertSame(user, result);

        verify(user).setName("Novo nome");
        verify(user).setEmail("novoemail@example.com");
        verify(user).setPassword("novaSenha123");

        verify(userRepository).findById("user-1");
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {

        UserDTO dto = new UserDTO(
                "Novo nome",
                "novoemail@example.com",
                "novaSenha123");

        when(userRepository.findById("user-404"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.update("user-404", dto, user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());

        verify(userRepository).findById("user-404");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUserThatBelongsToAnotherUser() {

        UserDTO dto = new UserDTO(
                "Novo nome",
                "novoemail@example.com",
                "novaSenha123");

        User userFromRepository = mock(User.class);
        when(userFromRepository.getId())
                .thenReturn("user-1");

        when(user.getId())
                .thenReturn("user-2");

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(userFromRepository));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.update("user-1", dto, user));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Esse usuário pertence a outro usuário", exception.getReason());

        verify(userRepository).findById("user-1");
        verify(userRepository, never()).save(any(User.class));
        verify(userFromRepository, never()).setName(any());
    }

    // ---------- delete ----------

    @Test
    void shouldDeleteUserSuccessfully() {

        when(user.getId())
                .thenReturn("user-1");

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(user));

        userService.delete("user-1", user);

        verify(userRepository).findById("user-1");
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {

        when(userRepository.findById("user-404"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.delete("user-404", user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());

        verify(userRepository).findById("user-404");
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingUserThatBelongsToAnotherUser() {

        User userFromRepository = mock(User.class);
        when(userFromRepository.getId())
                .thenReturn("user-1");

        when(user.getId())
                .thenReturn("user-2");

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(userFromRepository));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.delete("user-1", user));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Esse usuário pertence a outro usuário", exception.getReason());

        verify(userRepository).findById("user-1");
        verify(userRepository, never()).delete(any(User.class));
    }

    // ---------- findById ----------

    @Test
    void shouldFindUserByIdSuccessfully() {

        when(userRepository.findById("user-1"))
                .thenReturn(Optional.of(user));

        User result = userService.findById("user-1");

        assertSame(user, result);

        verify(userRepository).findById("user-1");
    }

    @Test
    void shouldThrowExceptionWhenFindingByIdNonExistentUser() {

        when(userRepository.findById("user-404"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.findById("user-404"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());

        verify(userRepository).findById("user-404");
    }

    // ---------- findByEmail ----------

    @Test
    void shouldFindUserByEmailSuccessfully() {

        when(userRepository.findByEmail("autor@example.com"))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmail("autor@example.com");

        assertSame(user, result);

        verify(userRepository).findByEmail("autor@example.com");
    }

    @Test
    void shouldThrowExceptionWhenFindingByEmailNonExistentUser() {

        when(userRepository.findByEmail("naoexiste@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.findByEmail("naoexiste@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());

        verify(userRepository).findByEmail("naoexiste@example.com");
    }
}