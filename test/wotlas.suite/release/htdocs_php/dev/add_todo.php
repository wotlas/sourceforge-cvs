<html>
<head>
<title>Adding an entry to to-do list</title>
</head>

<body>
<h1>Adding a news</h1>
<br>
<?
	include("../connect.inc");
	$connect=Connect();
	 
	$query="INSERT INTO todo (num,title,status,draft_ref,pre_req,descr,devel) VALUES ($num,\"$title\",$status,\"$draft_ref\",\"$pre_req\",\"$descr\",\"$devel\")";
	mysql_query($query,$connect) or die("SQL request failure : $query</body></html>");
		
	echo "Your entry has been added.";
				
	mysql_close($connect);
?>

</body>
</html>
