package com.project.qampus.dto;

import com.project.qampus.model.Announcement;
import com.project.qampus.model.enums.AnnouncementType;

import java.time.LocalDateTime;

public record AnnouncementResponseDTO(
        String id,
        String title,
        String description,
        AnnouncementType type,
        LocalDateTime publicationDate,
        UserResponseDTO author
) {
    public static AnnouncementResponseDTO from(Announcement announcement) {
        return new AnnouncementResponseDTO(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getDescription(),
                announcement.getType(),
                announcement.getPublicationDate(),
                announcement.getAuthor() != null ? UserResponseDTO.from(announcement.getAuthor()) : null
        );
    }
}
