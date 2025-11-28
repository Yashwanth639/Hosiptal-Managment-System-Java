import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import './NavigationBar.css';
import { Bell, User } from 'lucide-react';
import PatientNotification from './NotificationComponents/PatientNotification';


function NavigationBar() { // Removed props
    const navigate = useNavigate();
    const location = useLocation();
    const userLoggedIn = JSON.parse(localStorage.getItem('userLoggedIn')) || {};
    const isLoggedIn = localStorage.getItem('jwtToken') !== null;
    const userRole = userLoggedIn.role;
    const userId = userLoggedIn.userId;
    const patientId = localStorage.getItem('patientId');
    const doctorId = localStorage.getItem('doctorId');
    const [showNotifications, setShowNotifications] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [loadingNotifications, setLoadingNotifications] = useState(false);
    const [errorNotifications, setErrorNotifications] = useState(null);
    const [showDoctorDetailsPopup, setShowDoctorDetailsPopup] = useState(false);
    const [doctorDetails, setDoctorDetails] = useState(null);
    const [loadingDoctorDetails, setLoadingDoctorDetails] = useState(true);
    const [errorDoctorDetails, setErrorDoctorDetails] = useState(null);
    const [showAppointmentsDropdown, setShowAppointmentsDropdown] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);
    const detailsPopupRef = useRef(null);

    // New state for patient details
    const [patientInfo, setPatientInfo] = useState(null);
    const [loadingPatient, setLoadingPatient] = useState(false);
    const [errorPatient, setErrorPatient] = useState(null);
    const [showPatientDetails, setShowPatientDetails] = useState(false); // Added state for toggling

    const getJwtToken = () => {
        return localStorage.getItem('jwtToken');
    };

    // Fetch notifications and unread count
