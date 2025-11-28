package com.example.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.notificationservice.client.AppointmentClient;
import com.example.notificationservice.dto.BookAppointmentAndCancelNotificationDto;
import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.dto.RescheduleNotificationDto;
import com.example.notificationservice.dto.SendReminderDto;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.enums.Session;
import com.example.notificationservice.exception.CustomException;
import com.example.notificationservice.exception.NotificationInsertionException;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@SpringBootTest
@ExtendWith(MockitoExtension.class)
class NotificationServiceApplicationTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AppointmentClient appointmentClient;

    @InjectMocks
    private NotificationService notificationService;
    
    @Test
    void insertNotificationsForBookAppointment_shouldSuccessfullyInsertNotificationsUsingBuilder() {
        // Arrange
        BookAppointmentAndCancelNotificationDto dto = BookAppointmentAndCancelNotificationDto.builder()
                .appointmentId(UUID.randomUUID().toString())
                .patientId(UUID.randomUUID().toString())
                .doctorId(UUID.randomUUID().toString())
                .appointmentDate(LocalDate.now())
                .session(Session.FN)
                .doctorName("Dr. Smith")
                .patientName("John Doe")
                .build();

        ArgumentCaptor<Notification> patientNotificationCaptor = ArgumentCaptor.forClass(Notification.class);
        ArgumentCaptor<Notification> doctorNotificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // Act
        notificationService.insertNotificationsForBookAppointment(dto);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(notificationRepository).save(patientNotificationCaptor.capture());
        verify(notificationRepository).save(doctorNotificationCaptor.capture());

        Notification patientNotification = patientNotificationCaptor.getValue();
        assertEquals(dto.getPatientId(), patientNotification.getUserId());
        assertEquals(dto.getAppointmentId(), patientNotification.getAppointmentId());
        assertEquals(NotificationType.Appointment, patientNotification.getNotificationType());
        assertEquals(NotificationStatus.Pending, patientNotification.getStatus());
        assertTrue(patientNotification.getMessage().contains("Your appointment with Dr. Smith has been booked"));

        Notification doctorNotification = doctorNotificationCaptor.getValue();
        assertEquals(dto.getDoctorId(), doctorNotification.getUserId());
        assertEquals(dto.getAppointmentId(), doctorNotification.getAppointmentId());
        assertEquals(NotificationType.Appointment, doctorNotification.getNotificationType());
        assertEquals(NotificationStatus.Pending, doctorNotification.getStatus());
        assertTrue(doctorNotification.getMessage().contains("An appointment with patient John Doe has been booked"));
    }
    
    @Test
    void insertNotificationsForBookAppointment_shouldThrowExceptionOnRepositoryFailureUsingBuilder() {
        // Arrange
        BookAppointmentAndCancelNotificationDto dto = BookAppointmentAndCancelNotificationDto.builder()
                .appointmentId(UUID.randomUUID().toString())
                .patientId(UUID.randomUUID().toString())
                .doctorId(UUID.randomUUID().toString())
                .appointmentDate(LocalDate.now())
                .session(Session.FN)
                .doctorName("Dr. Smith")
                .patientName("John Doe")
                .build();
        when(notificationRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(NotificationInsertionException.class, () -> notificationService.insertNotificationsForBookAppointment(dto));
        verify(notificationRepository, times(1)).save(any(Notification.class)); 
    }
    
    // -----------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    void insertNotificationsForRescheduleAppointment_shouldSuccessfullyInsertNotifications() {
        // Arrange
        RescheduleNotificationDto dto = RescheduleNotificationDto.builder()
                .appointmentId(UUID.randomUUID().toString())
                .patientId(UUID.randomUUID().toString())
                .doctorId(UUID.randomUUID().toString())
                .oldDate(LocalDate.now().minusDays(1))
                .oldSession(Session.AN)
                .newDate(LocalDate.now())
                .newSession(Session.FN)
                .patientName("Jane Doe")
                .build();

        ArgumentCaptor<Notification> patientNotificationCaptor = ArgumentCaptor.forClass(Notification.class);
        ArgumentCaptor<Notification> doctorNotificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // Act
        notificationService.insertNotificationsForRescheduleAppointment(dto);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(notificationRepository).save(patientNotificationCaptor.capture());
        verify(notificationRepository).save(doctorNotificationCaptor.capture());

        Notification patientNotification = patientNotificationCaptor.getValue();
        assertEquals(dto.getPatientId(), patientNotification.getUserId());
        assertEquals(dto.getAppointmentId(), patientNotification.getAppointmentId());
        assertEquals(NotificationType.Reschedule, patientNotification.getNotificationType());
        assertEquals(NotificationStatus.Pending, patientNotification.getStatus());
        assertTrue(patientNotification.getMessage().contains("Your appointment has been rescheduled from"));
        assertTrue(patientNotification.getMessage().contains(dto.getOldDate().toString()));
        assertTrue(patientNotification.getMessage().equals(dto.getOldSession()));
        assertTrue(patientNotification.getMessage().contains(dto.getNewDate().toString()));
        assertTrue(patientNotification.getMessage().equals(dto.getNewSession()));

        Notification doctorNotification = doctorNotificationCaptor.getValue();
        assertEquals(dto.getDoctorId(), doctorNotification.getUserId());
        assertEquals(dto.getAppointmentId(), doctorNotification.getAppointmentId());
        assertEquals(NotificationType.Reschedule, doctorNotification.getNotificationType());
        assertEquals(NotificationStatus.Pending, doctorNotification.getStatus());
        assertTrue(doctorNotification.getMessage().contains("The appointment with patient Jane Doe has been rescheduled from"));
        assertTrue(doctorNotification.getMessage().contains(dto.getOldDate().toString()));
        assertTrue(doctorNotification.getMessage().equals(dto.getOldSession()));
        assertTrue(doctorNotification.getMessage().contains(dto.getNewDate().toString()));
        assertTrue(doctorNotification.getMessage().equals(dto.getNewSession()));
    }
    
    @Test
    void insertNotificationsForRescheduleAppointment_shouldThrowExceptionOnRepositoryFailure() {
        // Arrange
        RescheduleNotificationDto dto = RescheduleNotificationDto.builder()
                .appointmentId(UUID.randomUUID().toString())
                .patientId(UUID.randomUUID().toString())
                .doctorId(UUID.randomUUID().toString())
                .oldDate(LocalDate.now().minusDays(1))
                .oldSession(Session.AN)
                .newDate(LocalDate.now())
                .newSession(Session.FN)
                .patientName("Jane Doe")
                .build();
        when(notificationRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(NotificationInsertionException.class, () -> notificationService.insertNotificationsForRescheduleAppointment(dto));
        verify(notificationRepository, times(1)).save(any(Notification.class)); 
    }
    
    // -------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void insertNotificationsForCancelAppointment_shouldSuccessfullyInsertNotifications() {
        // Arrange
        BookAppointmentAndCancelNotificationDto dto = BookAppointmentAndCancelNotificationDto.builder()
                .appointmentId(UUID.randomUUID().toString())
                .patientId(UUID.randomUUID().toString())
                .doctorId(UUID.randomUUID().toString())
                .appointmentDate(LocalDate.now())
                .session(Session.AN)
                .doctorName("Dr. Lee")
                .patientName("Peter Pan")
                .build();

        ArgumentCaptor<Notification> patientNotificationCaptor = ArgumentCaptor.forClass(Notification.class);
        ArgumentCaptor<Notification> doctorNotificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // Act
        notificationService.insertNotificationsForCancelAppointment(dto);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(notificationRepository).save(patientNotificationCaptor.capture());
        verify(notificationRepository).save(doctorNotificationCaptor.capture());

        Notification patientNotification = patientNotificationCaptor.getValue();
        assertEquals(dto.getPatientId(), patientNotification.getUserId());
        assertEquals(dto.getAppointmentId(), patientNotification.getAppointmentId());
        assertEquals(NotificationType.Cancellation, patientNotification.getNotificationType());
        assertEquals(NotificationStatus.Pending, patientNotification.getStatus());
        assertTrue(patientNotification.getMessage().contains("Your appointment with Dr. Lee on"));
        assertTrue(patientNotification.getMessage().contains(dto.getAppointmentDate().toString()));
        assertTrue(patientNotification.getMessage().equals(dto.getSession()));
        assertTrue(patientNotification.getMessage().contains("has been cancelled"));

        Notification doctorNotification = doctorNotificationCaptor.getValue();
        assertEquals(dto.getDoctorId(), doctorNotification.getUserId());
        assertEquals(dto.getAppointmentId(), doctorNotification.getAppointmentId());
        assertEquals(NotificationType.Cancellation, doctorNotification.getNotificationType());
        assertEquals(NotificationStatus.Pending, doctorNotification.getStatus());
        assertTrue(doctorNotification.getMessage().contains("The appointment with patient Peter Pan has been cancelled"));
    }
    
    @Test
    void insertNotificationsForCancelAppointment_shouldThrowExceptionOnRepositoryFailure() {
        // Arrange
        BookAppointmentAndCancelNotificationDto dto = BookAppointmentAndCancelNotificationDto.builder()
                .appointmentId(UUID.randomUUID().toString())
                .patientId(UUID.randomUUID().toString())
                .doctorId(UUID.randomUUID().toString())
                .appointmentDate(LocalDate.now())
                .session(Session.AN)
                .doctorName("Dr. Lee")
                .patientName("Peter Pan")
                .build();
        when(notificationRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(NotificationInsertionException.class, () -> notificationService.insertNotificationsForCancelAppointment(dto));
        verify(notificationRepository, times(1)).save(any(Notification.class)); 
    }
    
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
    
    
}