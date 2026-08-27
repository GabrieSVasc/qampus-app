package com.project.qampus.controllers;

import com.project.qampus.dto.AnnouncementDTO;
import com.project.qampus.dto.AnnouncementResponseDTO;
import com.project.qampus.model.Announcement;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.AnnouncementType;
import com.project.qampus.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/announcements", "/api/announcements"})
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<AnnouncementResponseDTO> createAnnouncement(
            @Valid @RequestBody AnnouncementDTO body,
            @AuthenticationPrincipal User user) {

        Announcement saved = announcementService.create(body, user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AnnouncementResponseDTO.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponseDTO>> getAllAnnouncements(
            @RequestParam(name = "type", required = false) AnnouncementType type) {

        List<AnnouncementResponseDTO> response = announcementService.findAll(type).stream()
                .map(AnnouncementResponseDTO::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponseDTO> getAnnouncementById(@PathVariable String id) {
        Announcement announcement = announcementService.findById(id);
        return ResponseEntity.ok(AnnouncementResponseDTO.from(announcement));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<AnnouncementResponseDTO> updateAnnouncement(
            @PathVariable String id,
            @Valid @RequestBody AnnouncementDTO body,
            @AuthenticationPrincipal User user) {

        Announcement updated = announcementService.update(id, body, user);
        return ResponseEntity.ok(AnnouncementResponseDTO.from(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        announcementService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
