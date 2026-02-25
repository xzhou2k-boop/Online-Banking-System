package com.userfront.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String profilePost(@ModelAttribute("user") User newUser, Model model) {
        User user = userService.findByUsername(newUser.getUsername());
        user.setUsername(newUser.getUsername());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPhone(newUser.getPhone());

        model.addAttribute("user", user);

        userService.saveUser(user);

        return "profile";
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
        
        if (newPassword == null || newPassword.length() < 6) {
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

