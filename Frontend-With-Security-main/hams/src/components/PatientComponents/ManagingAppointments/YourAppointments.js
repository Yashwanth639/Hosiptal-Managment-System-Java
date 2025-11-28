// YourAppointments.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './YourAppointments.css';
import Header from '../../Header';
import NavigationBar from '../../NavigationBar';
import Footer from '../../Footer';


const YourAppointments = () => {
    const navigate = useNavigate();
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [filteredAppointments, setFilteredAppointments] = useState([]);
    const [showCancelConfirmation, setShowCancelConfirmation] = useState(false);
    const [appointmentToCancel, setAppointmentToCancel] = useState(null);

    useEffect(() => {
        const patientId = localStorage.getItem('patientId');
        const token = localStorage.getItem('jwtToken');

        if (patientId && token) {
            fetchAppointments(patientId, token);
        } else {
            setError('Patient ID or Token not found. Please log in or ensure patient information is available.');
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        applyDateFilter();
    }, [appointments, startDate, endDate]);

    const fetchAppointments = async (patientId, token) => {
        setLoading(true);
        setError(null);

        if (patientId && token) {
            try {
                const response = await axios.get(`http://localhost:8083/api/patients/current/patient/${patientId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (response.data.success && response.data.data) {
                    const sortedAppointments = response.data.data.sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
                    setAppointments(sortedAppointments);
                } else {
                    setError(response.data.message || 'Failed to fetch appointments');
                }
            } catch (err) {
                setError(err.message || 'An unexpected error occurred');
                console.error('Error fetching appointments:', err);
            } finally {
                setLoading(false);
            }
        } else {
            setError('Patient ID or Token not found. Please log in or ensure patient information is available.');
            setLoading(false);
        }
    };

    const handleCancelClick = (appointmentId) => {
        setAppointmentToCancel(appointmentId);
        setShowCancelConfirmation(true);
       
    };

    const handleCancelConfirm = async () => {
        if (appointmentToCancel) {
            try {
                const response = await axios.post(`http://localhost:8083/api/patients/cancelAppointment/${appointmentToCancel}`, {}, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('jwtToken')}`,
                    },
                });
                navigate('/appointment-cancel-confirmation')
                if (response.status === 204) {
                    fetchAppointments(localStorage.getItem('patientId'), localStorage.getItem('jwtToken'));
                } else if (response.data && response.data.message) {
                    alert(`Cancellation failed: ${response.data.message}`);
                } else {
                    alert('Cancellation failed');
                }
            } catch (err) {
                setError(err.message || 'An unexpected error occurred during cancellation');
                console.error('Error cancelling appointment:', err);
            } finally {
                setShowCancelConfirmation(false);
                setAppointmentToCancel(null);
            }
        }
    };

    const handleCancelReject = () => {
        setShowCancelConfirmation(false);
        setAppointmentToCancel(null);
    };

    const handleReschedule = (appointmentId, doctorId) => {
        navigate(`/rescheduleappointment/${appointmentId}/${doctorId}`);
    };

    const handleBookNew = () => {
        navigate('/bookappointment');
    };

    const handleStartDateChange = (event) => {
        setStartDate(event.target.value);
    };

    const handleEndDateChange = (event) => {
        setEndDate(event.target.value);
    };

    const applyDateFilter = () => {
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            const filtered = appointments.filter((appointment) => {
                const appointmentDate = new Date(appointment.appointmentDate);
                return appointmentDate >= start && appointmentDate <= end;
            });
            setFilteredAppointments(filtered);
        } else {
            setFilteredAppointments(appointments);
        }
    };

    return (
        <div>
            <Header/>
            <NavigationBar />
            <div className="your-appointments-container">
                <h1>Your Current Appointments</h1>

                <div className="date-filter">
                    <label htmlFor="startDate">Start Date:</label>
                    <input type="date" id="startDate" value={startDate} onChange={handleStartDateChange} />

                    <label htmlFor="endDate">End Date:</label>
                    <input type="date" id="endDate" value={endDate} onChange={handleEndDateChange} />
                </div>

                {loading && <div className="loading-spinner"></div>}
                {error && <p className="error-message">{error}</p>}
                {!loading && !error && filteredAppointments.length === 0 && <p>No appointments found within the selected date range.</p>}
                {!loading && !error && filteredAppointments.length > 0 && (
                    <div className="appointments-list">
                        {filteredAppointments.map((appointment) => (
                            <div key={appointment.appointmentId} className="appointment-item">
                                <div className="appointment-info">
                                    <span className="doctor">Doctor: {appointment.doctorName}</span>
                                    <span className="specialization">Specialization: {appointment.specializationName}</span>
                                    <span className="date-time">
                                        {new Date(appointment.appointmentDate).toLocaleDateString()} {appointment.session}
                                    </span>
                                    <span className="status">Status: {appointment.status}</span>
                                </div>
                                <div className="appointment-actions">
                                    <button onClick={() => handleCancelClick(appointment.appointmentId)} className="cancel-button">
                                        Cancel
                                    </button>
                                    <button onClick={() => handleReschedule(appointment.appointmentId, appointment.doctorId)} className="reschedule-button">
                                        Reschedule
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {showCancelConfirmation && (
                    <div className="cancel-confirmation-overlay">
                        <div className="cancel-confirmation-modal">
                            <p>Are you sure you want to cancel this appointment?</p>
                            <div className="confirmation-buttons">
                                <button onClick={handleCancelConfirm} className="confirm-button">
                                    Yes, Cancel
                                </button>
                                <button onClick={handleCancelReject} className="reject-button">
                                    No, Keep
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
            <Footer/>
        </div>
    );
};

export default YourAppointments;