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
import com.leadingsoft.core.mapper.KTransMonitorDao;
import com.leadingsoft.core.model.KTransMonitor;
import com.leadingsoft.web.utils.CommonUtils;

@Service
public class TransMonitorService {

	@Autowired
	private KTransMonitorDao kTransMonitorDao;

	/**
	 * @Title getList
	 * @Description 获取分页列表
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
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template, start, size);
		Long allCount = kTransMonitorDao.templateCount(template);
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(kTransMonitorList);
		bootTablePage.setTotal(allCount);
		return bootTablePage;
	}

	/**
	 * @Title getList
	 * @Description 获取不分页列表
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
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
		Collections.sort(kTransMonitorList);
		List<KTransMonitor> newKTransMonitorList = new ArrayList<KTransMonitor>();
		newKTransMonitorList = kTransMonitorList.subList(0,
				kTransMonitorList.size() > 5 ? 5 : kTransMonitorList.size());
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(newKTransMonitorList);
		bootTablePage.setTotal(5);
		return bootTablePage;
	}

	/**
	 * @Title getAllMonitorTrans
	 * @Description 获取全部被监控的转换
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllRunningMonitorTrans(Integer uId) {
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		template.setMonitorStatus(1);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
		return kTransMonitorList.size();
	}

	/**
	 * @Title getAllMonitorTrans
	 * @Description 获取全部被监控的转换
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllMonitorTrans(Integer uId) {
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
		return kTransMonitorList.size();
	}

	/**
	 * @Title getAllSuccess
	 * @Description 获取所有转换执行成功的次数的和
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllSuccess(Integer uId) {
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
		Integer allSuccess = 0;
		for (KTransMonitor KTransMonitor : kTransMonitorList) {
			allSuccess += KTransMonitor.getMonitorSuccess();
		}
		return allSuccess;
	}

	/**
	 * @Title getAllFail
	 * @Description 获取所有转换执行失败的次数的和
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllFail(Integer uId) {
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		// template.setMonitorStatus(1);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
		Integer allSuccess = 0;
		for (KTransMonitor KTransMonitor : kTransMonitorList) {
			allSuccess += KTransMonitor.getMonitorFail();
		}
		return allSuccess;
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
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		template.setMonitorId(monitorId);
		template.setMonitorSuccess(0);
		template.setMonitorFail(0);
		template.setRunStatus("");
		kTransMonitorDao.updateTemplateById(template);
	}

	/**
	 * @Title getTransLine
	 * @Description 获取7天内转换的折线图
	 * @param uId
	 *            用户ID
	 * @return
	 * @return Map<String,Object>
	 */
	public Map<String, Object> getTransLine(Integer uId) {
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(uId);
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		List<Integer> resultList = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			resultList.add(i, 0);
		}
		if (kTransMonitorList != null && !kTransMonitorList.isEmpty()) {
			for (KTransMonitor KTransMonitor : kTransMonitorList) {
				String runStatus = KTransMonitor.getRunStatus();
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
		resultMap.put("name", "转换");
		resultMap.put("data", resultList);
		return resultMap;
	}

	// 防止run_status超长
	public void maintain() {
		final String REGEX_USERNAME = "^\\d{13}-\\d{13}$";
		List<KTransMonitor> kTransMonitorList = kTransMonitorDao.all();
		Calendar ccalendar = Calendar.getInstance();
		// 过去七天
		ccalendar.setTime(new Date());
		ccalendar.add(Calendar.DATE, -7);
		Date weekAgo = ccalendar.getTime();
		// System.out.println(weekAgo.getTime());
		String temp = "";
		for (KTransMonitor kTransMonitor : kTransMonitorList) {
			temp = "";
			String runStatus = kTransMonitor.getRunStatus();
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
				KTransMonitor k = kTransMonitorDao.single(kTransMonitor.getMonitorId());
				k.setRunStatus(StringUtils.substringAfter(k.getRunStatus(), temp));
				kTransMonitorDao.updateTemplateById(k);
			}
		}

	}
}