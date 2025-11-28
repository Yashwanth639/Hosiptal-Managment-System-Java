import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../Header';
import NavigationBar from '../NavigationBar';
import Footer from '../Footer';
import './PatientMedicalHistory.css';
import axios from 'axios';

const PatientMedicalHistory = () => {
    const navigate = useNavigate();
    const [medicalHistory, setMedicalHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [noHistoryMessage, setNoHistoryMessage] = useState('');

    useEffect(() => {
        fetchMedicalHistory();
    },[]);

    const getPatientId = () => {
        return localStorage.getItem('patientId');
    };

    const getJwtToken = () => {
        return localStorage.getItem('jwtToken');
    };

    const fetchMedicalHistory = async () => {
        setLoading(true);
        setError(null);
        setNoHistoryMessage('');
        const patientId = getPatientId();
        const token = getJwtToken();

        if (patientId && token) {
            try {
                const response = await axios.get(`http://localhost:8083/api/patients/medicalHistory/${patientId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.data.success) {
                    if (response.data.data && Array.isArray(response.data.data) && response.data.data.length > 0) {
                        setMedicalHistory(response.data.data);
                    } else if (response.data.message) {
                        setNoHistoryMessage(response.data.message);
                    } else {
                        setNoHistoryMessage('No medical history found for this patient.');
                    }
                } else {
                    if (response.data && response.data.message) {
                        setError(response.data.message);
                    } else {
                        setError('Failed to fetch medical history');
                    }
                }
            } catch (err) {
                setError(err.message || 'An unexpected error occurred while fetching medical history');
                console.error('Error fetching medical history:', err);
            } finally {
                setLoading(false);
            }
        } else {
            setError('Patient ID or Token not found. Please log in.');
            setLoading(false);
        }
    };

    const handleGoBack = () => {
        navigate('/dashboard');
    };

    return (
        <div>
            <Header />
            <NavigationBar/>
            <div className="patient-medical-history-container">
                <h1>Patient Medical History</h1>
                {loading && <div className="loading-spinner"></div>}
                {error && <p className="error-message">{error}</p>}
                {noHistoryMessage && <p className="no-history">{noHistoryMessage}</p>}

                {!loading && !error && !noHistoryMessage && medicalHistory.length > 0 && (
                    <div className="medical-history-grid">
                        {medicalHistory.map((record) => (
                            <div key={record.historyId || Math.random()} className="history-record">
                                <div className="tape"></div>
                                <div className="record-details">
                                    <p><strong>Date:</strong> {record.dateOfVisit}</p>
                                    <p><strong>Doctor Name:</strong> {record.doctorName}</p>
                                    <p><strong>Specialization:</strong> {record.specializationName}</p>
                                    <p><strong>Diagnosis:</strong> {record.diagnosis}</p>
                                    <p><strong>Treatment:</strong> {record.treatment}</p>
                                    <p><strong>Medication:</strong> {record.medications}</p>
                                    <p><strong>BP:</strong> {record.bloodPressure}</p>
                                    <p><strong>Heart Rate:</strong> {record.heartRate}</p>
                                    <p><strong>Temperature:</strong> {record.temperature}</p>

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

export default PatientMedicalHistory;