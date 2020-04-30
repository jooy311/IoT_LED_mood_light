<?php
	$con = mysqli_connect("localhost","jooy311","gosemvhs1!","jooy311");
	/*if(mysqli_connect_errno($con))
	{
		echo "mysql error: ";
		echo mysqli_connect_error();
		exit();
	}*/
	
	$ID = $_POST["ID"];
	$PASSWORD = $_POST["PASSWORD"];
	$NAME = $_POST["NAME"];
	$BIRTH = $_POST["BIRTH"];
	$PHONE = $_POST["PHONE"];
	
	$statement = mysqli_prepare($con, "INSERT INTO MEMBER VALUES (?,?,?,?,?)");
	mysqli_stmt_bind_param($statement, "sssss", $ID, $PASSWORD, $NAME, $BIRTH, $PHONE);
	mysqli_stmt_execute($statement);
	
	//mysqli_set_charset($con,"utf8");

	/*$sql = "select * from MEMBER";
	$result = mysqli_query($con,$sql);
	$data = array();
	if($result) {
		echo "sucess to bring from data of table.";
		while($row = mysqli_fetch_array($result)){
			array_push($data,
				array('ID' => $row[0],
					'PASSWORD'=>$row[1],
					'NAME'=>$row[2],
					'BIRTH'=>$row[3],
					'PHONE'=>$row[4]));
			}//while

			echo"<pre>";
			print_r($data);
			echo '</pre>';
		}//if
		else{
			echo "error : ";
			echo mysqli_error($con);
	}//else
*/
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	//echo json_encode($data);

	//mysql_close($con);

 ?>
