
/*
    弹性数值变换参数与接口
    param				//接口对象
        param.aimInterV	//数值变换的帧频,即隔多长时间执行一次变换,通常使用默认值即可,单位为 ms 
        param.onChange	//当目标数值发生变换时执行的函数接口,参数为,当前值
        param.onStart	//当目标数值开始发生变换时执行的函数接口
        param.onEnd		//当目标数值变换结束时执行的函数接口
        
        param.aimSpeed	//动画的变换速度,值为小于1的小数,数值越大,速度越快,默认为 0.5
        param.setFunc	//能够设置目标数值的函数接口,参数是设置的值,整形
        param.sV		//目标值开始变换的值
        param.eV		//目标值结束变换的值

*/
function execAnimate(param)
{

	this.timerID=null;
	if(typeof(param.setFunc)!="function")
	{
		alert("你必须设置一个能够-设置-目标变换值的函数接口.");
		return;
	}
	if(typeof(param.sV)=="undefined")
	{
		alert("你必须设置目标变换值的起始值.");
		return;
	}
	if((typeof(param.eV)=="undefined"))
	{
		alert("你必须设置目标变换值的最终值.");
		return;
	}

	this.setV	=param.setFunc;
	this.eV		=parseInt(param.eV);
	this.sV		=parseInt(param.sV);
	this.curV	=this.sV;//当前值	

	var minSpeed	=0.1;//最慢速度
	var maxSpeed	=0.9;//最快速度

	this.aimInterV	=parseInt(param.aimInterV)	|| 8;
	this.aimSpeed	=param.aimSpeed				|| 0.5;
	if (this.aimSpeed>maxSpeed){this.aimSpeed=maxSpeed;}	
	if (this.aimSpeed<=0){this.aimSpeed=minSpeed;}
	this.actTimes	=(0.5-this.aimSpeed/2)*100+(2-this.aimSpeed)*8;	

	this.onChange	=param.onChange	|| function (v){};
	this.onStart	=param.onStart	|| function (){};
	this.onEnd		=param.onEnd	|| function (){};

	this.vLength	=this.eV-this.sV;//变换长度范围
	this.execTimes	=0;

	this.setAimValue=function ()
	{
		this.execTimes++;
		var xValue	=(Math.PI/2)*(this.execTimes/this.actTimes);
		var setValue=Math.ceil((Math.sin(xValue)*this.vLength)+this.sV);
		this.curV	=setValue;
		this.setV(setValue);
		this.onChange(setValue);

		if (this.execTimes==1){this.onStart();}
		return (this.execTimes<this.actTimes);
	}

	this.stop	=function (){clearTimeout(this.timerID);this.onEnd();};	

	this.start	=function ()
	{
		var me=this;
		this.timerID=setTimeout(function ()
								{
									if (me.setAimValue()){me.start();}									
									else{me.stop();}
								},me.aimInterV);
	}
}

export default execAnimate;