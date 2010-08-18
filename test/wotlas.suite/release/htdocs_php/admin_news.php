<html>
<head>
<title>News Administration Page</title>
</head>

<body>
<h1>Administration</h1>
<br>

<?
    if ($pass!="Bob")
	   die("Authorized personel only !</body></html>");  																			 
?>

<table border=1 width="90%">
<tr><th>#</th><th>Title</th><th>Date</th><th>Content</th><th>Action</th></tr>

<?
	include("connect.inc");
	$connect=Connect();

	include("date_func.inc");	
	
	$query="SELECT * FROM news ORDER BY NUM ASC";
	$result=mysql_query($query,$connect) or die("SQL request failure : $query</body></html>");

	while ($lin=mysql_fetch_array($result))
		{	
	 		echo "\n<tr>";
			echo "\n<td>".$lin["num"]."</td>";
			echo "\n<td>".$lin["titre"]."</td>";
	 		echo "\n<td>".Conv_date($lin["date"])."</td>";
			echo "\n<td>".$lin["contenu"]."</td>";
			echo "\n<td><a href=delete.php?num=".$lin["num"].">Delete</a> | <a href=modify.php?num=".$lin["num"].">Modify</a></td>";
			echo "\n</tr>";
		}

	mysql_free_result($result);
	mysql_close($connect);
?>

</table>

</body>
</html>