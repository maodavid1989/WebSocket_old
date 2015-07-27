
//股票圖表
	function dynamicChartStock(ds, className, top , bottom){
		$(className).html("");
		var Ymax = top, Ymin = bottom;
		var widthRange=500;
		if (ds.length>500){
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
			.attr('class', 'mycontent')
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

	
//指數圖表	
	function dynamicChartIndex(ds){
		var className=".twIndex";
		var indexName=".twIndexN"
		var Ymax =Math.max.apply(null, ds); 
		var Ymin =Math.min.apply(null, ds);

		$(indexName).html(ds[ds.length-1]);
		$(className).html("");
		var widthRange=50;
		if (ds.length>50){
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
			.attr('class', 'mycontent')
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
