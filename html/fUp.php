<?php
ini_set('display_errors',1);
error_reporting(E_ALL);

$target_dir = "modelFiles/";
$target_file = $target_dir . basename($_FILES["theFile"]["name"]);
$imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

if($imageFileType != "obj"){
	echo "Only WAVEFRONT allowed!?";
}
else{
	if(!file_exists($target_file)){
		echo $_FILES["theFile"]["error"];
		if(move_uploaded_file($_FILES["theFile"]["tmp_name"],$target_file)){
			echo "File ".basename($_FILES["theFile"]["name"]). "UPLOADED!!!";
			$dbConn = mysqli_connect("localhost","dbu","qq","appMain");
		
$sql = "Insert into files (mId,fileLoc) values (".$_POST['modelId'].",'".$target_file."')";
			if($dbConn->query($sql) === TRUE){
				echo "Insert succ";
			}
			else{echo $dbConn->error;}
			$dbConn->close();
		}
		else{
			echo "No dice on the UPLOAD...";
		}
	}
	else{
		if(move_uploaded_file($_FILES["theFile"]["tmp_name"],$target_file)){
			echo "File replaced";
		}
		else{
			echo "Failed to upload";
		}
	
	}
}

