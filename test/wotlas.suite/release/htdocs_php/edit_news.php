<html>
<head>
<title>Adding a news to Wotlas site</title>
</head>

<body>
<h1>Adding a news</h1>
<br>
<?	
	if ($pass!="Bob")
		die("Sorry you are not authorized to add a news to Wotlas website</body></html>");
?>

<form action="add_news.php" method=post>
Title :<br>
<input size=60 maxlength=128 name="titre"><br>
<br>
Body :<br>
<textarea cols=60 name="contenu"></textarea><br>
<br>

<input type=submit>
</form> 

</body>
</html>
