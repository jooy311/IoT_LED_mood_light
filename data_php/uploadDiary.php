<?php

$con = mysqli_connect("localhost","jooy311","gosemvhs1!","jooy311");

$ID = $_POST["ID"];
$DATE = $_POST["DATE"];
$WRITE = $_POST["WRITE"];

$statement = mysqli_prepare($con, "INSERT INTO DIARY VALUES (?,?,?)");
    mysqli_stmt_bind_param($statement, "sss", $ID, $DATE, $WRITE);
    mysqli_stmt_execute($statement);


  $response = array();
    $response["success"] = true;
    
    echo json_encode($response);

    /*$size = GetImageSize($image); 
$width = $size[0]; 
$height = $size[1]; 
$imageblob = addslashes(fread(fopen($image, "r"), filesize($image))); 
$filesize = filesize($image) ; 

$query=" INSERT INTO gallery VALUES ('', '$imageblob', '$title', '$width','$height', '$filesize', '$detail' )" ; 
$result=mysql_query($query,$connect ); 
*/


/*if(isset($_FILES['upfile']) && $_FILES['upfile']['name'] != "") {

    $file = $_FILES['upfile'];

    //$upload_directory = 'upload_diary/';

    $ext_str = "txt";

    $allowed_extensions = explode(',', $ext_str);

    

    $max_file_size = 5242880;

    $ext = substr($file['name'], strrpos($file['name'], '.') + 1);

    
    // 확장자 체크

    if(!in_array($ext, $allowed_extensions)) {

        echo "업로드할 수 없는 확장자 입니다.";

    }

    
    // 파일 크기 체크

    if($file['size'] >= $max_file_size) {

        echo "5MB 까지만 업로드 가능합니다.";

    }

    
    $upload_path = "upload_image/$ID.txt"

    //$path = md5(microtime()) . '.' . $ext; //"upload_image/$ID.jpg"

    if(move_uploaded_file($file['tmp_name'], $upload_path)) {
    //if(move_uploaded_file($file['tmp_name'], $upload_directory.$path)) {

        $query = "INSERT INTO DIARY VALUES (?,?,?)";

       // $file_id = md5(uniqid(rand(), true));

        //$name_orig = $file['name'];

        //$name_save = $path;

        

        $stmt = mysqli_prepare($db_conn, $query);

        $bind = mysqli_stmt_bind_param($stmt, "sss", $ID, $DATE, $WRITE);

        $exec = mysqli_stmt_execute($stmt);

        file_put_contents($upload_path, base64_decode($WRITE));

      

        mysqli_stmt_close($stmt);

        

        echo"<h3>파일 업로드 성공</h3>";

        //echo '<a href="file_list.php">업로드 파일 목록</a>';

        

    }

} else {

    echo "<h3>파일이 업로드 되지 않았습니다.</h3>";

   // echo '<a href="javascript:history.go(-1);">이전 페이지</a>';

}



mysqli_close($db_conn);*/

?>