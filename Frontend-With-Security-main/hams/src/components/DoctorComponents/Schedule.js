import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './Schedule.css';
import helpIcon from './images/help icon.jpeg';
import Header from '../Header'; // Adjust path as needed
import NavigationBar from '../NavigationBar'; // Adjust path as needed
import Footer from '../Footer'; // Adjust path as needed

function Schedule() {
    const doctorId = localStorage.getItem('doctorId');
    const [schedule, setSchedule] = useState([]);
    const [appointments, setAppointments] = useState([]);
    const [toggledSlots, setToggledSlots] = useState(new Set());
    const [popupDetails, setPopupDetails] = useState(null);
    const [showHelp, setShowHelp] = useState(false);
    const [cellMessages, setCellMessages] = useState({});
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const currentDate = new Date();

    useEffect(() => {
        if (doctorId) {
            fetchSchedule();
            fetchAppointments();
        }
    }, [doctorId]);

    const fetchSchedule = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(`http://localhost:8089/api/doctorAvailability/getSchedule/${doctorId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.data && Array.isArray(response.data.data)) {
                const sortedSchedule = response.data.data.sort((a, b) => new Date(a.availableDate) - new Date(b.availableDate));
                setSchedule(sortedSchedule);
                setToggledSlots(new Set());
            } else {
                console.error('API did not return an array within the data property:', response.data);
                setSchedule([]);
            }
        } catch (error) {
            console.error('Error fetching schedule:', error);
        }
    };

    const fetchAppointments = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(`http://localhost:8089/api/doctors/current/appointments/${doctorId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            const sortedAppointments = response.data.data.sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
            setAppointments(sortedAppointments);
        } catch (error) {
            console.error('Error fetching appointments:', error);
        }
    };

    const handleToggleAvailability = async (availabilityId, date, session) => {
        try {
            const token = localStorage.getItem('jwtToken');
            await axios.put(`http://localhost:8089/api/doctorAvailability/toggle/${availabilityId}`, {}, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchSchedule();
            setCellMessages(prev => ({
                ...prev,
                [`${date}-${session}`]: prev[`${date}-${session}`] === "Marked as unavailable" ? session : session
            }));
        } catch (error) {
            console.error('Error toggling availability:', error);
        }
    };

    const handleCellClick = (entry, session) => {
        if (entry[session] === 'booked') {
            const appointment = appointments.find(app => app.appointmentDate === entry.date && app.session === session);
            setPopupDetails({ date: entry.date, session, patientName: appointment ? appointment.patientName : 'Unknown' });
        } else {
            const availabilityId = session === 'FN' ? entry.availabilityIdFN : entry.availabilityIdAN;
            handleToggleAvailability(availabilityId, entry.date, session);
        }
    };

    const closePopup = () => {
        setPopupDetails(null);
    };

    const toggleHelp = () => {
        setShowHelp(!showHelp);
    };

    const handleFilter = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(`http://localhost:8089/api/doctorAvailability/filterSchedule/${startDate}/${endDate}/${doctorId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            const sortedSchedule = response.data.data.sort((a, b) => new Date(a.availableDate) - new Date(b.availableDate));
            setSchedule(sortedSchedule);
        } catch (error) {
            console.error('Error fetching filtered schedule:', error);
        }
    };

    const groupedSchedule = schedule.reduce((acc, entry) => {
        const date = entry.availableDate;
        if (!acc[date]) {
            acc[date] = { date, FN: 'available', AN: 'available', availabilityIdFN: null, availabilityIdAN: null };
        }
        if (entry.session === 'FN') {
            acc[date].FN = toggledSlots.has(entry.availabilityId) ? 'not-available' : (entry.isAvailable === 1 ? 'available' : (entry.isAvailable === 0 ? 'booked' : 'not-available'));
            acc[date].availabilityIdFN = entry.availabilityId;
        } else if (entry.session === 'AN') {
            acc[date].AN = toggledSlots.has(entry.availabilityId) ? 'not-available' : (entry.isAvailable === 1 ? 'available' : (entry.isAvailable === 0 ? 'booked' : 'not-available'));
            acc[date].availabilityIdAN = entry.availabilityId;
        }
        return acc;
    }, {});

    appointments.forEach(appointment => {
        const date = appointment.appointmentDate;
        const session = appointment.session;
        if (groupedSchedule[date]) {
            groupedSchedule[date][session] = 'booked';
        }
    });

    const uniqueSchedule = Object.values(groupedSchedule);

    const generateCalendarDates = () => {
        const dates = [];
        const start = new Date();
        const end = new Date();
        end.setDate(start.getDate() + 62);

        for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
            dates.push(new Date(d));
        }

        return dates;
    };

    const calendarDates = generateCalendarDates();

    return (
        <div className="schedule-page-container"> {/* New container to manage overall layout */}
            <Header />
            <NavigationBar />
            <div className="schedule-content"> {/* Container for the main schedule content */}
                <div className="schedule-header">
                    <h1>Doctor Availability (Next 62 Days)</h1>
                    <div className="help-section">
                        <img src={helpIcon} alt="Help" className="help-icon" onClick={toggleHelp} />
                        {showHelp && (
                            <div className="help-popup">
                                <div className="help-content">
                                    <h2>Help</h2>
                                    <p>Here you can manage your schedule and view appointments.</p>
                                    <p> - Click on a green cell to mark slot as unavailable</p>
                                    <p> - Click on an orange cell to mark slot as available</p>
                                    <p> - Click on a red cell to view appointments</p>
                                    <button onClick={toggleHelp}>Close</button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
                <div className="filter-container">
                    <label>
                        Start Date:
                        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                    </label>
                    <label>
                        End Date:
                        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                    </label>
                    <button onClick={handleFilter}>Filter</button>
                </div>
                <div className="legend">
                    <div className="legend-item">
                        <span className="legend-color available"></span> Available
                    </div>
                    <div className="legend-item">
                        <span className="legend-color not-available"></span> Not Available
                    </div>
                    <div className="legend-item">
                        <span className="legend-color booked"></span> Booked
                    </div>
                </div>
                <div className="calendar-grid">
                    {calendarDates.map((date) => {
                        const dateString = date.toISOString().split('T')[0];
                        const entry = groupedSchedule[dateString] || {};
                        const isPastDate = date < currentDate;
                        return (
                            <div key={dateString} className={`calendar-cell ${isPastDate ? 'past-date' : ''}`}>
                                <div className="date">{dateString}</div>
                                <div className={`slot ${entry.FN}`} onClick={() => handleCellClick(entry, 'FN')}>
                                    {cellMessages[`${dateString}-FN`] || 'FN'}
                                </div>
                                <div className={`slot ${entry.AN}`} onClick={() => handleCellClick(entry, 'AN')}>
                                    {cellMessages[`${dateString}-AN`] || 'AN'}
                                </div>
                            </div>
                        );
                    })}
                </div>
                {popupDetails && (
                    <div className="popup">
                        <div className="popup-content">
                            <h2>Appointment Details</h2>
                            <p>Date: {popupDetails.date}</p>
                            <p>Session: {popupDetails.session}</p>
                            <p>Patient Name: {popupDetails.patientName}</p>
                            <button onClick={closePopup}>Close</button>
                        </div>
                    </div>
                )}
            </div>
            <Footer />
        </div>
    );
}

export default Schedule;