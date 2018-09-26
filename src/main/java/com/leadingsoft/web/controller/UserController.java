package com.leadingsoft.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.common.toolkit.Constant;
import com.leadingsoft.core.dto.ResultDto;
import com.leadingsoft.core.model.KUser;
import com.leadingsoft.web.service.UserService;
import com.leadingsoft.web.utils.JsonUtils;

@RestController
@RequestMapping("/user/")
public class UserController {

	Logger logger = Logger.getLogger(UserController.class);
	@Autowired
	private UserService userService;

	@RequestMapping("getList.shtml")
	public String getList(Integer offset, Integer limit) {
		return JsonUtils.objectToJson(userService.getList(offset, limit));
	}

	@RequestMapping("getAllList.shtml")
	public String getAllList(Integer offset, Integer limit) {
		return JsonUtils.objectToJson(userService.getAllList(offset, limit));
	}

	@RequestMapping("delete.shtml")
	public String delete(Integer uId) {
		userService.delete(uId);
		return ResultDto.success();
	}

	@RequestMapping("resetPassword.shtml")
	public String resetPassword(Integer uId, HttpServletRequest request) {
		userService.resetPassword(uId, "tupperware");
		return ResultDto.success();
	}

	@RequestMapping("updatePassword.shtml")
	public String updatePassword(Integer uId, String oPassword, String uPassword, HttpServletRequest request) {
		// KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		if (StringUtils.isBlank(uPassword)) {
			return ResultDto.fail("密码不能为空！");
		}
		if (userService.validatePassword(uId, oPassword)) {
			userService.resetPassword(uId, uPassword);
		} else {
			return ResultDto.fail("请输入正确的旧密码");
		}
		return ResultDto.success();
	}

	@RequestMapping("insert.shtml")
	public String insert(KUser kUser, HttpServletRequest request) {
		KUser operator = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		userService.insert(kUser, operator.getuId());
		return ResultDto.success();
	}

	@RequestMapping("update.shtml")
	public String update(KUser kUser, HttpServletRequest request) {
		KUser operator = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		userService.update(kUser, operator.getuId());
		return ResultDto.success();
	}

	@RequestMapping("getUser.shtml")
	public String getUser(Integer uId, HttpServletRequest request) {
		KUser operator = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		logger.info(operator.getuAccount());
		if (operator.getuAccount().trim().equals("admin")) {

			return ResultDto.success(userService.getUser(uId));
		}
		return ResultDto.fail("only admin can do it");
	}

}
