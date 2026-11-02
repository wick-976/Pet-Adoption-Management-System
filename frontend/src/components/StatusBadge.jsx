import React from 'react';

export const StatusBadge = ({ status, className = '' }) => {
  if (!status) return null;

  const normalizedStatus = String(status).toUpperCase();

  const getStatusConfig = () => {
    switch (normalizedStatus) {
      case 'AVAILABLE':
        return { label: 'Available', class: 'status-available' };
      case 'PENDING':
        return { label: 'Pending', class: 'status-pending' };
      case 'ADOPTED':
        return { label: 'Adopted', class: 'status-adopted' };
      case 'RECEIVED':
        return { label: 'Received', class: 'status-received' };
      case 'APPROVED':
        return { label: 'Approved', class: 'status-approved' };
      case 'REJECTED':
        return { label: 'Rejected', class: 'status-rejected' };
      default:
        return { label: status, class: 'status-pending' };
    }
  };

  const config = getStatusConfig();

  return (
    <span className={`status-badge ${config.class} ${className}`}>
      <span className="status-dot"></span>
      {config.label}
    </span>
  );
};

export default StatusBadge;
