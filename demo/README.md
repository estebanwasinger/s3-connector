Mule Amazon S3 Connector Demo
===========================

INTRODUCTION
------------
The Amazon S3 connector demo project consists of below flow:

* Amazon_S3_Twitter_Integration_Flow - Provides an example on how to create an image link in Amazon S3 and update the status in twitter along with the image link. In this process it will show you how to create buckets, create object, copy object, create object uri and delete buckets using Amazon S3 Connector.

HOW TO RUN DEMO
---------------

### Prerequisites
In order to build run this demo project you'll need;

* Anypoint Studio with Mule ESB 3.6 Runtime.
* Mule Amazon S3 Connector v3.0.0 or higher.
* Mule Twitter Connector v4.0.0 or higher.
* Amazon S3 & Twitter OAuth Credentials.

### Test the flows

With Anypoint Studio up and running, open the Import wizard from the File menu. A pop-up wizard will offer you the chance to pick Anypoint Studio Project from External Location. On the next wizard window point Project Root to the location of the demo project and select the Server Runtime as Mule Server 3.6.0 CE or EE. Once successfully imported the studio will automatically present the Mule Flows.

From the Package Explorer view, expand the demo project and open the mule-app.properties file. Fill in the credentials of Amazon S3 and Twitter instances.

Amazon_S3_Twitter_Integration_Flow : Run the demo project and with in the browser hit - **http://localhost:8081/publish**, The result would be a new tweet along with the url of the image.

SUMMARY
-------

Congratulations! You have imported the Mule Amazon S3 Demo project and used the Mule Amazon S3 Connector to post a image stored in Amazon S3 to twitter.