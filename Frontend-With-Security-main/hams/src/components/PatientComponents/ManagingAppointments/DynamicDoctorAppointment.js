import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './DynamicDoctorAppointment.css';
import Header from '../../Header';
import Footer from '../../Footer';
import NavigationBar from '../../NavigationBar';

 
const DynamicDoctorAppointment = () => {
    const navigate = useNavigate();
    const [specializations, setSpecializations] = useState([]);
    const [selectedSpecializationId, setSelectedSpecializationId] = useState('');
    const [doctors, setDoctors] = useState([]);
    const [selectedDoctorId, setSelectedDoctorId] = useState('');
    const [appointmentDate, setAppointmentDate] = useState(null);
    const [sessions, setSessions] = useState([
        { value: 'FN', label: 'Forenoon' },
        { value: 'AN', label: 'Afternoon' },
    ]);
    const [selectedSession, setSelectedSession] = useState('');
    const [doctorAvailabilityData, setDoctorAvailabilityData] = useState([]);
    const [isAvailabilityPopupVisible, setIsAvailabilityPopupVisible] = useState(false);
    const [loadingDoctors, setLoadingDoctors] = useState(false);
    const [error, setError] = useState('');
    const [bookingSuccess, setBookingSuccess] = useState(false);
    const [bookingLoading, setBookingLoading] = useState(false);
    const [bookingError, setBookingError] = useState(null);
    const [bookingSuccessMessage, setBookingSuccessMessage] = useState('');
 
    const getPatientId = () => localStorage.getItem('patientId');
    const getJwtToken = () => localStorage.getItem('jwtToken');
 
    useEffect(() => {
        const token = getJwtToken();
        axios.get('http://localhost:8089/specialization/getAll', { headers: { Authorization: `Bearer ${token}` } })
            .then(response => {
                if (response.data.success && response.data.data) setSpecializations(response.data.data);
                else setError('Failed to fetch specializations.');
            })
            .catch(error => setError('Error fetching specializations: ' + error.message));
    });
 
 
    useEffect(() => {
        if (selectedSpecializationId) {
            setLoadingDoctors(true);
            const token = getJwtToken();
            axios.get(`http://localhost:8089/api/doctors/specialization/id/${selectedSpecializationId}`, { headers: { Authorization: `Bearer ${token}` } })
                .then(response => {
                    if (response.data.success && response.data.data) setDoctors(response.data.data);
                    else { setDoctors([]); setError('No Doctor\'s Available for the selected criteria.'); }
                    setLoadingDoctors(false);
                })
                .catch(error => { setDoctors([]); setError('Error fetching doctors: ' + error.message); setLoadingDoctors(false); });
        } else setDoctors([]);
    }, [selectedSpecializationId]);
 
    const fetchDoctorAvailability = (doctorId) => {
        if (doctorId) {
            const token = getJwtToken();
            axios.get(`http://localhost:8089/api/doctorAvailability/getSchedule/${doctorId}`, { headers: { Authorization: `Bearer ${token}` } })
                .then(response => {
                    if (response.data.success && response.data.data) {
                        const today = new Date();
                        const futureDate = new Date();
                        futureDate.setDate(today.getDate() + 60);
 
                        const formattedData = response.data.data
                            .filter(item => new Date(item.availableDate) >= today && new Date(item.availableDate) <= futureDate)
                            .map(item => ({ date: item.availableDate, available: item.isAvailable === 1, session: item.session }))
                            .sort((a, b) => new Date(a.date) - new Date(b.date));
 
                        setDoctorAvailabilityData(formattedData);
                        setIsAvailabilityPopupVisible(true);
                    } else { setDoctorAvailabilityData([]); setIsAvailabilityPopupVisible(false); console.log('Failed to fetch doctor availability for popup.'); }
                })
                .catch(error => { setDoctorAvailabilityData([]); setIsAvailabilityPopupVisible(false); console.error('Error fetching doctor availability:', error); });
        } else { setIsAvailabilityPopupVisible(false); setDoctorAvailabilityData([]); }
    };
 
    const handleSpecializationChange = (event) => {
        setSelectedSpecializationId(event.target.value);
        setSelectedDoctorId('');
        setIsAvailabilityPopupVisible(false);
        setAppointmentDate(null);
        setSelectedSession('');
    };
 
    const handleDoctorChange = (event) => {
        const doctorId = event.target.value;
        setSelectedDoctorId(doctorId);
        setIsAvailabilityPopupVisible(false);
        setAppointmentDate(null);
        setSelectedSession('');
        fetchDoctorAvailability(doctorId);
    };
 
    const closeAvailabilityPopup = () => setIsAvailabilityPopupVisible(false);
 
    const selectDateAndSession = (date, session) => {
        setAppointmentDate(new Date(date));
        setSelectedSession(session === 'FN' ? 'FN' : session === 'AN' ? 'AN' : '');
        setIsAvailabilityPopupVisible(false);
    };
 
    const getDayOfWeek = (dateString) => {
        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        return days[new Date(dateString).getDay()];
    };
 
    const getMonthAndDate = (dateString) => new Date(dateString).toLocaleDateString(undefined, { month: 'short', day: 'numeric' });
 
    const handleBookAppointmentSubmission = async () => {
        if (selectedSpecializationId && selectedDoctorId && appointmentDate && selectedSession) {
            setBookingLoading(true);
            setBookingError(null);
            setBookingSuccessMessage('');
 
            const formattedDate = appointmentDate.toISOString().split('T')[0];
            const patientId = getPatientId();
            const selectedSpecialization = specializations.find(spec => spec.specializationId === selectedSpecializationId);
            const specializationName = selectedSpecialization?.specializationName;
            const token = getJwtToken();
 
            if (!patientId || !token) {
                setError(patientId ? 'Token not found. Please ensure you are logged in.' : 'Patient ID not found. Please ensure you are logged in.');
                setBookingLoading(false);
                return;
            }
 
            const bookingData = {
                appointmentDate: formattedDate,
                patientId: patientId,
                doctorId: selectedDoctorId,
                session: selectedSession.toUpperCase(),
                specializationName: specializationName,
            };
 
            try {
                console.log("Booking Data:", bookingData);
                const response = await axios.post('http://localhost:8083/api/patients/bookAppointment', bookingData, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                console.log("API Response:", response);
 
                if (response.data.success) {
                    setBookingSuccess(true);
                    setError('');
                    setBookingSuccessMessage('Appointment booked successfully!');
                    navigate('/appointment-confirmation');
                } else {
                    // Ensure correct error extraction from nested response
                    const errorMessage = response.data.data?.message || response.data.message || "Failed to book appointment";
                    setError(errorMessage);
                    setBookingSuccess(false);
                    setBookingError(errorMessage);
                }
            } catch (error) {
 
    let errorMessage = "Error booking appointment";
 
    if (error.response?.data) {
        errorMessage = error.response.data.data?.message || error.response.data.message || "Error booking appointment";
    }
 
    // Set error state only once
    setError(errorMessage);
    setBookingSuccess(false);
 
    setBookingError(errorMessage);
            } finally {
                setBookingLoading(false);
            }
        } else {
            setError('Please select specialization, doctor, and a date/time slot to book an appointment.');
            setBookingSuccess(false);
        }
    };
 
 
    return (
        <div>
            <Header />
            <NavigationBar />
            <div className="book-appointment-page">
                <div className="book-appointment-card">
                    <h2>Book New Appointment</h2>
                    {error && <p className="error-message">{error}</p>}
                    {bookingSuccessMessage && <p className="success-message">{bookingSuccessMessage}</p>}
                  
                    <div className="form-group">
                        <label htmlFor="specialization">Specialization:</label>
                        <select id="specialization" value={selectedSpecializationId} onChange={handleSpecializationChange}>
                            <option value="">Select Specialization</option>
                            {specializations.map(specialization => (
                                <option key={specialization.specializationId} value={specialization.specializationId}>
                                    {specialization.specializationName}
                                </option>
                            ))}
                        </select>
                    </div>
 
                    <div className="form-group">
                        <label htmlFor="doctor">Doctor:</label>
                        <select id="doctor" value={selectedDoctorId} onChange={handleDoctorChange} disabled={!selectedSpecializationId || loadingDoctors || doctors.length === 0}>
                            <option value="">{loadingDoctors ? 'Loading Doctors...' : (doctors.length === 0 ? 'No Doctor\'s Available' : 'Select Doctor')}</option>
                            {doctors.map(doctor => (
                                <option key={doctor.doctorId} value={doctor.doctorId}>
                                    {doctor.name}
                                </option>
                            ))}
                        </select>
                    </div>
 
                    <div className="form-group">
                        <label htmlFor="appointmentDate">Appointment Date:</label>
                        {selectedDoctorId ? (
                            <button type="button" className="view-availability" onClick={() => setIsAvailabilityPopupVisible(true)}>
                                View Availability
                            </button>
                        ) : (
                            <input type="text" value="Select a doctor to see availability" disabled />
                        )}
                        {appointmentDate && <p>Selected Date: {appointmentDate?.toLocaleDateString()}</p>}
                        {selectedSession && <p>Selected Session: {sessions.find(s => s.value === selectedSession)?.label || 'No Session Selected'}</p>}
                    </div>
 
                    {isAvailabilityPopupVisible && (
                        <div className="availability-popup compact">
                            <h3>Doctor Availability (Next 60 Days)</h3>
                            {doctorAvailabilityData.length > 0 ? (
                                <div className="availability-grid">
                                    {doctorAvailabilityData.map(item => (
                                        <div
                                            key={item.date}
                                            className={`availability-slot ${item.available ? 'available' : 'unavailable'}`}
                                            title={item.available ? `Available for ${item.session}` : 'Unavailable'}
                                            onClick={() => item.available && selectDateAndSession(item.date, item.session)}
                                        >
                                            <span className="day-of-week">{getDayOfWeek(item.date)}</span>
                                            <span className="date-number">{getMonthAndDate(item.date)}</span>
                                            {item.available && <span className="session-indicator dynamic">{item.session}</span>}
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p>No availability found for the next 60 days.</p>
                            )}
                            <div className="popup-actions">
                                <button onClick={closeAvailabilityPopup}>Close</button>
                            </div>
                        </div>
                    )}
 
                    <button className="book-button" onClick={handleBookAppointmentSubmission} disabled={!selectedSpecializationId || !selectedDoctorId || !appointmentDate || !selectedSession || bookingLoading || bookingSuccess}>
                        {bookingLoading ? 'Booking...' : (bookingSuccess ? 'Booked!' : 'Book Appointment')}
                    </button>
                </div>
            </div>
            <Footer />
        </div>
    );
};
 
export default DynamicDoctorAppointment;