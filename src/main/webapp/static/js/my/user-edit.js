$(document).ready(function () {
 
	reset();
});

var reset = function(){
	var uId = $("#uId").val();
	$.ajax({
        type: 'POST',
        async: false,
        url: 'user/getUser.shtml',
        data: {
        	uId : uId
        },
        success: function (data) {
        	var user = data.data;
        	$("#uAccount").val(user.uAccount);
        	$("#uNickname").val(user.uAccount);
        	$("#uEmail").val(user.uEmail);
        	$("#uPhone").val(user.uPhone); 
        },
        error: function () { 
        	layer.msg('请求失败！请刷新页面重试',{
    			time: 2000,
    			icon: 7
    		});   
        },
        dataType: 'json'
    });
}


$.validator.setDefaults({
	highlight: function (element) {
        $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
    },
    success: function (element) {
        element.closest('.form-group').removeClass('has-error').addClass('has-success');
    },
    errorElement: "span",
    errorPlacement: function (error, element) {
        if (element.is(":radio") || element.is(":checkbox")) {
            error.appendTo(element.parent().parent().parent());
        } else {
            error.appendTo(element.parent());
        }
    },
    errorClass: "help-block m-b-none",
    validClass: "help-block m-b-none"	
});
$().ready(function () {
	
    var icon = "<i class='fa fa-times-circle'></i> ";

    $("#userEditForm").validate({
    	rules: {
    		uAccount: {
         		required: true,
         		maxlength: 50
         	},
         	uEmail: {
         		required: true
         	} ,
         	uPhone: {
         		required: true
         	} 
         },
         messages: {
        	 uAccount: {
         		required: icon + "请输入名称",
         		maxlength: icon + "名称不能超过50个字符"
         	},
         	uEmail: {
         		required: icon + "请输入邮箱"
         	} ,
         	uPhone: {
         		required: icon + "请输入电话"
         	} 
         },
        submitHandler:function(form){
        
        	$.post("user/update.shtml", decodeURIComponent($(form).serialize(),true), function(data){
        		var result = JSON.parse(data);
    			if(result.status == "success"){
    				
    				layer.msg('更新成功',{
            			time: 2000,
            			icon: 6
            		});              		
            		setTimeout(function(){
            			location.href = "view/user/listUI.shtml";
            		},2000);
    			}else {
    				layer.msg(result.message, {icon: 2}); 
    				
    			}
    		});
        } 
    });

});

var cancel = function(){
	location.href = "view/user/listUI.shtml";
	
}