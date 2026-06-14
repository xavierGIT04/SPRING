package com.ipnet.rentalapi.Gbails.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipnet.rentalapi.Gbails.models.Notification;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {
    Optional<Notification> findByUuid(UUID uuid);
    List<Notification> findByLocataireUuidAndEstLuFalse(UUID locataireUuid);
}
