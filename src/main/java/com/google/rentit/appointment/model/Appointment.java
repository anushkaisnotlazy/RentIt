package com.google.rentit.appointment.model;

import java.time.Instant;

import com.google.rentit.common.enums.AppointmentStatus;
import com.google.rentit.property.model.Property;
import com.google.rentit.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Requester user cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requester; // The user who initiates the appointment request

    @NotNull(message = "Target user cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User target; // The property owner or flatmate being contacted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property; // Optional: If the appointment is related to a specific property

    @NotNull(message = "Appointment start time cannot be null")
    @Column(name = "appointment_start_time", nullable = false)
    private Instant appointmentStartTime;

    @NotNull(message = "Appointment end time cannot be null")
    @Column(name = "appointment_end_time", nullable = false)
    private Instant appointmentEndTime;

    @NotNull(message = "Appointment status cannot be null")
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;
    
}
