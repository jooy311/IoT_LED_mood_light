<?php

	//회원정보를 불러오는 php코드
	$con = mysqli_connect("localhost", "jooy311", "gosemvhs1!", "jooy311");
	$ID = $_POST["ID"];
	$PASSWORD = $_POST["PASSWORD"];

	$statement = mysqli_prepare($con, "SELECT * FROM MEMBER WHERE ID = ?");
	mysqli_stmt_bind_param($statement, "s", $ID);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $ID, $PASSWORD, $NAME, $BIRTH, $PHONE);

	$response = array();
	$response["success"] = false;

	while(mysqli_stmt_fetch($statement)) {
		$response["success"] = true;
		$response["ID"] = $ID;
		$response["PASSWORD"] = $PASSWORD;
		$response["NAME"] = $NAME;
		$response["BIRTH"] = $BIRTH;
		$response["PHONE"] = $PHONE;
	}
	echo json_encode($response);
?>