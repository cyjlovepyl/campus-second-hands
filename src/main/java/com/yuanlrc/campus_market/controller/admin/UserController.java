package com.yuanlrc.campus_market.controller.admin;

import java.util.List;

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
import com.yuanlrc.campus_market.entity.admin.Role;
import com.yuanlrc.campus_market.entity.admin.User;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;
import com.yuanlrc.campus_market.service.admin.RoleService;
import com.yuanlrc.campus_market.service.admin.UserService;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台用户管理控制器
 */
@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private OperaterLogService operaterLogService;

	/**
	 * 用户列表
	 */
	@RequestMapping(value = "/list")
	public String list(Model model, User user, PageBean<User> pageBean) {
		model.addAttribute("pageBean", userService.findList(user, pageBean));
		model.addAttribute("username", user.getUsername());
		return "admin/user/list";
	}

	/**
	 * 用户添加页面
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("roleList", roleService.findAll());
		return "admin/user/add";
	}

	/**
	 * 用户添加表单提交
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> add(User user) {
		CodeMsg validate = ValidateEntityUtil.validate(user);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		if (user.getRole() == null || user.getRole().getId() == null) {
			return Result.error(CodeMsg.ADMIN_USER_ROLE_EMPTY);
		}
		if (userService.isExistUsername(user.getUsername(), 0L)) {
			return Result.error(CodeMsg.ADMIN_USERNAME_EXIST);
		}
		User savedUser = userService.save(user);
		if (savedUser == null) {
			return Result.error(CodeMsg.ADMIN_USE_ADD_ERROR);
		}
		operaterLogService.add("添加用户：" + user.getUsername());
		return Result.success(true);
	}

	/**
	 * 用户编辑页面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model, @RequestParam(name = "id", required = true) Long id) {
		model.addAttribute("user", userService.find(id));
		model.addAttribute("roleList", roleService.findAll());
		return "admin/user/edit";
	}

	/**
	 * 用户编辑表单提交
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> edit(User user) {
		CodeMsg validate = ValidateEntityUtil.validate(user);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		if (user.getRole() == null || user.getRole().getId() == null) {
			return Result.error(CodeMsg.ADMIN_USER_ROLE_EMPTY);
		}
		User existUser = userService.find(user.getId());
		if (existUser == null) {
			return Result.error(CodeMsg.ADMIN_USE_NO_EXIST);
		}
		if (userService.isExistUsername(user.getUsername(), user.getId())) {
			return Result.error(CodeMsg.ADMIN_USERNAME_EXIST);
		}
		existUser.setUsername(user.getUsername());
		existUser.setPassword(user.getPassword());
		existUser.setRole(user.getRole());
		existUser.setStatus(user.getStatus());
		existUser.setHeadPic(user.getHeadPic());
		existUser.setSex(user.getSex());
		existUser.setMobile(user.getMobile());
		existUser.setEmail(user.getEmail());
		User savedUser = userService.save(existUser);
		if (savedUser == null) {
			return Result.error(CodeMsg.ADMIN_USE_EDIT_ERROR);
		}
		operaterLogService.add("编辑用户：" + user.getUsername());
		return Result.success(true);
	}

	/**
	 * 用户删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		User user = userService.find(id);
		if (user == null) {
			return Result.error(CodeMsg.ADMIN_USE_NO_EXIST);
		}
		try {
			userService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.ADMIN_USE_DELETE_ERROR);
		}
		operaterLogService.add("删除用户：" + user.getUsername());
		return Result.success(true);
	}
}
