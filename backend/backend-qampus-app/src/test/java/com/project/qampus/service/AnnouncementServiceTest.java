package com.project.qampus.service;

import com.project.qampus.dto.AnnouncementDTO;
import com.project.qampus.model.Announcement;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.AnnouncementType;
import com.project.qampus.model.enums.Role;
import com.project.qampus.repositories.AnnouncementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    private User professor;
    private User otherProfessor;
    private User student;
    private Announcement announcement;

    @BeforeEach
    void setUp() {
        professor = new User();
        professor.setId("prof-1");
        professor.setName("Prof. Carlos");
        professor.setEmail("carlos@ufape.edu.br");
        professor.setRole(Role.PROFESSOR);

        otherProfessor = new User();
        otherProfessor.setId("prof-2");
        otherProfessor.setName("Prof. Ana");
        otherProfessor.setEmail("ana@ufape.edu.br");
        otherProfessor.setRole(Role.PROFESSOR);

        student = new User();
        student.setId("student-1");
        student.setName("Aluno João");
        student.setEmail("joao@ufape.edu.br");
        student.setRole(Role.STUDENT);

        announcement = new Announcement();
        announcement.setId("ann-1");
        announcement.setTitle("Inscrições para Monitoria");
        announcement.setDescription("Estão abertas as inscrições.");
        announcement.setType(AnnouncementType.ACADEMIC);
        announcement.setPublicationDate(LocalDateTime.now());
        announcement.setAuthor(professor);
    }

    @Test
    void shouldCreateAnnouncementSuccessfullyWhenUserIsProfessor() {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Monitoria",
                "Descrição da monitoria",
                AnnouncementType.ACADEMIC
        );

        when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> {
            Announcement a = invocation.getArgument(0);
            a.setId("ann-generated");
            return a;
        });

        Announcement result = announcementService.create(dto, professor);

        assertNotNull(result);
        assertEquals("Monitoria", result.getTitle());
        assertEquals("Descrição da monitoria", result.getDescription());
        assertEquals(AnnouncementType.ACADEMIC, result.getType());
        assertEquals(professor, result.getAuthor());

        verify(announcementRepository).save(any(Announcement.class));
    }

    @Test
    void shouldThrowForbiddenWhenUserIsNotProfessorOnCreate() {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Monitoria",
                "Descrição",
                AnnouncementType.ACADEMIC
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.create(dto, student)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals("Apenas professores podem publicar anúncios", ex.getReason());
        verify(announcementRepository, never()).save(any());
    }

    @Test
    void shouldThrowForbiddenWhenUserIsNullOnCreate() {
        AnnouncementDTO dto = new AnnouncementDTO(
                "Monitoria",
                "Descrição",
                AnnouncementType.ACADEMIC
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.create(dto, null)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(announcementRepository, never()).save(any());
    }

    @Test
    void shouldFindAllAnnouncementsWithoutTypeFilter() {
        when(announcementRepository.findAllByOrderByPublicationDateDesc())
                .thenReturn(List.of(announcement));

        List<Announcement> result = announcementService.findAll(null);

        assertEquals(1, result.size());
        assertEquals(announcement, result.getFirst());
        verify(announcementRepository).findAllByOrderByPublicationDateDesc();
        verify(announcementRepository, never()).findByTypeOrderByPublicationDateDesc(any());
    }

    @Test
    void shouldFindAllAnnouncementsWithTypeFilter() {
        when(announcementRepository.findByTypeOrderByPublicationDateDesc(AnnouncementType.ACADEMIC))
                .thenReturn(List.of(announcement));

        List<Announcement> result = announcementService.findAll(AnnouncementType.ACADEMIC);

        assertEquals(1, result.size());
        assertEquals(announcement, result.getFirst());
        verify(announcementRepository).findByTypeOrderByPublicationDateDesc(AnnouncementType.ACADEMIC);
        verify(announcementRepository, never()).findAllByOrderByPublicationDateDesc();
    }

    @Test
    void shouldFindAnnouncementByIdSuccessfully() {
        when(announcementRepository.findById("ann-1")).thenReturn(Optional.of(announcement));

        Announcement result = announcementService.findById("ann-1");

        assertNotNull(result);
        assertEquals("ann-1", result.getId());
        assertEquals("Inscrições para Monitoria", result.getTitle());
    }

    @Test
    void shouldThrowNotFoundWhenAnnouncementDoesNotExistOnFindById() {
        when(announcementRepository.findById("inexistente")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.findById("inexistente")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Anúncio não encontrado", ex.getReason());
    }

    @Test
    void shouldUpdateAnnouncementSuccessfullyWhenUserIsAuthor() {
        AnnouncementDTO updateDTO = new AnnouncementDTO(
                "Título Editado",
                "Descrição Editada",
                AnnouncementType.EVENT
        );

        when(announcementRepository.findById("ann-1")).thenReturn(Optional.of(announcement));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(i -> i.getArgument(0));

        Announcement updated = announcementService.update("ann-1", updateDTO, professor);

        assertNotNull(updated);
        assertEquals("Título Editado", updated.getTitle());
        assertEquals("Descrição Editada", updated.getDescription());
        assertEquals(AnnouncementType.EVENT, updated.getType());

        verify(announcementRepository).save(announcement);
    }

    @Test
    void shouldThrowForbiddenWhenUserIsNotAuthorOnUpdate() {
        AnnouncementDTO updateDTO = new AnnouncementDTO(
                "Título Editado",
                "Descrição Editada",
                AnnouncementType.EVENT
        );

        when(announcementRepository.findById("ann-1")).thenReturn(Optional.of(announcement));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.update("ann-1", updateDTO, otherProfessor)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals("Você não tem permissão para alterar este anúncio", ex.getReason());
        verify(announcementRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenAnnouncementDoesNotExistOnUpdate() {
        AnnouncementDTO updateDTO = new AnnouncementDTO(
                "Título Editado",
                "Descrição Editada",
                AnnouncementType.EVENT
        );

        when(announcementRepository.findById("ann-999")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.update("ann-999", updateDTO, professor)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void shouldDeleteAnnouncementSuccessfullyWhenUserIsAuthor() {
        when(announcementRepository.findById("ann-1")).thenReturn(Optional.of(announcement));

        announcementService.delete("ann-1", professor);

        verify(announcementRepository).delete(announcement);
    }

    @Test
    void shouldThrowForbiddenWhenUserIsNotAuthorOnDelete() {
        when(announcementRepository.findById("ann-1")).thenReturn(Optional.of(announcement));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.delete("ann-1", otherProfessor)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals("Você não tem permissão para alterar este anúncio", ex.getReason());
        verify(announcementRepository, never()).delete(any());
    }

    @Test
    void shouldThrowNotFoundWhenAnnouncementDoesNotExistOnDelete() {
        when(announcementRepository.findById("ann-999")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                announcementService.delete("ann-999", professor)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(announcementRepository, never()).delete(any());
    }
}
