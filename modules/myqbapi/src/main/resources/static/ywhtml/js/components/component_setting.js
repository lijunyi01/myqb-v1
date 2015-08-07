var Load_user_setting = React.createClass({
	setting_jiben: function(){
		$(".user_setting_jiben_xinxi").slideToggle(200);
		$(".user_setting_xiangxi_xinxi").slideUp(200);
		$(".user_setting_uppass_xinxi").slideUp(200);
		$("#user_setting_jiben").toggleClass("user_setting_xinxi_bordercolor");
		$("#user_setting_xiangxi").removeClass("user_setting_xinxi_bordercolor");
		$("#user_setting_uppass").removeClass("user_setting_xinxi_bordercolor");
	},
	setting_xiangxi: function(){
		$(".user_setting_xiangxi_xinxi").slideToggle(200,function(){$(".user_setting").scrollTop($(".user_setting")[0].scrollHeight);});
		$(".user_setting_jiben_xinxi").slideUp(200);
		$(".user_setting_uppass_xinxi").slideUp(200);
		$("#user_setting_xiangxi").toggleClass("user_setting_xinxi_bordercolor");
		$("#user_setting_jiben").removeClass("user_setting_xinxi_bordercolor");
		$("#user_setting_uppass").removeClass("user_setting_xinxi_bordercolor");
	},
	setting_uppass: function(){
		$(".user_setting_uppass_xinxi").slideToggle(200,function(){$(".user_setting").scrollTop($(".user_setting")[0].scrollHeight);});
		$(".user_setting_xiangxi_xinxi").slideUp(200);
		$(".user_setting_jiben_xinxi").slideUp(200);
		$("#user_setting_uppass").toggleClass("user_setting_xinxi_bordercolor");
		$("#user_setting_xiangxi").removeClass("user_setting_xinxi_bordercolor");
		$("#user_setting_jiben").removeClass("user_setting_xinxi_bordercolor");
	},
	user_setting_close: function(){
		$("#html_tanchu_a").animate({top:'-100%'},200,function(){
			$(this).hide();
		});
	},
	render: function(){
		return <div className="user_setting">
			<div className="user_setting_title">用户信息设置<div onClick={this.user_setting_close} title="关闭">×</div></div>
			
			<div className="user_setting_xinxi" id="user_setting_jiben" onClick={this.setting_jiben}>基本信息设置</div>
			<div className="user_setting_jiben_xinxi">
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"200px",float:"left"}}>电话号码：<span>133****3333</span></div>
					<div style={{width:"auto",float:"right"}}><button>&nbsp;&nbsp;修改绑定手机&nbsp;&nbsp;</button></div>
				</div>
				<div className="user_setting_xinxi_A">
					<div style={{width:"200px",float:"left"}}>电子邮箱：<span>qin******.com</span></div>
					<div style={{width:"auto",float:"right"}}><button>&nbsp;&nbsp;修改绑定邮箱&nbsp;&nbsp;</button></div>
					<div style={{width:"auto",float:"left"}}><button>&nbsp;&nbsp;邮箱验证&nbsp;&nbsp;</button></div>
					<div style={{width:"auto",float:"left","margin-left":"10px",color:"#ff0000","font-size":"12px"}}>电子邮箱未经验证,邮件相关功能将受限制</div>
				</div>
			</div>
			
			<div className="user_setting_xinxi" id="user_setting_xiangxi" onClick={this.setting_xiangxi}>详细信息设置</div>
			<div className="user_setting_xiangxi_xinxi">
				<div className="user_setting_xinxi_A user_setting_xinxi_border" style={{height:"80px","line-height":"80px"}}>
					<div style={{width:"80px",height:"100%",float:"left"}}><img src="images/user_head.png" /></div>
					<div style={{width:"auto",float:"left","margin-left":"10px"}}><button>&nbsp;&nbsp;换一张头像&nbsp;&nbsp;</button></div>
				</div>
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"auto",float:"left"}}>昵　　称：&nbsp;&nbsp;<input type="text" value="qinjie005" /></div>
				</div>
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"auto",float:"left"}}>年　　级：&nbsp;&nbsp;
						<select>
							<option value ="0">---请选择---</option>
							<option value ="-30">幼儿园小班</option>
							<option value ="-20">幼儿园中班</option>
							<option value ="-10">幼儿园大班</option>
							<option value ="10">小学一年级</option>
							<option value ="20">小学二年级</option>
							<option value ="30">小学三年级</option>
							<option value ="40">小学四年级</option>
							<option value ="50">小学五年级</option>
							<option value ="60">小学六年级</option>
							<option value ="61">初中预备班</option>
							<option value ="70">初中一年级</option>
							<option value ="80">初中二年级</option>
							<option value ="90">初中三年级</option>
							<option value ="100">高中一年级</option>
							<option value ="110">高中二年级</option>
							<option value ="120">高中三年级</option>
							<option value ="130">大学一年级</option>
							<option value ="140">大学二年级</option>
							<option value ="150">大学三年级</option>
							<option value ="160">大学四年级</option>
							<option value ="170">大学五年级</option>
							<option value ="180">研究生一年级</option>
							<option value ="190">研究生二年级</option>
							<option value ="200">研究生三年级</option>
						</select>
					</div>
				</div>
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"auto",float:"left"}}>地　　址：&nbsp;&nbsp;<input type="text" style={{width:"350px"}} value="上海市徐汇区桂菁路15号北4楼" /></div>
				</div>
				<div className="user_setting_xinxi_A">
					<div className="user_setting_xiangxi_tj">保存详细信息</div>
				</div>
			</div>
			
			<div className="user_setting_xinxi" id="user_setting_uppass" onClick={this.setting_uppass}>修改密码</div>
			<div className="user_setting_uppass_xinxi">
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"auto",float:"left"}}>　原密码：&nbsp;&nbsp;<input type="password" /></div>
				</div>
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"auto",float:"left"}}>　新密码：&nbsp;&nbsp;<input type="password" /></div>
				</div>
				<div className="user_setting_xinxi_A user_setting_xinxi_border">
					<div style={{width:"auto",float:"left"}}>重新输入：&nbsp;&nbsp;<input type="password" /></div>
				</div>
				<div className="user_setting_xinxi_A">
					<div className="user_setting_xiangxi_tj">修改密码</div>
				</div>
			</div>
			
		</div>;
	}
});

