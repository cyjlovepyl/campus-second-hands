package com.yuanlrc.campus_market.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yuanlrc.campus_market.bean.CodeMsg;
import com.yuanlrc.campus_market.bean.Result;
import com.yuanlrc.campus_market.entity.common.SiteSetting;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;
import com.yuanlrc.campus_market.service.common.SiteSettingService;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台网站设置控制器
 */
@RequestMapping("/site_setting")
@Controller
public class SiteSettingController {

	@Autowired
	private SiteSettingService siteSettingService;
	@Autowired
	private OperaterLogService operaterLogService;

	/**
	 * 网站设置页面
	 */
	@RequestMapping(value = "/setting")
	public String setting(Model model) {
		model.addAttribute("siteSetting", siteSettingService.find());
		return "admin/site_setting/setting";
	}

	/**
	 * 保存网站设置
	 */
	@RequestMapping(value = "/save_setting", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> saveSetting(SiteSetting siteSetting) {
		CodeMsg validate = ValidateEntityUtil.validate(siteSetting);
		if (validate.getCode() != CodeMsg.SUCCESS.getCode()) {
			return Result.error(validate);
		}
		SiteSetting savedSetting = siteSettingService.save(siteSetting);
		if (savedSetting == null) {
			return Result.error(CodeMsg.ADMIN_SITESETTING_EDIT_ERROR);
		}
		operaterLogService.add("更新网站设置：" + siteSetting);
		return Result.success(true);
	}
}
