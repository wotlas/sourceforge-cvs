<?
	function Connect()
	{
		$connect=mysql_connect("mysql4-w","w25453rw","robertjordan") or 
			$connect=mysql_connect("localhost","wotlasweb","robertjordan") or // set here to your local MySQL params
				die("Echec de la connexion à la base");
	
		mysql_select_db("w25453_wotlasweb",$connect) or die("Base de données introuvable");
		
		return $connect;
	}		
?>