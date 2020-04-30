<?php

	//회원정보를 불러오는 php코드
	$con = mysqli_connect("localhost", "jooy311", "gosemvhs1!", "jooy311");
	$ID = $_POST["ID"];
	$PASSWORD = $_POST["PASSWORD"];

	$statement = mysqli_prepare($con, "SELECT * FROM PROFILE WHERE ID = ?");
	mysqli_stmt_bind_param($statement, "s", $ID);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $ID, $IMAGE);

	$bring_path = "upload_image/$ID.jpg"; //사용자 아이디로 파일 저장되도록
	$imagedata = file_get_contents("upload_image/$ID.jpg");
    //file_put_contents($bring_path,base64_encode($imagedata));
    $base64_encode = base64_encode($imagedata);

	$response = array();
	$response["success"] = false;

	while(mysqli_stmt_fetch($statement)) {
		$response["success"] = true;
		$response["ID"] = $ID;
		//$response["IMAGE"] = $IMAGE;
		$response["IMAGE"] = $base64_encode
	}
	echo json_encode($response);
?>