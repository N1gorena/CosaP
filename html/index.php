<?php
$dbConn = mysqli_connect("127.0.0.1", "dbu","qq","appMain");

if($dbConn->connect_error){
	echo "Failed to connect to Mysql: " . $dbConn->connect_error;
}

$modelsQuery = "Select * from models where 1";
$result = $dbConn->query($modelsQuery);

?>
<html>
	<head>
		<Title>Hidden Model Loader</Title>
	</head>
	<body>
		<h1>Hello Friend</h1>
		<h3>Insert files here!</h3>
		<form action="fUp.php" method="post" enctype="multipart/form-data">
			<input type = "file" name="theFile" id = "theFile">
			<label>For model:</label>
			<select name="modelId">
			<?php
			if($result->num_rows > 0){
				while($row = $result->fetch_assoc()){
			
		echo "<option value=".$row["id"].">".$row["name"]."</option>";
				}
			}
			$dbConn->close();

			?>
			</select></br>
			<input type ="submit" value = "Upload WAV" name = "submit">
		</form>
	</body>

</html>


