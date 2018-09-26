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

    $("#userAddForm").validate({
    	 rules: {
    		 uAccount: {
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
         	} ,
         	
         	uNickname: {
         		required: true
         	} ,	
         	uEmail: {
         		required: true, 
         		email:true
         	} ,	
         	uPhone: {
         		required: true,
         		digits:true,
         		minlength:7
         	} 
         },
         messages: {
        	 uAccount: {
         		required: icon + "账号",
         		maxlength: icon + "名称不能超过50个字符"
         	},
        	uPassword: {
         		required: icon + "＊请输入新密码",
         		minlength: "＊密码,至少6位"
         	} ,
         	vPassword: {
         		required: icon + "＊请重复输入新密码",
         		minlength: "＊密码,至少6位",
         		equalTo:"＊两次输入密码不一致！"
         	} ,
         	uNickname: {
         		required: icon + "请输入密码"
         	} ,	
         	uEmail: {
         		required: icon + "请输入正确邮箱地址", 
         		email:icon + "请输入正确邮箱地址"
         	} ,	
         	uPhone: {
         		required: icon + "请输入电话号码",
         		digits:icon + "请输入正确电话号码",
         		minlength:icon + "请输入正确电话号码"
         	} 
         },
        submitHandler:function(form){
        
        	$.post("user/insert.shtml", decodeURIComponent($(form).serialize(),true), function(data){
        		var result = JSON.parse(data);
    			if(result.status == "success"){
    				
    				layer.msg('添加成功',{
            			time: 2000,
            			icon: 6
            		}
    				);              		
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