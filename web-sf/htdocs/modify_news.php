<html>
<head>
<title>News Administration Page</title>
</head>

<body>
<center>
<br><br>
<?
	include("connect.inc");
	$connect=Connect();
	
	include("date_func.inc");
	 
	$query="REPLACE INTO news (num,titre,contenu,date) VALUES ($num,\"$titre\",\"$contenu\",\"".Unconv_date($date)."\")";
	mysql_query($query,$connect) or die("SQL request failure : $query</body></html>");
		
	echo "The news has been updated.";
				
	mysql_close($connect);
?>
<br><br>
<br>
<a href="news.php">Back to news</a>
</center>
</body>
</html>