<?php

	//회원정보를 업데이트 하는 php코드
	$con = mysqli_connect("localhost","jooy311","gosemvhs1!","jooy311");

	$ID = $_POST["ID"];
	$PASSWORD = $_POST["PASSWORD"];
	$NAME = $_POST["NAME"];
	$BIRTH = $_POST["BIRTH"];
	$PHONE = $_POST["PHONE"];
	
	$statement = mysqli_prepare($con, "UPDATE MEMBER SET PASSWORD =? , NAME =? , BIRTH =?,  PHONE =? WHERE ID = '$ID'");
	mysqli_stmt_bind_param($statement, "ssss", $PASSWORD, $NAME, $BIRTH, $PHONE);
	mysqli_stmt_execute($statement);
	
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);

 ?>
