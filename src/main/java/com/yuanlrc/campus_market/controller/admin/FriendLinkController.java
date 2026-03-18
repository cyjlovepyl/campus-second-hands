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
import com.yuanlrc.campus_market.entity.common.FriendLink;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;
import com.yuanlrc.campus_market.service.common.FriendLinkService;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台友情链接管理控制器
 */
@RequestMapping("/friend_link")
@Controller
public class FriendLinkController {

	@Autowired
	private FriendLinkService friendLinkService;
	@Autowired
	private OperaterLogService operaterLogService;

	/**
	 * 友情链接列表
	 */
	@RequestMapping(value = "/list")
	public String list(Model model, FriendLink friendLink, PageBean<FriendLink> pageBean) {
		model.addAttribute("pageBean", friendLinkService.findList(pageBean, friendLink));
		model.addAttribute("name", friendLink.getName());
		return "admin/friend_link/list";
	}

	/**
	 * 友情链接添加页面
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		return "admin/friend_link/add";
	}

	/**
	 * 友情链接添加表单提交
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> add(FriendLink friendLink) {
		CodeMsg validate = ValidateEntityUtil.validate(friendLink);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		FriendLink savedFriendLink = friendLinkService.save(friendLink);
		if (savedFriendLink == null) {
			return Result.error(CodeMsg.ADMIN_FRIENDLINK_ADD_ERROR);
		}
		operaterLogService.add("添加友情链接：" + friendLink.getName());
		return Result.success(true);
	}

	/**
	 * 友情链接编辑页面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model, @RequestParam(name = "id", required = true) Long id) {
		model.addAttribute("friendLink", friendLinkService.find(id));
		return "admin/friend_link/edit";
	}

	/**
	 * 友情链接编辑表单提交
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> edit(FriendLink friendLink) {
		CodeMsg validate = ValidateEntityUtil.validate(friendLink);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		FriendLink existFriendLink = friendLinkService.find(friendLink.getId());
		if (existFriendLink == null) {
			return Result.error(CodeMsg.ADMIN_FRIENDLINK_EDIT_ERROR);
		}
		existFriendLink.setName(friendLink.getName());
		existFriendLink.setUrl(friendLink.getUrl());
		existFriendLink.setSort(friendLink.getSort());
		FriendLink savedFriendLink = friendLinkService.save(existFriendLink);
		if (savedFriendLink == null) {
			return Result.error(CodeMsg.ADMIN_FRIENDLINK_EDIT_ERROR);
		}
		operaterLogService.add("编辑友情链接：" + friendLink.getName());
		return Result.success(true);
	}

	/**
	 * 友情链接删除（支持批量，ids以逗号分隔）
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "ids", required = true) String ids) {
		if (ids == null || ids.trim().length() == 0) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			friendLinkService.delete(Long.parseLong(idStr.trim()));
		}
		operaterLogService.add("删除友情链接，ids=" + ids);
		return Result.success(true);
	}
}
