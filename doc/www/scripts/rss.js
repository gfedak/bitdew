
	var rssBoxTxt_readMore = 'Read more';
	var rssBoxOpenLinksInNewWindow = true;
	
	
	var rssBoxAjaxObjects = new Array();
	
	function changeSlide(boxId,secondBetweenSlides,slideIndex)
	{
		var el = document.getElementById(boxId);
		var divs = el.getElementsByTagName('DIV');
		if(slideIndex>0){
			divs[slideIndex-1].style.display='none';
		}else{
			divs[divs.length-1].style.display='none';	
		}	
		divs[slideIndex].style.display='block';
		slideIndex++;
		if(slideIndex>=divs.length)slideIndex = 0;
		setTimeout('changeSlide("' + boxId + '",' + secondBetweenSlides + ',' + slideIndex + ')',secondBetweenSlides*1000);
		
	}
	
	function openUrlInNewWindow()
	{
		var rssBoxWin = window.open(this.href);
		return false;
	}
	function showAjaxContent(ajaxIndex,boxId,secondBetweenSlides)
	{
		
		var rssContent = rssBoxAjaxObjects[ajaxIndex].response;
		tokens = rssContent.split(/\n\n/g);
		var destinationObj = document.getElementById(boxId);
		var headerTokens = tokens[0].split(/\n/g);
		if(headerTokens[0]=='0'){
			headerTokens[1] = '';
			headerTokens[0] = 'Invalid source';
		}
		
		
		for(var no=1;no<tokens.length;no++){	// Looping through RSS items
			var string = '';
			var itemTokens = tokens[no].split(/##/g);		
			var div = document.createElement('DIV');
			if(no>1)div.style.display='none';	
			destinationObj.appendChild(div);
			
			// Adding date
			var date = document.createElement('P');
			date.className='rssBoxDate';
			date.innerHTML = itemTokens[1];
			div.appendChild(date);
			
			// Adding heading
			var heading = document.createElement('P');
			heading.className = 'rssBoxHeading';
			heading.innerHTML = itemTokens[0];
			div.appendChild(heading);
			
			// Adding description
			var description = document.createElement('P');
			description.className='rssBoxDescription';
			description.innerHTML = itemTokens[2];
			div.appendChild(description);
			
			// Adding link
			var link = document.createElement('A');
			link.className = 'rssBoxLink';
			link.innerHTML = rssBoxTxt_readMore;
			link.href = itemTokens[3];
			if(rssBoxOpenLinksInNewWindow){
				link.onclick = openUrlInNewWindow;
			}
			div.appendChild(link);
			
		}
		
		setTimeout('changeSlide("' + boxId + '",' + secondBetweenSlides + ',1)',secondBetweenSlides*1000);
		
		

				
	}
	
	function insertAjaxContent(destinationId,url,maxItems,secondBetweenSlides)
	{
		var ajaxIndex = rssBoxAjaxObjects.length;
		rssBoxAjaxObjects[ajaxIndex] = new sack();
		rssBoxAjaxObjects[ajaxIndex].requestFile = 'scripts/readRSS.php?rssURL=' + url + '&maxRssItems=' + maxItems;	// Specifying which file to get
		rssBoxAjaxObjects[ajaxIndex].onCompletion = function(){ showAjaxContent(ajaxIndex,destinationId,secondBetweenSlides); };	// Specify function that will be executed after file has been found
		rssBoxAjaxObjects[ajaxIndex].runAJAX();		// Execute AJAX function	
	}
	
	function initRssBoxScript()
	{
	    //insertAjaxContent('rssBox','http://www.dhtmlgoodies.com/rss/dhtmlgoodies.xml',5,3);
	    insertAjaxContent('rssBox','http://xtremweb.wordpress.com/feed/',5,7);
		
	}
	
	
	window.onload = initRssBoxScript;
	
	
