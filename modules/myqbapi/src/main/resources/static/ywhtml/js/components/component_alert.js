/**
 * component.alert("1","你好吗?",alert_callbock)
 * 1 alert 2 confirm
 * context
 * callbock
 */
var Load_alert = React.createClass({
	alert: function(){
		this.close();
		this.props.callbock(false);
	},
	queren: function(){
		this.close();
		this.props.callbock(true);
	},
	close: function(){
		$("#html_alert").hide();
	},
	render: function(){
		var type = this.props.type;
		var button_html;
		if(type==1){
			button_html = <div className="alert_div_button_alert" onClick={this.alert}>确　　认</div>;
		}else if(type==2){
			button_html = <div className="alert_div_button_confirm">
				<div style={{border:"0px"}} onClick={this.queren}>确　　认</div>
				<div onClick={this.alert}>取　　消</div>
			</div>;
		}
		
		var alert_html= <div className="alert_div">
			<div className="alert_div_title"></div>
			<div className="alert_div_context">{this.props.context}</div>
			<div className="alert_div_button">{button_html}</div>
		</div>;
		return alert_html;
	}
});