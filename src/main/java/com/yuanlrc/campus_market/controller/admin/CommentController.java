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
import com.yuanlrc.campus_market.entity.common.Comment;
import com.yuanlrc.campus_market.entity.common.Goods;
import com.yuanlrc.campus_market.service.common.CommentService;
import com.yuanlrc.campus_market.service.common.GoodsService;

/**
 * 后台评论管理控制器
 */
@RequestMapping("/comment")
@Controller
public class CommentController {

	@Autowired
	private CommentService commentService;
	@Autowired
	private GoodsService goodsService;

	/**
	 * 评论列表
	 */
	@RequestMapping(value = "/list")
	public String list(Comment comment, PageBean<Comment> pageBean, Model model,
			@RequestParam(name = "goods.name", required = false) String goodsName,
			@RequestParam(name = "student.sn", required = false) String studentSn) {
		List<Goods> goodsList = null;
		if (goodsName != null && goodsName.trim().length() > 0) {
			goodsList = goodsService.findListByName(goodsName);
			model.addAttribute("name", goodsName);
		} else if (studentSn != null && studentSn.trim().length() > 0) {
			model.addAttribute("sn", studentSn);
		}
		model.addAttribute("pageBean", commentService.findlist(pageBean, comment, goodsList));
		return "admin/comment/list";
	}

	/**
	 * 评论删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "id", required = true) Long id) {
		Comment comment = commentService.find(id);
		if (comment == null) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		try {
			commentService.delete(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		return Result.success(true);
	}
}
