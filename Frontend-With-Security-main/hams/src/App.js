// App.js
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import AuthPage from './components/LoginComponents/AuthPage';
import PatientDashboard from './components/PatientComponents/PatientDashboard';
import PatientMedicalHistory from './components/MedicalHistoryComponents/PatientMedicalHistory';
import YourAppointments from './components/PatientComponents/ManagingAppointments/YourAppointments';
import DynamicDoctorAppointment from './components/PatientComponents/ManagingAppointments/DynamicDoctorAppointment';
import PastAppointments from './components/PatientComponents/PastAppointments/PastAppointments';
import RescheduleAppointment from './components/PatientComponents/ManagingAppointments/RescheduleAppointment';
import EditProfile from './components/PatientComponents/ProfileComponents/EditProfile';
import DoctorAppointmentsTable from './components/DoctorComponents/DoctorAppointmentsTable';
import DoctorPastAppointments from './components/DoctorComponents/DoctorPastAppointments';
import Schedule from './components/DoctorComponents/Schedule';
import DoctorMedicalHistory from './components/MedicalHistoryComponents/DoctorMedicalHistory';
import DoctorDashboard from './components/DoctorComponents/DoctorDashboard';
import AppointmentConfirmation from './components/PatientComponents/ManagingAppointments/AppointmentConfirmation';
import AboutUs from './components/AboutUs';
import ContactUs from './components/ContactUs';
import AppointmentCancelledConfirmation from './components/PatientComponents/ManagingAppointments/AppointmentCancelledConfirmation';
import AppointmentRescheduledConfirmation from './components/PatientComponents/ManagingAppointments/AppointmentRescheduledConfirmation';
import { jwtDecode } from 'jwt-decode';
 
function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userRole, setUserRole] = useState('');
    const [userLoggedIn, setUserLoggedIn] = useState(() => {
        return JSON.parse(localStorage.getItem('userLoggedIn')) || {};
    });
    const [sessionExpired, setSessionExpired] = useState(false);
    const [loginDetails, setLoginDetails] = useState(null);
    const [patientInfo, setPatientInfo] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const [loadingPatient, setLoadingPatient] = useState(false);
    const [errorPatient, setErrorPatient] = useState(null);
 
    useEffect(() => {
        if (userLoggedIn && userLoggedIn.userId) {
            setIsLoggedIn(true);
            setUserRole(userLoggedIn.role);
        }
    }, [userLoggedIn]);
 
    const isLoggedInCheck=()=>{
        if(localStorage.getItem("userLoggedIn")){
          return true
        }else{
          return false
        }
      }
      const checkTokenExpiration = () => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
          const decodedToken = jwtDecode(token);
          const currentTime = Date.now();
          const expirationTime = decodedToken.exp * 1000;
          if (currentTime >= expirationTime) {
            setSessionExpired(true);
            setIsLoggedIn(false);
            localStorage.clear();
          }
        }
      };
      useEffect(() => {
        const userdata = localStorage.getItem('userLoggedIn');
        if (userdata) {
          setLoginDetails(JSON.parse(userdata));
          console.log(userdata);
          setIsLoggedIn(true);
          const token = localStorage.getItem('jwtToken');
          if (token) {
            const decodedToken = jwtDecode(token);
            const currentTime = Date.now();
            const expirationTime = decodedToken.exp * 1000;
            const timeUntilExpiration = expirationTime - currentTime;
       
            if (timeUntilExpiration > 0) {
              setTimeout(() => {
                setSessionExpired(true);
                setIsLoggedIn(false);
                localStorage.clear();
              }, timeUntilExpiration);
            } else {
              setSessionExpired(true);
              setIsLoggedIn(false);
              localStorage.clear();
            }
          }
          const intervalId = setInterval(checkTokenExpiration, 60000);
          return () => clearInterval(intervalId);
        }
      }, []);
      useEffect(()=>{
       var obj=JSON.parse(localStorage.getItem("userLoggedIn"))
        console.log('isLoggedInCheck:',isLoggedInCheck())
        setIsLoggedIn(isLoggedInCheck());
        if(isLoggedInCheck()){
          console.log(localStorage.getItem("userLoggedIn"))
          setUserLoggedIn(obj)
          console.log(obj.role)
          setUserRole(obj.role)
        }
      },[])
       
    return (
        <Router>
            <div className="app-container">
             
                <main className="main-content">
                    <Routes>
                        <Route
                            path="/"
                            element={
                                isLoggedIn ? (
                                    userRole === 'ROLE_PATIENT' ? (
                                        <Navigate to={`/patient-dashboard/${userLoggedIn.userId}`} />
                                    ) : (
                                        <Navigate to={`/doctor-dashboard/${userLoggedIn.userId}`} />
                                    )
                                ) : (
                                    <AuthPage setIsLoggedIn={setIsLoggedIn} setUserRole={setUserRole} />
                                )
                            }
                        />
                        <Route path="/about" element={<AboutUs />} />
                        <Route path="/contact" element={<ContactUs />} />
                        {isLoggedIn && userRole === 'ROLE_PATIENT' && (
                            <>
                                <Route path="/patient-dashboard/:userId" element={<PatientDashboard setLoggedIn={setIsLoggedIn} setNotifications={setNotifications} setPatientInfo={setPatientInfo} setLoadingPatient={setLoadingPatient} setErrorPatient={setErrorPatient} />} />
                                <Route path="/medical-history" element={<PatientMedicalHistory setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/appointments" element={<YourAppointments setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/bookappointment" element={<DynamicDoctorAppointment setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/appointment-confirmation" element={<AppointmentConfirmation setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/appointment-cancel-confirmation" element={<AppointmentCancelledConfirmation setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/appointment-reschedule-confirmation" element={<AppointmentRescheduledConfirmation setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/pastappointments" element={<PastAppointments setLoggedIn={setIsLoggedIn} />} />
                                <Route path="rescheduleappointment/:appointmentId/:doctorId" element={<RescheduleAppointment setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/editprofile" element={<EditProfile setLoggedIn={setIsLoggedIn} />} />
                            </>
                        )}
                        {isLoggedIn && userRole === 'ROLE_DOCTOR' && (
                            <>
                                <Route path="/doctor-dashboard/:userId" element={<DoctorDashboard setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/doctor/:doctorId/appointments" element={<DoctorAppointmentsTable setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/doctor/:doctorId/past-appointments" element={<DoctorPastAppointments setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/doctor/:doctorId/schedule" element={<Schedule setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/doctor/:doctorId/prescriptions" element={<DoctorMedicalHistory setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/doctor/:doctorId/medical-history" element={<DoctorMedicalHistory setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/doctor/:doctorId/medical-history/patient/:patientId" element={<DoctorMedicalHistory setLoggedIn={setIsLoggedIn} />} />
                                <Route path="/appointment-cancel-confirmation" element={<AppointmentCancelledConfirmation setLoggedIn={setIsLoggedIn} />} />
                            </>
                        )}
                        <Route path="*" element={<Navigate to="/" />} />
                    </Routes>
                </main>
            </div>
        </Router>
    );
}
 
export default App;
 
 