<html>

<head>
<title>Wotlas - MySQL Tables creation</title>
</head>

<body>

<?
	if ($pass!="Bob") // the Creator ;-)
		die("No op !</body></html>");
		
	include("../connect.inc");
	
	$connect=Connect();
	
	$query="DROP TABLE todo";
	mysql_query($query,$connect);

	$query="CREATE TABLE todo (num INT UNSIGNED NOT NULL,title VARCHAR(128) NOT NULL,status TINYINT UNSIGNED,draft_ref VARCHAR(128), pre_req VARCHAR(128),descr BLOB, devel BLOB, PRIMARY KEY(num))";
	mysql_query($query,$connect) or die("<br>SQL query failure : $query</body></html>");
	
	echo "Table todo créée<br>";
	
	mysql_close($connect);
?>


</body>

</html>