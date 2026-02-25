package com.userfront.service.UserServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userfront.dao.AppointmentDao;
import com.userfront.domain.Appointment;
import com.userfront.domain.User;
import com.userfront.service.AppointmentService;
import com.userfront.service.UserService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentDao appointmentDao;
    
    @Autowired
    private UserService userService;

    public Appointment createAppointment(Appointment appointment) {
       return appointmentDao.save(appointment);
    }

    public List<Appointment> findAll() {
        return appointmentDao.findAll();
    }
    
    public List<Appointment> findByUsername(String username) {
        User user = userService.findByUsername(username);
        return appointmentDao.findByUser(user);
    }

    public Appointment findAppointment(Long id) {
        return appointmentDao.findById(id).orElse(null);
    }

    public void confirmAppointment(Long id) {
        Appointment appointment = findAppointment(id);
        appointment.setConfirmed(true);
        appointmentDao.save(appointment);
    }
}