const fetchNotifications = async () => {
    const getSortedNotifications = (notifications) => {
      return [...notifications].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
    };
  
    if (userRole === 'ROLE_PATIENT' && patientId) {
      setLoadingNotifications(true);
      setErrorNotifications(null);
      try {
        const token = getJwtToken();
        const response = await fetch(`http://localhost:8083/api/patients/notifications/${patientId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        if (data.success && Array.isArray(data.data)) {
          // Sort the notifications before setting the state
          const sorted = getSortedNotifications(data.data);
          setNotifications(sorted);
          setUnreadCount(sorted.filter(notification => !notification.read).length);
        } else {
          setErrorNotifications(data.message || 'Failed to fetch notifications');
          setNotifications([]);
          setUnreadCount(0);
        }
      } catch (error) {
        setErrorNotifications(error.message || 'An unexpected error occurred');
        setNotifications([]);
        setUnreadCount(0);
      } finally {
        setLoadingNotifications(false);
      }
    }
    if (userRole === 'ROLE_DOCTOR' && doctorId) {
      setLoadingNotifications(true);
      setErrorNotifications(null);
      try {
        const token = getJwtToken();
        const response = await fetch(`http://localhost:8089/api/doctors/notifications/${doctorId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        if (data.success && Array.isArray(data.data)) {
          // Sort the notifications before setting the state
          const sorted = getSortedNotifications(data.data);
          setNotifications(sorted);
          setUnreadCount(sorted.filter(notification => !notification.read).length);
        } else {
          setErrorNotifications(data.message || 'Failed to fetch notifications');
          setNotifications([]);
          setUnreadCount(0);
        }
      } catch (error) {
        setErrorNotifications(error.message || 'An unexpected error occurred');
        setNotifications([]);
        setUnreadCount(0);
      } finally {
        setLoadingNotifications(false);
      }
    }
  };

    const fetchDoctorDetails = async () => {
        if (userRole === 'ROLE_DOCTOR' && doctorId) {
            setLoadingDoctorDetails(true);
            setErrorDoctorDetails(null);
            try {
                const token = getJwtToken();
                const response = await fetch(`http://localhost:8089/api/doctors/${doctorId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                if (data.success) {
                    setDoctorDetails(data.data);
                } else {
                    setErrorDoctorDetails(data.message || 'Failed to fetch doctor details');
                }
            } catch (error) {
                setErrorDoctorDetails(error.message || 'Error fetching doctor details');
            } finally {
                setLoadingDoctorDetails(false);
            }
        }
    };

    // New function to fetch patient details
    const fetchPatientDetails = async () => {
        if (userRole === 'ROLE_PATIENT' && patientId) {
            setLoadingPatient(true);
            setErrorPatient(null);
            try {
                const token = getJwtToken();
                const response = await fetch(`http://localhost:8083/api/patients/${patientId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                if (data.success && data.data) {
                    setPatientInfo({
                        name: data.data.name,
                        id: data.data.patientId,
                        age: calculateAge(data.data.dateOfBirth),
                        gender: data.data.gender,
                        weightInKg: data.data.weightInKg,
                        heightInCm: data.data.heightInCm ? String(data.data.heightInCm) : '',
                        phoneNumber: data.data.contactDetails, // Added phone number
                    });
                } else {
                    setErrorPatient(data.message || 'Failed to fetch patient details');
                }
            } catch (error) {
                setErrorPatient(error.message || 'An unexpected error occurred');
            } finally {
                setLoadingPatient(false);
            }
        }
    };

    const calculateAge = (dateOfBirth) => {
        const today = new Date();
        const birthDate = new Date(dateOfBirth);
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age.toString();
    };

    // Fetch notifications and other details on component mount and when the route changes
    useEffect(() => {
        if (userRole === 'ROLE_PATIENT') {
            fetchNotifications();
            fetchPatientDetails(); // Fetch patient details here
        }
        if (userRole === 'ROLE_DOCTOR') {
            fetchNotifications();
            fetchDoctorDetails();
        }
    }, [userRole, patientId, location.pathname, doctorId]);

    const handleLogout = () => {
        localStorage.clear();
        navigate('/');
        window.location.reload();
    };

    const toggleNotifications = () => {
        setShowNotifications(!showNotifications);
    };

    const closeNotificationPopup = () => {
        setShowNotifications(false);
    };
    const toggleDoctorDetailsPopup = () => {
        setShowDoctorDetailsPopup(!showDoctorDetailsPopup);
    };

    const togglePatientDetails = () => {
        setShowPatientDetails(!showPatientDetails);
    };

    const getLinks = () => {
        if (!isLoggedIn) {
            return null;
        }

        if (userRole === 'ROLE_DOCTOR') {
            return (
                <>
                    <Link to={`/doctor/${userId}/dashboard`} className="nav-link">Dashboard</Link>
                    <Link to={`/doctor/${userId}/appointments`} className="nav-link">Appointments</Link>
                    <Link to={`/doctor/${userId}/schedule`} className="nav-link">Schedule</Link>
                    <Link to={`/doctor/${userId}/prescriptions`} className="nav-link">Prescriptions</Link>
                    <Link to={`/doctor/${userId}/past-appointments`} className="nav-link">Past Appointments</Link>
                </>
            );
        } else if (userRole === 'ROLE_PATIENT') {
            return (
                <>
                    <Link to={`/patient-dashboard/${userId}`} className="nav-link">Dashboard</Link>
                    <div
                        className="nav-link appointments-dropdown-container"
                        onClick={() => setShowAppointmentsDropdown(!showAppointmentsDropdown)}
                    >
                        Appointments
                        {showAppointmentsDropdown && (
                            <div className="appointments-dropdown">
                                <Link to="/appointments" className="dropdown-link">Current Appointments</Link>
                                <Link to="/pastappointments" className="dropdown-link">Past Appointments</Link>
                            </div>
                        )}
                    </div>
                    <Link to="/medical-history" className="nav-link">Medical History</Link>
                    <Link to="/bookappointment" className="nav-link">Book Appointment</Link>
                </>
            );
        }
        return null;
    };

    const handleEditProfile = () => {
        navigate('/editprofile');
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (detailsPopupRef.current && !detailsPopupRef.current.contains(event.target)) {
                setShowDoctorDetailsPopup(false);
                setShowPatientDetails(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <nav className="nav">
            <div className="nav-links">
                {getLinks()}
                {isLoggedIn && (userRole === 'ROLE_DOCTOR' || userRole === 'ROLE_PATIENT') && (
                    <>
                        <div className="icon-container-nav">
                            <div className="notification-icon-container" onClick={toggleNotifications}>
                                <Bell className="notification-bell-icon" />
                                {unreadCount > 0 && (
                                    <span className="unread-count">{unreadCount}</span>
                                )}
                            </div>
                            {showNotifications && (
                                <PatientNotification
                                    notifications={notifications}
                                    setNotifications={setNotifications}
                                    onClose={closeNotificationPopup}
                                    setUnreadCount={setUnreadCount}
                                />
                            )}
                            {loadingNotifications && <div className="notification-loading-nav">Loading...</div>}
                            {errorNotifications && <div className="notification-error-nav">Error</div>}
                        </div>
                        {userRole === 'ROLE_DOCTOR' && (
                            <div className="icon-container-nav profile-nav">
                                <div className="circular-icon-container" onClick={toggleDoctorDetailsPopup}>
                                    <User className="circular-icon" />
                                </div>
                                {showDoctorDetailsPopup && doctorDetails && (
                                    <div className="details-popup-nav" ref={detailsPopupRef}>
                                        <h2>Doctor Details</h2>
                                        <p><span className="detail-label">Name:</span> {doctorDetails.name}</p>
                                        <p><span className="detail-label">Specialization Name:</span> {doctorDetails.specializationName}</p>
                                        <p><span className="detail-label">Gender:</span> {doctorDetails.gender}</p>
                                        <p><span className="detail-label">Contact Details:</span> {doctorDetails.contactDetails}</p>
                                        <button onClick={() => setShowDoctorDetailsPopup(false)}>Close</button>
                                    </div>
                                )}
                                {loadingDoctorDetails && <div className="profile-loading-nav">Loading...</div>}
                                {errorDoctorDetails && <div className="profile-error-nav">Error</div>}
                            </div>
                        )}
                        {userRole === 'ROLE_PATIENT' && (
                            <div className="profile-icon" onClick={togglePatientDetails} style={{ cursor: 'pointer' }}>
                                <div className="circular-icon-container">
                                    <User className="circular-icon" />
                                </div>
                            </div>
                        )}
                        {/* Patient Details Popup */}
                        {showPatientDetails && (
                            <div className="profile-details-expanded" ref={detailsPopupRef} style={{ position: 'absolute', right: 0, top: '40px' }}>
                                {loadingPatient ? (
                                    <p>Loading patient information...</p>
                                ) : errorPatient ? (
                                    <p className="error-message">Error: {errorPatient}</p>
                                ) : patientInfo ? (
                                    <>
                                        <p><span className="detail-label">Name:</span> {patientInfo.name}</p>
                                        {/* <p><span className="detail-label">ID:</span> {patientInfo.id}</p> */}
                                        <p><span className="detail-label">Gender:</span> {patientInfo.gender}</p>
                                        <p><span className="detail-label">Weight:</span> {patientInfo.weightInKg}</p>
                                        <p><span className="detail-label">Height:</span> {patientInfo.heightInCm}</p>
                                        <p><span className="detail-label">Phone:</span> {patientInfo.phoneNumber}</p>
                                        <p><span className="detail-label">Age:</span> {patientInfo.age}</p>
                                        <button onClick={handleEditProfile} className="edit-profile-button">Edit Profile</button>
                                    </>
                                ) : (
                                    <p>No patient information available.</p> //Or any message you want to show when there is no data
                                )}
                            </div>
                        )}
                    </>
                )}
                {isLoggedIn && (
                    <button onClick={handleLogout} className="logout-button">Logout</button>
                )}
            </div>
        </nav>
    );
}

export default NavigationBar;
