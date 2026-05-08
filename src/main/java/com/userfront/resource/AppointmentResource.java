package com.userfront.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.userfront.domain.Appointment;
import com.userfront.service.AppointmentService;

@RestController
@RequestMapping("/api/appointment")
@PreAuthorize("hasRole('ADMIN')")
public class AppointmentResource {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 获取所有预约列表
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ApiResponse<List<Appointment>> findAppointmentList() {
        try {
            List<Appointment> appointmentList = appointmentService.findAll();

            if (appointmentList == null || appointmentList.isEmpty()) {
                return ApiResponse.success("暂无预约记录", appointmentList);
            }

            return ApiResponse.success("获取预约列表成功", appointmentList);

        } catch (Exception e) {
            return ApiResponse.error(500, "获取预约列表失败: " + e.getMessage());
        }
    }

    /**
     * 确认预约
     */
    @RequestMapping(value = "/{id}/confirm", method = RequestMethod.PUT)
    public ApiResponse<Appointment> confirmAppointment(@PathVariable("id") Long id) {
        try {
            // 验证ID有效性
            if (id == null || id <= 0) {
                return ApiResponse.error(400, "预约ID无效");
            }

            // 查找预约
            Appointment appointment = appointmentService.findAppointment(id);

            // 检查预约是否存在
            if (appointment == null) {
                return ApiResponse.error(404, "预约不存在，ID: " + id);
            }

            // 检查预约是否已确认
            if (appointment.isConfirmed()) {
                return ApiResponse.error(400, "预约已被确认，请勿重复操作", appointment);
            }

            // 执行确认操作
            appointmentService.confirmAppointment(id);

            // 获取更新后的预约信息
            Appointment updatedAppointment = appointmentService.findAppointment(id);

            String message = String.format("预约 %d 已成功确认", id);
            return ApiResponse.success(message, updatedAppointment);

        } catch (Exception e) {
            return ApiResponse.error(500, "确认预约失败: " + e.getMessage());
        }
    }

}
