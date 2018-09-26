package com.leadingsoft.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.common.toolkit.Constant;
import com.leadingsoft.core.dto.BootTablePage;
import com.leadingsoft.core.dto.ResultDto;
import com.leadingsoft.core.model.KUser;
import com.leadingsoft.web.service.JobRecordService;
import com.leadingsoft.web.utils.JsonUtils;

@RestController
@RequestMapping("/job/record/")
public class JobRecordController {

	@Autowired
	private JobRecordService jobRecordService;
	
	@RequestMapping("getList.shtml")
	public String getList(Integer offset, Integer limit, Integer JobId, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = jobRecordService.getList(offset, limit, kUser.getuId(), JobId);				
		return JsonUtils.objectToJson(list);
	}
	
	@RequestMapping("getLogContent.shtml")
	public String getLogContent(Integer recordId){
		try {
			String logContent = jobRecordService.getLogContent(recordId);
			return ResultDto.success(logContent.replace("\r\n", "<br/>"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	
	@RequestMapping("delete.shtml")
	public String delete(Integer recordId) {
		try {
			jobRecordService.delete(recordId);
			return ResultDto.success();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping("deleteAll.shtml")
	public String deleteAll(HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		try {
			jobRecordService.deleteAll(kUser.getuId());
			return ResultDto.success();
		} catch (IOException e) {
			e.printStackTrace();
			return ResultDto.success(e.getMessage()); 
		}
	}

	@RequestMapping("deleteAllByJob.shtml")
	public String deleteAllByJob(Integer jobId, HttpServletRequest request) {
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		try {
			jobRecordService.deleteAllByTran(kUser.getuId(), jobId);
			return ResultDto.success();
		} catch (IOException e) {
			e.printStackTrace();
			return ResultDto.success(e.getMessage()); 
		}
	}
}