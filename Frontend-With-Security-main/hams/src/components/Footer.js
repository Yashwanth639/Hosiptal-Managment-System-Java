import React from 'react';
import { Link } from 'react-router-dom';
import './Footer.css';

function Footer() {
    return (
        <footer className="app-footer">
            <div className="footer-content">
                <p className="footer-copyright">
                    &copy; {new Date().getFullYear()} Hospital Management System. All rights reserved.
                </p>
                <div className="footer-social-links">
                    <a href="#" className="footer-social-link">
                        <i className="fab fa-facebook-f"></i>
                        <span className="sr-only">Facebook</span>
                    </a>
                    <span className="footer-separator">|</span> {/* Separator */}
                    <a href="#" className="footer-social-link">
                        <i className="fab fa-twitter"></i>
                        <span className="sr-only">Twitter</span>
                    </a>
                    <span className="footer-separator">|</span> {/* Separator */}
                    <a href="#" className="footer-social-link">
                        <i className="fab fa-linkedin-in"></i>
                        <span className="sr-only">LinkedIn</span>
                    </a>
                </div>
                <div className="footer-contact-info">
                    <p>
                        Contact Us:
                        <Link to="mailto:info@hospital.com">info@hospital.com</Link>
                    </p>
                    <p>Phone: +1 123-456-7890</p>
                </div>
            </div>
        </footer>
    );
}

export default Footer;