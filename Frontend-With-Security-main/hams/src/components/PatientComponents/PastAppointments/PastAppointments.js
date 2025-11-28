import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './PastAppointments.css';
import Header from '../../Header';
import NavigationBar from '../../NavigationBar';
import Footer from '../../Footer';

const PastAppointments = () => {
    const navigate = useNavigate();
    const [pastAppointments, setPastAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [filteredAppointments, setFilteredAppointments] = useState([]);

    useEffect(() => {
        fetchAppointmentsData();
    },[]);

    useEffect(() => {
        applyDateFilter();
    }, [pastAppointments, startDate, endDate]);

    const getPatientId = () => {
        return localStorage.getItem('patientId');
    };

    const getJwtToken = () => {
        return localStorage.getItem('jwtToken');
    };

    const fetchAppointmentsData = async () => {
        setLoading(true);
        setError(null);
        const patientId = getPatientId();
        const token = getJwtToken();

        if (patientId && token) {
            try {
                const response = await axios.get(`http://localhost:8083/api/patients/past/patient/${patientId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (response.data.success && Array.isArray(response.data.data)) {
                    setPastAppointments(response.data.data);
                } else if (response.data.message) {
                    console.log('Message from backend:', response.data.message);
                    setPastAppointments([]);
                } else {
                    setPastAppointments([]);
                }
            } catch (err) {
                setError(err.message || 'An unexpected error occurred while fetching past appointments');
                console.error('Error fetching past appointments:', err);
                setPastAppointments([]);
            } finally {
                setLoading(false);
            }
        } else {
            setError('Patient ID or Token not found. Please ensure you are logged in.');
            setLoading(false);
            setPastAppointments([]);
        }
    };

    const handleGoBack = () => {
        navigate('/dashboard');
    };

    const formatSession = (session) => {
        return session === 'FN' ? 'Forenoon' : (session === 'AN' ? 'Afternoon' : session);
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
            const filtered = pastAppointments.filter(appointment => {
                const appointmentDate = new Date(appointment.appointmentDate);
                return appointmentDate >= start && appointmentDate <= end;
            });
            setFilteredAppointments(filtered);
        } else {
            setFilteredAppointments(pastAppointments);
        }
    };

    return (
        <div>
            <Header />
            <NavigationBar />
            <div className="past-appointments-container">
                <h1>Your Past Appointments</h1>

                <div className="date-filter">
                    <label htmlFor="startDate">Start Date:</label>
                    <input
                        type="date"
                        id="startDate"
                        value={startDate}
                        onChange={handleStartDateChange}
                    />

                    <label htmlFor="endDate">End Date:</label>
                    <input
                        type="date"
                        id="endDate"
                        value={endDate}
                        onChange={handleEndDateChange}
                    />
                </div>

                {loading && <div className="loading-spinner"></div>}
                {error && <p className="error-message">{error}</p>}

                {!loading && !error && filteredAppointments.length === 0 && (
                    <p>No past appointments found within the selected date range.</p>
                )}

                {!loading && !error && filteredAppointments.length > 0 && (
                    <div className="past-appointments-list">
                        {filteredAppointments.map((appointment) => (
                            <div key={appointment.appointmentId} className="past-appointment-item">
                                <div className="tape"></div>
                                <div className="appointment-details">
                                    <span className="date-time">
                                        {new Date(appointment.appointmentDate).toLocaleDateString()} - {formatSession(appointment.session)}
                                    </span>
                                    <span className="doctor">Doctor: {appointment.doctorName}</span>
                                    <span className="specialization">Specialization: {appointment.specializationName}</span>
                                    <span className="status">Status: {appointment.status}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                <button onClick={handleGoBack} className="go-back-button">
                    Go Back to Dashboard
                </button>
            </div>
            <Footer />
        </div>
    );
};

export default PastAppointments;