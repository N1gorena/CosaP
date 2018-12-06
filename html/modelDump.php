<?php
ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(E_ALL);


if($_POST['password'] == 'alpastordetrack719'){
	//TODO
	$dbConn = new mysqli("localhost","dbu","qq","appMain");
	if($dbConn->connect_error){
		die("Failed:".$dbConn->connect_error);
	}
	$sql = "SELECT models.name, files.fileLoc FROM models JOIN files ON files.mId = models.id   WHERE 1";
	$result = $dbConn->query($sql);
	$nAndLoc = [];
	if($result->num_rows > 0){
		while($row = $result->fetch_assoc()){
			$nAndLoc[$row["name"]] = $row["fileLoc"];

		}
		echo json_encode($nAndLoc);
	}
	else{
		echo "Something went Wrong!";
	}
	
	$dbConn->close();
 
}	

?>
