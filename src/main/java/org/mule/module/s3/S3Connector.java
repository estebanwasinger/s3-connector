/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.lang.Validate;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.module.s3.connection.strategy.S3ConnectionManagement;
import org.mule.module.s3.simpleapi.*;
import org.mule.module.s3.simpleapi.Region;
import org.mule.module.s3.simpleapi.S3ObjectId;
import org.mule.module.s3.simpleapi.SimpleAmazonS3.S3ObjectContent;
import org.mule.module.s3.simpleapi.content.TempFileS3ObjectContent;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mule.module.s3.util.InternalUtils.coalesce;

/**
 * Amazon S3 (Simple Storage Service) is an online storage web service offered by Amazon Web Services. Amazon S3
 * provides storage through web services interfaces (REST, SOAP, and BitTorrent).
 *
 * @author MuleSoft, Inc.
 */
@Connector(name = "s3", schemaVersion = "2.0", friendlyName = "Amazon S3")
public class S3Connector {

    @ConnectionStrategy
    private S3ConnectionManagement connection;

    /**
     * Creates a new bucket; connector must not be configured as anonymous for this
     * operation to succeed. Bucket names must be unique across all of Amazon S3,
     * that is, among all their users. Bucket ownership is similar to the ownership
     * of Internet domain names. Within Amazon S3, only a single user owns each
     * bucket. Once a uniquely named bucket is created in Amazon S3, organize and
     * name the objects within the bucket in any way. Ownership of the bucket is
     * retained as long as the owner has an Amazon S3 account. To conform with DNS
     * requirements, buckets names must: not contain underscores, be between 3 and 63
     * characters long, not end with a dash, not contain adjacent periods, not
     * contain dashes next to periods and not contain uppercase characters. Do not
     * make bucket create or delete calls in the high availability code path of an
     * application. Create or delete buckets in a separate initialization or setup.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:create-bucket}
     *
     * @param bucketName The bucket to create. It must not exist yet.
     * @param region     the region where to create the new bucket
     * @param acl        the access control list of the new bucket
     * @return the non null, new Bucket
     */
    @Processor
    public Bucket createBucket(String bucketName,
                               @Default("US_STANDARD") Region region,
                               @Default("PRIVATE") AccessControlList acl) {
        return connection.getClient().createBucket(bucketName, region, acl.toS3Equivalent());
    }

    /**
     * Deletes the specified bucket. All objects (and all object versions, if
     * versioning was ever enabled) in the bucket must be deleted before the bucket
     * itself can be deleted; this restriction can be relaxed by specifying the
     * attribute  force="true".
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:delete-bucket}
     *
     * @param bucketName the bucket to delete
     * @param force      optional true if the bucket must be deleted even if it is not empty, false if operation should fail in such scenario.
     */
    @Processor
    public void deleteBucket(String bucketName,
                             @Default("false") boolean force) {
        if (force) {
            connection.getClient().deleteBucketAndObjects(bucketName);
        } else {
            connection.getClient().deleteBucket(bucketName);
        }
    }

    /**
     * Removes the website configuration for a bucket; this operation requires the
     * DeleteBucketWebsite permission. By default, only the bucket owner can delete
     * the website configuration attached to a bucket. However, bucket owners can
     * grant other users permission to delete the website configuration by writing a
     * bucket policy granting them the <code>S3:DeleteBucketWebsite</code>
     * permission. Calling this operation on a bucket with no website configuration
     * does not fail, but calling this operation a bucket that does not exist does.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:delete-bucket-website-configuration}
     *
     * @param bucketName the bucket whose policy to delete
     */
    @Processor
    public void deleteBucketWebsiteConfiguration(String bucketName) {
        connection.getClient().deleteBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Answers the policy for the given bucket. Only the owner of the bucket can
     * retrieve it. If no policy has been set for the bucket, then a null policy text
     * field will be returned.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-bucket-policy}
     *
     * @param bucketName the bucket whose policy to retrieve
     * @return the bucket policy, or null, if not set
     */
    @Processor
    public String getBucketPolicy(String bucketName) {
        return connection.getClient().getBucketPolicy(bucketName);
    }

