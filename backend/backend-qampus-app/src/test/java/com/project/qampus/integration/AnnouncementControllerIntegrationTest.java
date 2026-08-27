package com.project.qampus.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qampus.dto.AnnouncementDTO;
import com.project.qampus.dto.RegisterRequestDTO;
import com.project.qampus.model.Announcement;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.AnnouncementType;
import com.project.qampus.model.enums.Role;
import com.project.qampus.repositories.AnnouncementRepository;
import com.project.qampus.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnnouncementControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    private String professorToken;
    private String otherProfessorToken;
    private String studentToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        announcementRepository.deleteAll();
        userRepository.deleteAll();

        professorToken = registerAndGetToken("Prof 1", "prof1@qampus.com", "senha123", Role.PROFESSOR);
        otherProfessorToken = registerAndGetToken("Prof 2", "prof2@qampus.com", "senha123", Role.PROFESSOR);
        studentToken = registerAndGetToken("Aluno", "aluno@qampus.com", "senha123", Role.STUDENT);
    }

    private String registerAndGetToken(String name, String email, String password, Role role) throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO(name, email, password, role);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(
                result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void shouldAllowProfessorToCreateAnnouncement() throws Exception {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Monitoria de Banco de Dados",
                "Inscrições abertas até sexta-feira.",
                AnnouncementType.ACADEMIC
        );

        mockMvc.perform(post("/announcements")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Monitoria de Banco de Dados"))
                .andExpect(jsonPath("$.type").value("ACADEMIC"))
                .andExpect(jsonPath("$.author.name").value("Prof 1"));

        assertEquals(1, announcementRepository.count());
    }

    @Test
    void shouldDenyStudentFromCreatingAnnouncement() throws Exception {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Tentativa de anúncio por aluno",
                "Descrição",
                AnnouncementType.NOTICE
        );

        mockMvc.perform(post("/announcements")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        assertEquals(0, announcementRepository.count());
    }

    @Test
    void shouldDenyUnauthenticatedUserFromCreatingAnnouncement() throws Exception {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Anúncio Anônimo",
                "Sem token",
                AnnouncementType.NOTICE
        );

        mockMvc.perform(post("/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldListAnnouncementsForAuthenticatedUsers() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann1 = new Announcement();
        ann1.setTitle("Palestra 1");
        ann1.setDescription("Descrição 1");
        ann1.setType(AnnouncementType.EVENT);
        ann1.setAuthor(professor);

        Announcement ann2 = new Announcement();
        ann2.setTitle("Projeto de Extensão");
        ann2.setDescription("Descrição 2");
        ann2.setType(AnnouncementType.PROJECT);
        ann2.setAuthor(professor);

        announcementRepository.save(ann1);
        announcementRepository.save(ann2);

        mockMvc.perform(get("/announcements")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldFilterAnnouncementsByType() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann1 = new Announcement();
        ann1.setTitle("Palestra 1");
        ann1.setDescription("Descrição 1");
        ann1.setType(AnnouncementType.EVENT);
        ann1.setAuthor(professor);

        Announcement ann2 = new Announcement();
        ann2.setTitle("Projeto de Extensão");
        ann2.setDescription("Descrição 2");
        ann2.setType(AnnouncementType.PROJECT);
        ann2.setAuthor(professor);

        announcementRepository.save(ann1);
        announcementRepository.save(ann2);

        mockMvc.perform(get("/announcements")
                        .param("type", "EVENT")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Palestra 1"));
    }

    @Test
    void shouldGetAnnouncementById() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann = new Announcement();
        ann.setTitle("Aviso Urgente");
        ann.setDescription("Conteúdo do aviso");
        ann.setType(AnnouncementType.NOTICE);
        ann.setAuthor(professor);

        Announcement saved = announcementRepository.save(ann);

        mockMvc.perform(get("/announcements/" + saved.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Aviso Urgente"));
    }

    @Test
    void shouldReturnNotFoundWhenAnnouncementDoesNotExist() throws Exception {
        mockMvc.perform(get("/announcements/id-inexistente")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowAuthorToUpdateAnnouncement() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann = new Announcement();
        ann.setTitle("Título Antigo");
        ann.setDescription("Descrição Antiga");
        ann.setType(AnnouncementType.ACADEMIC);
        ann.setAuthor(professor);

        Announcement saved = announcementRepository.save(ann);

        AnnouncementDTO updateDto = new AnnouncementDTO(
                "Título Atualizado",
                "Descrição Atualizada",
                AnnouncementType.NOTICE
        );

        mockMvc.perform(put("/announcements/" + saved.getId())
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título Atualizado"))
                .andExpect(jsonPath("$.type").value("NOTICE"));
    }

    @Test
    void shouldDenyNonAuthorProfessorFromUpdatingAnnouncement() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann = new Announcement();
        ann.setTitle("Anúncio do Prof 1");
        ann.setDescription("Descrição");
        ann.setType(AnnouncementType.ACADEMIC);
        ann.setAuthor(professor);

        Announcement saved = announcementRepository.save(ann);

        AnnouncementDTO updateDto = new AnnouncementDTO(
                "Tentativa de edição",
                "Descrição",
                AnnouncementType.ACADEMIC
        );

        mockMvc.perform(put("/announcements/" + saved.getId())
                        .header("Authorization", "Bearer " + otherProfessorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAuthorToDeleteAnnouncement() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann = new Announcement();
        ann.setTitle("Anúncio a ser removido");
        ann.setDescription("Descrição");
        ann.setType(AnnouncementType.NOTICE);
        ann.setAuthor(professor);

        Announcement saved = announcementRepository.save(ann);

        mockMvc.perform(delete("/announcements/" + saved.getId())
                        .header("Authorization", "Bearer " + professorToken))
                .andExpect(status().isNoContent());

        assertEquals(0, announcementRepository.count());
    }

    @Test
    void shouldDenyNonAuthorProfessorFromDeletingAnnouncement() throws Exception {
        User professor = userRepository.findByEmail("prof1@qampus.com").orElseThrow();

        Announcement ann = new Announcement();
        ann.setTitle("Anúncio do Prof 1");
        ann.setDescription("Descrição");
        ann.setType(AnnouncementType.NOTICE);
        ann.setAuthor(professor);

        Announcement saved = announcementRepository.save(ann);

        mockMvc.perform(delete("/announcements/" + saved.getId())
                        .header("Authorization", "Bearer " + otherProfessorToken))
                .andExpect(status().isForbidden());

        assertEquals(1, announcementRepository.count());
    }
}
