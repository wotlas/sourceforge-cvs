<html>
<head>
<title>Wotlas Devel</title>
</head>

<style type="text/css">
p	{text-align:justify}
h1	{color:navy}
</style>

<body bgcolor="#FFFFFF" text="#000000">
<p><img src="../images/motif-vigne.gif" width="620" height="40"></p>
<p><font size="4"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Light And Shadow<br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Copyright (C) 2001 - 2002. WOTLAS Team.<br>
  <br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TO DO LIST...<br>
  </font><br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Here are devel proposals for wotlas. Explanations are short so feel free to ask for more details.<br>
</p>
<p>Project status : 
  <font color="#0033CC"><i>non developped</i></font>, 
  <font color="#FF6600"><i>assigned</i></font>, 
  <font color="#CC3300"><i>under development</i></font>, 
  <font color="#00CC00"><i>developped</i></font>
  </p>
<p align="center">&nbsp;</p>
<p align="center"><img src="../images/separator.gif" width="300" height="2"></p>

<?
	include("../connect.inc");
	$connect=Connect();
	
	$query="SELECT * FROM todo ORDER by num ASC";
	$result=mysql_query($query,$connect) or die ("SQL query failure : $query</body></html>");
	
	while ($lin=mysql_fetch_array($result))
	{
		echo "\n<p align=center><img src=\"../images/separator.gif\" width=300 height=2></p>\n";
		echo "<h1>".$lin["num"]." - ".$lin["title"]."</h1>\n";
		echo "\n<p><b>Status :</b> ";
		switch ($lin["status"])
		{
			case 0:$stat="<font color=#0033CC><i>non developped</i></font>";break;
			case 1:$stat="<font color=#FF6600><i>assigned</i></font>";break;
			case 2:$stat="<font color=#CC3300><i>under development</i></font>";break;
			case 3:$stat="<font color=#00CC00><i>developped</i></font>";break;
			default:$stat="<font color=#0033CC><i>non developped</i></font>"; 
		}
		echo "$stat<br>\n";		
		echo "<b>Draft reference :</b> ".$lin["draft_ref"]."<br>\n";
		echo "<b>Pre-requisite :</b> ".$lin["pre_req"]."<br>\n";
		echo "</p>\n\n<p><b>Description :</b> ";
		echo $lin["descr"];
		echo "\n</p>\n\n<p><b>Development :</b> ";
		echo $lin["devel"];
		echo "</p>";
	}	
	
	mysql_free_result($result);
	mysql_close($connect);
?>



</body>
</html>