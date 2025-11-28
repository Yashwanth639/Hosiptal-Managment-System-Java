import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './DoctorMedicalHistory.css';
import Header from '../Header';
import NavigationBar from '../NavigationBar';
import Footer from '../Footer';

function MedicalHistory() {
    const { patientId, doctorId } = useParams();
    const [medicalHistories, setMedicalHistories] = useState([]);
    const [filter, setFilter] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchMedicalHistories = async () => {
            setLoading(true);
            setError(null);
            try {
                const token = localStorage.getItem('jwtToken');
                let response;
                const headers = {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                };
                if (patientId) {
                    if (startDate && endDate) {
                        response = await axios.get(`http://localhost:8089/api/doctors/filterByDate/${startDate}/${endDate}/${patientId}`, headers);
                    } else {
                        response = await axios.get(`http://localhost:8089/api/doctors/medical-history/patient/${patientId}`, headers);
                    }
                } else {
                    response = await axios.get(`http://localhost:8089/api/doctors/medical-history/doctor/${doctorId}`, headers);
                }
                setMedicalHistories(response.data.data);
            } catch (error) {
                setError('Failed to fetch medical history. Please check your network connection and try again.');
                console.error('Error fetching medical histories:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchMedicalHistories();
    }, [doctorId, patientId, startDate, endDate]);

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    };

    const handleStartDateChange = (e) => {
        setStartDate(e.target.value);
    };

    const handleEndDateChange = (e) => {
        setEndDate(e.target.value);
    };

    const filteredHistories = !patientId
        ? medicalHistories.filter(history =>
            history.patientName.toLowerCase().includes(filter.toLowerCase())
        )
        : medicalHistories.filter(history => {
            if (!startDate || !endDate) return true;
            const historyDate = new Date(history.dateOfVisit);
            const start = new Date(startDate);
            const end = new Date(endDate);
            return historyDate >= start && historyDate <= end;
        });

    return (
        <div>
            <Header />
            <NavigationBar />
            <div className="container">
                <h1>{patientId ? 'Patient Medical History' : 'Prescriptions'}</h1>
                {!patientId ? (
                    <div className="filter-container">
                        <label>
                            Filter by Patient Name:
                            <input type="text" value={filter} onChange={handleFilterChange} />
                        </label>
                    </div>
                ) : (
                    <div className="filter-container">
                        <label>
                            Start Date:
                            <input type="date" value={startDate} onChange={handleStartDateChange} />
                        </label>
                        <label>
                            End Date:
                            <input type="date" value={endDate} onChange={handleEndDateChange} />
                        </label>
                    </div>
                )}
                {loading ? (
                    <p>Loading Medical History...</p>
                ) : error ? (
                    <p className="error-message">{error}</p>
                ) : (
                    <div className="medical-history-grid">
                        {filteredHistories.map((history) => (
                            //console.log(history),
                            <div key={history.historyId} className="medical-history-grid-item">
                                
                                <div className="grid-info">
                                    <span className="grid-label">Patient:</span>
                                    <span className="grid-value">{history.patientName}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Doctor Visited:</span>
                                    <span className="grid-value">{history.doctorName}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Specialization:</span>
                                    <span className="grid-value">{history.specializationName}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Diagnosis:</span>
                                    <span className="grid-value">{history.diagnosis}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Treatment:</span>
                                    <span className="grid-value">{history.treatment}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Medications:</span>
                                    <span className="grid-value">{history.medications}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Date:</span>
                                    <span className="grid-value">{history.dateOfVisit}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">BP:</span>
                                    <span className="grid-value">{history.bloodPressure}</span>
                                </div>
                                <div className="grid-info">
                                <span className="grid-label">Heart Rate:</span>
                                    <span className="grid-value">{history.heartRate}</span>
                                </div>
                                <div className="grid-info">
                                    <span className="grid-label">Temperature:</span>
                                    <span className="grid-value">{history.temperature}</span>
                                </div>
                            </div>
                        ))}
                        {filteredHistories.length === 0 && !loading && !error && (
                            <p>No medical history found for the selected criteria.</p>
                        )}
                    </div>
                )}
            </div>
            <Footer />
        </div>
    );
}

export default MedicalHistory;