    /**
     * Sets the bucket's policy, overriding any previously set. Only the owner of the
     * bucket can set a bucket policy. Bucket policies provide access control
     * management at the bucket level for both the bucket resource and contained
     * object resources. Only one policy can be specified per-bucket.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:set-bucket-policy}
     *
     * @param bucketName the bucket name
     * @param policyText the policy text
     */
    @Processor
    public void setBucketPolicy(String bucketName,
                                String policyText) {
        connection.getClient().setBucketPolicy(bucketName, policyText);
    }

    /**
     * Deletes the bucket's policy. Only the owner of the bucket can delete the
     * bucket policy. Bucket policies provide access control management at the bucket
     * level for both the bucket resource and contained object resources.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:delete-bucket-policy}
     *
     * @param bucketName the bucket whose policy to delete
     */
    @Processor
    public void deleteBucketPolicy(String bucketName) {
        connection.getClient().deleteBucketPolicy(bucketName);
    }

    /**
     * Sets the given bucket's website configuration. This operation requires the
     * PutBucketWebsite permission. By default, only the bucket owner can configure
     * the website attached to a bucket. However, bucket owners can allow other users
     * to set the website configuration by writing a bucket policy granting them the
     * S3:PutBucketWebsite permission.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:set-bucket-website-configuration}
     *
     * @param bucketName                 the target bucket's name
     * @param bucketWebsiteConfiguration bucket website configuration
     */
    @Processor
    public void setBucketWebsiteConfiguration(String bucketName,
                                              BucketWebsiteConfiguration bucketWebsiteConfiguration) {
        connection.getClient().setBucketWebsiteConfiguration(bucketName, bucketWebsiteConfiguration);
    }

