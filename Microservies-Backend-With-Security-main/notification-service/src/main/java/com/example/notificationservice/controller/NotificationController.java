package com.example.notificationservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.notificationservice.dto.BookAppointmentAndCancelNotificationDto;
import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.dto.RescheduleNotificationDto;
import com.example.notificationservice.dto.SendReminderDto;
import com.example.notificationservice.entity.ResultResponse;
import com.example.notificationservice.exception.UnauthorizedAccessException;
import com.example.notificationservice.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/notification")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	// Admin Functionalities --> backend

	/**
	 * Retrieves all notifications.
	 *
	 * @return ResponseEntity containing a ResultResponse with a list of
	 *         NotificationDTOs.
	 */
	// getAllNotifications
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> findAll() {
		List<NotificationDto> notifications = notificationService.findAll();
		ResultResponse<List<NotificationDto>> response = ResultResponse.<List<NotificationDto>>builder()
				.data(notifications).success(true).message("Retrieved all notifications successfully")
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Retrieves a notification by its ID.
	 *
	 * @param notificationId The unique ID of the notification.
	 * @return ResponseEntity containing a ResultResponse with a NotificationDTO.
	 */
	// getNotificationById
	@GetMapping("/{notificationId}")
	public ResponseEntity<ResultResponse<NotificationDto>> findById(@Valid @PathVariable String notificationId) {
		NotificationDto notification = notificationService.findById(notificationId);
		ResultResponse<NotificationDto> response = ResultResponse.<NotificationDto>builder().data(notification)
				.success(true).message("Notification with given id is successfully retrieved")
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved notification with ID: {}", notificationId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Adds a new notification.
	 *
	 * @param notificationDto The NotificationDTO containing details of the new
	 *                        notification.
	 * @return ResponseEntity containing a ResultResponse with the added
	 *         NotificationDTO.
	 */
	// addNotification
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<NotificationDto>> addNotification(
			@Valid @RequestBody NotificationDto notificationDto) {
		log.debug("Received add notification request: {}", notificationDto); // Check incoming request data
		NotificationDto notification = notificationService.addNotification(notificationDto);
		ResultResponse<NotificationDto> response = ResultResponse.<NotificationDto>builder().data(notification)
				.success(true).message("Successfully added a notification to the table").timestamp(LocalDateTime.now())
				.build();
		log.info("Successfully added notification with ID: {}", notification.getNotificationId());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Updates an existing notification.
	 *
	 * @param notificationId  The unique ID of the notification to be updated.
	 * @param notificationDto The NotificationDTO containing updated notification
	 *                        details.
	 * @return ResponseEntity containing a ResultResponse with the updated
	 *         NotificationDTO.
	 */
	// updateNotificationById
	@PutMapping("/update/{notificationId}")
	public ResponseEntity<ResultResponse<NotificationDto>> updateNotification(
			@Valid @PathVariable String notificationId, @Valid @RequestBody NotificationDto notificationDto) {
		log.debug("Received update notification request: {}", notificationDto); // Check incoming request data
		NotificationDto notification = notificationService.updateNotification(notificationId, notificationDto);
		ResultResponse<NotificationDto> response = ResultResponse.<NotificationDto>builder().data(notification)
				.success(true).message("Successfully updated the notification").timestamp(LocalDateTime.now()).build();
		log.info("Successfully updated notification with ID: {}", notificationId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Deletes a notification by its ID.
	 *
	 * @param notificationId The unique ID of the notification to be deleted.
	 * @return ResponseEntity containing a ResultResponse with no content.
	 */
	// deleteNotificationById
	@DeleteMapping("/delete/{notificationId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String notificationId) {
		log.debug("Received delete notification request for ID: {}", notificationId); // Check incoming request data
		notificationService.deleteById(notificationId);
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null).success(true)
				.message("Successfully deleted notification with the given id").timestamp(LocalDateTime.now()).build();
		log.info("Successfully deleted notification with ID: {}", notificationId);
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}

	/**
	 * Deletes all notifications.
	 *
	 * @return ResponseEntity containing a ResultResponse with no content.
	 */
	// deleteAllNotifications
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllNotification() {
		log.debug("Received delete all notifications request"); // Check incoming request data
		notificationService.deleteAllNotifications();
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null).success(true)
				.message("Successfully deleted all notifications").timestamp(LocalDateTime.now()).build();
		log.info("Successfully deleted all notifications.");
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}

	// ----------------------------------------------------------------------------------------------------------------------------

	// User functionalities --> frontend

	/**
	 * Inserts notifications for a booked appointment.
	 *
	 * @param bookAndCancelNotificationDto The DTO containing details of the booked
	 *                                     appointment.
	 * @return ResponseEntity containing a ResultResponse confirming successful
	 *         insertion.
	 */
	// bookAppointmentNotificationInsertion
	@PostMapping("/bookAppointmentInsertion")
	public ResponseEntity<ResultResponse<String>> insertNotificationsForBookAppointment(
			@Valid @RequestBody BookAppointmentAndCancelNotificationDto bookAndCancelNotificationDto) {
		log.debug("Received BookAndCancellationDTO");
		notificationService.insertNotificationsForBookAppointment(bookAndCancelNotificationDto);
		ResultResponse<String> response = ResultResponse.<String>builder().data(null).success(true)
				.message("Successfully inserted notifications for book appointment").timestamp(LocalDateTime.now())
				.build();
		log.info("Successfully inserted notifications for book appointment");
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	/**
	 * Inserts notifications for a rescheduled appointment.
	 *
	 * @param rescheduleNotificationDto The DTO containing details of the
	 *                                  rescheduled appointment.
	 * @return ResponseEntity containing a ResultResponse confirming successful
	 *         insertion.
	 */
	// rescheduleAppointmentNotificationInsertion
	@PostMapping("/rescheduleAppointmentInsertion")
	public ResponseEntity<ResultResponse<String>> insertNotificationsForRescheduleAppointment(
			@Valid @RequestBody RescheduleNotificationDto rescheduleNotificationDto) {
		log.debug("Received RescheduleNotificationDto");
		notificationService.insertNotificationsForRescheduleAppointment(rescheduleNotificationDto);
		ResultResponse<String> response = ResultResponse.<String>builder().data(null).success(true)
				.message("Successfully inserted notifications for reschedule appointment")
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully inserted notifications for reschedule appointment");
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	/**
	 * Inserts notifications for a cancelled appointment.
	 *
	 * @param bookAndCancelNotificationDto The DTO containing details of the
	 *                                     cancelled appointment.
	 * @return ResponseEntity containing a ResultResponse confirming successful
	 *         insertion.
	 */
	// cancelAppointmentNotificationInsertion
	@PostMapping("/cancelAppointmentInsertion")
	public ResponseEntity<ResultResponse<Void>> insertNotificationsForCancelAppointment(
			@Valid @RequestBody BookAppointmentAndCancelNotificationDto bookAndCancelNotificationDto) {
		log.debug("Received BookAndCancellationDTO");
		notificationService.insertNotificationsForCancelAppointment(bookAndCancelNotificationDto);
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null).success(true)
				.message("Successfully inserted notifications for cancel appointment").timestamp(LocalDateTime.now())
				.build();
		log.info("Successfully inserted notifications for cancel appointment");
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------

	// Scheduler --> automation

	/**
	 * Retrieves today's appointments.
	 *
	 * @return A list of SendReminderDTO instances representing today's
	 *         appointments.
	 */
	// getTodayAppointments
	@GetMapping("/getTodayAppointments")
	public ResponseEntity<ResultResponse<List<SendReminderDto>>> getTodayAppointments() {
		List<SendReminderDto> appointments = notificationService.getTodayAppointments();

		ResultResponse<List<SendReminderDto>> response = ResultResponse.<List<SendReminderDto>>builder()
				.data(appointments).success(true).message("Successfully retrieved today's appointments")
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved today's appointments");

		return new ResponseEntity<>(response, HttpStatus.OK);
		// return new ResponseEntity<>(response,HttpStatus.FOUND);

	}

	/**
	 * Sends reminders for today's appointments.
	 *
	 * @return ResponseEntity containing a ResultResponse confirming successful
	 *         execution.
	 */
	// sendReminders
	@GetMapping("/sendReminders")
	public ResponseEntity<ResultResponse<String>> sendAppointmentReminders() {
		log.debug("Invoked sendAppointmentReminders scheduler manually");
		notificationService.sendAppointmentReminders();
		ResultResponse<String> response = ResultResponse.<String>builder().data(null).success(true)
				.message("Successfully inserted notifications for upcoming appointment").timestamp(LocalDateTime.now())
				.build();
		log.info("Successfully inserted notifications for upcoming appointment");
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	// -----------------------------------------------------------------------------------------------------------------------------------------

	// getPatientNotificationsByPatientId
	@GetMapping("/patientProfile/{patientId}")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getPatientNotificationsByPatientId(
			@RequestAttribute("userId") String userId,
			@Valid @PathVariable String patientId) {
		
		// Ensure the logged-in user can only update their own patient details
	    if (!userId.equals(patientId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
	        throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
	    }
	    
		List<NotificationDto> notifications = notificationService.getPatientNotificationsByPatientId(patientId);
		ResultResponse<List<NotificationDto>> response = ResultResponse.<List<NotificationDto>>builder()
				.data(notifications).message("Notifications displayed on Patient Dashboard successfully").success(true)
				.timestamp(LocalDateTime.now()).build();
		log.info("Notifications displayed on patient dashboard successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// getDoctorNotificationsByDoctorId
	@GetMapping("/doctorProfile/{doctorId}")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getDoctorNotificationsByDoctorId(
			@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId) {
		
		// Ensure the logged-in user can only update their own doctor details
	    if (!userId.equals(doctorId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
	        throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
	    }
	    
		List<NotificationDto> notifications = notificationService.getPatientNotificationsByPatientId(doctorId);
		ResultResponse<List<NotificationDto>> response = ResultResponse.<List<NotificationDto>>builder()
				.data(notifications).message("Notifications displayed on Doctor Dashboard successfully").success(true)
				.timestamp(LocalDateTime.now()).build();
		log.info("Notifications displayed on doctor dashboard successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------

	// markNotificationAsRead
	@PutMapping("/markAsRead/{notificationId}")
	public ResponseEntity<ResultResponse<Void>> markNotificationAsRead(@Valid @PathVariable String notificationId) {
		notificationService.markNotificationAsCompleted(notificationId);
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null)
				.message("Notification successfully marked as read").success(true).timestamp(LocalDateTime.now())
				.build();
		log.info("Notification successfully marked as read");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	// markAllNotificatoinsAsRead
	@PutMapping("/markAllAsRead/{userId}")
	public ResponseEntity<ResultResponse<Void>> markAllNotificationsAsRead(@Valid @PathVariable String userId) {
		notificationService.markAllNotificationsAsRead(userId);
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null)
				.message("All notifications are successfully marked as read").success(true)
				.timestamp(LocalDateTime.now())
				.build();
		log.info("All notifications are successfully marked as read");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
