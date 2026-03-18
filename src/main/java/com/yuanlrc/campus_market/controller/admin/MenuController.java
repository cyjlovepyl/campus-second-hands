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
import com.yuanlrc.campus_market.bean.Result;
import com.yuanlrc.campus_market.entity.admin.Menu;
import com.yuanlrc.campus_market.service.admin.MenuService;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台菜单管理控制器
 */
@RequestMapping("/menu")
@Controller
public class MenuController {

	@Autowired
	private MenuService menuService;
	@Autowired
	private OperaterLogService operaterLogService;

	/**
	 * 菜单列表
	 */
	@RequestMapping(value = "/list")
	public String list(Model model) {
		model.addAttribute("menuList", menuService.findAll());
		return "admin/menu/list";
	}

	/**
	 * 菜单添加页面
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("menuList", menuService.findAll());
		return "admin/menu/add";
	}

	/**
	 * 菜单添加表单提交
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> add(Menu menu) {
		CodeMsg validate = ValidateEntityUtil.validate(menu);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		Menu savedMenu = menuService.save(menu);
		if (savedMenu == null) {
			return Result.error(CodeMsg.ADMIN_MENU_ADD_ERROR);
		}
		operaterLogService.add("添加菜单：" + menu.getName());
		return Result.success(true);
	}

	/**
	 * 菜单编辑页面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model, @RequestParam(name = "id", required = true) Long id) {
		model.addAttribute("menu", menuService.find(id));
		model.addAttribute("menuList", menuService.findAll());
		return "admin/menu/edit";
	}

	/**
	 * 菜单编辑表单提交
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> edit(Menu menu) {
		CodeMsg validate = ValidateEntityUtil.validate(menu);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		if (menu.getId() == null) {
			return Result.error(CodeMsg.ADMIN_MENU_ID_EMPTY);
		}
		Menu existMenu = menuService.find(menu.getId());
		if (existMenu == null) {
			return Result.error(CodeMsg.ADMIN_MENU_ID_ERROR);
		}
		existMenu.setName(menu.getName());
		existMenu.setParent(menu.getParent());
		existMenu.setUrl(menu.getUrl());
		existMenu.setIcon(menu.getIcon());
		existMenu.setSort(menu.getSort());
		existMenu.setButton(menu.isButton());
		existMenu.setShow(menu.isShow());
		Menu savedMenu = menuService.save(existMenu);
		if (savedMenu == null) {
			return Result.error(CodeMsg.ADMIN_MENU_EDIT_ERROR);
		}
		operaterLogService.add("编辑菜单：" + menu.getName());
		return Result.success(true);
	}

	/**
	 * 菜单删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		Menu menu = menuService.find(id);
		if (menu == null) {
			return Result.error(CodeMsg.ADMIN_MENU_ID_ERROR);
		}
		// 检查是否有子菜单
		List<Menu> allMenus = menuService.findAll();
		for (Menu m : allMenus) {
			if (m.getParent() != null && m.getParent().getId().longValue() == id.longValue()) {
				return Result.error(CodeMsg.ADMIN_MENU_DELETE_ERROR);
			}
		}
		try {
			menuService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.ADMIN_MENU_DELETE_ERROR);
		}
		operaterLogService.add("删除菜单：" + menu.getName());
		return Result.success(true);
	}
}
