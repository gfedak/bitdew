<?

include_once("lastRSS.php");

if(!isset($_GET['rssURL'])){
	die("No RSS url given");
}

ini_set('display_errors', false);

$rss = new lastRSS();	// Creating RSS reader object
$rss->cache_dir='./cache';	// cache directory
$rss->cache_time = 300; // Cache data on the server for 5 minutes
$rss->date_format = "U";	// Dateformat = timestamp 
$rss->items_limit = 100;	// Maximum number of items
$rss->CDATA = "content";	// how to deal with CDATA
$data = $rss->Get($_GET['rssURL']);	// Get css data



$rssItems = $data["items"];	// News items

$maxRssItems = $_GET['maxRssItems']; 	// Maximum number of RSS news to show.

echo $data["title"]."\n";	// Site title
echo min($maxRssItems,count($rssItems))."\n";	// Number of news

$outputItems = array();	// Creating new array - this items holds the data we send back to the client

for($no=0;$no<count($rssItems);$no++){
	if(!isset($rssItems[$no]["pubDate"]))$rssItems[$no]["pubDate"]=mktime();
	if(!isset($rssItems[$no]["description"]))$rssItems[$no]["description"]=" ";
	$outputItems[$rssItems[$no]["pubDate"].$no] = array(
		"title"=>$rssItems[$no]["title"],
		"pubDate"=>$rssItems[$no]["pubDate"],
		"description"=>$rssItems[$no]["description"],
		"link"=>$rssItems[$no]["link"],
		"category"=>$rssItems[$no]["category"]);	
}

ksort($outputItems);	// Sorting items from the key
$outputItems = array_reverse($outputItems);	// Reverse array so that the newest item appear first

$countItems = 0;

foreach($outputItems as $key=>$value){	// Output items - looping throught the array $outputItems
	echo "\n\n";	
	echo preg_replace("/[\r\n]/"," ",$value["title"])."##";	// Title
	echo date("Y-m-d H:i:s",$value["pubDate"])."##";	// Date
	echo preg_replace("/[\r\n]/"," ",$value["description"])."##";	// Description
	echo preg_replace("/[\r\n]/"," ",$value["link"])."##";	// Link
	$countItems++;
	if($countItems>=$maxRssItems)exit;	

}

?>
