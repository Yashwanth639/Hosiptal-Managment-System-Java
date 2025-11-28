package com.example.notificationservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.notificationservice.client.AppointmentClient;

import com.example.notificationservice.dto.BookAppointmentAndCancelNotificationDto;
import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.dto.RescheduleNotificationDto;
import com.example.notificationservice.dto.SendReminderDto;
import com.example.notificationservice.entity.Notification;

import com.example.notificationservice.enums.AppointmentStatus;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.enums.Session;
import com.example.notificationservice.exception.CustomException;
import com.example.notificationservice.exception.NotificationInsertionException;
import com.example.notificationservice.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private AppointmentClient appointmentClient;

	// private helper methods

	/**
	 * Converts a Notification entity into a NotificationDTO.
	 * 
	 * @param notification The Notification entity to be converted.
	 * @return A NotificationDTO populated with data from the Notification entity.
	 */
	// convertEntityToDTO
	private NotificationDto convertToDTO(Notification notification) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setCreatedAt(notification.getCreatedAt());
		notificationDto.setUpdatedAt(notification.getUpdatedAt());
		notificationDto.setNotificationId(notification.getNotificationId());
		notificationDto.setAppointmentId(notification.getAppointmentId());
		notificationDto.setUserId(notification.getUserId());
		notificationDto.setNotificationType(notification.getNotificationType());
		notificationDto.setStatus(notification.getStatus());
		notificationDto.setMessage(notification.getMessage());
		notificationDto.setTimestamp(notification.getTimeStamp());
		return notificationDto;
	}

	/**
	 * Converts a NotificationDTO into a Notification entity.
	 * 
	 * @param notificationDto The NotificationDTO to be converted.
	 * @return A Notification entity populated with data from the NotificationDTO.
	 */
	// convertDTOToEntity
	private Notification convertToEntity(NotificationDto notificationDto) {
		Notification notification = new Notification();
		notification.setCreatedAt(notificationDto.getCreatedAt());
		notification.setUpdatedAt(notificationDto.getUpdatedAt());
		notification.setNotificationId(notificationDto.getNotificationId());
		notification.setAppointmentId(notificationDto.getAppointmentId());
		notification.setUserId(notificationDto.getUserId());
		notification.setNotificationType(notificationDto.getNotificationType());
		notification.setStatus(notificationDto.getStatus());
		notification.setMessage(notificationDto.getMessage());
		notification.setTimeStamp(notificationDto.getTimestamp());
		return notification;
	}

	// Admin functionalities --> backend

	/**
	 * Retrieves all notifications from the repository, converts them into
	 * NotificationDTOs, and returns them as a list.
	 * 
	 * @return A list of NotificationDTO instances representing all notifications.
	 * @throws CustomException If an error occurs during retrieval.
	 */
	// findAllNotifications
	public List<NotificationDto> findAll() {
		List<NotificationDto> result = new ArrayList<>();

		List<Notification> notifications = notificationRepository.findAll();
		for (Notification notification : notifications) {
			NotificationDto notificationDto = convertToDTO(notification);
			result.add(notificationDto);
		}
		log.info("Successfully retrieved all notifications.");
		return result;

	}

	/**
	 * Finds a notification by its unique ID, converts it into a NotificationDTO,
	 * and returns the DTO.
	 * 
	 * @param notificationId The unique ID of the notification to be retrieved.
	 * @return A NotificationDTO representing the requested notification.
	 * @throws CustomException If the notification is not found or an error occurs.
	 */
	// findNotificationById
	public NotificationDto findById(String notificationId) {

		List<Notification> notifications = notificationRepository.findAll();
		for (Notification notification : notifications) {
			if (notification.getNotificationId().equals(notificationId)) {
				NotificationDto notificationDto = convertToDTO(notification);
				log.info("Successfully retrieved notification with ID: {}", notificationId);
				return notificationDto;
			}
		}
		log.warn("Notification with ID: {} not found.", notificationId);
		throw new CustomException("Notification not found with id: " + notificationId);

	}

	/**
	 * Adds a new notification by converting the provided NotificationDTO into a
	 * Notification entity, saving it in the repository, and returning the saved
	 * notification as a DTO.
	 * 
	 * @param notificationDto The NotificationDTO containing the details of the
	 *                        notification to be added.
	 * @return A NotificationDTO representing the newly added notification.
	 * @throws CustomException If an error occurs while adding the notification.
	 */
	// addNotification
	public NotificationDto addNotification(NotificationDto notificationDto) {

		Notification notification = convertToEntity(notificationDto);
		Notification savedNotification = notificationRepository.save(notification);
		log.info("Successfully added notification with ID: {}", savedNotification.getNotificationId());
		return convertToDTO(savedNotification);

	}

	/**
	 * Public method that updates an existing notification.
	 * 
	 * This method finds a notification by its unique ID, updates its details with
	 * the information provided in the given NotificationDTO, and saves the updated
	 * notification.
	 *
	 * @param notificationId The unique ID of the notification to be updated.
	 * @param nDto           The NotificationDTO containing updated notification
	 *                       details.
	 * @return A NotificationDTO representing the updated notification.
	 * @throws CustomException If the notification is not found or if an error
	 *                         occurs during the update process.
	 */
	// updateNotification
	public NotificationDto updateNotification(String notificationId, NotificationDto nDto) {

		Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
		if (optionalNotification.isPresent()) {
			Notification existingNotification = optionalNotification.get();
			existingNotification.setNotificationType(nDto.getNotificationType());
			existingNotification.setStatus(nDto.getStatus());
			existingNotification.setMessage(nDto.getMessage());
			Notification saved = notificationRepository.save(existingNotification);
			log.info("Successfully updated notification with ID: {}", notificationId);
			return convertToDTO(saved);
		} else {
			log.warn("Notification not found with ID: {}", notificationId);
			throw new CustomException("Notification not found with id: " + notificationId);
		}

	}

	/**
	 * Public method that deletes a notification by its unique ID.
	 * 
	 * This method checks if a notification with the given ID exists in the
	 * repository. If found, it deletes the notification. Otherwise, it throws an
	 * exception.
	 *
	 * @param notificationId The unique ID of the notification to be deleted.
	 * @throws CustomException If the notification is not found or if an error
	 *                         occurs during deletion.
	 */
	// deleteNotificationById
	public void deleteById(String notificationId) {

		if (notificationRepository.existsById(notificationId)) {
			notificationRepository.deleteById(notificationId);
			log.info("Successfully deleted notification with ID: {}", notificationId);
		} else {
			log.warn("Notification not found with ID: {}", notificationId);
			throw new CustomException("Notification not found with id: " + notificationId);
		}

	}

	/**
	 * Public method that deletes all notifications.
	 * 
	 * This method deletes all notifications from the repository and logs the
	 * success.
	 *
	 * @throws CustomException If an error occurs while deleting the notifications.
	 */
	// deleteAllNotifications
	public void deleteAllNotifications() {

		notificationRepository.deleteAll();
		log.info("Successfully deleted all notifications.");

	}

