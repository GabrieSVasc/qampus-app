package com.project.qampus.controllers;

import com.project.qampus.dto.UserDTO;
import com.project.qampus.model.Answer;
import com.project.qampus.model.Post;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.Role;
import com.project.qampus.service.AnswerService;
import com.project.qampus.service.PostService;
import com.project.qampus.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private AnswerService answerService;

    @Mock
    private User user;

    @InjectMocks
    private UserController userController;

    private Post buildPost(String id, String title, User author) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setContent("Conteúdo de exemplo");
        post.setUpVotes(0L);
        post.setDownVotes(0L);
        post.setTags(java.util.Set.of());
        post.setUser(author);
        return post;
    }

    private User buildUser(String id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setRole(Role.STUDENT);
        return u;
    }

    @Test
    void shouldGetUserPostsSuccessfully() {

        User author = buildUser("user-1", "Autor", "autor@example.com");
        Post post = buildPost("post-1", "Post do usuário", author);

        when(postService.findByUserId("user-1"))
                .thenReturn(List.of(post));

        var response = userController.getUserPosts("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(postService).findByUserId("user-1");
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoPosts() {

        when(postService.findByUserId("user-1"))
                .thenReturn(List.of());

        var response = userController.getUserPosts("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(postService).findByUserId("user-1");
    }

    @Test
    void shouldGetUserAnswersSuccessfully() {

        User answerAuthor = buildUser("user-1", "Autor", "autor@example.com");

        Answer answer = mock(Answer.class);
        when(answer.getUser()).thenReturn(answerAuthor);
        when(answer.getPost()).thenReturn(buildPost("post-1", "Post relacionado", answerAuthor));

        when(answerService.findByUserId("user-1"))
                .thenReturn(List.of(answer));

        var response = userController.userAnswers("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(answerService).findByUserId("user-1");
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoAnswers() {

        when(answerService.findByUserId("user-1"))
                .thenReturn(List.of());

        var response = userController.userAnswers("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(answerService).findByUserId("user-1");
    }

    @Test
    void shouldGetUserByEmailSuccessfully() {

        User found = buildUser("user-1", "Autor", "autor@example.com");

        when(userService.findByEmail("autor@example.com"))
                .thenReturn(found);

        var response = userController.getUser("autor@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService).findByEmail("autor@example.com");
    }

    @Test
    void shouldUpdateUserSuccessfully() {

        UserDTO dto = new UserDTO(
                "Nome atualizado",
                "novoemail@example.com",
                "novaSenha123");

        User updated = buildUser("user-1", dto.name(), dto.email());

        when(userService.update("user-1", dto, user))
                .thenReturn(updated);

        var response = userController.updateUser("user-1", dto, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userService).update("user-1", dto, user);
    }

    @Test
    void shouldDeleteUserSuccessfully() {

        var response = userController.deleteUser("user-1", user);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService).delete("user-1", user);
    }
}