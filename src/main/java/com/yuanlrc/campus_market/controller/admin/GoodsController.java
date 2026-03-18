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
import com.yuanlrc.campus_market.entity.common.Goods;
import com.yuanlrc.campus_market.service.common.GoodsCategoryService;
import com.yuanlrc.campus_market.service.common.GoodsService;
import com.yuanlrc.campus_market.service.common.StudentService;

/**
 * 后台物品管理控制器
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {

	@Autowired
	private GoodsCategoryService goodsCategoryService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private StudentService studentService;

	/**
	 * 物品列表
	 */
	@RequestMapping(value = "/list")
	public String list(Goods goods, PageBean<Goods> pageBean, Model model) {
		model.addAttribute("pageBean", goodsService.findlist(pageBean, goods));
		model.addAttribute("name", goods.getName());
		model.addAttribute("goodsCategoryList", goodsCategoryService.findAll());
		return "admin/goods/list";
	}

	/**
	 * 物品上下架
	 */
	@RequestMapping(value = "/up_down", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> upDown(@RequestParam(name = "id", required = true) Long id,
			@RequestParam(name = "status", required = true) Integer status) {
		Goods goods = goodsService.findById(id);
		if (goods == null) {
			return Result.error(CodeMsg.ADMIN_GOODS_NO_EXIST);
		}
		if (goods.getStatus() == status) {
			return Result.error(CodeMsg.ADMIN_GOODS_STATUS_NO_CHANGE);
		}
		if (status != Goods.GOODS_STATUS_UP && status != Goods.GOODS_STATUS_DOWN) {
			return Result.error(CodeMsg.ADMIN_GOODS_STATUS_ERROR);
		}
		if (goods.getStatus() == Goods.GOODS_STATUS_SOLD) {
			return Result.error(CodeMsg.ADMIN_GOODS_STATUS_UNABLE);
		}
		goods.setStatus(status);
		Goods savedGoods = goodsService.save(goods);
		if (savedGoods == null) {
			return Result.error(CodeMsg.ADMIN_GOODS_EDIT_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 物品推荐/取消推荐
	 */
	@RequestMapping(value = "/recommend", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> recommend(@RequestParam(name = "id", required = true) Long id,
			@RequestParam(name = "recommend", required = true) Integer recommend) {
		Goods goods = goodsService.findById(id);
		if (goods == null) {
			return Result.error(CodeMsg.ADMIN_GOODS_NO_EXIST);
		}
		goods.setRecommend(recommend);
		Goods savedGoods = goodsService.save(goods);
		if (savedGoods == null) {
			return Result.error(CodeMsg.ADMIN_GOODS_EDIT_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 物品删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		Goods goods = goodsService.findById(id);
		if (goods == null) {
			return Result.error(CodeMsg.ADMIN_GOODS_NO_EXIST);
		}
		try {
			goodsService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.ADMIN_GOODS_DELETE_ERROR);
		}
		return Result.success(true);
	}
}
