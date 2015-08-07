var publics = {
	h: 0,
	w: 0,
	load_type: 0,
	post: function(url,data,callback){
        $.post(url,data,function(result){
			console.log(result);
			callback(result);
		});
	},
	data_fg: function(content,i){
		return content.split("<[CDATA]>")[(i-1)].split("=")[1];
	},
	load_click: function(id,callback){
		$("#"+id).unbind("click");
		$("#"+id).bind("click",callback);
	},
	load_blur: function(id,callback){
		$("#"+id).unbind("blur");
		$("#"+id).bind("blur",callback);
	},
	substring: function(content,i){
		if(content.length<i) return content;
		else return content.substring(0,i);
	},
	load_html: function(){
		var w_ = $(window).width();
		var h_ = $(window).height();
		var load_type = $("#html_load_type").val();
		if(this.h != h_ || this.w != w_ || this.load_type != load_type){
			if(w_ < 1024) w_ = 1024;
			//if(h_ < 500) h_ = 500;
			
			this.h = h_;
			this.w = w_;
			this.load_type = load_type;
			
			var left_w = 0,left_b = 0,middle_w = 0,middle_b = 0;
			if(load_type == "0"){left_w = 75;left_b = 1;middle_w = 455;middle_b = 1;$(".middle").css("border-right","1px solid #ececec");}
			else if(load_type == 1){left_w = 75;left_b = 1;middle_w = 0; middle_b = 0; ;$(".middle").css("border-right","0px")}
			
			$(".mian").width(this.w);
			$(".mian").height(this.h);
			
			$(".gntb_div").height(this.h-left_w-left_b);
			
			$(".middle").width(middle_w);
			
			$(".right").width(this.w-left_w-left_b-middle_w-middle_b);
			$(".right").height(this.h);
			
		}
		setTimeout("publics.load_html()",100);
	}
};