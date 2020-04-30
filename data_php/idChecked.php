<?php
	$con = mysqli_connect("localhost", "jooy311", "gosemvhs1!", "jooy311");
	$ID = $_POST["ID"];

	$statement = mysqli_prepare($con, "SELECT * FROM MEMBER WHERE ID = ?");
	mysqli_stmt_bind_param($statement, "s", $ID);
	mysqli_stmt_execute($statement);
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $ID);

	$response = array();
	$response["success"] = true;

	while(mysqli_stmt_fetch($statement)) {
		$response["success"] = false;
		$response["ID"] = $ID;
	}
	echo json_encode($response);
?>