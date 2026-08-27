package com.project.qampus.repositories;

import com.project.qampus.model.Announcement;
import com.project.qampus.model.enums.AnnouncementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, String> {

    List<Announcement> findAllByOrderByPublicationDateDesc();

    List<Announcement> findByTypeOrderByPublicationDateDesc(AnnouncementType type);

    List<Announcement> findByAuthorIdOrderByPublicationDateDesc(String authorId);
}
