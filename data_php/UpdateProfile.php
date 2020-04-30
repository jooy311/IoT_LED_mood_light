<?php
$user_name = "jooy311";
$user_pass = "gosemvhs1!";
$host_name = "localhost";
$db_name = "jooy311";

$con = mysqli_connect($host_name,$user_name,$user_pass,$db_name);


    $IMAGE = $_POST["IMAGE"];
    $ID = $_POST["ID"];
    //$name = $_POST["name"];
    
    $statement = mysqli_prepare($con, "UPDATE PROFILE SET IMAGE = ? WHERE ID = '$ID'");
    mysqli_stmt_bind_param($statement, "s", $IMAGE);
    mysqli_stmt_execute($statement);
    //$sql = "INSERT INTO PROFILE(id,name) VALUES('$id','$name')";
    $upload_path = "upload_image/$ID.jpg"; //사용자 아이디로 파일 저장되도록
    file_put_contents($upload_path,base64_decode($IMAGE));
   
    $response = array();
    $response["success"] = true;
    echo json_encode($response);

    //mysqli_close($con);


?>