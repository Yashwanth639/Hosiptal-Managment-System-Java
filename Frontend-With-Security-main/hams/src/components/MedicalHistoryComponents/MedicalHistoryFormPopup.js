import React, { useState } from 'react';
import './MedicalHistoryFormPopup.css';

function MedicalHistoryFormPopup({ appointmentId, onClose, onSubmit, errorMessage }) {
  const [medicalHistory, setMedicalHistory] = useState({
    appointmentId,
    medications: '',
    treatment: '',
    diagnosis: '',
    bloodPressure:'',
    heartRate:'',
    temperature:''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setMedicalHistory((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(medicalHistory);
  };

  return (
    
    <div className="popup">
      <div className="popup-content">
        <h2>Add Medical History</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Diagnosis:
            <input type="text" name="diagnosis" value={medicalHistory.diagnosis} onChange={handleChange} required />
          </label>
          <label>
            Treatment:
            <input type="text" name="treatment" value={medicalHistory.treatment} onChange={handleChange} required />
          </label>
          <label>
            Medications:
            <input type="text" name="medications" value={medicalHistory.medications} onChange={handleChange} required />
          </label>
          <label>
            BP:
            <input type="text" name="bloodPressure" value={medicalHistory.bloodPressure} onChange={handleChange} required />
          </label>
          <label>
            Heart Rate:
            <input type="text" name="heartRate" value={medicalHistory.heartRate} onChange={handleChange} required />
          </label>
          <label>
            Temperature:
            <input type="text" name="temperature" value={medicalHistory.temperature} onChange={handleChange} required />
          </label>
          <button type="submit">Submit</button>
          <button type="button" onClick={onClose}>Close</button>
          {errorMessage && <p className="error-message">{errorMessage}</p>}
        </form>
      </div>
    </div>
  );
}

export default MedicalHistoryFormPopup;
