package com.yuanlrc.campus_market.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yuanlrc.campus_market.bean.CodeMsg;
import com.yuanlrc.campus_market.bean.PageBean;
import com.yuanlrc.campus_market.bean.Result;
import com.yuanlrc.campus_market.entity.common.Student;
import com.yuanlrc.campus_market.service.common.StudentService;

/**
 * 后台学生管理控制器
 */
@RequestMapping("/student")
@Controller
public class StudentController {

	@Autowired
	private StudentService studentService;

	/**
	 * 学生列表
	 */
	@RequestMapping(value = "/list")
	public String list(Student student, PageBean<Student> pageBean, Model model) {
		model.addAttribute("pageBean", studentService.findlist(pageBean, student));
		model.addAttribute("sn", student.getSn());
		return "admin/student/list";
	}

	/**
	 * 学生状态修改（冻结/解冻）
	 */
	@RequestMapping(value = "/update_status", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> upDown(@RequestParam(name = "id", required = true) Long id,
			@RequestParam(name = "status", required = true) Integer status) {
		Student student = studentService.findById(id);
		if (student == null) {
			return Result.error(CodeMsg.ADMIN_STUDENT_NO_EXIST);
		}
		if (student.getStatus() == status) {
			return Result.error(CodeMsg.ADMIN_STUDENT_STATUS_NO_CHANGE);
		}
		if (status != Student.STUDENT_STATUS_ENABLE && status != Student.STUDENT_STATUS_UNABLE) {
			return Result.error(CodeMsg.ADMIN_STUDENT_STATUS_ERROR);
		}
		student.setStatus(status);
		Student savedStudent = studentService.save(student);
		if (savedStudent == null) {
			return Result.error(CodeMsg.ADMIN_STUDENT_EDIT_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 学生删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		Student student = studentService.findById(id);
		if (student == null) {
			return Result.error(CodeMsg.ADMIN_STUDENT_NO_EXIST);
		}
		try {
			studentService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.ADMIN_STUDENT_DELETE_ERROR);
		}
		return Result.success(true);
	}
}
