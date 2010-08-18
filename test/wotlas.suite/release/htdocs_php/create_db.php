<html>

<head>
<title>Wotlas - MySQL Tables creation</title>
</head>

<body>

<?
	if ($pass!="Bob") // the Creator ;-)
		die("No op !</body></html>");
		
	include("connect.inc");
	
	$connect=Connect();
	
	$query="DROP TABLE news";
	mysql_query($query,$connect);

	$query="CREATE TABLE news (num INT UNSIGNED NOT NULL AUTO_INCREMENT,date DATE,titre VARCHAR(128) NOT NULL,contenu BLOB, PRIMARY KEY(num))";
	mysql_query($query,$connect) or die("<br>SQL query failure : $query</body></html>");
	
	echo "Table news créée<br>";
	
	mysql_close($connect);
?>


</body>

</html>