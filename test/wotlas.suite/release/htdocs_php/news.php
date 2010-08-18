<html>
<head>
<title>Wotlas News</title>

<link rel=stylesheet href="style.css">
</head>

<body bgcolor="#FFFFFF">

<br>
<img src="images/motif-vigne.gif" width="620" height="40"><br>
<center><img src="images/logo-wotlas.jpg" width="360" height="180"></center><br>

<p>
<font size="2">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Wotlas (<b> W</b>heel<b> O</b>f<b> T</b>ime<b> - L</b>ight<b> A</b>nd<b> S</b>hadow ) is a free software available under the <a href="http://www.gnu.org/copyleft/gpl.html" target="_blank">GNU General Public License</a>. It's an <i>online virtual community</i> based on Robert Jordan's Wheel of Time books. Written 100% in Java, Wotlas uses 2D graphics, enhanced communication systems and an hybrid client-server approach.
</font>
</p>

<p align="center">
<font size="2">
Click <a href="overview.html">here</a> for an overview of the Wotlas project.
</font></p>
<center><img src="images/separator.gif" width="300" height="2"></center><br>
<img src="images/news.gif" width="90" height="36"><br>  
  
<?
	include("connect.inc");
	$connect=Connect();

	include("date_func.inc");
	
	if ($start=="")
		$start=0;
	
	if ($end=="" || $end<=$start)
		$end=$start+9;
		
	$query="SELECT date,title,content FROM news ORDER BY Date DESC, NewsId DESC";
	$result=mysql_query($query,$connect) or die("SQL Request failure : $query</body></html>");
	
	$nb=mysql_num_rows($result);

	if ($nb<$start)
	{
		echo "<center>";
		echo "<br>No news in the specified boundaries : ".$start." - ".$end." !<br><br>";		
		echo "<a href=news.php?start=0>10 latest news</a>";
		echo "</center>";
		mysql_free_result($result);
		mysql_close($connect);
		die("</body></html>");
	}	

	for ($i=1;$i<$start;$i++)
		mysql_fetch_array($result);	
	
	while (($line=mysql_fetch_array($result)) && ($i<=$end))
		{	
	 		echo "\n<blockquote>";
	 		echo "\n<img src=\"images/button.gif\" width=14 height=14 align=absmiddle> <b><font size=3>";
	 		echo Conv_date($line["date"]);
	 		echo " - <font size=4>".$line["title"]."</font>";
	 		echo "</font></b><br>\n";
	 		echo "<blockquote>\n<p align=justify><font size=2>\n";
	 		echo $line["content"]; 	// body
	 		echo "</font></p>\n</blockquote>\n</blockquote>\n";
	 		echo "<!-- Anti-bug --></a></p></table>";
	 		echo "\n<hr>\n";	
			$i++;
		}
	
	echo "<center>";
	// must modify to manage the case > 10 but < 20		
	if ($nb>=10)
	{
	 if ($start>=10)
	 {
	 	echo "<a href=news.php?start=".($start-10).">10 next news</a>";
		if ($end<$nb)
			echo " - <a href=news.php?start=".($start+10).">10 previous news</a>";
	 }
	 else
	 {
		echo "<a href=news.php?start=0>10 latest news</a>";
		if ($end<$nb)
			echo " - <a href=news.php?start=10>10 previous news</a>";
	 }
	}		
	
	 
	 
	echo "</center>";
	
	mysql_free_result($result);
	mysql_close($connect);
?>

</body>
</html>
