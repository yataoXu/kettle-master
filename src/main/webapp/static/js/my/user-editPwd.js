$(document).ready(function () {
  
});
 

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

    $("#userEditPwdForm").validate({
    	rules: {
    		oPassword: {
         		required: true,
         		maxlength: 50
         	},
         	uPassword: {
         		required: true,
         		minlength: 6
         	} ,
         	vPassword: {
         		required: true,         		
         		minlength: 6,
         		equalTo: "#uPassword"
         	} 
         },
         messages: {
        	 oPassword: {
         		required: icon + "＊请输入原密码",
         		maxlength: icon + "＊名称不能超过50个字符"
         	},
         	uPassword: {
         		required: icon + "＊请输入新密码",
         		minlength: "＊密码,至少6位"
         	} ,
         	vPassword: {
         		required: icon + "＊请重复输入新密码",
         		minlength: "＊密码,至少6位",
         		equalTo:"＊两次输入密码不一致！"
         	} 
         },
        submitHandler:function(form){
        
        	$.post("user/updatePassword.shtml", decodeURIComponent($(form).serialize(),true), function(data){
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