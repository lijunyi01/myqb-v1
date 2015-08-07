$(function(){
    localStorage.setItem("umid","1");
    localStorage.setItem("sessionid","11");
	//页面架构布局
	publics.load_html();
	//事件绑定
	index_js.load_click();
	
//	setTimeout("index_js.user_setting_click()",500);
//	setTimeout(function(){component.alert("2","你好吗?",alert_callbock)},500);
});
var index_js = {
	load_click: function(){
		publics.load_click("user_head_img",index_js.user_head_click);
		publics.load_click("user_setting",index_js.user_setting_click);
		publics.load_click("gn_add_img",index_js.dzb_add_click);
	},
	user_head_click:function(){
		$("#html_jiaodian").focus();
		publics.load_blur("html_jiaodian",index_js.user_head_click_blur);
		$(".user_head_div").show().animate({left:'74px'});
	},
	user_head_click_blur: function(){
		$(".user_head_div").show().animate({left:'-26px'});
	},
	user_setting_click: function(){
		component.user_setting();
		$("#html_tanchu_a").css("top","-100%").show().animate({top:'0px'},200);
	},
	dzb_add_click: function(){
		$("#html_load_type").val("1");
		setTimeout(function(){
			component.tmlxjm(index_js.dzb_add_click_callbock);
		},100);
	},
	dzb_add_click_callbock: function(type){
		if(type == "1"){
			component.tmlxjm_xzt();
		}
	},
	alert_callbock: function(){},
	add_timu_tijiao: function(data){
		var url = config.yw_myqbapi+config.yw_login;
		var data = {umid:localStorage.getItem("umid"),sessionId:localStorage.getItem("sessionid"),functionId:"30",generalInput:data};
		publics.post(url,data,index_js.add_timu_tijiao_call);
	},
	add_timu_tijiao_call: function(json){
		alert(json);
	}
};

//弹出框回调函数
function alert_callbock(e){
	alert("1-"+e);
}

