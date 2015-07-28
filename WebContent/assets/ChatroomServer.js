url = "ws://localhost:8084/WebSocket/ws"; 
//url = "ws://52.69.57.216:8080/WebSocket/ws"; 
ws = new WebSocket(url);
var interval=7000;//間隔(秒)
//chart
var margin = {top: 10, right: 40, bottom: 20, left: 40};
var w = 390 ; // 寬
var h = 100 ; // 高

var dataset=[];

$(document).ready(function() {	
		$('.sidebar').css('overflow','scroll');
		$('#queryTime').val(interval/1000);
		//web storage
		if(window.localStorage['stockAll']){
			$('#stockAll').val(window.localStorage['stockAll']);
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
		    	
				var sl=$('#stockAll').val().split(',').length;
		    	for(var i=0 ; i<sl ; i++){
		    		dataset[i].price.push(eval('data.JsonArray'+i)[0]);
		    		dataBind(eval('data.JsonArray'+i), dataset[i].price, ".demo"+i, i);
		    	}
		    	break;
		    case "OnIndex":
		    	var indexset=[];
	        	for (var i=0; i<data.ohlcArray.length;i++){
	        		indexset.push(data.ohlcArray[i].c);
	        	}
	        	dynamicChartIndex(indexset);
		    	break;
		    default:
		    	alert('system error');
		    	break;
			}
		}; 
});

	function start(){
		$('a').attr('disabled', true);
		//save to local storage
		window.localStorage.setItem("stockAll", $('#stockAll').val());		
		var stock=$('#stockAll').val().split(',');//取得股票代號
		var sl=stock.length;
		for(var i=0; i <sl ; i++){
			var arrayAhref=getStockAhref(stock[i]);//取得最新消息超連結
			appendStock(stock[i], i, arrayAhref);
			$('#number'+i).val(stock[i]);
			dataset.push( { price : [] } );
		}

		
		ws.send(stock);//first time
		ws.send("getTaiwanIndex"); 	//first time
		setInterval(function(){
			var now =new Date();
			var msg=now.getHours()+":"+now.getMinutes()+":"+now.getSeconds();
			ws.send(stock);  
			ws.send("getTaiwanIndex");  
		},interval);
	}
	
	//取得股票新聞連結
	function getStockAhref(SN){
		var arrayAhref=[];
	    $.ajax({
	        async : false,
	        url : "ajaxStockServlet",
	        dataType : 'json',
	        data : {
	            ajaxAction : "getStockAhref",
	            stockNumber : SN
	        },
	        success : function(data, textStatus, xhr) {
	        	arrayAhref.push(data.formData.href1);
	        	arrayAhref.push(data.formData.text1);
	        	arrayAhref.push(data.formData.href2);
	        	arrayAhref.push(data.formData.text2);
	        	arrayAhref.push(data.formData.href3);
	        	arrayAhref.push(data.formData.text3);
	        }
	    });
	    return arrayAhref;
	}
		
	function appendStock(number, count, arrayAhref){
		$('.stockAppend').append('<table border="0" width=100%><tr>'
		+'<td width=60%>'
		+'<div  class="mycontent"><br/><br/>'
		+'股票代號:&nbsp;<input id="number'+count+'" size="6" disabled>&nbsp;'
		+'成交:&nbsp;<div style="display:inline;" id="Price'+count+'"></div>&nbsp;'
		+'漲跌:&nbsp;<div style="display:inline;" id="UpandDown'+count+'"></div>&nbsp;'
		+'開盤:&nbsp;<div style="display:inline;" id="Open'+count+'"></div>&nbsp;'
		+'最高價:&nbsp;<div style="display:inline;" id="Top'+count+'"></div>&nbsp;'
		+'最低價:&nbsp;<div style="display:inline;" id="Bottom'+count+'"></div>&nbsp;'
		+'成交量:&nbsp;<div style="display:inline;" id="Count'+count+'"></div>'
		+'<br/><div class="demo'+count+'"></div></div>'
		+'</td>'
		+'<td width=40%>'
			+'<a target=_new href="'+arrayAhref[0]+'">'+arrayAhref[1]+'<br/>'
			+'<a target=_new href="'+arrayAhref[2]+'">'+arrayAhref[3]+'<br/>'
			+'<a target=_new href="'+arrayAhref[4]+'">'+arrayAhref[5]+'<br/>'
		+'</td>'
		
		+'</tr></table>');
	}

	function dataBind(data, ds, className, count){
    	if(data[1].indexOf("△") > -1){//漲
    		$('#UpandDown'+count).css("color","red");
    	}else if(data[1].indexOf("▽") > -1){//跌
    		$('#UpandDown'+count).css("color","green");
    	}else if(data[1].indexOf("▲") > -1){//漲停
    		$('#UpandDown'+count).css("background-color","red");
    		$('#UpandDown'+count).css("color","black");
    	}else{
    		$('#UpandDown'+count).css("color","black");
    	}
    	$('#Price'+count).empty().append('<b>'+data[0]+'</b>');
    	$('#UpandDown'+count).empty().empty().append(data[1]);
    	$('#Open'+count).empty().append(data[3]);//開盤價
    	$('#Top'+count).empty().append(data[4]);
    	$('#Bottom'+count).empty().append(data[5]);
    	$('#Count'+count).empty().append(data[2]);//成交量
    	//開盤價
    	//alert((data[3]*1.1).toFixed(2));
    	
    	dynamicChartStock(ds, className, data[4], data[5]);
	}
	

    