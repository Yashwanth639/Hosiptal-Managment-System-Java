import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { parseISO, format, isToday, isAfter} from 'date-fns';
import axios from 'axios';
import './PatientDashboard.css';
import Header from '../Header';
import Footer from '../Footer';
import NavigationBar from '../NavigationBar';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
 
const PatientDashboard = () => {
    const navigate = useNavigate();
    const [patientInfo, setPatientInfo] = useState({ name: '', id: '', phoneNumber: '', age: '' });
    const [loadingPatient, setLoadingPatient] = useState(true);
    const [errorPatient, setErrorPatient] = useState(null);
    const [appointments, setAppointments] = useState([]);
    const [loadingAppointments, setLoadingAppointments] = useState(true);
    const [errorAppointments, setErrorAppointments] = useState(null);
    const [calendarDate, setCalendarDate] = useState(new Date());
    const [selectedDateAppointments, setSelectedDateAppointments] = useState([]);
 
 
    useEffect(() => {
        fetchPatientDetails();
        fetchAppointments();
    }, []);
 
    useEffect(() => {
        setSelectedDateAppointments(getAppointmentsForSelectedDate());
    }, [calendarDate, appointments]);
 
    const getPatientId = () => {
        return localStorage.getItem('patientId');
    };
 
    const getJwtToken = () => {
        return localStorage.getItem('jwtToken');
    };
 
        const fetchPatientDetails = async () => {
        setLoadingPatient(true);
        setErrorPatient(null);
        const patientId = getPatientId();
        const token = getJwtToken();
 
        if (patientId && token) {
            try {
                const response = await axios.get(`http://localhost:8083/api/patients/${patientId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (response.data.success && response.data.data) {
                    setPatientInfo({
                        name: response.data.data.name,
                        id: response.data.data.patientId,
                        phoneNumber: response.data.data.contactDetails,
                    });
                } else {
                    setErrorPatient(response.data.message || 'Failed to fetch patient details');
                }
            } catch (err) {
                setErrorPatient(err.message || 'An unexpected error occurred');
                console.error('Error fetching patient details:', err);
            } finally {
                setLoadingPatient(false);
            }
        } else {
            setErrorPatient('Patient ID or Token not found. Please log in or ensure patient information is available.');
            setLoadingPatient(false);
        }
    };
 
    const fetchAppointments = async () => {
        setLoadingAppointments(true);
        setErrorAppointments(null);
        const patientId = getPatientId();
        const token = getJwtToken();
   
        if (patientId && token) {
            try {
                const response = await axios.get(
                    `http://localhost:8083/api/patients/current/patient/${patientId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );
                if (response.data && response.data.success && response.data.data) {
                    const sortedAppointments = response.data.data.sort(
                        (a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate)
                    );
                    setAppointments(sortedAppointments);
                } else {
                    setErrorAppointments(
                        response.data?.message || 'Failed to fetch appointments'
                    );
                }
            } catch (err) {
                setErrorAppointments(err.message || 'An unexpected error occurred');
                console.error('Error fetching appointments:', err);
            } finally {
                setLoadingAppointments(false);
            }
        } else {
            setErrorAppointments(
                'Patient ID or Token not found. Please log in or ensure patient information is available.'
            );
            setLoadingAppointments(false);
        }
    };
 
    const getNextUpcomingAppointment = () => {
        const now = new Date();
        const upcoming = appointments
            .filter(appointment => {
                const appointmentDate = parseISO(appointment.appointmentDate);
                return isToday(appointmentDate) || isAfter(appointmentDate, now);
            })
            .sort((a, b) => {
                const dateA = parseISO(a.appointmentDate);
                const dateB = parseISO(b.appointmentDate);
                if (dateA > dateB) return 1;
                if (dateA < dateB) return -1;
                // If dates are the same, prioritize FN over AN
                if (a.session === 'FN' && b.session === 'AN') return -1;
                if (a.session === 'AN' && b.session === 'FN') return 1;
                return 0;
            });
   
        return upcoming[0] || null;
    };
 
    const getUpcomingAppointmentsCount = () => {
        const now = new Date();
        return appointments.filter(appointment => {
            const appointmentDateTime = parseISO(appointment.appointmentDate);
            return appointmentDateTime >= new Date(now.getFullYear(), now.getMonth(), now.getDate());
        }).length;
    };
 
    const tileContent = ({ date, view }) => {
        if (view === 'month') {
            const appointmentsOnDate = appointments.filter(appointment => {
                const appointmentDate = parseISO(appointment.appointmentDate);
                return format(appointmentDate, 'yyyy-MM-dd') === format(date, 'yyyy-MM-dd');
            });
 
            if (appointmentsOnDate.length > 0) {
                return <div className="appointment-dot"></div>;
            }
        }
        return null;
    };
 
    const handleCalendarChange = (date) => {
        setCalendarDate(date);
    };
 
    const getAppointmentsForSelectedDate = () => {
        return appointments.filter(appointment => {
            const appointmentDate = parseISO(appointment.appointmentDate);
            return format(appointmentDate, 'yyyy-MM-dd') === format(calendarDate, 'yyyy-MM-dd');
        });
    };
 
    return (
        <div className="patient-dashboard">
            <Header />
            <NavigationBar/>
            <div className="patient-dashboard-content">
                <div className="dashboard-grid">
                    <div className="calendar-card">
                        <h3>Appointments Calendar</h3>
                        <Calendar
                            onChange={handleCalendarChange}
                            value={calendarDate}
                            tileContent={tileContent}
                        />
                    </div>
                    <div className="selected-appointments">
                        <h4>Appointments on {format(calendarDate, 'MMMM dd, yyyy')}</h4>
                        {selectedDateAppointments.length > 0 ? (
                            selectedDateAppointments.map(appointment => (
                                <div key={appointment.appointmentId} className="appointment-item">
                                    <div className="appointment-info">
                                        <span className="doctor">Doctor: {appointment.doctorName}</span>
                                        <span className="specialization">Specialization: {appointment.specializationName}</span>
                                        <span className="date-time">
                                            {new Date(appointment.appointmentDate).toLocaleDateString()} {appointment.session}
                                        </span>
                                        <span className="status">Status: {appointment.status}</span>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <div className='welcome-message'> <p>NO APPOINTMENTS FOR THIS DAY</p></div>
                        )}
                    </div>
                    <div className="appointments-card">
                        <h3>Appointment Statistics</h3>
                        {/* <p>Hello!!! {patientInfo.name}... Welcome Back</p> */}
                        <div class="welcome-message">Welcome back <strong>{patientInfo.name}</strong>!!! 
                        <p>You can now view your latest health information and manage your appointments here.</p></div>
                        {getNextUpcomingAppointment() && (
    <div className="next-appointment">
        <p>
            <strong>Next Appointment:</strong>
        </p>
        <p>
            {new Date(getNextUpcomingAppointment().appointmentDate).toLocaleDateString()} {getNextUpcomingAppointment().session}
        </p>
        <p>
        Doctor: {getNextUpcomingAppointment().doctorName}
        </p>    
        <div className="next-appointment">
        <p>Upcoming appointments count: {getUpcomingAppointmentsCount()}</p>
        </div>
    </div>
)}
                    </div>
                </div>
               
            </div>
            <Footer />
        </div>
    );
};
 
export default PatientDashboard;
 
 