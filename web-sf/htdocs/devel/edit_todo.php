<html>
<head>
<title>Adding an entry to the to-do list</title>
</head>

<body>
<h1>Adding an entry to the to-do list</h1>
<br>
<?	
	if ($pass!="Bob")
		die("Sorry you are not authorized to add an entry to Wotlas website</body></html>");
		
	if ($num!=0)
	{
		include("../connect.inc");
		$connect=Connect();
	
		$query="SELECT * FROM todo WHERE num=$num";
		$result=mysql_query($query,$connect) or die ("SQL query failure : $query</body></html>");
	
		$lin=mysql_fetch_array($result);

		echo "<form action=add_todo.php method=post>";
		echo "<table>\n<tr>\n<td>Num : </td><td><input size=3 maxlength=3 name=num value=$num>";
		echo "&nbsp;</td><td>&nbsp;Title : </td><td><input size=60 maxlength=128 name=title value=\"".$lin["title"]."\"></td></tr>";
		echo "<tr><td>Status : </td><td><input size=3 maxlength=1 name=status value=".$lin["status"]."></td></tr>";				
		echo "<tr><td colspan=3>Draft reference : </td><td><input size=60 maxlength=128 name=draft_ref value=\"".$lin["draft_ref"]."\"></td></tr>";
		echo "<tr><td colspan=3>Pre-requisite : </td><td><input size=60 maxlength=128 name=pre_req value=\"".$lin["pre_req"]."\"></td></tr>";
		echo "</table>\n<br>\nDescription :<br>\n<textarea cols=64 name=descr>";
		echo $lin["descr"];
		echo "</textarea><br>\nDevelopment :<br>\n<textarea cols=64 name=devel>";
		echo $lin["devel"];
		echo "</textarea><br>\n<br><br>\n<input type=submit>\n</form>"; 
		die("</body></html>");
	}
?>
	
<form action="add_todo.php" method=post>
<table>
<tr>
<td>Num : </td><td><input size=3 maxlength=3 name="num">&nbsp;</td><td>&nbsp;Title : </td><td><input size=60 maxlength=128 name="title"></td></tr>
<tr><td>Status : </td><td><input size=3 maxlength=1 name="status"></td></tr>
<tr><td colspan=3>Draft reference : </td><td><input size=60 maxlength=128 name="draft_ref"></td></tr>
<tr><td colspan=3>Pre-requisite : </td><td><input size=60 maxlength=128 name="pre_req"></td></tr>
</table>
<br>
Description :<br>
<textarea cols=64 name="descr"></textarea><br>
Development :<br>
<textarea cols=64 name="devel"></textarea><br>
<br>
<br>

<input type=submit>
</form> 

</body>
</html>
