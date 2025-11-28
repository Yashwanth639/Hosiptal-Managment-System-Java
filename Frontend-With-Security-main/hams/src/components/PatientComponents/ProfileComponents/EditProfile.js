import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './EditProfile.css';
import Header from '../../Header';
import NavigationBar from '../../NavigationBar';
import Footer from '../../Footer';

const EditProfile = () => {
    const navigate = useNavigate();
    const [patientInfo, setPatientInfo] = useState({
        name: '',
        contactDetails: '',
        dateOfBirth: '',
        gender: '', // New field
        heightInCm: '', // New field
        weightInKg: '', // New field
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');

    const getPatientId = () => localStorage.getItem('patientId');
    const getJwtToken = () => localStorage.getItem('jwtToken');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPatientInfo(prevState => ({ ...prevState, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccessMessage('');
        const patientId = getPatientId();
        const token = getJwtToken();

        if (patientId && token) {
            try {
                const response = await axios.put(`http://localhost:8083/api/patients/update/${patientId}`, {
                    ...patientInfo,
                    heightInCm: parseInt(patientInfo.heightInCm), // Ensure height is an integer
                    weightInKg: parseInt(patientInfo.weightInKg), // Ensure weight is an integer
                }, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (response.data.success) {
                    setSuccessMessage(response.data.message || 'Profile updated successfully!');
                    setTimeout(() => navigate('/dashboard'), 1500);
                } else {
                    setError(response.data.message || 'Failed to update profile');
                }
            } catch (err) {
                let errorMessage = 'An unexpected error occurred during update';
                if (err instanceof Error) {
                    errorMessage = err.message;
                }
                setError(errorMessage);
                console.error('Error updating profile:', err);
            } finally {
                setLoading(false);
            }
        } else {
            setError('Patient ID or Token not found.');
            setLoading(false);
        }
    };

    const handleGoBack = () => navigate('/dashboard');

    return (
        <div>
            <Header />
            <NavigationBar />
            <div className="edit-profile-container">
                <h1>Edit Profile</h1>
                {loading && <div className="loading-spinner"></div>}
                {error && <p className="error-message">{error}</p>}
                {successMessage && <p className="success-message">{successMessage}</p>}

                {!loading && !error && (
                    <form onSubmit={handleSubmit} className="edit-profile-form">
                        <div className="form-group">
                            <label htmlFor="name">Name:</label>
                            <input type="text" id="name" name="name" value={patientInfo.name} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="contactDetails">Phone Number:</label>
                            <input type="text" id="contactDetails" name="contactDetails" value={patientInfo.contactDetails} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="dateOfBirth">Date of Birth:</label>
                            <input type="date" id="dateOfBirth" name="dateOfBirth" value={patientInfo.dateOfBirth} onChange={handleChange} />
                        </div>

                        {/* New Fields */}
                        <div className="form-group">
                            <label htmlFor="gender">Gender:</label>
                            <select
                                id="gender"
                                name="gender"
                                value={patientInfo.gender}
                                onChange={handleChange}
                            >
                                <option value="">Select Gender</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Others">Others</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="heightInCm">Height (cm):</label>
                            <input
                                type="number"
                                id="heightInCm"
                                name="heightInCm"
                                value={patientInfo.heightInCm}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="weightInKg">Weight (kg):</label>
                            <input
                                type="number"
                                id="weightInKg"
                                name="weightInKg"
                                value={patientInfo.weightInKg}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="button-group">
                            <button type="submit" disabled={loading}>
                                {loading ? 'Updating...' : 'Update Profile'}
                            </button>
                            <button type="button" onClick={handleGoBack}>
                                Go Back to Dashboard
                            </button>
                        </div>
                    </form>
                )}
            </div>
            <Footer />
        </div>
    );
};

export default EditProfile;