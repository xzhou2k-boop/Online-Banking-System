package com.userfront.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.userfront.domain.Appointment;
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.AppointmentService;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private TransactionService transactionService;

    @RequestMapping("")
    public String adminHome() {
        return "redirect:/admin/users";
    }

    @RequestMapping("/users")
    public String userList(Model model) {
        List<User> userList = userService.findUserList();
        model.addAttribute("userList", userList);
        return "admin/users";
    }

    @RequestMapping("/user/{username}/enable")
    public String enableUser(@PathVariable("username") String username) {
        userService.disableUser(username); //Error
        return "redirect:/admin/users";
    }

    @RequestMapping("/user/{username}/disable")
    public String disableUser(@PathVariable("username") String username) {
        userService.enableUser(username); //Error
        return "redirect:/admin/users";
    }

    @RequestMapping("/appointments")
    public String appointmentList(Model model) {
        List<Appointment> appointmentList = appointmentService.findAll();
        model.addAttribute("appointmentList", appointmentList);
        return "admin/appointments";
    }

    @RequestMapping("/appointment/{id}/confirm")
    public String confirmAppointment(@PathVariable("id") Long id) {
//        appointmentService.confirmAppointment(id);
        return "redirect:/admin/appointments";
    }

    @RequestMapping("/transactions")
    public String transactionList(Model model) {
        List<PrimaryTransaction> primaryTransactionList = transactionService.findAllPrimaryTransactions();
        List<SavingsTransaction> savingsTransactionList = transactionService.findAllSavingsTransactions();
        model.addAttribute("primaryTransactionList", primaryTransactionList);
        model.addAttribute("savingsTransactionList", savingsTransactionList);
        return "admin/transactions";
    }
}
