for(var fn in runtime) {
  if(typeof runtime[fn] === 'function') {
    this[fn] = (function() {
      var method = runtime[fn];
      return function() {
         return method.apply(runtime,arguments);
      };
    })();
  }else{
	  this[fn]=runtime[fn];
  }
}
function array(list){
	var arr=new Array();
	for(var i=0;i<list.size();i++)
	arr.push(list.get(i));
	return arr;
}
function async(fun,callback){
	var run={run:function(){callback(fun());}};
	new java.lang.Thread(new java.lang.Runnable(run)).start();
}
function request(opt) {
        opt = opt || {};
        opt.method = opt.method?opt.method.toUpperCase() : 'GET';
        opt.url = opt.url || '';
        opt.data = opt.data || null;
        opt.headers=opt.headers||{};
       var conn=open(opt.url);
	   conn.setRequestMethod(opt.method);
	   for(let header in opt.headers){
		   conn.setRequestProperty(header,opt.headers[header]);
	   }
	   if(opt.data){
		   let out=conn.getOutputStream();
		   out.write(opt.data);
		   out.flush();
	   }
	   let input=conn.getInputStream();
	   let bytearray=new java.io.ByteArrayOutputStream();
	   let len=-1;
	   var array=Byte(2048);
	   while((len=input.read(array))!=-1){
		   bytearray.write(array,0,len);
	   }
	   bytearray.flush();
	   return{
		   code:conn.getResponseCode(),
		   headers:()=>{return conn.getHeaderFields();},
		   header:(key)=>{return conn.getHeaderField(key);},
		   string:()=>{return bytearray.toString();},
		   bytes:()=>{return bytearray.toBytes();},
		   json:()=>{return JSON.parse(bytearray.toString()+"");},
		   close:()=>{bytearray.close();conn.disconnect();},
           eval:()=>{return eval("("+bytearray.toString()+")");}
	   }
}
function ajax(opt,callback){
	if(callback)
	async(()=>{
		try{
			return request(opt);
			}catch(e){
				return {code:0,data:e.getMessage()};
			}
	},(result)=>{
		callback(result);
	});
}
var module=new Object();
function $open(type,args){
	//toast(typeof args.url);
	open(type,JSON.stringify(args));
}
function setTimeout(fun,duration){
	var time=duration||0;
	var run={run:function(){java.lang.Thread.currentThread().sleep(time); fun();}};
	new java.lang.Thread(new java.lang.Runnable(run)).start();
}
eval(load("file:///android_asset/Promise.js")+"");
function prompt(msg,defaultValue){
	return new Promise(function(resolve,reject){
		_prompt(msg,defaultValue,(result)=>{
			resolve(result);
		})});
}
function confirm(title,msg){
	return new Promise(function(resolve,reject){
		_confirm(title,msg,(result)=>{
			resolve(result);
		})});
}
//(()=>{toast("aaa")})();
