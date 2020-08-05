var Promise=function(resolver){
	var value;
	var callback;
	function resolve(val){
		value=val;
		if(callback)
			callback(value);
	}
	function reject(val){
		value=val;
		if(callback)
			callback(value);
	}
	Promise.prototype.then=(res)=>{
		callback=res;
		if(value){
			callback(value);
			}
		
	}
	setTimeout(()=>{
	resolver(resolve,reject);
	},0);
	return this;
}
