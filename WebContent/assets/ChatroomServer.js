//url = "ws://localhost:8084/WebSocket/ws"; 
url = "ws://52.69.57.216:8080/WebSocket/ws"; 
ws = new WebSocket(url);
var interval=7000;//間隔(秒)
//chart
var margin = {top: 10, right: 30, bottom: 20, left: 30};
var w = 390 ; // 寬
var h = 100 ; // 高

var stock =[8086,3105,2455];//股票代號
var dataset0 = [];
var dataset1 = [];
var dataset2 = [];

$(document).ready(function() {
		$('#queryTime').val(interval/1000);
		for(var i=0; i <stock.length; i++){
			appendStock(stock[i],i);
		}

		ws.onopen = function() {  
			$('#messageTextArea').append("\nconnect to websocket...\n"); 
		};     
		ws.onclose = function() {  
			console.log('websocket closed...');  
		}; 
		ws.onmessage = function(message) {  
			var data=JSON.parse(message.data);
			
			switch(data.type) {
		    case "login":
		    	$('#messageTextArea').append(data.name+data.text+'\n');
		        break;
		    case "logout":
		        $('#messageTextArea').append(data.name+data.text+'\n');
		        break;
		    case "OnMessage":
		    	break;
		    case "OnStock":
		    	$('#time').empty().append(data.nowTime);
		    	//資料圖表產生
		    	dataset0.push(data.JsonArray0[0]);
		    	dataset1.push(data.JsonArray1[0]);
		    	dataset2.push(data.JsonArray2[0]);
		    	dataBind(data.JsonArray0, dataset0, ".demo0",0);
		    	dataBind(data.JsonArray1, dataset1, ".demo1",1);
		    	dataBind(data.JsonArray2, dataset2, ".demo2",2);
		    	break;
		    default:
		    	alert('system error');
		    	break;
			}
		}; 
});

	function appendStock(number, count){
		$('body').append('<div style="display:inline;"><br/><br/>'
		+'股票代號:&nbsp;<input id="number" value="'+number+'" size="6" disabled>&nbsp;'
		+'成交:&nbsp;<div style="display:inline;" id="Price'+count+'"></div>&nbsp;'
		+'漲跌:&nbsp;<div style="display:inline;" id="UpandDown'+count+'"></div>&nbsp;'
		+'開盤:&nbsp;<div style="display:inline;" id="Open'+count+'"></div>&nbsp;'
		+'最高價:&nbsp;<div style="display:inline;" id="Top'+count+'"></div>&nbsp;'
		+'最低價:&nbsp;<div style="display:inline;" id="Bottom'+count+'"></div>&nbsp;'
		+'成交量:&nbsp;<div style="display:inline;" id="Count'+count+'"></div>'
		+'<br/><div class="demo'+count+'"></div></div>');
	}
	//取得股票內容
	setInterval(function(){
		var now =new Date();
		var msg=now.getHours()+":"+now.getMinutes()+":"+now.getSeconds();
		ws.send(msg);  
	},interval);

	function dataBind(data, ds, className, count){
    	if(data[1].indexOf("△") > -1){
    		$('#UpandDown'+count).css("color","red");
    	}else{
    		$('#UpandDown'+count).css("color","green");
    	}
    	$('#Price'+count).empty().append('<b>'+data[0]+'</b>');
    	$('#UpandDown'+count).empty().empty().append(data[1]);
    	$('#Open'+count).empty().append(data[3]);//開盤價
    	$('#Top'+count).empty().append(data[4]);
    	$('#Bottom'+count).empty().append(data[5]);
    	$('#Count'+count).empty().append(data[2]);//成交量
    	//開盤價
    	//alert((data[3]*1.1).toFixed(2));
    	
    	dynamicChart(ds, className, (data[3]*1.1).toFixed(2), (data[3]/1.1).toFixed(2));
	}
	
	
	function dynamicChart(ds, className, top , bottom){
		$(className).html("");
		var Ymax = top, Ymin = bottom;
		var widthRange=100;
		if (ds.length>100){
			widthRange=ds.length;
		}
		var xScale = d3.scale.linear().domain([0, widthRange]).range([0, w]);
		var yScale = d3.scale.linear().domain([Ymin, Ymax]).range([h, 0]);
		// 增加一個line function，用來把資料轉為x, y
		var line = d3.svg.line()
			.x(function(d,i) { 
				return xScale(i + 1); //利用尺度運算資料索引，傳回x的位置
			})
			.y(function(d) { 
				return yScale(d); //利用尺度運算資料的值，傳回y的位置
			});

		//增加一個SVG元素
		var svg = d3.select(className).append('svg')
			.attr('width', w + margin.left + margin.right) //將左右補滿
			.attr('height', h + margin.top + margin.bottom) //上下補滿
			.append('g') //增加一個群組g
			.attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

		// 增加x軸線，tickSize是軸線的垂直高度，-h會往上拉高
		var xAxis = d3.svg.axis().scale(xScale).orient('bottom').tickSize(-h).tickSubdivide(true);
		// SVG加入x軸線
		svg.append('g')
			.attr('class', 'x axis')
			.attr('transform', 'translate(0,' + h + ')')
			.call(xAxis);


		// 建立y軸線，4個刻度，數字在左
		var yAxisLeft = d3.svg.axis().scale(yScale).ticks(4).orient('left');
		// SVG加入y軸線
		svg.append('g')
			.attr('class', 'y axis')
			.attr('transform', 'translate(0,0)')
			.call(yAxisLeft);
					
		svg.append('path').attr('d', line(ds)); //將資料套用d3.svg.line()
	}

    
