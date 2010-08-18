<?
	function Conv_date($date)	// convert the date from MySQL fmt (yyyy-mm-dd)
	{
		$y=substr($date,0,4);
		$m=substr($date,5,2);
		$d=substr($date,8,2);
		
		return "$d-$m-$y"; // into dd-mm-yyyy
	}		


	function Unconv_date($date)	 // convert the date from dd-mm-yyyy
	{
	 	$y=substr($date,8,4);
	 	$m=substr($date,3,2);
	 	$d=substr($date,0,2);
	
	 	return "$y-$m.$d";
	}
	
?>