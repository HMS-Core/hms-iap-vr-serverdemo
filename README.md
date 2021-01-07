## HMS Kit-Sample



## Table of Contents

 * [Introduction](#introduction)
 * [Installation](#installation)
 * [Supported Environments](#supported-environments)
 * [Configuration ](#configuration )
 * [Sample Code](#SampleCode)
 * [License](#license)
 
 
## Introduction
    This sample code encapsulates APIs of the HMS Kit server. It provides sample program for your reference or usage.
    The following describes of sample code.

    pay: Sample code packages. This package contains code that implements Sign with sha256withrsa.
    

## Installation
    To use functions provided by examples, please make sure that the PHP environment has been installed. 
    
## Supported Environments
    PHP5.0 or later
	
## Configurations  
   
	 none
## Sample Code
The IAP service demo in PHP implementation includes the following two files:
1. productPublicFile.php: used to generate the essential .pem file, and is mandatory for security verification. If the public key is changed, this program needs to be run again, and the original .pem file will be automatically replaced by a newly generated one.
2. demoxxx.php: used for payment security verification, that is, used to execute payment verification using the previously generated .pem file. (There are two demoxxx.php files, demosha1withrsa.php and demosha256withrsa.php, which support sha1withrsa and sha256withrsa, respectively.)

    The IAP service demo in PHP implementation includes the following two files:
1. productPublicFile.php: used to generate the essential .pem file, and is mandatory for security verification. If the public key is changed, this program needs to be run again, and the original .pem file will be automatically replaced by a newly generated one.
2. demoxxx.php: used for payment security verification, that is, used to execute payment verification using the previously generated .pem file. (There are two demoxxx.php files, demosha1withrsa.php and demosha256withrsa.php, which support sha1withrsa and sha256withrsa, respectively.)


##  License
    HMS-kit sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).