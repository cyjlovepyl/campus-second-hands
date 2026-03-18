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
import com.yuanlrc.campus_market.entity.common.GoodsCategory;
import com.yuanlrc.campus_market.service.common.GoodsCategoryService;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台物品分类管理控制器
 */
@RequestMapping("/goods_category")
@Controller
public class GoodsCategoryController {

	@Autowired
	private GoodsCategoryService goodsCategoryService;

	/**
	 * 分类列表
	 */
	@RequestMapping(value = "/list")
	public String list(GoodsCategory goodsCategory, PageBean<GoodsCategory> pageBean, Model model) {
		model.addAttribute("pageBean", goodsCategoryService.findlist(pageBean, goodsCategory));
		model.addAttribute("name", goodsCategory.getName());
		return "admin/goods_category/list";
	}

	/**
	 * 分类添加页面
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("topCategoryList", goodsCategoryService.findTopCategorys());
		return "admin/goods_category/add";
	}

	/**
	 * 分类添加表单提交
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> add(GoodsCategory goodsCategory) {
		CodeMsg validate = ValidateEntityUtil.validate(goodsCategory);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		GoodsCategory savedCategory = goodsCategoryService.save(goodsCategory);
		if (savedCategory == null) {
			return Result.error(CodeMsg.ADMIN_GOODSCATEGORY_ADD_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 分类编辑页面
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam(name = "id", required = true) Long id, Model model) {
		model.addAttribute("goodsCategory", goodsCategoryService.findById(id));
		model.addAttribute("topCategoryList", goodsCategoryService.findTopCategorys());
		return "admin/goods_category/edit";
	}

	/**
	 * 分类编辑表单提交
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> edit(GoodsCategory goodsCategory) {
		CodeMsg validate = ValidateEntityUtil.validate(goodsCategory);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		GoodsCategory existCategory = goodsCategoryService.findById(goodsCategory.getId());
		if (existCategory == null) {
			return Result.error(CodeMsg.ADMIN_GOODSCATEGORY_EDIT_ERROR);
		}
		existCategory.setName(goodsCategory.getName());
		existCategory.setParent(goodsCategory.getParent());
		existCategory.setIcon(goodsCategory.getIcon());
		existCategory.setSort(goodsCategory.getSort());
		GoodsCategory savedCategory = goodsCategoryService.save(existCategory);
		if (savedCategory == null) {
			return Result.error(CodeMsg.ADMIN_GOODSCATEGORY_EDIT_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 分类删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		try {
			goodsCategoryService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.ADMIN_GOODSCATEGORY_DELETE_ERROR);
		}
		return Result.success(true);
	}
}