// --------------------------------------------------------------------------------------------------------------------------------------------------

	// User functionalities --> frontend

	/**
	 * Public method that inserts notifications for successfully booked
	 * appointments.
	 * 
	 * This method creates and inserts notifications for both the patient and doctor
	 * regarding a successfully booked appointment. It uses details provided in the
	 * BookAppointmentAndCancelNotificationDto to construct and add the
	 * notifications.
	 *
	 * @param bookAndCancelNotificationDto The DTO containing details about the
	 *                                     appointment, such as patient ID, doctor
	 *                                     ID, appointment date, session, and
	 *                                     relevant messages.
	 * @throws NotificationInsertionException If an error occurs during the
	 *                                        notification insertion process.
	 */
	// bookAppointmentNotificationInsertion
	@Transactional
	public void insertNotificationsForBookAppointment(
			BookAppointmentAndCancelNotificationDto bookAndCancelNotificationDto) {
		try {
			NotificationDto patientNotification = new NotificationDto();
			patientNotification.setCreatedAt(LocalDateTime.now());
			patientNotification.setUpdatedAt(LocalDateTime.now());
			patientNotification.setNotificationId(UUID.randomUUID().toString());
			patientNotification.setAppointmentId(bookAndCancelNotificationDto.getAppointmentId());
			patientNotification.setUserId(bookAndCancelNotificationDto.getPatientId());
			patientNotification.setNotificationType(NotificationType.Appointment);
			patientNotification.setStatus(NotificationStatus.Pending);
			patientNotification.setMessage("Your appointment with Dr. " + bookAndCancelNotificationDto.getDoctorName()
					+ " has been booked for " + bookAndCancelNotificationDto.getAppointmentDate() + " ("
					+ bookAndCancelNotificationDto.getSession() + ").");
			patientNotification.setTimestamp(LocalDateTime.now());

			addNotification(patientNotification);
			log.info("Notification for patient {} added successfully.", bookAndCancelNotificationDto.getPatientId());

			NotificationDto doctorNotification = new NotificationDto();
			doctorNotification.setCreatedAt(LocalDateTime.now());
			doctorNotification.setUpdatedAt(LocalDateTime.now());
			doctorNotification.setNotificationId(UUID.randomUUID().toString());
			doctorNotification.setAppointmentId(bookAndCancelNotificationDto.getAppointmentId());
			doctorNotification.setUserId(bookAndCancelNotificationDto.getDoctorId());
			doctorNotification.setNotificationType(NotificationType.Appointment);
			doctorNotification.setStatus(NotificationStatus.Pending);
			doctorNotification.setMessage("An appointment with patient " + bookAndCancelNotificationDto.getPatientName()
					+ " has been booked for " + bookAndCancelNotificationDto.getAppointmentDate() + " ("
					+ bookAndCancelNotificationDto.getSession() + ").");
			doctorNotification.setTimestamp(LocalDateTime.now());

			addNotification(doctorNotification);
			log.info("Notification for doctor {} added successfully.", bookAndCancelNotificationDto.getDoctorId());
		} catch (Exception e) {
			log.error("Error occurred while inserting notifications for booking appointment: ", e);
			throw new NotificationInsertionException("Failed to insert notifications for booking appointment");
		}
	}

	/**
	 * Public method that inserts notifications for successfully rescheduled
	 * appointments.
	 * 
	 * This method creates and inserts notifications for both the patient and doctor
	 * regarding a rescheduled appointment. It uses details from the provided
	 * RescheduleNotificationDto and constructs messages with the old and new dates
	 * and sessions.
	 * 
	 * @param rescheduleNotificationDto The DTO containing details about the
	 *                                  rescheduled appointment, including patient
	 *                                  ID, doctor ID, old and new dates and
	 *                                  sessions, and appointment ID.
	 * @throws NotificationInsertionException If an error occurs during the
	 *                                        notification insertion process.
	 */
	// rescheduleAppointmentNotificationInsertion
	@Transactional
	public void insertNotificationsForRescheduleAppointment(RescheduleNotificationDto rescheduleNotificationDto) {
		try {
			NotificationDto patientNotification = new NotificationDto();
			patientNotification.setCreatedAt(LocalDateTime.now());
			patientNotification.setUpdatedAt(LocalDateTime.now());
			patientNotification.setNotificationId(UUID.randomUUID().toString());
			patientNotification.setAppointmentId(rescheduleNotificationDto.getAppointmentId());
			patientNotification.setUserId(rescheduleNotificationDto.getPatientId());
			patientNotification.setNotificationType(NotificationType.Reschedule);
			patientNotification.setStatus(NotificationStatus.Pending);
			patientNotification.setMessage("Your appointment has been rescheduled from "
					+ rescheduleNotificationDto.getOldDate() + " (" + rescheduleNotificationDto.getOldSession()
					+ ") to " + rescheduleNotificationDto.getNewDate() + " ("
					+ rescheduleNotificationDto.getNewSession() + ").");
			patientNotification.setTimestamp(LocalDateTime.now());

			addNotification(patientNotification);
			log.info("Notification for patient {} added successfully.", rescheduleNotificationDto.getPatientId());

			NotificationDto doctorNotification = new NotificationDto();
			doctorNotification.setCreatedAt(LocalDateTime.now());
			doctorNotification.setUpdatedAt(LocalDateTime.now());
			doctorNotification.setNotificationId(UUID.randomUUID().toString());
			doctorNotification.setAppointmentId(rescheduleNotificationDto.getAppointmentId());
			doctorNotification.setUserId(rescheduleNotificationDto.getDoctorId());
			doctorNotification.setNotificationType(NotificationType.Reschedule);
			doctorNotification.setStatus(NotificationStatus.Pending);
			doctorNotification.setMessage("The appointment with patient " + rescheduleNotificationDto.getPatientName()
					+ " has been rescheduled from " + rescheduleNotificationDto.getOldDate() + " ("
					+ rescheduleNotificationDto.getOldSession() + ") to " + rescheduleNotificationDto.getNewDate()
					+ " (" + rescheduleNotificationDto.getNewSession() + ").");
			doctorNotification.setTimestamp(LocalDateTime.now());

			addNotification(doctorNotification);
			log.info("Notification for doctor {} added successfully.", rescheduleNotificationDto.getDoctorId());
		} catch (Exception e) {
			log.error("Error occurred while inserting notifications for rescheduling appointment: ", e);
			throw new NotificationInsertionException("Failed to insert notifications for rescheduling appointment");
		}
	}

	/**
	 * Public method that inserts notifications for successfully cancelled
	 * appointments.
	 * 
	 * This method creates and inserts notifications for both the patient and doctor
	 * regarding a cancelled appointment. It uses details from the provided
	 * BookAppointmentAndCancelNotificationDto to construct messages indicating the
	 * cancellation.
	 * 
	 * @param bookAndCancelNotificationDto The DTO containing details about the
	 *                                     cancelled appointment, including patient
	 *                                     ID, doctor ID, appointment date, session,
	 *                                     and appointment ID.
	 * @throws NotificationInsertionException If an error occurs during the
	 *                                        notification insertion process.
	 */
	// cancelAppointmentNotificationInsertion
	@Transactional
	public void insertNotificationsForCancelAppointment(
			BookAppointmentAndCancelNotificationDto bookAndCancelNotificationDto) {
		try {
			NotificationDto patientNotification = new NotificationDto();
			patientNotification.setCreatedAt(LocalDateTime.now());
			patientNotification.setUpdatedAt(LocalDateTime.now());
			patientNotification.setNotificationId(UUID.randomUUID().toString());
			patientNotification.setAppointmentId(bookAndCancelNotificationDto.getAppointmentId());
			patientNotification.setUserId(bookAndCancelNotificationDto.getPatientId());
			patientNotification.setNotificationType(NotificationType.Cancellation);
			patientNotification.setStatus(NotificationStatus.Pending);
			patientNotification.setMessage("Your appointment with Dr. " + bookAndCancelNotificationDto.getDoctorName()
					+ " on " + bookAndCancelNotificationDto.getAppointmentDate() + " ("
					+ bookAndCancelNotificationDto.getSession() + ") has been cancelled.");
			patientNotification.setTimestamp(LocalDateTime.now());

			addNotification(patientNotification);
			log.info("Notification for patient {} added successfully.", bookAndCancelNotificationDto.getPatientId());

			NotificationDto doctorNotification = new NotificationDto();
			doctorNotification.setCreatedAt(LocalDateTime.now());
			doctorNotification.setUpdatedAt(LocalDateTime.now());
			doctorNotification.setNotificationId(UUID.randomUUID().toString());
			doctorNotification.setAppointmentId(bookAndCancelNotificationDto.getAppointmentId());
			doctorNotification.setUserId(bookAndCancelNotificationDto.getDoctorId());
			doctorNotification.setNotificationType(NotificationType.Cancellation);
			doctorNotification.setStatus(NotificationStatus.Pending);
			doctorNotification.setMessage("The appointment with patient "
					+ bookAndCancelNotificationDto.getPatientName() + " has been cancelled.");
			doctorNotification.setTimestamp(LocalDateTime.now());

			addNotification(doctorNotification);
			log.info("Notification for doctor {} added successfully.", bookAndCancelNotificationDto.getDoctorId());
		} catch (Exception e) {
			log.error("Error occurred while inserting notifications for cancelling appointment: ", e);
			throw new NotificationInsertionException("Failed to insert notifications for cancelling appointment");
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------

	// Scheduler Tasks --> Send Reminders (automation)
	/**
	 * Public method that retrieves appointments for the current date.
	 * 
	 * This method formats the current date to 'yyyy-MM-dd', then uses the
	 * AppointmentClient to fetch appointments for that date.
	 * 
	 * @return A ResponseEntity containing a list of SendReminderDto instances
	 *         representing today's appointments.
	 */
	// getTodayAppointments
	public List<SendReminderDto> getTodayAppointments() {

		LocalDate currentDate = LocalDate.now();
		// Format the date as 'yyyy-MM-dd'
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		List<SendReminderDto> responseEntity = appointmentClient.findAppointmentsByAppointmentDate(formattedDate)
				.getBody().getData();

		System.out.println(responseEntity);
		return responseEntity;

	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Scheduled method that sends reminder notifications for today's appointments.
	 *
	 * This method retrieves appointments for the current date using
	 * `getTodayAppointments`. For appointments with the status `SCHEDULED`, it
	 * creates and inserts reminder notifications for both the patient and doctor,
	 * including session details.
	 *
	 * @Scheduled(cron = "0 36 09 * * ?") Executes every day at 8:00 AM.
	 */
	// sendAppointmentReminders
	@Scheduled(cron = "0 49 09 * * ?")
	public void sendAppointmentReminders() {

		// log.info("----------------------Current date: {}---------------------",
		// currentDate;

		// Fetch appointments for the current date

		List<SendReminderDto> listOfAppointments = getTodayAppointments();

		for (SendReminderDto appointment : listOfAppointments) {
			// Check if the appointment status is Scheduled
			if (appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
				// Create reminder notifications for patient and doctor
				NotificationDto patientReminder = new NotificationDto();
				patientReminder.setCreatedAt(LocalDateTime.now());
				patientReminder.setUpdatedAt(LocalDateTime.now());
				patientReminder.setNotificationId(UUID.randomUUID().toString());
				patientReminder.setAppointmentId(appointment.getAppointmentId());
				patientReminder.setUserId(appointment.getPatientId());
				patientReminder.setNotificationType(NotificationType.Reminder);
				patientReminder.setStatus(NotificationStatus.Pending);
				patientReminder.setMessage("Reminder: You have an appointment with " + appointment.getDoctorName()
						+ " today at " + appointment.getSession() + ".");
				patientReminder.setTimestamp(LocalDateTime.now());

				NotificationDto doctorReminder = new NotificationDto();
				doctorReminder.setCreatedAt(LocalDateTime.now());
				doctorReminder.setUpdatedAt(LocalDateTime.now());
				doctorReminder.setNotificationId(UUID.randomUUID().toString());
				doctorReminder.setAppointmentId(appointment.getAppointmentId());
				doctorReminder.setUserId(appointment.getDoctorId());
				doctorReminder.setNotificationType(NotificationType.Reminder);
				doctorReminder.setStatus(NotificationStatus.Pending);
				doctorReminder.setMessage("Reminder: You have an appointment with patient "
						+ appointment.getPatientName() + " today at " + appointment.getSession() + ".");
				doctorReminder.setTimestamp(LocalDateTime.now());

				// Add notifications
				addNotification(patientReminder);
				addNotification(doctorReminder);

				log.info("Reminder notifications for appointment ID {} added successfully.",
						appointment.getAppointmentId());
			}
		}

	}



	// -------------------------------------------------------------------------------------------------------------------------------------

	// getNotificationsByPatientId
	public List<NotificationDto> getPatientNotificationsByPatientId(String patientId) {
		List<Notification> notifications = notificationRepository.findAll();

		List<NotificationDto> result = notifications.stream()
				.filter(n -> n.getUserId().equals(patientId) && n.getStatus().equals(NotificationStatus.Pending))
				.map(this::convertToDTO).collect(Collectors.toList());

		return result;

	}

	// getNotificationsByDoctorId
	public List<NotificationDto> getDoctorNotificationsByDoctorId(String doctorId) {
		List<Notification> notifications = notificationRepository.findAll();
		List<NotificationDto> result = notifications.stream()
				.filter(n -> n.getUserId().equals(doctorId) && n.getStatus().equals(NotificationStatus.Pending))
				.map(this::convertToDTO).collect(Collectors.toList());
		return result;
	}

	// markNotificationAsRead
	public void markNotificationAsCompleted(String notificationId) {
		Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
		if (!optionalNotification.isPresent()) {
			throw new CustomException("Notification not found with the given id");
		}
		Notification notification = optionalNotification.get();
		notification.setStatus(NotificationStatus.Read);
		notificationRepository.save(notification);
	}
		
	// markAllNotificationsAsRead
	public void markAllNotificationsAsRead(String userId) {
		List<Notification> notifications = notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.Pending);
		
		for(Notification notification : notifications) {
			notification.setStatus(NotificationStatus.Read);
			notificationRepository.save(notification);
		}
		
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	
}
