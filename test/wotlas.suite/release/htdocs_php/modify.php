<html>
<head>
<title>News Administration Page</title>
</head>

<body>
<h1>Modify a news</h1>
<br>
<form action="modify_news.php" method=post>

<?
	include("connect.inc");
	$connect=Connect();

	include("date_func.inc");

	$query="SELECT * FROM news WHERE num=$num ORDER BY DATE DESC,NUM DESC";
	$result=mysql_query($query,$connect) or die("SQL Request failure : $query</body></html>");

	$lin=mysql_fetch_array($result);

	echo "\n<input type=hidden value=$num name=num>";	
	echo "\nTitle :<br>";
	echo "<input size=60 maxlength=128 name=titre value=\"".$lin["titre"]."\"><br>";
	
	echo "\nDate : (dd-mm-yyyy)<br>";
	echo "<input size=12 maxlength=10 name=date value=".Conv_date($lin["date"])."><br>";
	echo "<br>";
	echo "\nBody :<br>";
	echo "<textarea cols=60 name=contenu>".$lin["contenu"]."</textarea><br>";
	echo "<br>";
	echo "\n<input type=submit>";

?>
</form> 

</body>
</html>
