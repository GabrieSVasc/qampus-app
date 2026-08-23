package com.project.qampus.integration;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qampus.dto.PostDTO;
import com.project.qampus.dto.RegisterRequestDTO;
import com.project.qampus.model.Post;
import com.project.qampus.model.Tag;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.Role;
import com.project.qampus.repositories.PostRepository;
import com.project.qampus.repositories.TagRepository;
import com.project.qampus.repositories.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        postRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Aluno Teste", "aluno@qampus.com", "senha123", Role.STUDENT);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        validToken = objectMapper.readTree(
                result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void shouldDenyCreatePostWithoutAuthentication() throws Exception {
        PostDTO dto = new PostDTO("Título sem auth", "Conteúdo sem auth", Set.of("tag1"));

        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreatePostSuccessfullyWithValidJwtToken() throws Exception {
        PostDTO dto = new PostDTO(
                "Dúvida sobre Spring Boot",
                "Como configurar testes de integração?",
                Set.of("spring", "junit"));

        mockMvc.perform(post("/post/create")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Dúvida sobre Spring Boot"))
                .andExpect(jsonPath("$.content").value("Como configurar testes de integração?"));

        assertEquals(1, postRepository.count());
    }

    @Test
    void shouldRejectCreatePostAfterUserLogsOut() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        PostDTO dto = new PostDTO(
                "Título após logout", "Conteúdo após logout", Set.of("tag1"));

        mockMvc.perform(post("/post/create")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSearchPostsSuccessfully() throws Exception {
        User user = userRepository.findByEmail("aluno@qampus.com").orElseThrow();
        Tag tag = tagRepository.save(new Tag(null, "java", null));

        Post post1 = new Post();
        post1.setTitle("Como aprender Spring Boot?");
        post1.setContent("Conteúdo relevante");
        post1.setUser(user);
        post1.setTags(Set.of(tag));

        Post post2 = new Post();
        post2.setTitle("Dúvida sobre Python");
        post2.setContent("Outro assunto");
        post2.setUser(user);
        post2.setTags(Set.of(tag));

        postRepository.save(post1);
        postRepository.save(post2);

        mockMvc.perform(get("/post/search")
                        .param("busca", "Spring")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Como aprender Spring Boot?"));
    }

    @Test
    void shouldFilterQuestionsByTagAndOrderByMostVoted() throws Exception {
        User user = userRepository.findByEmail("aluno@qampus.com").orElseThrow();
        Tag spring = tagRepository.save(new Tag(null, "spring", null));
        Tag java = tagRepository.save(new Tag(null, "java", null));

        Post post1 = createPost("Spring Dúvida Menos Votada", 2, 0, user, spring);
        Post post2 = createPost("Spring Dúvida Mais Votada", 20, 1, user, spring);
        Post post3 = createPost("Java Dúvida", 50, 0, user, java);

        postRepository.saveAll(Set.of(post1, post2, post3));

        mockMvc.perform(get("/post")
                        .param("tag", "spring")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Spring Dúvida Mais Votada"))
                .andExpect(jsonPath("$[0].upVotes").value(20))
                .andExpect(jsonPath("$[1].title").value("Spring Dúvida Menos Votada"))
                .andExpect(jsonPath("$[1].upVotes").value(2));
    }

    @Test
    void shouldFilterPostsByCategoryAndOrderByMostVoted() throws Exception {
        User user = userRepository.findByEmail("aluno@qampus.com").orElseThrow();
        Tag java = tagRepository.save(new Tag(null, "java", null));

        Post post = createPost("Java dúvida mais votada", 30, 5, user, java);
        postRepository.save(post);

        mockMvc.perform(get("/post")
                        .param("category", "java")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Java dúvida mais votada"))
                .andExpect(jsonPath("$[0].upVotes").value(30));
    }

    @Test
    void shouldDenyGetPostsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/post"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldOrderPostsByVoteBalanceAndTieBreaking() throws Exception {
        User user = userRepository.findByEmail("aluno@qampus.com").orElseThrow();
        Tag tag = tagRepository.save(new Tag(null, "test", null));

        Post postA = createPost("Post A - mais upVotes", 12, 2, user, tag);
        Post postB = createPost("Post B - menos upVotes", 10, 0, user, tag);
        Post postC = createPost("Post C - menor saldo", 9, 1, user, tag);

        postRepository.saveAll(Set.of(postA, postB, postC));

        mockMvc.perform(get("/post")
                        .param("tag", "test")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title").value("Post A - mais upVotes"))
                .andExpect(jsonPath("$[0].upVotes").value(12))
                .andExpect(jsonPath("$[1].title").value("Post B - menos upVotes"))
                .andExpect(jsonPath("$[1].upVotes").value(10))
                .andExpect(jsonPath("$[2].title").value("Post C - menor saldo"))
                .andExpect(jsonPath("$[2].upVotes").value(9));
    }

    @Test
    void shouldOrderByMostRecentWhenVoteBalanceAndUpVotesAreEqual() throws Exception {
        User user = userRepository.findByEmail("aluno@qampus.com").orElseThrow();
        Tag tag = tagRepository.save(new Tag(null, "ordenacao", null));

        Post older = createPost("Post mais antigo", 10, 2, user, tag);
        Post newer = createPost("Post mais recente", 10, 2, user, tag);

        postRepository.save(older);
        postRepository.flush();

        postRepository.save(newer);
        postRepository.flush();

        mockMvc.perform(get("/post")
                        .param("tag", "ordenacao")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Post mais recente"))
                .andExpect(jsonPath("$[1].title").value("Post mais antigo"));
    }

    private Post createPost(
            String title,
            long upVotes,
            long downVotes,
            User user,
            Tag tag) {

        Post post = new Post();
        post.setTitle(title);
        post.setContent("Conteúdo");
        post.setUpVotes(upVotes);
        post.setDownVotes(downVotes);
        post.setUser(user);
        post.setTags(Set.of(tag));

        return post;
    }
}