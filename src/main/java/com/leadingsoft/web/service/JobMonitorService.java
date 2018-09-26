package com.leadingsoft.web.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadingsoft.common.toolkit.Constant;
import com.leadingsoft.core.dto.BootTablePage;
import com.leadingsoft.core.mapper.KJobMonitorDao;
import com.leadingsoft.core.model.KJobMonitor;
import com.leadingsoft.web.utils.CommonUtils;

@Service
public class JobMonitorService {

	@Autowired
	private KJobMonitorDao kJobMonitorDao;

	/**
	 * @Title getList
	 * @Description 获取作业监控分页列表
	 * @param start
	 *            起始行数
	 * @param size
	 *            每页数据条数
	 * @param uId
	 *            用户ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer start, Integer size, Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template, start, size);
		Long allCount = kJobMonitorDao.templateCount(template);
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(kJobMonitorList);
		bootTablePage.setTotal(allCount);
		return bootTablePage;
	}

	/**
	 * @Title getList
	 * @Description 获取作业监控不分页列表
	 * @param start
	 *            起始行数
	 * @param size
	 *            每页数据条数
	 * @param uId
	 *            用户ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
		Collections.sort(kJobMonitorList);
		List<KJobMonitor> newKJobMonitorList = new ArrayList<KJobMonitor>();
		if (kJobMonitorList.size() >= 5) {
			newKJobMonitorList = kJobMonitorList.subList(0, 5);
		} else {
			newKJobMonitorList = kJobMonitorList;
		}
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(newKJobMonitorList);
		bootTablePage.setTotal(5);
		return bootTablePage;
	}

	/**
	 * @Title getAllMonitorJob
	 * @Description 获取所有的监控作业
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllMonitorJob(Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
		return kJobMonitorList.size();
	}

	/**
	 * @Title getAllMonitorJob
	 * @Description 获取所有的监控作业
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllRunningMonitorJob(Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
		return kJobMonitorList.size();
	}

	/**
	 * @Title getAllSuccess
	 * @Description 获取执行成功的数
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllSuccess(Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
		Integer allSuccess = 0;
		for (KJobMonitor KJobMonitor : kJobMonitorList) {
			allSuccess += KJobMonitor.getMonitorSuccess();
		}
		return allSuccess;
	}

	/**
	 * @Title getAllFail
	 * @Description 获取执行失败的数
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllFail(Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
		Integer allSuccess = 0;
		for (KJobMonitor KJobMonitor : kJobMonitorList) {
			allSuccess += KJobMonitor.getMonitorFail();
		}
		return allSuccess;
	}

	/**
	 * @Title getTransLine
	 * @Description 获取7天内作业的折线图
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Map<String,Object>
	 */
	public Map<String, Object> getJobLine(Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		List<Integer> resultList = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			resultList.add(i, 0);
		}
		if (kJobMonitorList != null && !kJobMonitorList.isEmpty()) {
			for (KJobMonitor KJobMonitor : kJobMonitorList) {
				String runStatus = KJobMonitor.getRunStatus();
				if (runStatus != null && runStatus.contains(",")) {
					String[] startList = runStatus.split(",");
					for (String startOnce : startList) {
						String[] startAndStopTime = startOnce.split(Constant.RUNSTATUS_SEPARATE);
						// 得到一次任务的起始时间和结束时间的毫秒值
						if (startAndStopTime.length > 1)
							resultList = CommonUtils.getEveryDayData(Long.parseLong(startAndStopTime[0]),
									Long.parseLong(startAndStopTime[1]), resultList);
					}
				}
			}
		}
		resultMap.put("name", "作业");
		resultMap.put("data", resultList);
		return resultMap;
	}

	// 防止run_status超长
	public void maintain() {
		final String REGEX_USERNAME = "^\\d{13}-\\d{13}$";
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.all();
		Calendar ccalendar = Calendar.getInstance();
		// 过去七天
		ccalendar.setTime(new Date());
		ccalendar.add(Calendar.DATE, -7);
		Date weekAgo = ccalendar.getTime();
		// System.out.println(weekAgo.getTime());
		String temp = "";
		for (KJobMonitor kJobMonitor : kJobMonitorList) {
			temp = "";
			String runStatus = kJobMonitor.getRunStatus();
			if ((runStatus.length() / 2) > 1) {
				runStatus = runStatus.substring(1, runStatus.length() / 2);
				String[] arr = runStatus.split(",");
				for (int i = arr.length - 1; i > 0; i--) {
					if (Pattern.matches(REGEX_USERNAME, arr[i])) {
						String[] timeRecords = arr[i].split("-");
						if (timeRecords.length == 2
								&& Long.valueOf(timeRecords[0].toString()) <= Long.valueOf(weekAgo.getTime())) {
							temp = arr[i];
							break;
						}

					}
				}
			}
			if (!StringUtils.isBlank(temp)) {
				KJobMonitor k = kJobMonitorDao.single(kJobMonitor.getMonitorId());
				k.setRunStatus(StringUtils.substringAfter(k.getRunStatus(), temp));
				kJobMonitorDao.updateTemplateById(k);
			}
		}

	}
	/**
	 * @Title getTransLine
	 * @Description 获取7天内转换的折线图
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Map<String,Object>
	 */
	public void resetCount(Integer monitorId, Integer uId) {
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);
		template.setMonitorId(monitorId);
		template.setMonitorSuccess(0);
		template.setMonitorFail(0);
		template.setRunStatus("");
		kJobMonitorDao.updateTemplateById(template);
	}
}