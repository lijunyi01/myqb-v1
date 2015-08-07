var Load_tmlx = React.createClass({
	tx_xzt: function(){
		this.props.callbock("1");
	},
	tx_tkt: function(){
		this.props.callbock("2");
	},
	tx_pdt: function(){
		this.props.callbock("3");
	},
	tx_wdt: function(){
		this.props.callbock("4");
	},
	tx_fht: function(){
		this.props.callbock("5");
	},
	render: function(){
		return <div className="tmlx">
			<div className="tmlx_title">选择题目类型</div>
			<div className="tmlx_context">
				<div onClick={this.tx_xzt}>选择题</div>
				<div onClick={this.tx_tkt}>填空题</div>
				<div onClick={this.tx_pdt}>判断题</div>
				<div onClick={this.tx_wdt}>问答题</div>
				<div onClick={this.tx_fht}>复合题</div>
				
				<div onClick={this.tx_xzt}>选择题</div>
				<div onClick={this.tx_tkt}>填空题</div>
				<div onClick={this.tx_pdt}>判断题</div>
				<div onClick={this.tx_wdt}>问答题</div>
				<div onClick={this.tx_fht}>复合题</div>
			</div>
		</div>;
	}
});

var Load_tmlx_xzt = React.createClass({
	xxxuhao: "A,B,C,D,E,F,G,H,I,J,K",
	componentDidMount: function(){
		this.addxx();
		this.addxx();
		this.addxx();
		this.addxx();
	},
	addxx: function(){
		$(".tmlx_xzxx").show();
		var xxitem = $(".tmlx_xzxx>div").length;
		if(xxitem == this.xxxuhao.split(",").length){component.alert("1","选项数量已达上限",index_js.alert_callbock);return;}
		var xxval = this.xxxuhao.split(",",xxitem+1)[xxitem];
		
		var xxhtml = '<div class="tmlx_xzxx_div">'+
				'<div style="width:20px;text-align: center;">'+xxval+'</div>'+
				'<div style="width:65%">：<input type="text" id="xx_'+xxval+'" /></div>'+
				'<div style="width:auto"><input type="checkbox" name="xx_zq" value="'+xxval+'"><span style="color:#38B2FD;">这是正确答案</span></div>'+
				'<div style="width:auto; margin-left: 20px;"><input type="checkbox" name="xx_cw" value="'+xxval+'"><span style="color:#ff0000;">这是错误答案</span></div>'+
			'</div>';
		
		$(".tmlx_xzxx").append(xxhtml);
		
		$(".tmlx_xzxx").scrollTop($(".tmlx_xzxx").scrollTop()+50);
	},
	delxx: function(){
		if($(".tmlx_xzxx>div").length>0){
			component.alert("2","确定删除最后一个选项吗?",this.delxx_call);
		}else{
			component.alert("1","已经没有选项给你删啦!",index_js.alert_callbock);
		}
	},
	delxx_call: function(e){
		if(e){
			$(".tmlx_xzxx>div").last().remove();
			$(".tmlx_xzxx").scrollTop($(".tmlx_xzxx").scrollTop()+5000);
		}
	},
	xinde: function(){
		$(".tmlx_xinde").toggle();
		if($("#xinde_button").html() == "　编写心得　"){$("#xinde_button").html("　收起心得　");}
		else{$("#xinde_button").html("　编写心得　");}
		$(".tmlx").scrollTop($(".tmlx").scrollTop()+5000);
	},
	getxx: function(){
		if($(".tmlx_xzxx>div").length == 0){return "0";}
		var xx_val = "";
		var return_val = "";
		var xxxuhao = this.xxxuhao;
		$(".tmlx_xzxx>div").each(function(item){
			var xx_xh = xxxuhao.split(",",item+1)[item];
			var val = $(this).find("input[type=text]").val().trim();
			if(val == "") return_val = "1";
			xx_val = xx_val + xx_xh+"："+val+"<[CDATA3]>";
		});
		if(return_val != "") return return_val;
		xx_val = xx_val.substring(0,xx_val.length-10);
		return xx_val;
	},
	get_checkbox: function(name){
		var checkbox_val = "";
		$(".tmlx_xzxx_div input[name="+name+"]").each(function(itme){
			if(this.checked){
				checkbox_val = checkbox_val + $(this).val() + ",";
			}
		});
		checkbox_val = checkbox_val.substring(0,checkbox_val.length-1);
		return checkbox_val;
	},
	baocun: function(){
		var timu = $(".tmlx_tmnr>textarea").val().trim();
		var xuanxiang = this.getxx();
		var daan_zq = this.get_checkbox("xx_zq");
		var daan_cw = this.get_checkbox("xx_cw");
		var xinde = $(".tmlx_xinde>textarea").val().trim();
		var titou = publics.substring(timu,10);
		
		if(timu==""){component.alert("1","题目还是要填写的!",index_js.alert_callbock); return;}
		if(xuanxiang == "0"){component.alert("1","选择题请添加选项!",index_js.alert_callbock); return;}
		if(xuanxiang == "1"){component.alert("1","有的选项没有填写值!",index_js.alert_callbock); return;}
//		if(daan_zq == ""){component.alert("1","请选择正确答案!",index_js.alert_callbock); return;}
//		if(daan_cw == ""){component.alert("1","请选择遇到该题时选择的错误答案!",index_js.alert_callbock); return;}
		
		var generalInput = "";
		
		//年级
		var grade = "10";
		//题目类型 1选择题
		var questionType = "1";
		//科目大类
		var classType = "1";
		//科目子类
		var classSubType = "1";
		//是否为复合类题型 0否 1是
		var multiplexFlag = "0";
		//子题数量
		var subQuestionCount = "1";
		//题头
		var contentHeader = "";
		//题目标题
		var subject = titou;
		//附件id 多个按冒号分隔
		var attachmentIds = "";
		//子题
		//var subQuestions = "";
			//子题序号
			var seqId = "1";
			//子题题型
			var qType = "1";
			//子题内容
			var content = timu;
			//子题附加类容 A:6<[CDATA3]>B:7<[CDATA3]>C:8<[CDATA3]>D:9
			var attachedInfo = xuanxiang;
			//子题附件id 多个按冒号分隔
			var attachmentIds = "";
			//子题正确答案
			var correctAnswer = daan_zq;
			//子题错误答案
			var wrongAnswer = daan_cw;
			//子题心得（备注）
			var note = xinde;
			
		generalInput = "grade="+grade+"<[CDATA]>questionType="+questionType+"<[CDATA]>classType="+classType+"<[CDATA]>classSubType="+classSubType+"<[CDATA]>multiplexFlag="+multiplexFlag+
			"<[CDATA]>subQuestionCount="+subQuestionCount+"<[CDATA]>contentHeader="+contentHeader+"<[CDATA]>subject="+subject+"<[CDATA]>attachmentIds="+attachmentIds+
			"<[CDATA]>subQuestions=seqId="+seqId+"<[CDATA2]>qType="+qType+"<[CDATA2]>content="+content+"<[CDATA2]>attachedInfo="+attachedInfo+"<[CDATA2]>attachmentIds="+attachmentIds+
			"<[CDATA2]>correctAnswer="+correctAnswer+"<[CDATA2]>wrongAnswer="+wrongAnswer+"<[CDATA2]>note="+note;
		index_js.add_timu_tijiao(generalInput);
	},
	render: function(){
		return <div className="tmlx">
			<div className="tmlx_title">选择题<div onClick={this.baocun}>　保 存　</div></div>
			<div className="tmlx_tmnr">
				<textarea placeholder="这里键入题目..."></textarea>
			</div>
			<div className="tmlx_xzxx"></div>
			<div className="tmlx_button">
				<div onClick={this.addxx}>　添加选项　</div>
				<div onClick={this.delxx}>　删除选项　</div>
				<div onClick={this.xinde} id="xinde_button">　编写心得　</div>
			</div>
			<div className="tmlx_xinde">
				<textarea placeholder="这里键入心得内容..."></textarea>
			</div>
		</div>;
	}
});
















