import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import './AuthPage.css';
import Footer from '../Footer';
import Header from '../Header';
 
const AuthPage = ({ setIsLoggedIn, setUserRole }) => {
  const [isSignIn, setIsSignIn] = useState(true);
  const [role, setRole] = useState('ROLE_PATIENT');
  const navigate = useNavigate();
 
  const [signInEmail, setSignInEmail] = useState('');
  const [signInPassword, setSignInPassword] = useState('');
  const [signInError, setSignInError] = useState('');
  const [isSignInLoading, setIsSignInLoading] = useState(false);
 
  const [signUpFormData, setSignUpFormData] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
    dateOfBirth: '',
    password: '',
    retypePassword: '',
    gender: '', // New field
    heightInCm: '', // New field
    weightInKg: '', // New field
  });
  const [signUpErrors, setSignUpErrors] = useState({});
  const [isSignUpLoading, setIsSignUpLoading] = useState(false);
  const [signUpError, setSignUpError] = useState('');
 
  const toggleAuthMode = () => {
    setIsSignIn(!isSignIn);
    setSignInEmail('');
    setSignInPassword('');
    setSignInError('');
    setSignUpFormData({
      fullName: '',
      email: '',
      phoneNumber: '',
      dateOfBirth: '',
      password: '',
      retypePassword: '',
      gender: '',
    });
    setSignUpErrors({});
    setSignUpError('');
  };
 
  const handleRoleChange = (e) => {
    const newRole = e.target.value;
    setRole(newRole);
    if (newRole === 'ROLE_DOCTOR') {
      setIsSignIn(true);
    }
  };
 
  const handleSignIn = async (e) => {
    e.preventDefault();
    setSignInError('');
 
    if (!signInEmail || !signInPassword) {
      setSignInError('Email and password are required');
      return;
    }
 
    setIsSignInLoading(true);
 
    try {
      const response = await fetch('http://localhost:8083/api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: signInEmail.trim(),
          passwordHash: signInPassword.trim(),
          role: { roleName: role },
        }),
      });
 
      if (response.ok) {
        const token = await response.text();
        const decodedToken = jwtDecode(token);
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('userLoggedIn', JSON.stringify(decodedToken));
        setIsLoggedIn(true);
        setUserRole(role);
 
        const userId = decodedToken.userId;
        localStorage.setItem('userId',userId);
        
        if (role === 'ROLE_PATIENT') {
          
          localStorage.setItem('patientId', userId);
          navigate(`/patient-dashboard/${userId}`);
        } else if (role === 'ROLE_DOCTOR') {
          localStorage.setItem('doctorId', userId);
          navigate(`/doctor-dashboard/${userId}`);
        }
      } else {
        if (response.status === 401) {
          try {
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
              const errorData = await response.json();
              console.log('Parsed JSON error:', errorData);
              setSignInError(errorData.message || 'Bad credentials. Please check your email and password.');
            } else {
              const textData = await response.text();
              console.log('Parsed Text error:', textData);
              setSignInError(textData || 'Bad credentials. Please check your email and password.');
            }
          } catch (parseError) {
            const textData = await response.text();
            console.log('Parse error, text data:', textData);
            setSignInError(textData || 'Bad credentials. Please check your email and password.');
          }
        } else {
          const errorData = await response.json();
          setSignInError(errorData.message || 'Login failed. Please check your credentials.');
        }
      }
    } catch (err) {
      console.error("Login error:", err); // Make sure this is present
      setSignInError('An unexpected error occurred. Please try again.');
    } finally {
      setIsSignInLoading(false);
    }
  };
 
 
  const handleSignUpChange = (e) => {
    const { name, value } = e.target;
    setSignUpFormData((prev) => ({ ...prev, [name]: value }));
  };
 
  const validateSignUpForm = () => {
    const newErrors = {};

    if (!signUpFormData.fullName) {
      newErrors.fullName = 'Full name is required';
    } else if (!/^[A-Za-z\s]+$/.test(signUpFormData.fullName)) {
      newErrors.fullName = 'Full name must only contain alphabets and spaces';
    }
    
    
    if (!signUpFormData.email) newErrors.email = 'Email is required';
    if (!signUpFormData.phoneNumber) newErrors.phoneNumber = 'Phone number is required';
    if (!signUpFormData.dateOfBirth) newErrors.dateOfBirth = 'Date of birth is required';
    if (!signUpFormData.password) newErrors.password = 'Password is required';
    if (signUpFormData.password !== signUpFormData.retypePassword) {
      newErrors.retypePassword = 'Passwords do not match';
    }
    if (!signUpFormData.gender) newErrors.gender = 'Gender is required'; // Basic validation for gender
 
    return newErrors;
  };
 
  const handleSignUpSubmit = async (e) => {
    e.preventDefault();
    const formErrors = validateSignUpForm();
    setSignUpErrors(formErrors);
 
    if (Object.keys(formErrors).length === 0) {
      setIsSignUpLoading(true);
      setSignUpError('');
      try {
        const response = await fetch('http://localhost:8083/api/users/register/patient', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            roleId: '3580387a-f57e-4df8-b9f1-0b4ed2650734',
            email: signUpFormData.email.trim(),
            passwordHash: signUpFormData.password.trim(),
            name: signUpFormData.fullName.trim(),
            dateOfBirth: signUpFormData.dateOfBirth,
            contactDetails: signUpFormData.phoneNumber.trim(),
            gender: signUpFormData.gender, // Include gender
        
          }),
        });
 
        if (response.ok) {
          alert('Registration successful! Please sign in.');
          setIsSignIn(true);
        } else {
          const errorData = await response.json();
          console.log("Error data:", errorData);
          if (errorData && errorData.message === "Validation failed" && errorData.data) {
            if (errorData.data.email) {
              console.log("Email error:", errorData.data.email);
              setSignUpError(errorData.data.email);
            } else if (errorData.data.passwordHash) {
              setSignUpError(errorData.data.passwordHash);
            } else if (errorData.data.dateOfBirth) {
              setSignUpError(errorData.data.dateOfBirth);
            } else if (errorData.data.contactDetails) {
              setSignUpError(errorData.data.contactDetails);
            } else if (errorData.data.gender) {
              setSignUpError(errorData.data.gender);
            }
          } else if (errorData && errorData.message === "Duplicate Entry : User with this email already exists") {
            setSignUpError("Duplicate Entry : User with this email already exists");
          } else if (errorData && errorData.message) {
            setSignUpError(errorData.message);
          } else {
            setSignUpError('Registration failed');
          }
        }
      } catch (error) {
        console.error('Error:', error);
        setSignUpError('An error occurred during registration');
      } finally {
        setIsSignUpLoading(false);
      }
    }
  };
 
 
  return (
    <div>
      <Header/>
    <div className="auth-container">
      <div className="auth-header">
        <h3>WELCOME to HAMS</h3>
      </div>
 
      <div className="auth-card">
        <div className="role-selector">
          <label>Select Role</label>
          <select value={role} onChange={handleRoleChange} className="role-dropdown">
            <option value="ROLE_PATIENT">Patient</option>
            <option value="ROLE_DOCTOR">Doctor</option>
          </select>
        </div>
 
        {role === 'ROLE_PATIENT' && (
          <div className="auth-toggle">
            <button className={`toggle-button ${isSignIn ? 'active' : ''}`} onClick={toggleAuthMode}>
              Sign In
            </button>
            <button className={`toggle-button ${!isSignIn ? 'active' : ''}`} onClick={toggleAuthMode}>
              Sign Up
            </button>
          </div>
        )}
 
        {(isSignIn || role === 'ROLE_DOCTOR') && (
          <form onSubmit={handleSignIn} className="auth-form">
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                value={signInEmail}
                onChange={(e) => setSignInEmail(e.target.value)}
                placeholder="Enter your email"
                required
              />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                value={signInPassword}
                onChange={(e) => setSignInPassword(e.target.value)}
                placeholder="Enter your password"
                required
              />
            </div>
            {signInError && <div className="error-message">{signInError}</div>}
            <button type="submit" className="auth-button" disabled={isSignInLoading}>
              {isSignInLoading ? 'Signing in...' : `Sign In as ${role === 'ROLE_PATIENT' ? 'Patient' : 'Doctor'}`}
            </button>
            <div className="form-footer">
              {role === 'ROLE_PATIENT' && (
                <p>
                  Don't have an account? <span className="link" onClick={toggleAuthMode}>Register</span>
                </p>
              )}
              <p className="security-info">Protected by industry standard encryption</p>
            </div>
          </form>
        )}
 
        {!isSignIn && role === 'ROLE_PATIENT' && (
          <form onSubmit={handleSignUpSubmit} className="auth-form" data-mode="signup">
            <div className="form-group">
              <label>Full Name</label>
              <input
                type="text"
                name="fullName"
                value={signUpFormData.fullName}
                onChange={handleSignUpChange}
                placeholder="Enter your full name"
              />
              {signUpErrors.fullName && <div className="error-message">{signUpErrors.fullName}</div>}
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={signUpFormData.email}
                onChange={handleSignUpChange}
                placeholder="Enter your email"
              />
              {signUpErrors.email && <div className="error-message">{signUpErrors.email}</div>}
            </div>
            <div className="form-group">
              <label>Phone Number</label>
              <input
                type="tel"
                name="phoneNumber"
                value={signUpFormData.phoneNumber}
                onChange={handleSignUpChange}
                placeholder="Enter your phone number"
              />
              {signUpErrors.phoneNumber && <div className="error-message">{signUpErrors.phoneNumber}</div>}
            </div>
            <div className="form-group">
              <label>Date of Birth</label>
              <input
                type="date"
                name="dateOfBirth"
                value={signUpFormData.dateOfBirth}
                onChange={handleSignUpChange}
              />
              {signUpErrors.dateOfBirth && <div className="error-message">{signUpErrors.dateOfBirth}</div>}
            </div>
            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                name="password"
                value={signUpFormData.password}
                onChange={handleSignUpChange}
                placeholder="Enter your password"
              />
              {signUpErrors.password && <div className="error-message">{signUpErrors.password}</div>}
            </div>
            <div className="form-group">
              <label>Retype Password</label>
              <input
                type="password"
                name="retypePassword"
                value={signUpFormData.retypePassword}
                onChange={handleSignUpChange}
                placeholder="Retype your password"
              />
              {signUpErrors.retypePassword && <div className="error-message">{signUpErrors.retypePassword}</div>}
            </div>
 
            {/* New Fields */}
            <div className="form-group">
              <label>Gender</label>
              <select
                name="gender"
                value={signUpFormData.gender}
                onChange={handleSignUpChange}
                className="role-dropdown" // You can reuse the style
              >
                <option value="">Select Gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Others">Others</option>
              </select>
              {signUpErrors.gender && <div className="error-message">{signUpErrors.gender}</div>}
            </div>
 
            {signUpError && <div className="error-message">{signUpError}</div>}
            <button type="submit" className="auth-button" disabled={isSignUpLoading}>
              {isSignUpLoading ? 'Registering...' : 'Register'}
            </button>
            <div className="form-footer">
              <p>
                Already have an account? <span className="link" onClick={toggleAuthMode}>Sign In</span>
              </p>
              <p className="security-info">Protected by industry standard encryption</p>
            </div>
          </form>
        )}
      </div>
    </div>
      <Footer/>
      </div>
  );
};
 
export default AuthPage;
 