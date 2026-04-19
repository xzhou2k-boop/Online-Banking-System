package com.userfront.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.userfront.domain.User;
import com.userfront.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);

        return "profile";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profilePost(@ModelAttribute("user") User newUser, Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        
        if (!newUser.getEmail().equals(currentUser.getEmail())) {
            User existingUserByEmail = userService.findByEmail(newUser.getEmail());
            if (existingUserByEmail != null && !existingUserByEmail.getUsername().equals(principal.getName())) {
                model.addAttribute("emailExists", true);
                model.addAttribute("user", currentUser);
                return "profile";
            }
        }
        
        if (!newUser.getUsername().equals(currentUser.getUsername())) {
            User existingUserByUsername = userService.findByUsername(newUser.getUsername());
            if (existingUserByUsername != null) {
                model.addAttribute("usernameExists", true);
                model.addAttribute("user", currentUser);
                return "profile";
            }
        }
        
        boolean usernameChanged = !newUser.getUsername().equals(currentUser.getUsername());
        String oldUsername = currentUser.getUsername();
        
        currentUser.setUsername(newUser.getUsername());
        currentUser.setFirstName(newUser.getFirstName());
        currentUser.setLastName(newUser.getLastName());
        currentUser.setEmail(newUser.getEmail());
        currentUser.setPhone(newUser.getPhone());

        userService.saveUser(currentUser);
        
        if (usernameChanged) {
            updateSecurityContext(newUser.getUsername());
        }
        
        model.addAttribute("user", currentUser);
        model.addAttribute("profileSuccess", "个人资料更新成功");

        return "profile";
    }
    
    private void updateSecurityContext(String newUsername) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                newUsername, auth.getCredentials(), auth.getAuthorities());
            newAuth.setDetails(auth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(Principal principal, 
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model) {
        User user = userService.findByUsername(principal.getName());
        
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordError", "两次输入的密码不一致");
            model.addAttribute("user", user);
            return "profile";
        }
        
//        if (newPassword == null || newPassword.length() < 6) {
        if (newPassword == null) { // Error
            model.addAttribute("passwordError", "密码长度不能少于6位");
            model.addAttribute("user", user);
            return "profile";
        }
        
        userService.updatePassword(principal.getName(), newPassword);
        model.addAttribute("passwordSuccess", "密码修改成功");
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        
        return "profile";
    }


}

