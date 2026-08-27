package com.project.qampus.controllers;

import com.project.qampus.dto.AnnouncementDTO;
import com.project.qampus.dto.AnnouncementResponseDTO;
import com.project.qampus.model.Announcement;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.AnnouncementType;
import com.project.qampus.model.enums.Role;
import com.project.qampus.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementControllerTest {

    @Mock
    private AnnouncementService announcementService;

    @Mock
    private User user;

    @InjectMocks
    private AnnouncementController announcementController;

    private Announcement announcement;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setId("prof-1");
        author.setName("Professor Teste");
        author.setEmail("prof@ufape.edu.br");
        author.setRole(Role.PROFESSOR);

        announcement = new Announcement();
        announcement.setId("ann-1");
        announcement.setTitle("Palestra de IA");
        announcement.setDescription("Palestra aberta a todos os alunos");
        announcement.setType(AnnouncementType.EVENT);
        announcement.setPublicationDate(LocalDateTime.now());
        announcement.setAuthor(author);
    }

    @Test
    void shouldCreateAnnouncementSuccessfully() {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Palestra de IA",
                "Palestra aberta a todos os alunos",
                AnnouncementType.EVENT
        );

        when(announcementService.create(dto, user)).thenReturn(announcement);

        ResponseEntity<AnnouncementResponseDTO> response = announcementController.createAnnouncement(dto, user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Palestra de IA", response.getBody().title());
        assertEquals(AnnouncementType.EVENT, response.getBody().type());
        verify(announcementService).create(dto, user);
    }

    @Test
    void shouldGetAllAnnouncementsWithoutFilter() {
        when(announcementService.findAll(null)).thenReturn(List.of(announcement));

        ResponseEntity<List<AnnouncementResponseDTO>> response = announcementController.getAllAnnouncements(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Palestra de IA", response.getBody().getFirst().title());
        verify(announcementService).findAll(null);
    }

    @Test
    void shouldGetAllAnnouncementsWithTypeFilter() {
        when(announcementService.findAll(AnnouncementType.EVENT)).thenReturn(List.of(announcement));

        ResponseEntity<List<AnnouncementResponseDTO>> response =
                announcementController.getAllAnnouncements(AnnouncementType.EVENT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(announcementService).findAll(AnnouncementType.EVENT);
    }

    @Test
    void shouldGetAnnouncementByIdSuccessfully() {
        when(announcementService.findById("ann-1")).thenReturn(announcement);

        ResponseEntity<AnnouncementResponseDTO> response = announcementController.getAnnouncementById("ann-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ann-1", response.getBody().id());
        assertEquals("Palestra de IA", response.getBody().title());
        verify(announcementService).findById("ann-1");
    }

    @Test
    void shouldUpdateAnnouncementSuccessfully() {
        AnnouncementDTO updateDTO = new AnnouncementDTO(
                "Palestra de IA - Nova Data",
                "Descrição atualizada",
                AnnouncementType.EVENT
        );

        Announcement updated = new Announcement();
        updated.setId("ann-1");
        updated.setTitle(updateDTO.title());
        updated.setDescription(updateDTO.description());
        updated.setType(updateDTO.type());
        updated.setPublicationDate(announcement.getPublicationDate());
        updated.setAuthor(announcement.getAuthor());

        when(announcementService.update("ann-1", updateDTO, user)).thenReturn(updated);

        ResponseEntity<AnnouncementResponseDTO> response =
                announcementController.updateAnnouncement("ann-1", updateDTO, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Palestra de IA - Nova Data", response.getBody().title());
        verify(announcementService).update("ann-1", updateDTO, user);
    }

    @Test
    void shouldDeleteAnnouncementSuccessfully() {
        ResponseEntity<Void> response = announcementController.deleteAnnouncement("ann-1", user);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(announcementService).delete("ann-1", user);
    }
}
