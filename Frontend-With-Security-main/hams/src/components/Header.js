import React from 'react';
import { Link } from 'react-router-dom'; // Use Link for internal navigation
import './Header.css';

function Header() {
    return (
        <header className="app-header">
            <div className="header-content">
                <Link to="/" className="app-title-link">  {/* Wrap title with Link */}
                    <h1 className="app-title">Hospital Appointment and Management System</h1>
                </Link>
                <nav className="header-nav">
                    <Link to="/" className="nav-link">Home</Link>
                    <Link to="/about" className="nav-link">About</Link>
                    <Link to="/contact" className="nav-link">Contact</Link>
                </nav>
            </div>
        </header>
    );
}

export default Header;