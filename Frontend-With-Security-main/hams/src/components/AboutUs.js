// src/components/AboutUs.js

import React from 'react';
import './AboutUs.css'; // Import the CSS file
import Header from './Header';
import Footer from './Footer';
import NavigationBar from './NavigationBar';

function AboutUs() {
  return (
    <div>
      <Header />
      <NavigationBar />
      <div className="about-us-container">
        <h1>About Us</h1>
        <p>
          Welcome to Hospital Management System, a cutting-edge platform designed to streamline and enhance the healthcare experience for patients, doctors, and administrators alike. Our mission is to provide an efficient, reliable, and user-friendly system that simplifies hospital operations and improves patient care.
        </p>
        <section className="our-mission">
          <h2>Our Mission</h2>
          <p>
            To revolutionize healthcare management through technology, ensuring seamless communication, efficient appointment scheduling, and comprehensive medical record management. We strive to empower healthcare providers with the tools they need to deliver exceptional care.
          </p>
        </section>
        <section className="our-values">
          <h2>Our Values</h2>
          <ul>
            <li><strong>Patient-Centricity:</strong> We prioritize the needs and well-being of patients in every aspect of our system.</li>
            <li><strong>Innovation:</strong> We continuously seek innovative solutions to improve our platform and adapt to the evolving healthcare landscape.</li>
            <li><strong>Integrity:</strong> We uphold the highest standards of integrity and ethical conduct in all our operations.</li>
            <li><strong>Collaboration:</strong> We foster a collaborative environment that encourages teamwork and open communication.</li>
            <li><strong>Excellence:</strong> We are committed to delivering excellence in our services and exceeding the expectations of our users.</li>
          </ul>
        </section>
        <section className="our-team">
          <h2>Our Team</h2>
          <p>
            Our team comprises dedicated professionals with expertise in healthcare, technology, and customer service. We work tirelessly to ensure that our platform meets the highest standards of quality and reliability.
          </p>
          <p>
            We are passionate about making a positive impact on the healthcare industry and are committed to continuously improving our system to better serve our users.
          </p>
        </section>
      </div>
      <Footer />
    </div>
  );
}

export default AboutUs;