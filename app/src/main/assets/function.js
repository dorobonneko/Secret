for(var fn in runtime) {
  if(typeof runtime[fn] === 'function') {
    this[fn] = (function() {
      var method = runtime[fn];
      return function() {
         return method.apply(runtime,arguments);
      };
    })();
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
       var conn=new java.net.URL(opt.url).openConnection();
	   if(conn instanceof javax.net.ssl.HttpsUrlConnection){
		   
	   }
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
		   string:()=>{return bytearray.toString();},
		   bytes:()=>{return bytearray.toBytes();},
		   json:()=>{return JSON.parse(bytearray.toString());},
		   close:()=>{bytearray.close();conn.disconnect();}
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
//(()=>{toast("aaa")})();
