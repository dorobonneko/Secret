for (let fn in runtime)
{
    if (typeof runtime[fn] === 'function')
    {
        this[fn] = (function() {
            let method = runtime[fn];
            return function() {
                return method.apply(runtime, arguments);
            };
        })();
    }
    else
    {
        this[fn] = runtime[fn];
    }
}
require("file:///android_asset/Promise.js");
function urlEncode(str) {
    return encodeURIComponent(str);
}
function urlDecode(str) {
    return decodeURIComponent(str);
}
function $(arg) {
    //屏蔽jquery
}
function array(list) {
    return list.toArray();
//	var arr=new Array();
//	for(var i=0;i<list.size();i++)
//	arr.push(list.get(i));
//	return arr;
}
function async(fun, callback) {
	var run={run:function() {callback(fun());}};
	new java.lang.Thread(new java.lang.Runnable(run)).start();
}
function request(opt) {
    opt = opt || {};
    opt.method = opt.method ?opt.method.toUpperCase() : 'GET';
    opt.url = opt.url || '';
    opt.data = opt.data || null;
    opt.zip = opt.zip == null ?true: opt.zip;
    opt.headers = opt.headers || {};
    var conn=openUrl(opt.url);
    conn.setRequestMethod(opt.method);
    for(var header in opt.headers){
        conn.setRequestProperty(header, opt.headers[header]);
    }
    var accept=conn.getRequestProperty("Accept-Encoding");
    if (!accept)
    {
        if (opt.zip)
        {
            conn.setRequestProperty("Accept-Encoding", "gzip;q=1.0, identity; q=0.5, *;q=0");
        }
    }
    if (opt.data)
    {
        var bytes=getBytes(opt.data);
        conn.setRequestProperty("content-length",bytes.length+"");
        var out=conn.getOutputStream();
        out.write(bytes);
        out.flush();
    }
    var input=conn.getInputStream();
    var encoding=conn.getHeaderField("Content-Encoding");
    if (encoding && encoding.equals("gzip"))
        input = new Packages.java.util.zip.GZIPInputStream(input);
    var bytearray=new Packages.java.io.ByteArrayOutputStream();
    var len=-1;
    var array=getBytes(2048);
    while ((len = input.read(array)) != -1)
    {
        bytearray.write(array, 0, len);
    }
    bytearray.flush();
    return{
    code:conn.getResponseCode(),
    headers:()=>{return conn.getHeaderFields();},
    header:(key) =>{return conn.getHeaderField(key);},
    string:()=>{return bytearray.toString();},
    bytes:()=>{return bytearray.toBytes();},
    json:()=>{return JSON.parse(bytearray.toString() + "");},
    close:()=>{bytearray.close();conn.disconnect();},
    eval:()=>{return eval("(" + bytearray.toString() + ")");}
	}
}
function ajax(opt,callback){
    if(opt.async){
        return new Promise(function(resolve,reject){
            async(()=>{
                try{
                    return request(opt);
                }catch(e){
                    return {code:0,data:e.getMessage()};
                }
            },(result)=>{
                resolve(result);
            });//async结束
        });
    }
    
	if(callback){
	    async(()=>{
		    try{
			    return request(opt);
			}catch(e){
				return {code:0,data:e.getMessage()};
			}
	    },(result)=>{
		    callback(result);
	    });//async结束
    }else{
        return request(opt);
    }
}
var module=new Object();
function open(args){
	//toast(typeof args.url);
	let data=JSON.stringify(args,(key,value)=>{
       if(typeof value==="function")return "";
       return value instanceof java.lang.String?value+"":value;
       });
       window.open(data);
}
function setTimeout(fun,duration){
	var time=duration||0;
	var run={run:function(){java.lang.Thread.currentThread().sleep(time); fun();}};
	new java.lang.Thread(new java.lang.Runnable(run)).start();
}
//eval(load("file:///android_asset/Promise.js")+"");

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
function progress(title){
    return new Promise(function(resolve,reject){
        _progress(title,(result)=>{
            resolve(result);
        });
    });
}
//(()=>{toast("aaa")})();
