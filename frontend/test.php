<?php
error_reporting(E_ALL);

echo "<h2>Random Actors</h2>\n";

/* Get the port for the WWW service. */
$service_port = '8081';

/* Get the IP address for the target host. */
$address = '127.0.0.1';

/* Create a TCP/IP socket. */
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
if ($socket === false) {
    echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "<br>\n";
} else {
    // echo "Socket created.<br>\n";
}
 
// echo "Attempting to connect to '$address' on port '$service_port'...<br>\n";
$result = socket_connect($socket, $address, $service_port);
if ($result === false) {
    echo "socket_connect() failed.\nReason: ($result) " . socket_strerror(socket_last_error($socket)) . "<br>\n";
} else {
    // echo "Socket connected.<br>\n";
}

receiveReply($socket);
receiveReply($socket);
sendInput($socket, rand(1, 200) . "\r\n");
receiveReply($socket);
printReply($socket);
sendInput($socket, rand(1, 200) . "\r\n");
receiveReply($socket);
printReply($socket);
sendInput($socket, rand(1, 200) . "\r\n");
receiveReply($socket);
printReply($socket);

// echo "Closing socket...";
sendInput($socket, "bye\r\n");
socket_close($socket);
// echo "OK.<br>\n\n";

function printReply($socket) {
    $response = '';
    while ($out = socket_read($socket, 1024)) {
        $response .= $out;
        if (strpos($response, "\r\n") !== false) break;
    }
    echo $response . "<br>\n";
}

function receiveReply($socket) {
    $response = '';
    while ($out = socket_read($socket, 1024)) {
        $response .= $out;
        if (strpos($response, "\r\n") !== false) break;
    }
}

function sendInput($socket, $string) {
    socket_write($socket, $string, strlen($string));
}
?>
