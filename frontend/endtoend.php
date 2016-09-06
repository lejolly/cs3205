<?php 
    // create curl resource
    $ch = curl_init();

    // set url
    curl_setopt($ch, CURLOPT_URL, "");

    //return the transfer as a string
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

    // $output contains the output string
    $output = curl_exec($ch);
    $array = json_decode($output);
    $rand_keys = array_rand($array, 5);
    echo '<pre>';
    print_r($array[$rand_keys[0]]);
    print_r($array[$rand_keys[1]]);
    print_r($array[$rand_keys[2]]);
    print_r($array[$rand_keys[3]]);
    print_r($array[$rand_keys[4]]);
    echo '</pre>';

    // close curl resource to free up system resources
    curl_close($ch);
?>