    /**
     * Answers the website of the given bucket. This operation requires the
     * GetBucketWebsite permission. By default, only the bucket owner can read the
     * bucket website configuration. However, bucket owners can allow other users to
     * read the website configuration by writing a bucket policy granting them the
     * GetBucketWebsite permission.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-bucket-website-configuration}
     *
     * @param bucketName the target bucket's name
     * @return a non null com.amazonaws.services.s3.model.BucketWebsiteConfiguration
     */
    @Processor
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String bucketName) {
        return connection.getClient().getBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Answers a list of all Amazon S3 buckets that the authenticated sender of the
     * request owns. Users must authenticate with a valid AWS Access Key ID that is
     * registered with Amazon S3. Anonymous requests cannot list buckets, and users
     * cannot list buckets that they did not create.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:list-buckets}
     *
     * @return a non null list of com.amazonaws.services.s3.model.Bucket
     */
    @Processor
    public List<Bucket> listBuckets() {
        return connection.getClient().listBuckets();
    }

    /**
     * Lazily lists all objects for a given prefix. As S3 does not limit in any
     * way the number of objects, such listing can retrieve an arbitrary amount
     * of objects, and may need to perform extra calls to the api while it is
     * iterated.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:list-objects}
     *
     * @param bucketName   the target bucket's name
     * @param prefix       the prefix of the objects to be listed. If unspecified, all
     *                     objects are listed
     * @param marker       where in the bucket to begin listing. The list will only
     *                     include keys that occur lexicographically after the marker
     * @param delimiter    causes keys that contain the same string between a prefix and
     *                     the first occurrence of the delimiter to be rolled up into a
     *                     single result element. These rolled-up keys are not returned
     *                     elsewhere in the response. The most commonly used delimiter is
     *                     "/", which simulates a hierarchical organization similar to a
     *                     file system directory structure.
     * @param maxKeys      The maximum number of keys to include in the response. If
     *                     maxKeys is not specified, Amazon S3 will limit the number of
     *                     results in the response.
     * @param encodingType The encoding method to be applied on the response. An object
     *                     key can contain any Unicode character; however, XML 1.0 parser
     *                     cannot parse some characters, such as characters with an ASCII
     *                     value from 0 to 10. For characters that are not supported in
     *                     XML 1.0, you can add this parameter to request that Amazon S3
     *                     encode the keys in the response.
     * @return An iterable
     */
    @Processor
    public Iterable<S3ObjectSummary> listObjects(String bucketName,
                                                 @Optional String prefix,
                                                 @Optional String marker,
                                                 @Optional String delimiter,
                                                 @Optional Integer maxKeys,
                                                 @Default("NOT_ENCODED") EncodingType encodingType) {
        String encodingTypeString = null;
        if (encodingType != null) {
            encodingTypeString = encodingType.sdkValue();
        }
        ListObjectsRequest request = new ListObjectsRequest(bucketName,
                prefix,
                marker,
                delimiter,
                maxKeys)
                .withEncodingType(encodingTypeString);
        return connection.getClient().listObjects(request);
    }

    /**
     * Lazily lists all object versions for a given bucket that has versioning
     * enabled. As S3 does not limit in any way the number of objects, such
     * listing can retrieve an arbitrary amount of object versions, and may need
     * to perform extra calls to the api while it is iterated.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample
     * s3:list-object-versions}
     *
     * @param bucketName      the target bucket's name
     * @param prefix          optional parameter restricting the response to keys which
     *                        begin with the specified prefix. You can use prefixes to
     *                        separate a bucket into different sets of keys in a way similar
     *                        to how a file system uses folders.
     * @param keyMarker       where in the sorted list of all versions in the specified
     *                        bucket to begin returning results. Results are always ordered
     *                        first lexicographically (i.e. alphabetically) and then from
     *                        most recent version to least recent version. If a keyMarker is
     *                        used without a versionIdMarker, results begin immediately
     *                        after that key's last version. When a keyMarker is used with a
     *                        versionIdMarker, results begin immediately after the version
     *                        with the specified key and version ID.
     * @param versionIdMarker where in the sorted list of all versions in the specified
     *                        bucket to begin returning results. Results are always ordered
     *                        first lexicographically (i.e. alphabetically) and then from
     *                        most recent version to least recent version. A keyMarker must
     *                        be specified when specifying a versionIdMarker. Results begin
     *                        immediately after the version with the specified key and
     *                        version ID.
     * @param delimiter       causes keys that contain the same string between the prefix
     *                        and the first occurrence of the delimiter to be rolled up into
     *                        a single result element in the
     *                        {@link VersionListing#getCommonPrefixes()} list. These
     *                        rolled-up keys are not returned elsewhere in the response. The
     *                        most commonly used delimiter is "/", which simulates a
     *                        hierarchical organization similar to a file system directory
     *                        structure.
     * @param maxResults      the maximum number of results to include in the response.
     * @param encodingType    the encoding method to be applied on the response. An object
     *                        key can contain any Unicode character; however, XML 1.0 parser
     *                        cannot parse some characters, such as characters with an ASCII
     *                        value from 0 to 10. For characters that are not supported in
     *                        XML 1.0, you can add this parameter to request that Amazon S3
     *                        encode the keys in the response.
     * @return An iterable
     */
    @Processor
    public Iterable<S3VersionSummary> listObjectVersions(String bucketName,
                                                         @Optional String prefix,
                                                         @Optional String keyMarker,
                                                         @Optional String versionIdMarker,
                                                         @Optional String delimiter,
                                                         @Optional Integer maxResults,
                                                         @Default("NOT_ENCODED") EncodingType encodingType) {
        String sdkEncoding = null;
        if (encodingType != null) {
            sdkEncoding = encodingType.sdkValue();
        }
        ListVersionsRequest request = new ListVersionsRequest(bucketName, prefix, keyMarker, versionIdMarker, delimiter, maxResults).withEncodingType(sdkEncoding);
        return connection.getClient().listObjectVersions(request);
    }

    /**
     * Uploads an object to S3. Supported contents are InputStreams, Strings, byte
     * arrays and Files.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:create-object}
     *
     * @param bucketName         the object's bucket
     * @param key                the object's key
     * @param content            the content to be uploaded to S3, capable of creating a {@link PutObjectRequest}.
     * @param contentLength      the content length. If content is a InputStream,
     *                           this parameter should be specified, as not doing so will
     *                           introduce a performance loss as the contents will have to be persisted on disk before being uploaded.
     *                           Otherwise, it is ignored. An exception to this
     *                           rule are InputStreams returned by Mule Http Connector: if stream has Content-Length
     *                           information, it will be used.
     *                           In any case a content length of 0 is interpreted as an unspecified content length
     * @param contentMd5         the content md5, encoded in base 64. If content is a file,
     *                           it is ignored.
     * @param contentType        the content type of the new object.
     * @param contentDisposition the content disposition of the new object.
     * @param acl                the access control list of the new object
     * @param storageClass       the storage class of the new object
     * @param encryption         Encryption method for server-side encryption. Supported value AES256.
     * @param userMetadata       a map of arbitrary object properties keys and values
     * @return the id of the created object, or null, if versioning is not enabled
     * @throws IOException if there are problems manipulating the File or InputStream content
     */
    @Processor
    public String createObject(String bucketName,
                               String key,
                               @Default("#[payload]") Object content,
                               @Optional Long contentLength,
                               @Optional String contentMd5,
                               @Optional String contentType,
                               @Optional String contentDisposition,
                               @Default("PRIVATE") AccessControlList acl,
                               @Default("STANDARD") StorageClass storageClass,
                               @Optional Map<String, String> userMetadata,
                               @Optional String encryption) throws IOException {
        S3ObjectContent s3Content = S3ContentUtils.createContent(content, contentLength, contentMd5);
        String response = connection.getClient().createObject(new S3ObjectId(bucketName, key), s3Content, contentType, contentDisposition, acl.toS3Equivalent(), storageClass.toS3Equivalent(),
                userMetadata, encryption);
        if (s3Content instanceof TempFileS3ObjectContent) {
            ((TempFileS3ObjectContent) s3Content).delete();
        }
        return response;
    }

    /**
     * Deletes a given object, only the owner of the bucket containing the version
     * can perform this operation. If version is specified, versioning must be
     * enabled, and once deleted, there is no method to restore such version.
     * Otherwise, once deleted, the object can only be restored if versioning was
     * enabled when the object was deleted. If attempting to delete an object that
     * does not exist, Amazon S3 will return a success message instead of an error
     * message.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:delete-object}
     *
     * @param bucketName the object's bucket
     * @param key        the object's key
     * @param versionId  the specific version of the object to delete, if versioning
     *                   is enabled. Left unspecified if the latest version is desired, or
     *                   versioning is not enabled.
     */
    @Processor
    public void deleteObject(String bucketName,
                             String key,
                             @Optional String versionId) {
        connection.getClient().deleteObject(new S3ObjectId(bucketName, key, versionId));
    }

    /**
     * Deletes multiple objects in a single bucket from S3. Version of the keys is optional.
     * <p/>
     * In some cases, some objects will be successfully deleted, while some
     * attempts will cause an error. If any object in the request cannot be
     * deleted, this method throws a {@link com.amazonaws.services.s3.model.MultiObjectDeleteException} with
     * details of the error.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:delete-objects}
     *
     * @param bucketName the objects bucket name
     * @param keys       the objects keys, version is optional
     * @throws com.amazonaws.services.s3.model.MultiObjectDeleteException if one or more of the objects couldn't be deleted.
     * @throws com.amazonaws.AmazonClientException                        If any errors are encountered in the client while making the
     *                                                                    request or handling the response.
     * @throws com.amazonaws.AmazonServiceException                       If any errors occurred in Amazon S3 while processing the
     *                                                                    request.
     */
    @Processor
    public void deleteObjects(String bucketName,
                              List<KeyVersion> keys) {
        connection.getClient().deleteObjects(bucketName, keys);
    }


    /**
     * Sets the Amazon S3 storage class for the given object. Changing the storage
     * class of an object in a bucket that has enabled versioning creates a new
     * version of the object with the new storage class. The existing version of the
     * object preservers the previous storage class.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:set-object-storage-class}
     *
     * @param bucketName   the object's bucket name
     * @param key          the object's key
     * @param storageClass the storage class to set
     */
    @Processor
    public void setObjectStorageClass(String bucketName,
                                      String key,
                                      StorageClass storageClass) {
        Validate.notNull(storageClass);
        connection.getClient().setObjectStorageClass(new S3ObjectId(bucketName, key), storageClass.toS3Equivalent());
    }

    /**
     * Copies a source object to a new destination; to copy an object, the caller's
     * account must have read access to the source object and write access to the
     * destination bucket. By default, all object metadata for the source object are
     * copied to the new destination object, unless new object metadata in the
     * specified is provided. The AccesControlList is not copied to the new object,
     * and, unless another ACL specified, PRIVATE is assumed. If no destination
     * bucket is specified, the same that the source bucket is used - local copy.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:copy-object}
     *
     * @param sourceBucketName        the source object's bucket
     * @param sourceKey               the source object's key
     * @param sourceVersionId         the specific version of the source object to copy, if
     *                                versioning is enabled. Left unspecified if the latest version is
     *                                desired, or versioning is not enabled.
     * @param destinationBucketName   the destination object's bucket. If none
     *                                provided, a local copy is performed, that is, it is copied within
     *                                the same bucket.
     * @param destinationKey          the destination object's key
     * @param destinationAcl          the acl of the destination object.
     * @param destinationStorageClass one of {@link StorageClass} enumerated values, defaults to {@link StorageClass#STANDARD}
     * @param destinationUserMetadata the new metadata of the destination object,
     *                                that if specified, overrides that copied from the source object
     * @param modifiedSince           The modified constraint that restricts this request to
     *                                executing only if the object has been modified after the specified
     *                                date. This constraint is specified but does not match, no copy is performed
     * @param unmodifiedSince         The unmodified constraint that restricts this request
     *                                to executing only if the object has not been modified after this
     *                                date. This constraint is specified but does not match, no copy is performed
     * @param encryption              Encryption method for server-side encryption. Supported value AES256.
     * @return the version id of the new object, or null, if versioning is not
     * enabled
     */
    @Processor
    public String copyObject(String sourceBucketName,
                             String sourceKey,
                             @Optional String sourceVersionId,
                             @Optional String destinationBucketName,
                             String destinationKey,
                             @Default("PRIVATE") AccessControlList destinationAcl,
                             @Default("STANDARD") StorageClass destinationStorageClass,
                             @Optional Map<String, String> destinationUserMetadata,
                             @Optional Date modifiedSince,
                             @Optional Date unmodifiedSince,
                             @Optional String encryption) {
        return connection.getClient().copyObject(
                new S3ObjectId(sourceBucketName, sourceKey, sourceVersionId),
                new S3ObjectId(coalesce(destinationBucketName, sourceBucketName), destinationKey),
                ConditionalConstraints.from(modifiedSince, unmodifiedSince),
                destinationAcl.toS3Equivalent(),
                destinationStorageClass.toS3Equivalent(),
                destinationUserMetadata,
                encryption);
    }

    /**
     * Returns a pre-signed URL for accessing an Amazon S3 object. The pre-signed URL
     * can be shared to other users, allowing access to the resource without
     * providing an account's AWS security credentials.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:create-object-presigned-uri}
     *
     * @param bucketName the object's bucket
     * @param key        the object's key
     * @param versionId  the specific version of the object to create the URI, if
     *                   versioning is enabled. Left unspecified if the latest version is
     *                   desired, or versioning is not enabled.
     * @param expiration The time at which the returned pre-signed URL will expire.
     * @param method     The HTTP method verb to use for this URL
     * @return A non null pre-signed URI that can be used to access an Amazon S3
     * resource without requiring the user of the URL to know the account's
     * AWS security credentials.
     */
    @Processor
    public URI createObjectPresignedUri(String bucketName,
                                        String key,
                                        @Optional String versionId,
                                        @Optional Date expiration,
                                        @Default("PUT") String method) {
        return connection.getClient().createObjectPresignedUri(new S3ObjectId(bucketName, key, versionId), expiration,
                toHttpMethod(method));
    }

    private HttpMethod toHttpMethod(String method) {
        return method != null ? HttpMethod.valueOf(method) : null;
    }

    /**
     * Gets the content of an object stored in Amazon S3 under the specified bucket
     * and key. Returns null if the specified constraints weren't met. To get an
     * object's content from Amazon S3, the caller must have {@link Permission#Read}
     * access to the object. Regarding conditional get constraints, Amazon S3 will
     * ignore any dates occurring in the future.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-object-content}
     *
     * @param bucketName      the object's bucket
     * @param key             the object's key
     * @param versionId       the specific version of the object to get its contents, if
     *                        versioning is enabled, left unspecified if the latest version is
     *                        desired, or versioning is not enabled.
     * @param modifiedSince   The modified constraint that restricts this request to
     *                        executing only if the object has been modified after the specified
     *                        date.
     * @param unmodifiedSince The unmodified constraint that restricts this request
     *                        to executing only if the object has not been modified after this
     *                        date.
     * @return an input stream to the objects contents
     */
    @Processor
    public S3ObjectInputStream getObjectContent(String bucketName,
                                                String key,
                                                @Optional String versionId,
                                                @Optional Date modifiedSince,
                                                @Optional Date unmodifiedSince) {
        return connection.getClient().getObjectContent(new S3ObjectId(bucketName, key, versionId),
                ConditionalConstraints.from(modifiedSince, unmodifiedSince));
    }

    /**
     * Gets the object stored in Amazon S3 under the specified bucket and key.
     * Returns null if the specified constraints weren't met. To get an object from
     * Amazon S3, the caller must have {@link Permission#Read} access to the object.
     * Callers should be very careful when using this method; the returned Amazon S3
     * object contains a direct stream of data from the HTTP connection. The
     * underlying HTTP connection cannot be closed until the user finishes reading
     * the data and closes the stream. Regarding conditional get constraints, Amazon
     * S3 will ignore any dates occurring in the future.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-object}
     *
     * @param bucketName      the object's bucket
     * @param key             the object's key
     * @param versionId       the specific version of the object to get its contents, if
     *                        versioning is enabled. Left unspecified if the latest version is
     *                        desired, or versioning is not enabled.
     * @param modifiedSince   The modified constraint that restricts this request to
     *                        executing only if the object has been modified after the specified
     *                        date.
     * @param unmodifiedSince The unmodified constraint that restricts this request
     *                        to executing only if the object has not been modified after this
     *                        date.
     * @return the S3Object, or null, if conditional get constraints did not match
     */
    @Processor
    public S3Object getObject(String bucketName,
                              String key,
                              @Optional String versionId,
                              @Optional Date modifiedSince,
                              @Optional Date unmodifiedSince) {
        return connection.getClient().getObject(new S3ObjectId(bucketName, key, versionId),
                ConditionalConstraints.from(modifiedSince, unmodifiedSince));
    }

    /**
     * Gets the metadata for the specified Amazon S3 object without actually fetching
     * the object itself. This is useful in obtaining only the object metadata, and
     * avoids wasting bandwidth on fetching the object data.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-object-metadata}
     *
     * @param bucketName the object's bucket
     * @param key        the object's key
     * @param versionId  the object metadata for the given bucketName and key
     * @return the non null object metadata
     */
    @Processor
    public ObjectMetadata getObjectMetadata(String bucketName,
                                            String key,
                                            @Optional String versionId) {
        return connection.getClient().getObjectMetadata(new S3ObjectId(bucketName, key, versionId));
    }

    /**
     * Sets the versioning status for the given bucket. A bucket's versioning
     * configuration can be in one of three possible states: Off, Enabled and
     * Suspended. By default, new buckets are in the Off state. Once versioning is
     * enabled for a bucket the status can never be reverted to Off.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:set-bucket-versioning-status}
     *
     * @param bucketName       the target bucket name
     * @param versioningStatus the version status to set
     */
    @Processor
    public void setBucketVersioningStatus(String bucketName,
                                          VersioningStatus versioningStatus) {
        connection.getClient().setBucketVersioningStatus(bucketName, versioningStatus);
    }

    /**
     * <p>
     * Returns the versioning configuration for the specified bucket.
     * </p>
     * <p>
     * A bucket's versioning configuration can be in one of three possible
     * states:
     * <ul>
     * <li>{@link BucketVersioningConfiguration#OFF}
     * <li>{@link BucketVersioningConfiguration#ENABLED}
     * <li>{@link BucketVersioningConfiguration#SUSPENDED}
     * </ul>
     * </p>
     * <p>
     * By default, new buckets are in the
     * {@link BucketVersioningConfiguration#OFF off} state. Once versioning is
     * enabled for a bucket the status can never be reverted to
     * {@link BucketVersioningConfiguration#OFF off}.
     * </p>
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-bucket-versioning-configuration}
     *
     * @param bucketName The bucket whose versioning configuration will be retrieved.
     * @return The bucket versioning configuration for the specified bucket.
     */
    @Processor
    public BucketVersioningConfiguration getBucketVersioningConfiguration(String bucketName) {
        return connection.getClient().getBucketVersioningConfiguration(bucketName);
    }

    /**
     * Creates an http URI for the given object id. The useDefaultServer option
     * enables using default US Amazon server subdomain in the URI regardless of the
     * region. The main benefit of such feature is that this operation does not need
     * to hit the Amazon servers, but the drawback is that using the given URI as an
     * URL to the resource have unnecessary latency penalties for standard regions
     * other than US_STANDARD.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:create-object-uri}
     *
     * @param bucketName       the object's bucket
     * @param key              the object's key
     * @param useDefaultServer if the default US Amazon server subdomain should be
     *                         used in the URI regardless of the region.
     * @param secure           whether to use http or https
     * @return a non secure http URI to the object. Unlike the presigned URI, object
     * must have PUBLIC_READ or PUBLIC_READ_WRITE permission
     */
    @Processor
    public URI createObjectUri(String bucketName,
                               String key,
                               @Default("false") boolean useDefaultServer,
                               @Default("false") boolean secure) {
        if (useDefaultServer) {
            return connection.getClient().createObjectUriUsingDefaultServer(new S3ObjectId(bucketName, key), secure);
        } else {
            return connection.getClient().createObjectUri(new S3ObjectId(bucketName, key), secure);
        }
    }

    /**
     * Gets the geographical region where Amazon S3 stores the specified bucket.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-s3.xml.sample s3:get-bucket-location}
     *
     * @param bucketName The target bucket name.
     * @return the location of the specified Amazon S3 bucket.
     */
    @Processor
    public String getBucketLocation(String bucketName) {
        return connection.getClient().getBucketLocation(bucketName);
    }

    public S3ConnectionManagement getConnection() {
        return connection;
    }

    public void setConnection(S3ConnectionManagement connection) {
        this.connection = connection;
    }
}
