<?
	function Conv_date($date)	// put the date from MySQL fmt (yyyy-mm-dd) into dd-mm-yyyy
	{
		$y=substr($date,0,4);
		$m=substr($date,5,2);
		$d=substr($date,8,2);
		
		return $d."-".$m."-".$y;
	}		
?>