<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<base href="<%=basePath %>">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>作业执行作业记录</title>
<link href="static/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
<link href="static/css/font-awesome.css?v=4.4.0" rel="stylesheet">
<link href="static/css/plugins/bootstrap-table/bootstrap-table.min.css"
	rel="stylesheet">
<link href="static/css/animate.css" rel="stylesheet">
<link href="static/css/style.css?v=4.1.0" rel="stylesheet">
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content animated fadeInRight">
		<div class="ibox float-e-margins">
			<div class="ibox-title">
				<h5>作业执行作业日志记录</h5>
				<div class="ibox-tools">
					<a class="collapse-link"> <i class="fa fa-chevron-up"></i>
					</a> <a class="close-link"> <i class="fa fa-times"></i>
					</a>
				</div>
			</div>
			<div class="ibox-content">
				<div class="col-sm-4 float-left">
					<a href="view/job/monitor/listUI.shtml" class="btn btn-w-m btn-info" type="button"> <i
						class="fa fa-reply" aria-hidden="true"></i>&nbsp;返回
					</a>
				</div>
				<div class="right col-sm-6 float-right ">
					<button onclick="searchfunc()" class="right btn btn-w-m btn-info"
						type="button">
						<i class="fa fa-search" aria-hidden="true"></i>&nbsp;搜索
					</button>
					<button onclick="shrinkLog()" class="right btn btn-w-m btn-info" type="button">
	            		<i class="fa fa-cut" aria-hidden="true"></i>&nbsp;&nbsp;清空日志&nbsp;&nbsp;
            		</button>
					<div class="right col-sm-6">
						<select id="jobId" name="jobId" class="form-control">
							<option value="">请选择日志</option>
						</select>
					</div>
				</div>
				<input type="hidden" id="jobDefaultId" name="jobDefaultId"
					value="${jobId }">
				<table id="jobRecordList" data-toggle="table"
					data-url="job/record/getList.shtml" data-query-params=queryParams
					data-query-params-type="limit" data-pagination="true"
					data-side-pagination="server" data-pagination-loop="false">
					<thead>
						<tr>
							<th data-field="recordId">记录编号</th>
							<th data-field="recordJob" data-formatter="recordjobFormatter">作业名称</th>
							<th data-field="startTime">作业执行起始时间</th>
							<th data-field="stopTime">作业执行结束时间</th>
							<th data-field="recordStatus"data-formatter="jobStatusFormatter">作业执行状态</th>
							<th data-field="action" data-formatter="actionFormatter"
								data-events="actionEvents">操作</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>
	<!-- 全局js -->
	<script src="static/js/jquery.min.js?v=2.1.4"></script>
	<script src="static/js/bootstrap.min.js?v=3.3.6"></script>
	<!-- layer javascript -->
	<script src="static/js/plugins/layer/layer.min.js"></script>
	<!-- 自定义js -->
	<script src="static/js/content.js?v=1.0.0"></script>
	<!-- Bootstrap table -->
	<script src="static/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script
		src="static/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script
		src="static/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script>
		$(document).ready(function () {
			$.ajax({
		        type: 'POST',
		        async: false,
		        url: 'job/getSimpleList.shtml',
		        data: {},
		        success: function (data) {
		        	for (var i=0; i<data.length; i++){
		        		$("#jobId").append('<option value="' + data[i].jobId + '">' + data[i].jobName + '</option>');
		        	}
		        },
		        error: function () {
		            alert("请求失败！请刷新页面重试");
		        },
		        dataType: 'json'
		    });
		});
    	function recordjobFormatter(value, row, index){
    		var recordjob = "";
    		$.ajax({
		        type: 'POST',
		        async: false,
		        url: 'job/getjob.shtml',
		        data: {
		            "jobId": value          
		        },
		        success: function (data) {
		        	var job = data.data;
		        	recordjob = job.jobName;		        	 				        	 
		        },
		        error: function () {
		            alert("系统出现问题，请联系管理员");
		        },
		        dataType: 'json'
		    });
    		return recordjob;
    	}; 
	    function actionFormatter(value, row, index) {
	    	return ['<a class="btn btn-primary btn-xs" id="view" type="button"><i class="fa fa-eye" aria-hidden="true"></i>&nbsp;查看</a>',
    			'&nbsp;&nbsp;',
    			'<a class="btn btn-primary btn-xs" id="download" type="button"><i class="fa fa-download" aria-hidden="true"></i>&nbsp;下载</a>',
    			'&nbsp;&nbsp;',
    			'<a class="btn btn-primary btn-xs" id="delete" type="button"><i class="fa fa-trash" aria-hidden="true"></i>&nbsp;删除</a>'].join('');	
	    };
	    window.actionEvents = {				
	    		'click #view' : function(e, value, row, index) {
	    			$.ajax({
				        type: 'POST',
				        async: false,
				        url: 'job/record/getLogContent.shtml',
				        data: {
				            "recordId": row.recordId          
				        },
				        success: function (data) {
				        	var log=data.data;
				        	var str=log.replace(/\r\n|\n|\r/g, '<br />');
				        	console.log(data.data);
				        	layer.open({
			    				type: 1,
			    				title: "转换作业记录",
			    				maxmin: true, //开启最大化最小化按钮
			    				area: ['80%', '80%'], //宽高
			    				content: str
			    			});				        	 
				        },
				        error: function () {
				            alert("系统出现问题，请联系管理员");
				        },
				        dataType: 'json'
				    });	    			
	    		},	'click #delete' : function(e, value, row, index) {
	    			$.ajax({
				        type: 'POST',
				        async: false,
				        url: 'job/record/delete.shtml',
				        data: {
				            "recordId": row.recordId          
				        },
				        success: function (data) {
				        	searchfunc();
				        	layer.msg('删除日志成功！',{
				    			time: 2000,
				    			icon: 6
				    		});					        	 
				        },
				        error: function () {
				            alert("系统出现问题，请联系管理员");
				        },
				        dataType: 'json'
				    });	    			
	    		},
	    		'click #download' : function(e, value, row, index) {
	    			layer.confirm('确定下载该日志记录？', {
	    				  btn: ['确定', '取消'] 
	    				},
	    				function(index){
	    				    layer.close(index);
							var recordId = row.recordId;    				    
	                		var form = $('<form>');
	        	            	form.attr('style', 'display:none');
	        	            	form.attr('method', 'post');  
	        	            	form.attr('action', 'download/job/record.shtml');  
		        	        var $recordId = $('<input>');   
		        	       		$recordId.attr('type', 'hidden');
		        	       		$recordId.attr('name', 'recordId');
		        	        	$recordId.attr('value', recordId);   
	                     	$('body').append(form); 
	                     	form.append($recordId);   
	                     	form.submit();
	    		  		}, 
	    		  		function(){
	    		  			layer.msg('取消操作');
    		  			}
    		  		);
	    		},
	    	};
	    function jobStatusFormatter(value, row, index){
    		if (value == "1"){
    			return "运行成功";
    		}else if (value == "2"){
    			return "运行失败";
    		}else {
    			return "未定义";
    		}
    	}
		    function queryParams(params) {
		    	var $jobId = $("#jobId").val();   	
		    	var temp = {limit: 10, offset: params.offset, jobId: $jobId};
		        return temp;
		    };
		    
		    function searchfunc(){
		    	$('#jobRecordList').bootstrapTable('refresh', "job/record/getList.shtml");
		    };
		    function shrinkLog() {
		    	 
				$.ajax({
					type : 'POST',
					async : false,
					url : 'job/record/deleteAll.shtml', 
					success: function (data) {
			        	 search();
				        	layer.msg('清空日志成功！',{
				    			time: 2000,
				    			icon: 6
				    		});					        	 
				        },
				        error: function () {
				            alert("系统出现问题，请联系管理员");
				        }
				});
			};
    </script>
</body>
</html>