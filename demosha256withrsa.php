/*
Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
<?php
header("Content-Type: application/json; charset=utf-8");

ksort($_POST);
$sign = $_POST['sign'];
$form = $_POST;
unset($form['sign']);
unset($form['signType']);
if(empty($sign))
{
echo "{\"result\":1}";
return;
}

$content = "";
$i = 0;
foreach($form as $key=>$value)
{
   if($key != "sign"  && $key != "signType")
    {
	   $content .= ($i == 0 ? '' : '&').$key.'='.$value;
	}
   $i++;
}
$filename = dirname(__FILE__)."/payPublicKey.pem";

if(!file_exists($filename))
{
echo "{\"result\" : 1 }";
return;
}
$pubKey = @file_get_contents($filename);
$openssl_public_key = @openssl_get_publickey($pubKey);
$ok = @openssl_verify($content,base64_decode($sign), $openssl_public_key, 'SHA256');
@openssl_free_key($openssl_public_key);

$result = "";

if($ok)
{
	$result = "0";//支付成功处理业务
}else
{
$result = "1";
}
$res = "{ \"result\": $result} ";
echo $res;
?>