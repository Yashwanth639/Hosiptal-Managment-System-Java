package com.example.notificationservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.enums.NotificationStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
	
	List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);

}
