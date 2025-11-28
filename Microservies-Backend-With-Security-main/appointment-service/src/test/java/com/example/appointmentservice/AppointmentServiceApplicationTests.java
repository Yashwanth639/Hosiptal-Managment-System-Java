package com.example.appointmentservice;

import com.example.appointmentservice.client.DoctorClient;
import com.example.appointmentservice.client.NotificationClient;
import com.example.appointmentservice.client.PatientClient;
import com.example.appointmentservice.dto.*;
import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.Session;
import com.example.appointmentservice.exception.*;
import com.example.appointmentservice.model.Appointment;
import com.example.appointmentservice.model.ResultResponse;
import com.example.appointmentservice.repository.AppointmentRepository;
import com.example.appointmentservice.service.AppointmentService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private DoctorClient doctorClient;

    @Mock
    private PatientClient patientClient;

    private Appointment appointment;
    private AppointmentDetailsDto appointmentDetailsDto;
    private BookAppointmentDto bookAppointmentDto;
    private DoctorDto doctorDto;
    private PatientDto patientDto;
    private SpecializationDto specializationDto;
    private DoctorAvailabilityDto doctorAvailabilityDto;
    private RescheduleRequestDto rescheduleRequestDto;
    private RescheduleNotificationDto rescheduleNotificationDto;
    private BookAppointmentAndCancelNotificationDto bookAppointmentAndCancelNotificationDto;
    private SendReminderDto sendReminderDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        appointment = new Appointment();
        appointment.setAppointmentId("appt1");
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setDoctorId("doc1");
        appointment.setPatientId("pat1");
        appointment.setSession(Session.FN);
        appointment.setAppointmentstatus(AppointmentStatus.SCHEDULED);
        appointment.setAvailabilityId("avail1");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentDetailsDto = AppointmentDetailsDto.builder()
                .appointmentId("appt1")
                .appointmentDate(LocalDate.now())
                .doctorId("doc1")
                .patientId("pat1")
                .session(Session.FN)
                .status(AppointmentStatus.SCHEDULED)
                .doctorName("Doctor One")
                .patientName("Patient One")
                .specializationName("Cardiology")
                .specializationId("spec1")
                .build();

        bookAppointmentDto = BookAppointmentDto.builder()
                .doctorId("doc1")
                .patientId("pat1")
                .appointmentDate(LocalDate.now())
                .session(Session.FN)
                .status(AppointmentStatus.SCHEDULED)
                .specializationName("Cardiology")
                .build();
        bookAppointmentDto.setAppointmentId("appt1"); // Setting after builder

        doctorDto = DoctorDto.builder()
                .doctorId("doc1")
                .name("Doctor One")
                .specializationId("spec1")
                .build();

        patientDto = PatientDto.builder()
                .patientId("pat1")
                .name("Patient One")
                .build();

        specializationDto = SpecializationDto.builder()
                .specializationId("spec1")
                .specializationName("Cardiology")
                .build();

        doctorAvailabilityDto = DoctorAvailabilityDto.builder()
                .availabilityId("avail1")
                .doctorId("doc1")
                .availableDate(LocalDate.now())
                .session("FN")
                .isAvailable(1)
                .specializationId("spec1")
                .build();

        rescheduleRequestDto = RescheduleRequestDto.builder()
                .appointmentId("appt1")
                .doctorId("doc1")
                .newAppointmentDate(LocalDate.now().plusDays(1))
                .newSession(Session.AN)
                .build();

        rescheduleNotificationDto = RescheduleNotificationDto.builder()
                .appointmentId("appt1")
                .patientId("pat1")
                .doctorId("doc1")
                .patientName("Patient One")
                .doctorName("Doctor One")
                .oldDate(LocalDate.now())
                .oldSession(Session.FN)
                .newDate(LocalDate.now().plusDays(1))
                .newSession(Session.AN)
                .build();

        bookAppointmentAndCancelNotificationDto = BookAppointmentAndCancelNotificationDto.builder()
                .appointmentId("appt1")
                .patientId("pat1")
                .doctorId("doc1")
                .patientName("Patient One")
                .doctorName("Doctor One")
                .appointmentDate(LocalDate.now())
                .session(Session.FN)
                .build();

        sendReminderDto = SendReminderDto.builder()
                .appointmentId("appt1")
                .patientId("pat1")
                .patientName("Patient One")
                .doctorName("Doctor One")
                .doctorId("doc1")
                .session(Session.FN)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    void findAllAppointments_success() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment));
        when(doctorClient.fetchDoctorById("doc1")).thenReturn(ResponseEntity.ok(ResultResponse.<DoctorDto>builder().data(doctorDto).build()));
        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.ok(ResultResponse.<PatientDto>builder().data(patientDto).build()));
        when(doctorClient.findById("spec1")).thenReturn(ResponseEntity.ok(ResultResponse.<SpecializationDto>builder().data(specializationDto).build()));

        List<AppointmentDetailsDto> result = appointmentService.findAllAppointments();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(appointmentDetailsDto, result.get(0));
        verify(appointmentRepository, times(1)).findAll();
        verify(doctorClient, times(1)).fetchDoctorById("doc1");
        verify(patientClient, times(1)).fetchPatientById("pat1");
        verify(doctorClient, times(1)).findById("spec1");
    }

    @Test
    void findAllAppointments_emptyList() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());

        List<AppointmentDetailsDto> result = appointmentService.findAllAppointments();

        assertTrue(result.isEmpty());
        verify(appointmentRepository, times(1)).findAll();
        verify(doctorClient, never()).fetchDoctorById(anyString());
        verify(patientClient, never()).fetchPatientById(anyString());
        verify(doctorClient, never()).findById(anyString());
    }

    @Test
    void findById_success() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment));
        when(doctorClient.fetchDoctorById("doc1")).thenReturn(ResponseEntity.ok(ResultResponse.<DoctorDto>builder().data(doctorDto).build()));
        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.ok(ResultResponse.<PatientDto>builder().data(patientDto).build()));
        when(doctorClient.findById("spec1")).thenReturn(ResponseEntity.ok(ResultResponse.<SpecializationDto>builder().data(specializationDto).build()));

        AppointmentDetailsDto result = appointmentService.findById("appt1");

        assertEquals(appointmentDetailsDto, result);
        verify(appointmentRepository, times(1)).findAll();
        verify(doctorClient, times(1)).fetchDoctorById("doc1");
        verify(patientClient, times(1)).fetchPatientById("pat1");
        verify(doctorClient, times(1)).findById("spec1");
    }

    @Test
    void findById_notFound() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.findById("appt1"));
        verify(appointmentRepository, times(1)).findAll();
        verify(doctorClient, never()).fetchDoctorById(anyString());
        verify(patientClient, never()).fetchPatientById(anyString());
        verify(doctorClient, never()).findById(anyString());
    }

