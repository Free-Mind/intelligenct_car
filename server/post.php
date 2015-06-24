<?php
	require_once 'vendor/autoload.php';

	use JPush\Model as M;
	use JPush\JPushClient;
	use JPush\Exception\APIConnectionException;
	use JPush\Exception\APIRequestException;
	
	$content=$_POST['content'];
	if(isset($content)){
		$br = '<br/>';
		$app_key='06222491278a982b54986376';
		$master_secret='3272efceeb5aafcfe237a7cb';
		$client = new JPushClient($app_key, $master_secret);

		$result = $client->push()
			->setPlatform(M\all)
			->setAudience(M\all)
			->setNotification(M\notification("您的车可能被盗走!"))
			->send();
		echo 'Push Success.' . $br;
		echo 'sendno : ' . $result->sendno . $br;
		echo 'msg_id : ' .$result->msg_id . $br;
		echo 'Response JSON : ' . $result->json . $br;
	}
	else{
		echo "illleagel";
	}
?>
