<?
	function Connect()
	{
		$connect=mysql_connect("mysql.sourceforge.net","wotlas","robertjordan") or 
			$connect=mysql_connect("localhost","julien","bidon") or // set here to your local MySQL params
				die("Echec de la connexion � la base");
	
		mysql_select_db("wotlas",$connect) or die("Base de donn�es introuvable");
		
		return $connect;
	}		
?>