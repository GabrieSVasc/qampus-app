package com.project.qampus.service;

import com.project.qampus.dto.AnnouncementDTO;
import com.project.qampus.model.Announcement;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.AnnouncementType;
import com.project.qampus.model.enums.Role;
import com.project.qampus.repositories.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private static final String ANNOUNCEMENT_NOT_FOUND = "Anúncio não encontrado";
    private static final String FORBIDDEN_ANNOUNCEMENT_MODIFICATION = "Você não tem permissão para alterar este anúncio";
    private static final String ONLY_PROFESSORS_ALLOWED = "Apenas professores podem publicar anúncios";

    private final AnnouncementRepository announcementRepository;

    public Announcement create(AnnouncementDTO dto, User user) {
        if (user == null || user.getRole() != Role.PROFESSOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ONLY_PROFESSORS_ALLOWED);
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(dto.title());
        announcement.setDescription(dto.description());
        announcement.setType(dto.type());
        announcement.setAuthor(user);

        return announcementRepository.save(announcement);
    }

    public List<Announcement> findAll(AnnouncementType type) {
        if (type != null) {
            return announcementRepository.findByTypeOrderByPublicationDateDesc(type);
        }
        return announcementRepository.findAllByOrderByPublicationDateDesc();
    }

    public Announcement findById(String id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ANNOUNCEMENT_NOT_FOUND));
    }

    public Announcement update(String id, AnnouncementDTO dto, User user) {
        Announcement announcement = findById(id);

        validateAuthor(announcement, user);

        announcement.setTitle(dto.title());
        announcement.setDescription(dto.description());
        announcement.setType(dto.type());

        return announcementRepository.save(announcement);
    }

    public void delete(String id, User user) {
        Announcement announcement = findById(id);

        validateAuthor(announcement, user);

        announcementRepository.delete(announcement);
    }

    private void validateAuthor(Announcement announcement, User user) {
        if (user == null || !announcement.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, FORBIDDEN_ANNOUNCEMENT_MODIFICATION);
        }
    }
}
