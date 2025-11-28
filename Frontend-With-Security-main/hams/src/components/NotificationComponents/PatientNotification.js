import React from 'react';
import { CheckCircle, Bell } from 'lucide-react';
import axios from 'axios';
import './Notification.css';

/**
 * @typedef {object} Notification
 * @property {number} notificationId
 * @property {string} message
 * @property {boolean} read
 */

/**
 * @typedef {object} PatientNotificationProps
 * @property {Notification[]} notifications
 * @property {function} setNotifications
 * @property {function} onClose
 * @property {function} setUnreadCount
 */

const PatientNotification = ({ notifications, setNotifications, onClose, setUnreadCount }) => {
  const token = localStorage.getItem('jwtToken');
  const userId = localStorage.getItem('userId'); 

  const handleMarkAsRead = async (notificationId) => {
    try {
      const response = await axios.put(
        `http://localhost:8083/api/patients/notifications/markAsRead/${notificationId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (response.data.success) {
        const updatedNotifications = notifications.filter(
          (notification) => notification.notificationId !== notificationId
        );
        setNotifications(updatedNotifications);

        if (setUnreadCount) {
          const newUnreadCount = updatedNotifications.filter((n) => !n.read).length;
          setUnreadCount(newUnreadCount);
        }
        console.log(`Notification ${notificationId} marked as read.`);
      } else {
        console.error(`Failed to mark notification ${notificationId} as read:`, response.data.message);
      }
    } catch (error) {
      console.error(`Error marking notification ${notificationId} as read:`, error);
    }
  };

  const handleMarkAllAsRead = async () => {
    if (!userId) {
      console.error("User ID not found in local storage.");
      return;
    }
    try {
      const response = await axios.put(
        `http://localhost:8087/notification/markAllAsRead/${userId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (response.data.success) {
        setNotifications([]);
        if (setUnreadCount) {
          setUnreadCount(0);
        }
        console.log('All notifications marked as read.');
      } else {
        console.error('Failed to mark all notifications as read:', response.data.message);
      }
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  return (
    <div className="notifications-popup-overlay">
      <div className="notifications-popup-container small">
        <div className="notifications-header">
          <h2 className="notifications-title small">
            <Bell className="notifications-bell-icon small" />
            Notifications
          </h2>
          <div className="notifications-actions">
            <button onClick={handleMarkAllAsRead} className="mark-all-read-button small" aria-label="Mark all notifications as read">
              Mark All Read
            </button>
            <button onClick={onClose} className="close-button small" aria-label="Close notifications">
              Cancel
            </button>
          </div>
        </div>
        <ul className="notifications-list small">
          {notifications.length > 0 ? (
            notifications.map((notification) => (
              <li
                key={notification.notificationId}
                className="notification-item small"
              >
                <span className="notification-message small">{notification.message}</span>
                <button
                  onClick={() => handleMarkAsRead(notification.notificationId)}
                  className="mark-as-read-button small"
                  aria-label={`Mark notification ${notification.notificationId} as read`}
                >
                  <CheckCircle className="action-icon small" />
                </button>
              </li>
            ))
          ) : (
            <li className="no-notifications small">No new notifications.</li>
          )}
        </ul>
      </div>
    </div>
  );
};

export default PatientNotification;