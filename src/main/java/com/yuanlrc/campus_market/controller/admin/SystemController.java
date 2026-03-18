package com.yuanlrc.campus_market.controller.admin;

import javax.servlet.http.HttpServletRequest;

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
import com.yuanlrc.campus_market.constant.SessionConstant;
import com.yuanlrc.campus_market.entity.admin.OperaterLog;
import com.yuanlrc.campus_market.entity.admin.User;
import com.yuanlrc.campus_market.listener.SessionListener;
import com.yuanlrc.campus_market.service.admin.DatabaseBakService;
import com.yuanlrc.campus_market.service.admin.OperaterLogService;
import com.yuanlrc.campus_market.service.admin.UserService;
import com.yuanlrc.campus_market.service.common.CommentService;
import com.yuanlrc.campus_market.service.common.GoodsService;
import com.yuanlrc.campus_market.service.common.StudentService;
import com.yuanlrc.campus_market.service.common.WantedGoodsService;
import com.yuanlrc.campus_market.util.SessionUtil;
import com.yuanlrc.campus_market.util.ValidateEntityUtil;

/**
 * 后台系统管理控制器（登录、主页、日志等）
 */
@RequestMapping("/system")
@Controller
public class SystemController {

	private Logger log = LoggerFactory.getLogger(SystemController.class);

	@Autowired
	private OperaterLogService operaterLogService;
	@Autowired
	private UserService userService;
	@Autowired
	private DatabaseBakService databaseBakService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private WantedGoodsService wantedGoodsService;
	@Autowired
	private CommentService commentService;

	/**
	 * 后台登录页面
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		return "admin/system/login";
	}

	/**
	 * 后台登录表单提交
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> login(HttpServletRequest request, User user,
			@RequestParam(name = "cpacha", required = false) String cpacha) {
		if (user.getUsername() == null || user.getUsername().trim().length() == 0) {
			return Result.error(CodeMsg.ADMIN_USERNAME_EMPTY);
		}
		if (user.getPassword() == null || user.getPassword().trim().length() == 0) {
			return Result.error(CodeMsg.ADMIN_PASSWORD_EMPTY);
		}
		User loginUser = userService.findByUsername(user.getUsername());
		if (loginUser == null) {
			return Result.error(CodeMsg.ADMIN_USERNAME_NO_EXIST);
		}
		if (!loginUser.getPassword().equals(user.getPassword())) {
			return Result.error(CodeMsg.ADMIN_PASSWORD_ERROR);
		}
		if (loginUser.getStatus() != User.ADMIN_USER_STATUS_ENABLE) {
			return Result.error(CodeMsg.ADMIN_USER_UNABLE);
		}
		if (loginUser.getRole() == null || loginUser.getRole().getStatus() != 1) {
			return Result.error(CodeMsg.ADMIN_USER_ROLE_UNABLE);
		}
		if (loginUser.getRole().getAuthorities() == null || loginUser.getRole().getAuthorities().size() == 0) {
			return Result.error(CodeMsg.ADMIN_USER_ROLE_AUTHORITES_EMPTY);
		}
		SessionUtil.set(SessionConstant.SESSION_USER_LOGIN_KEY, loginUser);
		operaterLogService.add(loginUser.getUsername(), "登录后台管理系统");
		return Result.success(true);
	}

	/**
	 * 后台主页
	 */
	@RequestMapping(value = "/index")
	public String index(Model model) {
		model.addAttribute("databaseBackupTotal", databaseBakService.total());
		model.addAttribute("userTotal", userService.total());
		model.addAttribute("onlineUserTotal", SessionListener.onlineUserCount);
		model.addAttribute("operatorLogTotal", operaterLogService.total());
		model.addAttribute("studentTotal", studentService.total());
		model.addAttribute("goodsTotal", goodsService.total());
		model.addAttribute("wantGoodsTotal", wantedGoodsService.total());
		model.addAttribute("commentTotal", commentService.total());
		model.addAttribute("latestLogList", operaterLogService.findLastestLog(10));
		return "admin/system/index";
	}

	/**
	 * 退出登录
	 */
	@RequestMapping(value = "/logout")
	public String logout() {
		SessionUtil.set(SessionConstant.SESSION_USER_LOGIN_KEY, null);
		return "redirect:login";
	}

	/**
	 * 无权限页面
	 */
	@RequestMapping(value = "/no_right")
	public String noRight() {
		return "admin/system/no_right";
	}

	/**
	 * 修改用户信息页面
	 */
	@RequestMapping(value = "/update_userinfo", method = RequestMethod.GET)
	public String updateUserInfo() {
		return "admin/system/update_userinfo";
	}

	/**
	 * 修改用户信息表单提交
	 */
	@RequestMapping(value = "/update_userinfo", method = RequestMethod.POST)
	public String updateUserInfo(User user) {
		User loginedUser = SessionUtil.getLoginedUser();
		if (loginedUser == null) {
			return "redirect:login";
		}
		loginedUser.setHeadPic(user.getHeadPic());
		loginedUser.setMobile(user.getMobile());
		loginedUser.setEmail(user.getEmail());
		loginedUser.setSex(user.getSex());
		userService.save(loginedUser);
		SessionUtil.set(SessionConstant.SESSION_USER_LOGIN_KEY, loginedUser);
		operaterLogService.add("修改个人信息");
		return "redirect:update_userinfo";
	}

	/**
	 * 修改密码页面
	 */
	@RequestMapping(value = "/update_pwd", method = RequestMethod.GET)
	public String updatePwd() {
		return "admin/system/update_pwd";
	}

	/**
	 * 修改密码表单提交
	 */
	@RequestMapping(value = "/update_pwd", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> updatePwd(
			@RequestParam(name = "oldPwd", required = true) String oldPwd,
			@RequestParam(name = "newPwd", required = true) String newPwd) {
		User loginedUser = SessionUtil.getLoginedUser();
		if (loginedUser == null) {
			return Result.error(CodeMsg.USER_SESSION_EXPIRED);
		}
		if (!loginedUser.getPassword().equals(oldPwd)) {
			return Result.error(CodeMsg.ADMIN_USER_UPDATE_PWD_ERROR);
		}
		if (newPwd == null || newPwd.trim().length() == 0) {
			return Result.error(CodeMsg.ADMIN_USER_UPDATE_PWD_EMPTY);
		}
		loginedUser.setPassword(newPwd);
		userService.save(loginedUser);
		SessionUtil.set(SessionConstant.SESSION_USER_LOGIN_KEY, loginedUser);
		operaterLogService.add("修改登录密码");
		return Result.success(true);
	}

	/**
	 * 操作日志列表
	 */
	@RequestMapping(value = "/operator_log_list")
	public String operatorLogList(Model model, OperaterLog operaterLog, PageBean<OperaterLog> pageBean) {
		model.addAttribute("pageBean", operaterLogService.findList(operaterLog, pageBean));
		model.addAttribute("operator", operaterLog.getOperator());
		return "admin/system/operator_log_list";
	}

	/**
	 * 删除操作日志（支持批量，ids以逗号分隔）
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> delete(@RequestParam(name = "ids", required = true) String ids) {
		if (ids == null || ids.trim().length() == 0) {
			return Result.error(CodeMsg.DATA_ERROR);
		}
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			operaterLogService.delete(Long.parseLong(idStr.trim()));
		}
		return Result.success(true);
	}

	/**
	 * 清空操作日志
	 */
	@RequestMapping(value = "/delete_all", method = RequestMethod.POST)
	@ResponseBody
	public Result<Boolean> deleteAll() {
		operaterLogService.deleteAll();
		return Result.success(true);
	}
}
