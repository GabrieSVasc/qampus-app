package com.project.qampus.dto;

import com.project.qampus.model.enums.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnnouncementDTO(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O tipo do anúncio é obrigatório")
        AnnouncementType type
) {
}
