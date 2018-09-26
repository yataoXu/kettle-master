package com.leadingsoft.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.common.toolkit.Constant;
import com.leadingsoft.core.dto.ResultDto;
import com.leadingsoft.core.model.KQuartz;
import com.leadingsoft.core.model.KUser;
import com.leadingsoft.web.service.QuartzService;
import com.leadingsoft.web.utils.JsonUtils;

@RestController
@RequestMapping("/quartz/")
public class QuartzController {

	@Autowired
	private QuartzService quartzService;

	@RequestMapping("getSimpleList.shtml")
	public String simpleList(HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(quartzService.getSampleList(kUser.getuId()));
	}

	@RequestMapping("delete.shtml")
	public String delete(Integer quartzId, HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		quartzService.delete(quartzId, kUser.getuId());
		return ResultDto.success();
	}

	@RequestMapping("update.shtml")
	public String update(Integer quartzId, String quartzDescription, String quartzCron, String customerQuarz,
			HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		quartzCron=StringUtils.isBlank(customerQuarz)?quartzCron:customerQuarz;
		quartzService.update(quartzId, quartzDescription, quartzCron, kUser.getuId());
		return ResultDto.success();
	}
	@RequestMapping("insert.shtml")
	public String insert(Integer quartzId, String quartzDescription, String customerQuarz,
			HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID); 
		quartzService.insert(quartzId, quartzDescription, customerQuarz, kUser.getuId());
 
		return ResultDto.success();
	}

	@RequestMapping("update1.shtml")
	public String update(KQuartz kQuartz, String customerQuarz, HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		try {
			quartzService.update(kQuartz, customerQuarz, kUser.getuId());
			return ResultDto.success();
		} catch (Exception e) {
			return ResultDto.success(e.toString());
		}
	}

	@RequestMapping("getQuartz.shtml")
	public String getQuartz(Integer quartzId) {
		return ResultDto.success(quartzService.getQuartz(quartzId));
	}

	@RequestMapping("getList.shtml")
	public String List(HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(quartzService.getList(kUser.getuId()));
	}

}
