 $(document).ready(function() {
 	$('input[type="submit"').click(function(){
		 var compurl = "http://" + bootstrap + ":8080/p2pQuery?term=" + term; 
     	$.ajax({
        	url: compurl,
     		method: GET,
     		success: function(data){
     		alert('data is ' + data);
     		$("div[id=tableresult]").append(data);
     		}	
     	});
	}
});
 




