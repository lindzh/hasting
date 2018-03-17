function weightSubmmit(){
    	var paramList = new Array();
    	var inputs = $('.form-control');
    	$.each(inputs,function(index,input){
    		var hostId = input.name;
    		var value = input.value;
    		var hostWeight = {};
    		hostWeight.id = hostId;
    		hostWeight.wantWeight = value.trim();
    		paramList.push(hostWeight);
        });
        var content = JSON.stringify(paramList);

        $('#weightData').val(content);
        $('#weightForm').submit();
}
