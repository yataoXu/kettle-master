package com.leadingsoft.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadingsoft.core.dto.BootTablePage;
import com.leadingsoft.core.mapper.KQuartzDao;
import com.leadingsoft.core.model.KJob;
import com.leadingsoft.core.model.KQuartz;

@Service
public class QuartzService {

	@Autowired
	private JobService jobService;

	@Autowired
	private KQuartzDao kQuartzDao;

	/**
	 * @Title getList
	 * @Description 获取定时策略列表
	 * @return
	 * @throws KettleException
	 * @return List<KQuartz>
	 */
	public void updateJobStatus(KJob kjob) {
		jobService.stop(kjob);
	}

	public List<KQuartz> getSampleList(Integer uId) {
		List<KQuartz> resultList = new ArrayList<KQuartz>();
		KQuartz kQuartz = new KQuartz();
		kQuartz.setDelFlag(1);
		kQuartz.setAddUser(uId);
		resultList.addAll(kQuartzDao.template(kQuartz));
		return resultList;
	}

	public BootTablePage getList(Integer uId) {
		List<KQuartz> resultList = new ArrayList<KQuartz>();
		KQuartz kQuartz = new KQuartz();
		kQuartz.setDelFlag(1);
		kQuartz.setAddUser(uId);
		resultList.addAll(kQuartzDao.template(kQuartz));
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(resultList);
		bootTablePage.setTotal(resultList.size());
		return bootTablePage;
	}

	public KQuartz getQuartz(Integer quartzId) {
		return kQuartzDao.single(quartzId);

	}

	public void update(Integer quartzId, String quartzDescription, String Quarz, Integer uId) {
		KQuartz kQuartz = kQuartzDao.single(quartzId);
		kQuartz.setQuartzDescription(quartzDescription);
		kQuartz.setQuartzCron(Quarz);
		kQuartz.setEditTime(new Date());
		kQuartzDao.updateTemplateById(kQuartz);
	}

	public void insert(Integer quartzId, String quartzDescription, String Quarz, Integer uId) {

		KQuartz kQuartz = kQuartzDao.single(quartzId);
		if (null == kQuartz) {
			kQuartz = new KQuartz();
			kQuartz.setQuartzDescription(quartzDescription);
			kQuartz.setQuartzCron(Quarz);
			kQuartz.setAddTime(new Date());
			kQuartz.setDelFlag(1);
			kQuartz.setAddUser(uId);
			kQuartzDao.insertReturnKey(kQuartz);
		} else {
			update(quartzId, quartzDescription, Quarz, uId);
		}
		kQuartz.setQuartzDescription(quartzDescription);
		kQuartz.setQuartzCron(Quarz);
		kQuartz.setEditTime(new Date());
		kQuartzDao.updateTemplateById(kQuartz);
	}

	public void update(KQuartz kQuartz, String customerQuarz, Integer uId) {
		if (!StringUtils.isBlank(customerQuarz)) {
			kQuartz.setQuartzCron(customerQuarz);
		}
		kQuartzDao.updateTemplateById(kQuartz);
	}

	public void delete(Integer quartzId, Integer uId) {
		KQuartz template = new KQuartz();
		template.setAddUser(uId);
		template.setQuartzId(quartzId);
		template.setDelFlag(0);
		template.setEditTime(new Date());
		template.setEditUser(uId);
		kQuartzDao.updateTemplateById(template);
	}

	/**
	 * @Title getList
	 * @Description 获取分页列表
	 * @param start
	 *            起始行数
	 * @param size
	 *            每页行数
	 * @param uId
	 *            用户ID
	 * @return
	 * @throws KettleException
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer start, Integer size, Integer uId) {
		KQuartz kQuartz = new KQuartz();
		kQuartz.setDelFlag(1);
		kQuartz.setAddUser(uId);
		List<KQuartz> kQuartzList = kQuartzDao.template(kQuartz, start, size);
		long allCount = kQuartzDao.templateCount(kQuartz);
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(kQuartzList);
		bootTablePage.setTotal(allCount);
		return bootTablePage;
	}
}