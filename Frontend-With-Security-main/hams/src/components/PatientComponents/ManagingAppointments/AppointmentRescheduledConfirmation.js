import React from 'react';
import { useNavigate } from 'react-router-dom';
import './AppointmentConfirmation.css';
import Header from '../../Header';
import NavigationBar from '../../NavigationBar';
import Footer from '../../Footer';

const AppointmentRescheduledConfirmation = () => {
    const navigate = useNavigate();

    const handleGoBack = () => {
        navigate('/appointments');
    };

    return (
        <div>
            <Header />
            <NavigationBar />
            <div className="appointment-confirmation-container">
                <h1>Appointment Rescheduled Successfully!</h1>
                <p>Your appointment has been rescheduled.</p>
                <button onClick={handleGoBack} className="go-back-button">
                    Go to My Appointments
                </button>
            </div>
            <Footer />
        </div>
    );
};

export default AppointmentRescheduledConfirmation;