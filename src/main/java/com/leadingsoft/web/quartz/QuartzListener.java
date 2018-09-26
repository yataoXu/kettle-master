package com.leadingsoft.web.quartz;

import java.util.Date;

import org.apache.log4j.Logger;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.leadingsoft.common.toolkit.Constant;
import com.leadingsoft.core.model.KJob;
import com.leadingsoft.core.model.KTrans;
import com.leadingsoft.web.quartz.model.DBConnectionModel;

public class QuartzListener implements JobListener {
	
	Logger logger = Logger.getLogger(QuartzListener.class);

	@Override
	public String getName() {
		return new Date().getTime() + "QuartzListener";
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		String jobName = context.getJobDetail().getKey().getName();
		String jobGroupName = context.getJobDetail().getKey().getGroup();
		String triggerName = context.getTrigger().getKey().getName();
		String triggerGroupName = context.getTrigger().getKey().getGroup();
		// 一次性任务，执行完之后需要移除

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		String jobId = String.valueOf(
				dataMap.get(Constant.JOBID) != null ? dataMap.get(Constant.JOBID) : dataMap.get(Constant.TRANSID));
		String jobtype = String.valueOf(dataMap.get(Constant.TYPE_JOB) != null ? dataMap.get(Constant.TYPE_JOB)
				: dataMap.get(Constant.TYPE_TRANS));

		Object DbConnectionObject = dataMap.get(Constant.DBCONNECTIONOBJECT);

		DBConnectionModel DBConnectionModel = (DBConnectionModel) DbConnectionObject;

		ConnectionSource source = ConnectionSourceHelper.getSimple(DBConnectionModel.getConnectionDriveClassName(),
				DBConnectionModel.getConnectionUrl(), DBConnectionModel.getConnectionUser(),
				DBConnectionModel.getConnectionPassword());

		DBStyle mysql = new MySqlStyle();
		SQLLoader loader = new ClasspathLoader("/");

		UnderlinedNameConversion nc = new UnderlinedNameConversion();

		SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, new Interceptor[] { new DebugInterceptor() });

		if (jobtype.equals(Constant.TYPE_JOB)) {

			KJob kJob = sqlManager.unique(KJob.class, Integer.valueOf(jobId));
			if (kJob.getJobQuartz() == 1) {
				QuartzManager.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
			}
			kJob.setJobStatus(2);
			sqlManager.updateTemplateById(kJob);

		} else if (jobtype.equals(Constant.TYPE_TRANS)) {

			KTrans kTrans = sqlManager.unique(KTrans.class, Integer.valueOf(jobId));
			if (kTrans.getTransQuartz() == 1) {
				QuartzManager.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
			}
			kTrans.setTransStatus(2);
			sqlManager.updateTemplateById(kTrans);

		}

		logger.info(jobName);
	}
}
