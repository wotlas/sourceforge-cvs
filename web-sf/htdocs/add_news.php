<html>
<head>
<title>Adding a news to Wotlas site</title>
</head>

<body>
<center>
<br><br>
<?
	if ($titre=="")
	 die ("Error : no title found !</body></html>");

	include("connect.inc");
	$connect=Connect();
	 
	$query="INSERT INTO news (titre,contenu,date) VALUES (\"".$titre."\",\"".$contenu."\",\"".date("Y-m-d")."\")";
	mysql_query($query,$connect) or die("Echec de la requête : ".$query);
		
	echo "Your news has been added.";
				
	mysql_close($connect);
?>
<br><br>
<br>
<a href="news.php">Back to news</a>
</center>
</body>
</html>