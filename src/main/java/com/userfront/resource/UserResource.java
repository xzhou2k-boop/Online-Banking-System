package com.userfront.resource;

import java.util.List;

import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/user/all", method = RequestMethod.GET)
    public ApiResponse<List<User>> userList() {
        try {
            List<User> users = userService.findUserList();
            return ApiResponse.success("获取用户列表成功", users);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户列表失败: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/user/primary/transaction", method = RequestMethod.GET)
    public ApiResponse<List<PrimaryTransaction>> getPrimaryTransactionList(@RequestParam("username") String username) {
        try {
            List<PrimaryTransaction> transactions = transactionService.findPrimaryTransactionList(username);
            return ApiResponse.success("获取主账户交易记录成功", transactions);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取主账户交易记录失败: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/user/savings/transaction", method = RequestMethod.GET)
    public ApiResponse<List<SavingsTransaction>> getSavingsTransactionList(@RequestParam("username") String username) {
        try {
            List<SavingsTransaction> transactions = transactionService.findSavingsTransactionList(username);
            return ApiResponse.success("获取储蓄账户交易记录成功", transactions);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取储蓄账户交易记录失败: " + e.getMessage());
        }
    }

    @RequestMapping(value="/user/{username}/enable", method = RequestMethod.PUT)
    public ApiResponse<String> enableUser(@PathVariable("username") String username) {
        try {
            // 检查用户是否存在
            User user = userService.findByUsername(username);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在: " + username);
            }

            // 检查用户是否已启用
            if (user.isEnabled()) {
                return ApiResponse.error(400, "用户已经是启用状态: " + username);
            }

            // 执行启用操作
            userService.enableUser(username);

            String message = String.format("用户 %s 已成功启用", username);
            return ApiResponse.success(message, username);

        } catch (Exception e) {
            return ApiResponse.error(500, "启用用户失败: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/user/{username}/disable", method = RequestMethod.PUT)
    public ApiResponse<String> disableUser(@PathVariable("username") String username) {
        try {
            // 检查用户是否存在
            User user = userService.findByUsername(username);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在: " + username);
            }

            // 检查用户是否已被禁用
            if (!user.isEnabled()) {
                return ApiResponse.error(400, "用户已被禁用: " + username);
            }

            // 获取用户角色
            String userRole = getUserRole(user);

            // 检查是否是管理员
            if (isAdmin(user)) {
               return ApiResponse.error(403, "无法禁用管理员账户: " + username);
            }

            // 执行禁用操作
            userService.disableUser(username);


            String message = String.format("用户 %s 已成功禁用", username);
            return ApiResponse.success(message,username);

        } catch (Exception e) {
            return ApiResponse.error(500, "禁用用户失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否是管理员
     */
    private boolean isAdmin(User user) {
        if (user.getUserRoles() == null) {
            return false;
        }
        return user.getUserRoles().stream()
                .anyMatch(role -> role.getRole().getName().equals("ROLE_ADMIN"));
    }

    /**
     * 获取用户角色
     */
    private String getUserRole(User user) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return "UNKNOWN";
        }
        return user.getUserRoles().stream()
                .findFirst()
                .map(role -> role.getRole().getName())
                .orElse("UNKNOWN");
    }
}
