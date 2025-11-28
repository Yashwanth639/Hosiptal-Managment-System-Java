import React, { useState, useEffect } from 'react';
import { parseISO, isBefore, format, startOfDay, isToday, isAfter } from 'date-fns';
import axios from 'axios';
import './DoctorDashboard.css';
import Header from '../Header';
import Footer from '../Footer';
import NavigationBar from '../NavigationBar';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip as ChartTooltip, Legend } from 'chart.js';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
 
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, ChartTooltip, Legend);
 
const DoctorDashboard = () => {
    const [appointments, setAppointments] = useState([]);
    const [loadingAppointments, setLoadingAppointments] = useState(true);
    const [errorAppointments, setErrorAppointments] = useState(null);
    const [calendarDate, setCalendarDate] = useState(new Date());
    const [selectedDateAppointments, setSelectedDateAppointments] = useState([]); // State to hold appointments for the selected date
    const [doctorInfo, setDoctorInfo] = useState({ name: '' }); // New state for doctor's name
    const [loadingDoctorInfo, setLoadingDoctorInfo] = useState(true); // New loading state
    const [errorDoctorInfo, setErrorDoctorInfo] = useState(null); // New error state
 
    const doctorId = localStorage.getItem('doctorId');
 
    useEffect(() => {
        fetchAppointments();
        fetchDoctorDetails(); // Fetch doctor details on component mount
    }, []);
 
    useEffect(() => {
        // This effect will run after the appointments are fetched
        if (!loadingAppointments && appointments.length > 0) {
            const today = new Date();
            const todaysAppointments = getAppointmentsForSelectedDate(today);
            setSelectedDateAppointments(todaysAppointments);
        }
    }, [appointments, loadingAppointments]);
 
    const getJwtToken = () => {
        return localStorage.getItem('jwtToken');
    };
 
    const fetchDoctorDetails = async () => {
        setLoadingDoctorInfo(true);
        setErrorDoctorInfo(null);
        const token = getJwtToken();
 
        if (doctorId && token) {
            try {
                const response = await axios.get(`http://localhost:8089/api/doctors/${doctorId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (response.data.success && response.data.data) {
                    setDoctorInfo({ name: response.data.data.name });
                } else {
                    setErrorDoctorInfo(response.data.message || 'Failed to fetch doctor details');
                }
            } catch (error) {
                setErrorDoctorInfo(error.message || 'An unexpected error occurred while fetching doctor details');
                console.error('Error fetching doctor details:', error);
            } finally {
                setLoadingDoctorInfo(false);
            }
        } else {
            //setErrorDoctorInfo('Doctor ID or Token not found for fetching doctor details.');
            setLoadingDoctorInfo(false);
        }
    };
 
    const fetchAppointments = async () => {
        setLoadingAppointments(true);
        setErrorAppointments(null);
        const token = getJwtToken();
 
        if (doctorId && token) {
            try {
                const response = await axios.get(`http://localhost:8089/api/doctors/current/appointments/${doctorId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
 
                if (response.data.success && response.data.data) {
                    const sortedAppointments = response.data.data.sort((a, b) => {
                        const dateA = parseISO(a.appointmentDate);
                        const dateB = parseISO(b.appointmentDate);
                        if (dateA > dateB) return 1;
                        if (dateA < dateB) return -1;
                        // If dates are equal, sort by session
                        if (a.session === 'FN' && b.session === 'AN') return -1;
                        if (a.session === 'AN' && b.session === 'FN') return 1;
                        return 0;
                    });
                    setAppointments(sortedAppointments);
                } else {
                    setErrorAppointments(response.data.message || 'Failed to fetch appointments');
                }
            } catch (err) {
                setErrorAppointments(err.message || 'An unexpected error occurred');
                console.error('Error fetching appointments:', err);
            } finally {
                setLoadingAppointments(false);
            }
        } else {
            setErrorAppointments('Doctor ID or Token not found. Please log in or ensure doctor information is available.');
            setLoadingAppointments(false);
        }
    };
 
    const getNextUpcomingAppointment = () => {
        const now = new Date();
        const upcoming = appointments
            .filter(appointment => isToday(parseISO(appointment.appointmentDate)) || isAfter(parseISO(appointment.appointmentDate), now))
            .sort((a, b) => {
                const dateA = parseISO(a.appointmentDate);
                const dateB = parseISO(b.appointmentDate);
                if (dateA > dateB) return 1;
                if (dateA < dateB) return -1;
                if (a.session === 'FN' && b.session === 'AN') return -1;
                if (a.session === 'AN' && b.session === 'FN') return 1;
                return 0;
            });
        return upcoming[0] || null;
    };
 
    const getUpcomingAppointmentsCount = () => {
        const now = startOfDay(new Date());
        return appointments.filter(appointment => {
            const appointmentDateTime = startOfDay(parseISO(appointment.appointmentDate));
            return isToday(appointmentDateTime) || appointmentDateTime > now;
            // return appointmentDateTime >= new Date(now.getFullYear(), now.getMonth(), now.getDate());
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
        const appointmentsForDate = getAppointmentsForSelectedDate(date);
        setSelectedDateAppointments(appointmentsForDate);
    };
 
    const getAppointmentsForSelectedDate = (date) => {
        return appointments.filter(appointment => {
            const appointmentDate = parseISO(appointment.appointmentDate);
            return format(appointmentDate, 'yyyy-MM-dd') === format(date, 'yyyy-MM-dd');
        });
    };
 
    const name = doctorInfo.name;
 
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
                        <h4>Appointments on {format(calendarDate, 'PPPP')}</h4>
                        {selectedDateAppointments.length > 0 ? (
                            selectedDateAppointments.map(appointment => (
                                <div key={appointment.appointmentId} className="appointment-item">
                                    <div className="appointment-info">
                                        <span className="patient">Patient: {appointment.patientName}</span>
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
                        {loadingDoctorInfo ? (
                            <p>Loading doctor information...</p>
                        ) : errorDoctorInfo ? (
                            <p className="error-message">Error: {errorDoctorInfo}</p>
                        ) : (
                            // <p><strong>Hello!!! Dr.{name}... Welcome Back</strong></p>
                            <div class="welcome-message">Welcome back <strong>Dr. {name}</strong>!!! 
                        <p>You can now view your schedule and manage your appointments here.</p></div>
                        )}
                        {getNextUpcomingAppointment() && (
                            <div className="next-appointment">
                                <p>
                                    <strong>Next Appointment:</strong>
                                </p>
                                <p>
                                    {new Date(getNextUpcomingAppointment().appointmentDate).toLocaleDateString()} {getNextUpcomingAppointment().session}
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
 
export default DoctorDashboard;
 
 