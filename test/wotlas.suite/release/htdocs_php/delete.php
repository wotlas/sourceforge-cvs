<html>
<head>
<title>News Administration Page</title>
</head>

<body>
<h1>Deletion</h1>
<br>

<?
	include("connect.inc");
	$connect=Connect();

	$query="DELETE FROM news WHERE num=$num";
	mysql_query($query,$connect) or die("Request failure : $query</body></html>");	
	
	mysql_close($connect);
?>

</body>
</html>
