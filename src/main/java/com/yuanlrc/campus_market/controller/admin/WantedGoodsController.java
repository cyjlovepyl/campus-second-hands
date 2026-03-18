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
import com.yuanlrc.campus_market.entity.common.WantedGoods;
import com.yuanlrc.campus_market.service.common.WantedGoodsService;

/**
 * 后台求购物品管理控制器
 */
@RequestMapping("/wanted_goods")
@Controller
public class WantedGoodsController {

	@Autowired
	private WantedGoodsService wantedGoodsService;

	/**
	 * 求购物品列表
	 */
	@RequestMapping(value = "/list")
	public String list(WantedGoods wantedGoods, PageBean<WantedGoods> pageBean, Model model,
			@RequestParam(name = "student.sn", required = false) String studentSn) {
		if (studentSn != null && studentSn.trim().length() > 0) {
			model.addAttribute("sn", studentSn);
		} else {
			model.addAttribute("name", wantedGoods.getName());
		}
		model.addAttribute("pageBean", wantedGoodsService.findWantedGoodslist(pageBean, wantedGoods));
		return "admin/wanted_goods/list";
	}

	/**
	 * 求购物品删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		WantedGoods wantedGoods = wantedGoodsService.find(id);
		if (wantedGoods == null) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		try {
			wantedGoodsService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		return Result.success(true);
	}
}
