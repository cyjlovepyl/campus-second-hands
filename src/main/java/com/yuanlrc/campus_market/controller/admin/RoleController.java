package com.yuanlrc.campus_market.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.yuanlrc.campus_market.entity.admin.Menu;
import com.yuanlrc.campus_market.entity.admin.Role;
import com.yuanlrc.campus_market.service.admin.MenuService;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;
import com.yuanlrc.campus_market.service.admin.RoleService;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台角色管理控制器
 */
@RequestMapping("/role")
@Controller
public class RoleController {

	private Logger log = LoggerFactory.getLogger(RoleController.class);

	@Autowired
	private MenuService menuService;
	@Autowired
	private OperaterLogService operaterLogService;
	@Autowired
	private RoleService roleService;

	/**
	 * 角色列表
	 */
	@RequestMapping(value = "/list")
	public String list(Model model, Role role, PageBean<Role> pageBean) {
		model.addAttribute("pageBean", roleService.findByName(role, pageBean));
		model.addAttribute("name", role.getName());
		return "admin/role/list";
	}

	/**
	 * 角色添加页面
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("menuList", menuService.findAll());
		return "admin/role/add";
	}

	/**
	 * 角色添加表单提交
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> add(Role role) {
		CodeMsg validate = ValidateEntityUtil.validate(role);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		Role savedRole = roleService.save(role);
		if (savedRole == null) {
			return Result.error(CodeMsg.ADMIN_ROLE_ADD_ERROR);
		}
		operaterLogService.add("添加角色：" + role.getName());
		return Result.success(true);
	}

	/**
	 * 角色编辑页面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam(name = "id", required = true) Long id, Model model) {
		Role role = roleService.find(id);
		if (role == null) {
			return "redirect:list";
		}
		model.addAttribute("role", role);
		model.addAttribute("menuList", menuService.findAll());
		return "admin/role/edit";
	}

	/**
	 * 角色编辑表单提交
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> edit(Role role) {
		CodeMsg validate = ValidateEntityUtil.validate(role);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		Role existRole = roleService.find(role.getId());
		if (existRole == null) {
			return Result.error(CodeMsg.ADMIN_ROLE_NO_EXIST);
		}
		existRole.setName(role.getName());
		existRole.setAuthorities(role.getAuthorities());
		existRole.setStatus(role.getStatus());
		existRole.setRemark(role.getRemark());
		Role savedRole = roleService.save(existRole);
		if (savedRole == null) {
			return Result.error(CodeMsg.ADMIN_ROLE_EDIT_ERROR);
		}
		operaterLogService.add("编辑角色：" + role.getName());
		return Result.success(true);
	}

	/**
	 * 角色删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		Role role = roleService.find(id);
		if (role == null) {
			return Result.error(CodeMsg.ADMIN_ROLE_NO_EXIST);
		}
		try {
			roleService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.ADMIN_ROLE_DELETE_ERROR);
		}
		operaterLogService.add("删除角色：" + role.getName());
		return Result.success(true);
	}
}
