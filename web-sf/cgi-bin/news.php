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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Wotlas (<b> W</b>heel<b> O</b>f<b> T</b>ime<b> - L</b>ight<b> A</b>nd<b> S</b>hadow ) is a free software available under the <a href="http://www.gnu.org/copyleft/gpl.html" target="_blank">GNU General Public License</a>. It's an <i>online virtual community</i> based on Robert Jordan's Wheel of Time books. Written 100% in Java, wotlas uses 2D graphics, enhanced communication systems and an hybrid client-server approach.
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
		
	$query="SELECT * FROM news ORDER BY DATE DESC,NUM DESC LIMIT 0,9";
	$result=mysql_query($query,$connect) or die("Echec de la requête : ".$query."</body></html>");

	while ($lin=mysql_fetch_array($result))
	{	
	 echo "\n<blockquote>";
	 echo "\n<img src=\"images/button.gif\" width=14 height=14 align=absmiddle> <b><font size=3>";
	 echo Conv_date($lin["date"]);
	 echo " - <font size=5>".$lin["titre"]."</font>";
	 echo "</font></b><br>\n";
	 echo "<blockquote>\n<p align=justify><font size=2>\n";
	 echo $lin["contenu"]; 	// corps du texte
	 echo "</font></p>\n</blockquote>\n</blockquote>\n";
	 echo "<!-- Anti-bug --></a></p></table>";
	 echo "\n<hr>\n";	
	}
	
	mysql_free_result($result);
	mysql_close($connect);
?>
</body>
</html>
