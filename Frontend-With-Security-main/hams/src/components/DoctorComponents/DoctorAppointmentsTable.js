import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './DoctorAppointmentsTable.css';
import MedicalHistoryFormPopup from '../MedicalHistoryComponents/MedicalHistoryFormPopup';
import Footer from '../Footer';
import NavigationBar from '../NavigationBar';
import Header from '../Header';
 
function DoctorAppointmentsTable() {
    const doctorId = localStorage.getItem('doctorId');
    const navigate = useNavigate();
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showPopup, setShowPopup] = useState(false);
    const [selectedAppointmentId, setSelectedAppointmentId] = useState(null);
    const [responseMessage, setResponseMessage] = useState('');
    const [errorFromBackend, setErrorFromBackend] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [appointmentStatus, setAppointmentStatus] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [dropdownOptions, setDropdownOptions] = useState(['SCHEDULED', 'COMPLETED', 'CANCELLED']);
    const [showCancelConfirmation, setShowCancelConfirmation] = useState(false);
    const [appointmentToCancel, setAppointmentToCancel] = useState(null);
    const todayISO = new Date().toISOString().split('T')[0];
 
    useEffect(() => {
        fetchAppointments();
    }, [doctorId]);
 
    const fetchAppointments = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(`http://localhost:8089/api/doctors/current/appointments/${doctorId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const sortedAppointments = response.data.data.sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
            setAppointments(sortedAppointments);
            setLoading(false);
        } catch (error) {
            setError('Error fetching appointments');
            setLoading(false);
            console.error(error);
        }
    };
 
    const handleViewMedicalHistory = (patientId) => {
        navigate(`/doctor/${doctorId}/medical-history/patient/${patientId}`);
    };
 
    const handleCancelClick = (appointmentId) => {
        setAppointmentToCancel(appointmentId);
        setShowCancelConfirmation(true);
    };
 
    const handleCancelConfirm = async () => {
        if (appointmentToCancel) {
            try {
                const token = localStorage.getItem('jwtToken');
                await axios.post(`http://localhost:8089/api/doctors/cancel/${appointmentToCancel}`, {}, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setAppointments(appointments.filter(appointment => appointment.appointmentId !== appointmentToCancel));
                setShowCancelConfirmation(false);
                setAppointmentToCancel(null);
                //alert("Appointment cancelled successfully."); // Optional feedback without redirecting
            } catch (error) {
                console.error('Error canceling appointment:', error);
                setError('Error canceling appointment.');
            }
        }
    };
   
 
    const handleCancelReject = () => {
        setShowCancelConfirmation(false);
        setAppointmentToCancel(null);
    };
 
    const handleCompleteAppointment = (appointmentId) => {
        setSelectedAppointmentId(appointmentId);
        setShowPopup(true);
    };
 
    const closePopup = () => {
        setShowPopup(false);
        setSelectedAppointmentId(null);
        setResponseMessage('');
        setErrorFromBackend('');
        setSuccessMessage('');
    };
 
    const handleFormSubmit = async (medicalHistory) => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await fetch("http://localhost:8089/api/doctors/markAsCompleted", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(medicalHistory)
            });
            if (response.ok) {
                const data = await response.json();
                setSuccessMessage("Medical record added successfully.");
                fetchAppointments();
                closePopup();
            } else {
                const data = await response.json();
                setErrorFromBackend(data.data.message);
            }
        } catch (error) {
            setErrorFromBackend("An error occurred while marking the appointment as completed.");
        }
    };
 
    const handleFilter = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(`http://localhost:8089/api/doctors/filterAppointmentsByDate/${startDate}/${endDate}/${doctorId}/${appointmentStatus}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const sortedAppointments = response.data.data.sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
            setAppointments(sortedAppointments);
        } catch (error) {
            setError('Error fetching filtered appointments');
            console.error(error);
        }
    };
 
    return (
        <div>
            <Header/>
            <NavigationBar/>
            <div className="container">
                <h1>Upcoming Appointments</h1>
                <div className="filter-container">
                    <label>Start Date:</label>
                    <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} min={todayISO} />
                    <label>End Date:</label>
                    <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                    <label>Status:</label>
                    <select value={appointmentStatus} onChange={(e) => setAppointmentStatus(e.target.value)}>
                        <option value="">All</option>
                        {dropdownOptions.map(option => (
                            <option key={option} value={option}>{option}</option>
                        ))}
                    </select>
                    <button onClick={handleFilter}>Filter</button>
                </div>
                <div className="appointments-container">
                    {appointments.map((appointment) => (
                        <div key={appointment.appointmentId} className="appointment-card">
                            <p><strong>Patient Name:</strong> {appointment.patientName}</p>
                            <p><strong>Appointment Date:</strong> {appointment.appointmentDate}</p>
                            <p><strong>Session:</strong> {appointment.session}</p>
                            <p><strong>Status:</strong> {appointment.status}</p>
                            <button className="view-medical-history-link" onClick={() => handleViewMedicalHistory(appointment.patientId)}>
                                View Medical History
                            </button>
                            {appointment.status === 'SCHEDULED' && (
                                <div className="cancel-complete-buttons">
                                    <button className="action-button" onClick={() => handleCancelClick(appointment.appointmentId)}>
                                        Cancel
                                    </button>
                                    <button className="action-button complete" onClick={() => handleCompleteAppointment(appointment.appointmentId)}>
                                        Complete
                                    </button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
                {showCancelConfirmation && (
                    <div className="cancel-confirmation-overlay">
                        <div className="cancel-confirmation-modal">
                            <p>Are you sure you want to cancel this appointment?</p>
                            <div className="confirmation-buttons">
                                <button onClick={handleCancelConfirm} className="confirm-button">Yes, Cancel</button>
                                <button onClick={handleCancelReject} className="reject-button">No, Keep</button>
                            </div>
                        </div>
                    </div>
                )}
                {showPopup && <MedicalHistoryFormPopup appointmentId={selectedAppointmentId} onClose={closePopup} onSubmit={handleFormSubmit} errorMessage={errorFromBackend} />}
            </div>
            <Footer/>
        </div>
    );
}
 
export default DoctorAppointmentsTable;
 
 