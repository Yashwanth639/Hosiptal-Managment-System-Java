// src/components/ContactUs.js

import React from 'react';
import './ContactUs.css';
import Header from './Header';
import Footer from './Footer';
import NavigationBar from './NavigationBar';

function ContactUs() {
  return (
    <div>
      <Header />
      <NavigationBar />
      <div className="contact-us-container">
        <h1>Connect With Us</h1>
        <p>
          We value your feedback and are always here to assist you. Whether you have questions about our services, need technical support, or want to provide suggestions, please feel free to reach out to us.
        </p>

        <section className="contact-ways">
          <h2>Ways to Contact Us</h2>

          <div className="contact-item">
            <i className="fas fa-map-marker-alt"></i>
            <div>
              <h3>Visit Us</h3>
              <p>123 Health Avenue, Cityville, State, 12345, Country</p>
            </div>
          </div>

          <div className="contact-item">
            <i className="fas fa-phone"></i>
            <div>
              <h3>Call Us</h3>
              <p>+1 (555) 123-4567</p>
              <p>Monday - Friday, 9:00 AM - 5:00 PM (Local Time)</p>
            </div>
          </div>

          <div className="contact-item">
            <i className="fas fa-envelope"></i>
            <div>
              <h3>Email Us</h3>
              <p>info@hospitalmanagementsystem.com</p>
              <p>We aim to respond within 24 hours.</p>
            </div>
          </div>

          <div className="contact-item">
            <i className="fas fa-globe"></i>
            <div>
              <h3>Our Website</h3>
              <p>www.hospitalmanagementsystem.com</p>
              <p>Visit our website for more information and resources.</p>
            </div>
          </div>
        </section>

        <section className="faq">
          <h2>Frequently Asked Questions</h2>
          <div className="faq-item">
            <h3>How do I book an appointment?</h3>
            <p>You can book an appointment through our website or by calling our appointment line.</p>
          </div>
          <div className="faq-item">
            <h3>What are your visiting hours?</h3>
            <p>Our visiting hours vary by department. Please check our website or contact us for specific timings.</p>
          </div>
          <div className="faq-item">
            <h3>Do you accept my insurance?</h3>
            <p>We work with a wide range of insurance providers. Please contact us to confirm if your insurance is accepted.</p>
          </div>
          <div className="faq-item">
            <h3>How can I access my medical records?</h3>
            <p>You can access your medical records through our patient portal or by requesting them in person.</p>
          </div>
        </section>

        <section className="social-media">
          <h2>Follow Us</h2>
          <div className="social-icons">
            <a href="#" className="social-icon"><i className="fab fa-facebook-f"></i></a>
            <a href="#" className="social-icon"><i className="fab fa-twitter"></i></a>
            <a href="#" className="social-icon"><i className="fab fa-linkedin-in"></i></a>
            <a href="#" className="social-icon"><i className="fab fa-instagram"></i></a>
          </div>
        </section>

        <p className="thank-you">
          Thank you for choosing Hospital Management System. We look forward to hearing from you!
        </p>
      </div>
      <Footer />
    </div>
  );
}

export default ContactUs;