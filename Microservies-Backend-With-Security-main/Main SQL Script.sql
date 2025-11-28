CREATE DATABASE NotificationService;
CREATE DATABASE AppointmentService;
CREATE DATABASE DoctorService;
CREATE DATABASE LoginService;
CREATE DATABASE MedicalHistoryService;
-- --------------------------------------------------------------------------------------------------------------------------
USE NotificationService;

SHOW TABLES;

SELECT * FROM Notification;
-- --------------------------------------------------------------------------------------------------------------------
USE AppointmentService;

SHOW TABLES;

SELECT * FROM appointment;
-- --------------------------------------------------------------------------------------------------------------------
USE DoctorService;

SHOW TABLES;
    
SELECT * FROM specialization;
SELECT * FROM doctor;
SELECT * FROM doctoravailability;
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
USE LoginService;

SHOW TABLES;

SELECT * FROM role;
SELECT * FROM user;
SELECT * FROM patient;

-- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
USE MedicalHistoryService;

SHOW TABLES;

SELECT * FROM medicalhistory;

-- ------------------------------------------------------------------------------------------------------------------------
-- DROP DATABASE NotificationService;
-- DROP DATABASE DoctorService;
-- DROP DATABASE AppointmentService;
-- DROP DATABASE MedicalHistoryService;
-- DROP DATABASE LoginService;

