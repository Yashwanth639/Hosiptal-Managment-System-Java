import React, { useState, useEffect } from 'react';
import './RescheduleAppointment.css';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import Header from '../../Header';
import NavigationBar from '../../NavigationBar';
import Footer from '../../Footer';

const RescheduleAppointment = () => {
  const navigate = useNavigate();
  const { appointmentId, doctorId } = useParams();
  const [rescheduleDate, setRescheduleDate] = useState(null);
  const [selectedSession, setSelectedSession] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [doctorAvailabilityData, setDoctorAvailabilityData] = useState([]);
  const [isAvailabilityPopupVisible, setIsAvailabilityPopupVisible] = useState(false);
  const [sessions] = useState([
    { value: 'FN', label: 'FN' },
    { value: 'AN', label: 'AN' },
  ]);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  const [bookingError, setBookingError] = useState(null);

  const [appointmentDetails, setAppointmentDetails] = useState(null); // New state

  const getJwtToken = () => {
    return localStorage.getItem('jwtToken');
  };

  useEffect(() => {
    const fetchAppointmentDetails = async () => {
      setLoading(true);
      setError(null);
      const token = getJwtToken();
      try {
        const response = await axios.get(
          `http://localhost:8041/api/appointments/${appointmentId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        if (response.data.success && response.data.data) {
          setAppointmentDetails({
            doctorName: response.data.data.doctorName,
            oldAppointmentDate: response.data.data.appointmentDate,
            oldSession: response.data.data.session,
            doctorId: response.data.data.doctorId, // Keep doctorId for fetching availability
            appointmentId: response.data.data.appointmentId, // Keep appointmentId for rescheduling
            // You might want to include other relevant details if needed
          });
        } else {
          setError(response.data.message || 'Failed to fetch appointment details.');
        }
      } catch (error) {
        setError('Error fetching appointment details: ' + error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchAppointmentDetails();
  }, [appointmentId]);

  const fetchDoctorAvailabilityForReschedule = (doctorId) => {
    if (doctorId) {
      const token = localStorage.getItem('jwtToken');
      axios.get(`http://localhost:8089/api/doctorAvailability/getSchedule/${doctorId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then(response => {
          if (response.data.success && response.data.data) {
            const today = new Date();
            const futureDate = new Date();
            futureDate.setDate(today.getDate() + 60);

            const formattedData = response.data.data
              .filter(item => {
                const itemDate = new Date(item.availableDate);
                return itemDate >= today && itemDate <= futureDate;
              })
              .map(item => ({
                date: item.availableDate,
                available: item.isAvailable === 1,
                session: item.session,
              }))
              .sort((a, b) => new Date(a.date) - new Date(b.date));

            setDoctorAvailabilityData(formattedData);
            setIsAvailabilityPopupVisible(true);
          } else {
            setDoctorAvailabilityData([]);
            setIsAvailabilityPopupVisible(false);
            setError('Could not fetch doctor availability.');
          }
        })
        .catch(error => {
          setDoctorAvailabilityData([]);
          setIsAvailabilityPopupVisible(false);
          setError('Error fetching doctor availability: ' + error.message);
        });
    } else {
      setIsAvailabilityPopupVisible(false);
      setDoctorAvailabilityData([]);
      setError('Doctor ID is not available.');
    }
  };

  const closeAvailabilityPopup = () => {
    setIsAvailabilityPopupVisible(false);
  };

  const selectDateAndSession = (date, session) => {
    const selectedDate = new Date(date);
    setRescheduleDate(selectedDate);
    setSelectedSession(session);
    setIsAvailabilityPopupVisible(false);
    console.log('Selected Reschedule Date:', selectedDate);
    console.log('Selected Reschedule Session:', session);
  };

  const getDayOfWeek = (dateString) => {
    const date = new Date(dateString);
    const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    return days[date.getDay()];
  };

  const getMonthAndDate = (dateString) => {
    const date = new Date(dateString);
    const options = { month: 'short', day: 'numeric' };
    return date.toLocaleDateString(undefined, options);
  };

  const handleUpdate = async () => {
    if (!rescheduleDate || !selectedSession) {
      setError('Please select a new date and time slot.');
      return;
    }

    setLoading(true);
    setError(null);
    setSuccessMessage('');

    const formattedDate = rescheduleDate.toISOString().split('T')[0];
    const reschedulePayload = {
      appointmentId: appointmentId,
      doctorId: doctorId,
      newAppointmentDate: formattedDate,
      newSession: selectedSession.toUpperCase(),
    };

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await axios.post('http://localhost:8083/api/patients/rescheduleAppointment', reschedulePayload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      console.log("Reschedule Response:", response);

      if (response.data.success) {
        setSuccessMessage(response.data.message || 'Appointment rescheduled successfully!');
        
        navigate('/appointment-reschedule-confirmation');
        
      } else {
        const errorMessage = response.data.data?.message || response.data.message || "Failed to reschedule appointment";
        setError(errorMessage);
        setBookingSuccess(false);
        setBookingError(errorMessage);
      }
    } catch (error) {
      console.error("Reschedule Error:", error);

      let errorMessage = "Error rescheduling appointment";

      if (error.response?.data) {
        errorMessage = error.response.data.data?.message || error.response.data.message || "Error rescheduling appointment";
      }

      setError(errorMessage);
      setBookingSuccess(false);
      setBookingError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleGoBack = () => {
    navigate('/appointments');
  };

  return (
    <div>
      <Header />
      <NavigationBar />
      <div className="reschedule-appointment-container">
        <h1>Reschedule</h1>
        {successMessage && <p className="success-message">{successMessage}</p>}
        {error && <p className="error-message">{error}</p>}

        {loading && <p>Loading appointment details...</p>}
        {/* {!loading && error && <p className="error-message">{error}</p>} */}
        {!loading && appointmentDetails && (
          <>
            <div className="form-group">
              <label htmlFor="doctorName">Doctor Name:</label>
              <input
                type="text"
                id="doctorName"
                value={appointmentDetails.doctorName}
                readOnly
              />
            </div>

            <div className="form-group">
              <label htmlFor="oldAppointmentDate">Old Appointment Date:</label>
              <input
                type="text"
                id="oldAppointmentDate"
                value={new Date(appointmentDetails.oldAppointmentDate).toLocaleDateString()}
                readOnly
              />
            </div>

            <div className="form-group">
              <label htmlFor="oldSession">Old Session:</label>
              <input
                type="text"
                id="oldSession"
                value={appointmentDetails.oldSession}
                readOnly
              />
            </div>

            <div className="form-group">
              <label htmlFor="rescheduleDate">Reschedule Date:</label>
              {appointmentDetails.doctorId ? (
                <button
                  type="button"
                  className="view-availability"
                  onClick={() =>
                    fetchDoctorAvailabilityForReschedule(
                      appointmentDetails.doctorId
                    )
                  }
                >
                  View Availability
                </button>
              ) : (
                <input type="text" value="Doctor ID not available" disabled />
              )}
              {rescheduleDate && (
                <p>Selected Date: {rescheduleDate?.toLocaleDateString()}</p>
              )}
              {selectedSession && (
                <p>
                  Selected Session:{' '}
                  {sessions.find((s) => s.value === selectedSession)?.label ||
                    'No Session Selected'}
                </p>
              )}
            </div>

            {isAvailabilityPopupVisible && (
              <div className="availability-popup compact">
                <h3>Doctor Availability (Next 60 Days)</h3>
                {doctorAvailabilityData.length > 0 ? (
                  <div className="availability-grid">
                    {doctorAvailabilityData.map((item) => (
                      <div
                        key={item.date}
                        className={`availability-slot ${
                          item.available ? 'available' : 'unavailable'
                        }`}
                        title={
                          item.available
                            ? `Available for ${
                                sessions.find((s) => s.value === item.session)
                                  ?.label
                              }`
                            : 'Unavailable'
                        }
                        onClick={() =>
                          item.available &&
                          selectDateAndSession(item.date, item.session)
                        }
                      >
                        <span className="day-of-week">
                          {getDayOfWeek(item.date)}
                        </span>
                        <span className="date-number">
                          {getMonthAndDate(item.date)}
                        </span>
                        <span className="session-indicator dynamic">
                          {sessions.find((s) => s.value === item.session)?.label}
                        </span>
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

            <div className="button-group">
              <button
                type="button"
                onClick={handleUpdate}
                disabled={!rescheduleDate || !selectedSession || loading}
              >
                {loading ? 'Updating...' : 'Update'}
              </button>
              <button type="button" onClick={handleGoBack} disabled={loading}>
                Go Back
              </button>
            </div>
          </>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default RescheduleAppointment;