import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './DoctorPastAppointments.css';
import Header from '../Header';
import NavigationBar from '../NavigationBar';
import Footer from '../Footer';

function DoctorPastAppointments() {
    const  doctorId  = localStorage.getItem('doctorId');
    const navigate = useNavigate();
    const [pastAppointments, setPastAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const appointmentStatus = 'COMPLETED';

    useEffect(() => {
        fetchPastAppointments();
    }, [doctorId]);

    const fetchPastAppointments = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(`http://localhost:8089/api/doctors/past/appointments/${doctorId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            const sortedAppointments = response.data.data.sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
            setPastAppointments(sortedAppointments);
            setLoading(false);
        } catch (error) {
            setError('Error fetching past appointments');
            setLoading(false);
            console.error(error);
        }
    };

    const handleViewMedicalHistory = (patientId) => {
        navigate(`/doctor/${doctorId}/medical-history/patient/${patientId}`);
    };

    const handleFilter = async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get(
                `http://localhost:8089/api/doctors/filterAppointmentsByDate/${startDate}/${endDate}/${doctorId}/${appointmentStatus}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            const sortedAppointments = response.data.data.sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
            setPastAppointments(sortedAppointments);
        } catch (error) {
            setError('Error fetching filtered past appointments');
            console.error(error);
        }
    };

    if (loading) {
        return <div>Loading past appointments...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div>
            <Header/>
            <NavigationBar/>
        <div className="container">
            <h1>Past Appointments (Completed)</h1>
            <div className="filter-container">
                <label>Start Date:</label>
                <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                <label>End Date:</label>
                <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                <button onClick={handleFilter}>Filter</button>
            </div>
            <div className="appointments-container">
                {pastAppointments.map((appointment) => (
                    <div key={appointment.appointmentId} className="appointment-card">
                        <p><strong>Patient Name:</strong> {appointment.patientName}</p>
                        <p><strong>Appointment Date:</strong> {appointment.appointmentDate}</p>
                        <p><strong>Session:</strong> {appointment.session}</p>
                        <p><strong>Status:</strong> {appointment.status}</p>
                        <button className="view-medical-history-link" onClick={() => handleViewMedicalHistory(appointment.patientId)}>
                            View Medical History
                        </button>
                    </div>
                ))}
            </div>
        </div>
        <Footer/>
        </div>
    );
}

export default DoctorPastAppointments;