//    @Test
//    void addAppointment_success() {
//        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.ok(ResultResponse.<PatientDto>builder().data(patientDto).build()));
//        when(doctorClient.fetchDoctorById("doc1")).thenReturn(ResponseEntity.ok(ResultResponse.<DoctorDto>builder().data(doctorDto).build()));
//        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
//        when(doctorClient.findById("spec1")).thenReturn(ResponseEntity.ok(ResultResponse.<SpecializationDto>builder().data(specializationDto).build()));
//
//        BookAppointmentDto result = appointmentService.addAppointment(bookAppointmentDto);
//
//        assertEquals(bookAppointmentDto.getAppointmentId(), result.getAppointmentId());
//        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
//        assertEquals("Doctor One", result.getDoctorName());
//        assertEquals("Patient One", result.getPatientName());
//        assertEquals("Cardiology", result.getSpecializationName());
//        verify(patientClient, times(1)).fetchPatientById("pat1");
//        verify(doctorClient, times(1)).fetchDoctorById("doc1");
//        verify(appointmentRepository, times(1)).save(any(Appointment.class));
//        verify(doctorClient, times(1)).findById("spec1");
//    }

    @Test
    void addAppointment_patientNotFound() {
        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        assertThrows(RuntimeException.class, () -> appointmentService.addAppointment(bookAppointmentDto));
        verify(patientClient, times(1)).fetchPatientById("pat1");
        verify(doctorClient, never()).fetchDoctorById(anyString());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(doctorClient, never()).findById(anyString());
    }

    @Test
    void addAppointment_doctorNotFound() {
        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.ok(ResultResponse.<PatientDto>builder().data(patientDto).build()));
        when(doctorClient.fetchDoctorById("doc1")).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        assertThrows(RuntimeException.class, () -> appointmentService.addAppointment(bookAppointmentDto));
        verify(patientClient, times(1)).fetchPatientById("pat1");
        verify(doctorClient, times(1)).fetchDoctorById("doc1");
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(doctorClient, never()).findById(anyString());
    }

    @Test
    void updateAppointment_success() {
        when(appointmentRepository.findById("appt1")).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(doctorClient.fetchDoctorById("doc1")).thenReturn(ResponseEntity.ok(ResultResponse.<DoctorDto>builder().data(doctorDto).build()));
        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.ok(ResultResponse.<PatientDto>builder().data(patientDto).build()));
        when(doctorClient.findById("spec1")).thenReturn(ResponseEntity.ok(ResultResponse.<SpecializationDto>builder().data(specializationDto).build()));

        AppointmentDetailsDto updatedDto = AppointmentDetailsDto.builder()
                .appointmentId("appt1")
                .appointmentDate(LocalDate.now().plusDays(1))
                .doctorId("doc1")
                .patientId("pat1")
                .session(Session.AN)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        AppointmentDetailsDto result = appointmentService.updateAppointment("appt1", updatedDto);

        assertEquals(updatedDto.getAppointmentDate(), result.getAppointmentDate());
        assertEquals(updatedDto.getSession(), result.getSession());
        assertEquals(updatedDto.getStatus(), result.getStatus());
        verify(appointmentRepository, times(1)).findById("appt1");
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(doctorClient, times(1)).fetchDoctorById("doc1");
        verify(patientClient, times(1)).fetchPatientById("pat1");
        verify(doctorClient, times(1)).findById("spec1");
    }

    @Test
    void updateAppointment_notFound() {
        when(appointmentRepository.findById("appt1")).thenReturn(Optional.empty());

        AppointmentDetailsDto updatedDto = AppointmentDetailsDto.builder()
                .appointmentId("appt1")
                .appointmentDate(LocalDate.now().plusDays(1))
                .doctorId("doc1")
                .patientId("pat1")
                .session(Session.AN)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.updateAppointment("appt1", updatedDto));
        verify(appointmentRepository, times(1)).findById("appt1");
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(doctorClient, never()).fetchDoctorById(anyString());
        verify(patientClient, never()).fetchPatientById(anyString());
        verify(doctorClient, never()).findById(anyString());
    }

    @Test
    void deleteById_success() {
        when(appointmentRepository.existsById("appt1")).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById("appt1");

        appointmentService.deleteById("appt1");

        verify(appointmentRepository, times(1)).existsById("appt1");
        verify(appointmentRepository, times(1)).deleteById("appt1");
    }

    @Test
    void deleteById_notFound() {
        when(appointmentRepository.existsById("appt1")).thenReturn(false);

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.deleteById("appt1"));
        verify(appointmentRepository, times(1)).existsById("appt1");
        verify(appointmentRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteAllAppointment_success() {
        doNothing().when(appointmentRepository).deleteAll();

        appointmentService.deleteAllAppointment();

        verify(appointmentRepository, times(1)).deleteAll();
    }

    @Test
    void getCurrentPatientAppointments_success() {
        when(appointmentRepository.findCurrentPatientAppointments("pat1", LocalDate.now(), AppointmentStatus.SCHEDULED))
                .thenReturn(Arrays.asList(appointment));
        when(doctorClient.fetchDoctorById("doc1")).thenReturn(ResponseEntity.ok(ResultResponse.<DoctorDto>builder().data(doctorDto).build()));
        when(patientClient.fetchPatientById("pat1")).thenReturn(ResponseEntity.ok(ResultResponse.<PatientDto>builder().data(patientDto).build()));
        when(doctorClient.findById("spec1")).thenReturn(ResponseEntity.ok(ResultResponse.<SpecializationDto>builder().data(specializationDto).build()));

        List<AppointmentDetailsDto> result = appointmentService.getCurrentPatientAppointments("pat1");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(appointmentDetailsDto, result.get(0));
        verify(appointmentRepository, times(1)).findCurrentPatientAppointments("pat1", LocalDate.now(), AppointmentStatus.SCHEDULED);
        verify(doctorClient, times(1)).fetchDoctorById("doc1");
        verify(patientClient, times(1)).fetchPatientById("pat1");
        verify(doctorClient, times(1)).findById("spec1");
    }

    @Test
    void getCurrentPatientAppointments_emptyList() {
        when(appointmentRepository.findCurrentPatientAppointments("pat1", LocalDate.now(), AppointmentStatus.SCHEDULED))
                .thenReturn(Arrays.asList());

        List<AppointmentDetailsDto> result = appointmentService.getCurrentPatientAppointments("pat1");

        assertTrue(result.isEmpty());
        verify(appointmentRepository, times(1)).findCurrentPatientAppointments("pat1", LocalDate.now(), AppointmentStatus.SCHEDULED);
        verify(doctorClient, never()).fetchDoctorById(anyString());
        verify(patientClient, never()).fetchPatientById(anyString());
        verify(doctorClient, never()).findById(anyString());
    }

}