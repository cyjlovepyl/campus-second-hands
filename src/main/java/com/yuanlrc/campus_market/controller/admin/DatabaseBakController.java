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
import com.yuanlrc.campus_market.entity.admin.DatabaseBak;
import com.yuanlrc.campus_market.service.admin.DatabaseBakService;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;

/**
 * 后台数据库备份管理控制器
 */
@RequestMapping("/database_bak")
@Controller
public class DatabaseBakController {

	@Autowired
	private DatabaseBakService databaseBakService;
	@Autowired
	private OperaterLogService operaterLogService;

	/**
	 * 数据库备份列表
	 */
	@RequestMapping(value = "/list")
	public String list(Model model, PageBean<DatabaseBak> pageBean) {
		model.addAttribute("pageBean", databaseBakService.findList(pageBean));
		return "admin/database_bak/list";
	}

	/**
	 * 立即备份数据库
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> add() {
		try {
			databaseBakService.backup();
		} catch (Exception e) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 还原数据库
	 */
	@RequestMapping(value = "/restore", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> restore(@RequestParam(name = "id", required = true) Long id) {
		DatabaseBak databaseBak = databaseBakService.find(id);
		if (databaseBak == null) {
			return Result.error(CodeMsg.ADMIN_DATABASE_BACKUP_NO_EXIST);
		}
		try {
			databaseBakService.restore(id);
		} catch (Exception e) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		return Result.success(true);
	}

	/**
	 * 删除备份记录（支持批量，ids以逗号分隔）
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "ids", required = true) String ids) {
		if (ids == null || ids.trim().length() == 0) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			databaseBakService.delete(Long.parseLong(idStr.trim()));
		}
		operaterLogService.add("删除数据库备份记录，ids=" + ids);
		return Result.success(true);
	}
}
