Amazon S3 Anypoint Connector Release Notes
==========================================

Date: 10-Mar-2015

Version: 3.0.0

Supported Mule Runtime Versions: 3.5.x or higher

Supported API versions
------------------------
Supported API versions: [2006-03-01](http://docs.aws.amazon.com/AmazonS3/latest/API/IntroductionAPI.html)

Supported Operations:

* Copy Object
* Create Bucket
* Create Object
* Create Object Presigned URI
* Create Object URI
* Delete Bucket
* Delete Bucket Policy
* Delete Bucket Website Configuration
* Delete Object
* Delete Objects
* Get Bucket Location
* Get Bucket Policy
* Get Bucket Versioning Configuration
* Get Bucket Website Configuration
* Get Object
* Get Object Content
* Get Object Metadata
* List Buckets
* List Object Versions
* List Objects
* Set Bucket Policy
* Set Bucket Versioning Status
* Set Bucket Website Configuration
* Set Object Storage Class

New Features and Functionality
------------------------------

Updated the Devkit & Tested for 3.6.0 release.

Updated the AWS Java SDK from 1.7.13 to 1.9.19.

Added Get Bucket Location operation.

Updated the test cases and demo project.

Previous Release
----------------

Updated the Devkit & Tested for 3.5.1 release.

Updated the AWS Java SDK from 1.7.2 to 1.7.13.

Updated the demo project to support the latest 3.5.1 Anypoint Studio.

Updated S3Connector.java for the latest versions (3.5.0) of DevKit Annotations (e.g. removing @Default @Optional)

Added Studio Interoperabilty Tests.

Fixed an issue where uploading an InputStream object without specifying content length would create a temporary file that was never deleted.

Added all optional parameters to `listObjects` and `listObjectVersions`.

Updated directory structure and documentation according to certification guidelines.

Updated [AWS SDK for Java](http://aws.amazon.com/sdkforjava/) from [1.4.7](http://aws.amazon.com/releasenotes/Java/7233847602537543) to [1.7.2](http://aws.amazon.com/releasenotes/Java/3795911094379719). The relevant changes from this update are:

* The `S3Object` now implements the `Closable` interface, to make it easier to handle HTTP connection resources.
* The new SDK version for Amazon Simple Storage Service will do a length validation on `GetObject` method when users finish reading the input stream.
* Performance enhancements to string manipulation for Amazon S3 bucket name validation and URL encoding.
* `RegionUtils` now correctly loads region metadata from the SDK jar when it is unable to connect to Amazon S3 to download the latest region metadata.
* `RegionUtils` now retrieves the region metadata through HTTPS connection, and also validates all region endpoints.
* Fix for Amazon S3 XML quote escaping.
* Fixed an issue where failed `InputStream` uploads were not being retried properly.
* Fixed the bug that Amazon S3 client cannot handle key names starting with slash character.
* `ProgressListener` callback is now executed in a separate single thread and no longer blocks the main transfer thread.
*  Fixed a bug parsing the `x-amz-restore` response header from Amazon S3 that caused the `ObjectMetadata.getRestoreExpirationTime()` method to always return `null`.
* `AmazonServiceException.getMessage()` now returns a more verbose message.
* Bug fix for parsing JSON responses that contain '/' characters inside field names.
* Fixed the issue that `AmazonS3Client.generatePresignedUrl` returns an incorrect URL if the key name starts with a slash character. 
* Fixed an issue in Amazon S3 V4 signer when pre-signing a request using session token credentials.
* Added support for case insensitivity in file extensions for MimeTypes.
