package com.userfront.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.userfront.domain.Appointment;
import com.userfront.domain.User;

public interface AppointmentDao extends CrudRepository<Appointment, Long> {

    List<Appointment> findAll();
    
    List<Appointment> findByUser(User user);
}
