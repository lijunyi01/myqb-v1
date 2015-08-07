var component = {
	user_setting: function(){//用户设置界面
		React.render(<Load_user_setting />,document.getElementById("html_tanchu_a_div"));
	},
	alert: function(type,context,callbock){//alert弹出框
		$("#html_alert").show();
		React.render(<Load_alert type={type} context={context} callbock={callbock} />,document.getElementById("html_alert_div"));
	},
	tmlxjm: function(callbock){
		React.render(<Load_tmlx callbock={callbock} />,document.getElementById("right"));
	},
	tmlxjm_xzt: function(){
		React.render(<Load_tmlx_xzt />,document.getElementById("right"));
	}
}