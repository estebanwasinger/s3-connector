/**
 * Mule S3 Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3;

import static org.mule.module.s3.util.InternalUtils.coalesce;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.s3.simpleapi.S3ObjectId;
import org.mule.module.s3.simpleapi.SimpleAmazonS3;
import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;
import org.mule.module.s3.simpleapi.SimpleAmazonS3.S3ObjectContent;
import org.mule.module.s3.simpleapi.content.FileS3ObjectContent;
import org.mule.module.s3.simpleapi.content.InputStreamS3ObjectContent;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * A cloud connector wrapper on {@link SimpleAmazonS3} api. Same exception handling
 * policies applies
 */
@Connector(namespacePrefix = "s3", namespaceUri = "http://www.mulesoft.org/schema/mule/s3")
public class S3CloudConnector implements Initialisable
{

    @Property
    private String accessKey;
    @Property
    private String secretKey;

    private SimpleAmazonS3 client;

    // TODO enums

    /**
     * Example: {@code <s3:create-bucket bucketName="my-bucket" acl="Private"/> }
     * 
     * @param bucketName . The bucket to create
     * @param region optional
     * @param acl optional
     * @return the new Bucket
     * @throws AmazonClientException
     */
    @Operation
    public Bucket createBucket(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = true, defaultValue = "US_Standard") String region,
                               @Parameter(optional = true, defaultValue = "Private") String acl)
    {
        return client.createBucket(bucketName, region, toAcl(acl));
    }

    private CannedAccessControlList toAcl(String acl)
    {
        return acl != null ? CannedAccessControlList.valueOf(acl) : null;
    }

    /**
     * Example: {@code <s3:delete-bucket bucketName="my-bucket" force="true"/> }
     * 
     * @param bucketName the bucket to delete
     * @param force optional true if the bucket must be deleted even if it is not
     *            empty, false if operation should fail in such scenario.
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public void deleteBucket(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = true, defaultValue = "false") boolean force)
    {
        if (force)
        {
            client.deleteBucketAndObjects(bucketName);
        }
        else
        {
            client.deleteBucket(bucketName);
        }
    }

    /**
     * Example: {@code <s3:delete-bucket-website-configuration
     * bucketName="my-bucket"/>}
     * 
     * @param bucketName the bucket whose policy to delete
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public void deleteBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
    {
        client.deleteBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Example: {@code <s3:get-bucket-policy bucketName="my-bucket"/>}
     * 
     * @param bucketName the bucket whose policy to retrieve
     * @return the bucket policy
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public String getBucketPolicy(@Parameter(optional = false) String bucketName)
    {
        return client.getBucketPolicy(bucketName);
    }

    /**
     * Example: {@code <s3:set-bucket-policy bucketName="my-bucket"
     * policyText="your policy" />}
     * 
     * @param bucketName the bucket name
     * @param policyText the policy text
     */
    @Operation
    public void setBucketPolicy(@Parameter(optional = false) String bucketName,
                                @Parameter(optional = false) String policyText)
    {
        client.setBucketPolicy(bucketName, policyText);
    }

    /**
     * Example: {@code <s3:delete-bucket-policy bucketName="my-bucket"/>}
     * 
     * @param bucketName the bucket whose policy to delete
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public void deleteBucketPolicy(@Parameter(optional = false) String bucketName)
    {
        client.deleteBucketPolicy(bucketName);
    }

    /**
     * Example: {@code <s3:set-bucket-website-configuration bucketName="my-bucket"
     * suffix="index.html" errorDocument="errorDocument.html" />}
     * 
     * @param bucketName
     * @param suffix The document to serve when a directory is specified (ex:
     *            index.html). This path is relative to the requested resource
     * @param errorDocument the full path to error document the bucket will use as
     *            error page for 4XX errors
     */
    @Operation
    public void setBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName,
                                              @Parameter(optional = false) String suffix,
                                              @Parameter(optional = true) String errorDocument)
    {
        client.setBucketWebsiteConfiguration(bucketName,
            errorDocument != null
                                 ? new BucketWebsiteConfiguration(suffix, errorDocument)
                                 : new BucketWebsiteConfiguration(suffix));
    }

    /**
     * Example: {@code <s3:get-bucket-website-configuration bucketName="my-bucket"
     * />}
     * 
     * @param bucketName
     * @return a com.amazonaws.services.s3.model.BucketWebsiteConfiguration
     */
    @Operation
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
    {
        return client.getBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Example {@code <s3:list-buckets />}
     * 
     * @return a list of com.amazonaws.services.s3.model.BucketWebsiteConfiguration
     */
    @Operation
    public List<Bucket> listBuckets()
    {
        return client.listBuckets();
    }

    /**
     * Lazily lists all objects for a given prefix. As S3 does not limit in any way
     * the number of objects, such listing can retrieve an arbitrary amount of
     * objects, and may need to perform extra calls to the api while it is iterated.
     * Example: {@code <s3:list-objects bucketName="my-bucket" prefix="mk" />}
     * 
     * @param bucketName
     * @param prefix
     * @return An iterable
     */
    @Operation
    public Iterable<S3ObjectSummary> listObjects(@Parameter(optional = false) String bucketName,
                                                 @Parameter(optional = false) String prefix)
    {
        return client.listObjects(bucketName, prefix);
    }

    /**
     * Uploads an object to S3. Supported contents are InputStreams, Strings, byte
     * arrays and Files. Example: {@code <s3:create-object bucketName="my-bucket"
     * key="helloWorld.txt" content="#[hello world]" contentType="text/plain" />}
     * 
     * @param bucketName
     * @param key
     * @param content
     * @param contentLength the content length. If content is a InputStream or byte
     *            arrays, this parameter should be passed, as not doing so will
     *            introduce a severe performance loss, otherwise, it is ignored. A
     *            content length of 0 is interpreted as an absent (null) content
     *            length
     * @param contentMd5 the content md5, encoded in base 64. If content is a file,
     *            it is ignored.
     * @param contentType
     * @param acl
     * @param storageClass
     * @param userMetadata TODO
     * @param userMetadata
     * @return the id of the created object, or null, if versioning is not enabled
     */
    @Operation
    public String createObject(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = false) String key,
                               @Parameter(optional = false) Object content,
                               @Parameter(optional = true) Long contentLength,
                               @Parameter(optional = true) String contentMd5,
                               @Parameter(optional = true) String contentType,
                               @Parameter(optional = true, defaultValue = "Private") String acl,
                               @Parameter(optional = true, defaultValue = "Standard") String storageClass,
                               @Parameter(optional = true) Map<String, String> userMetadata)
    {
        return client.createObject(new S3ObjectId(bucketName, key), createContent(content, contentLength,
            contentMd5), contentType, toAcl(acl), toStorageClass(storageClass), userMetadata);
    }

    /**
     * Example: {@code <s3:delete-object bucketName="my-bucket" key="foo.gzip"/> }
     * 
     * @param bucketName
     * @param key
     * @param versionId
     */
    @Operation
    public void deleteObject(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = false) String key,
                             @Parameter(optional = true) String versionId)
    {
        client.deleteObject(new S3ObjectId(bucketName, key, versionId));
    }

    @Operation
    public void setObjectStorageClass(@Parameter(optional = false) String bucketName,
                                      @Parameter(optional = false) String key,
                                      @Parameter(optional = false) String newStorageClass)
    {
        Validate.notNull(newStorageClass);
        client.setObjectStorageClass(new S3ObjectId(bucketName, key), toStorageClass(newStorageClass));
    }

    private StorageClass toStorageClass(String storageClass)
    {
        return storageClass != null ? StorageClass.valueOf(storageClass) : null;
    }

    /**
     * Example: {@code <s3:copy-object sourceBucketName="my-bucket"
     * sourceKey="foo.gzip" destinationKey="bar.gzip"
     * destinationStorageClass="Private" /> }
     * 
     * @param sourceBucketName
     * @param sourceKey
     * @param sourceVersionId
     * @param destinationBucketName the destination object's bucket. If none
     *            provided, a local copy is performed, that is, it is copied within
     *            the same bucket.
     * @param destinationKey
     * @param destinationAcl the acl of the destination object.
     * @param destinationStorageClass
     * @return the version id of the new object, or null, if versioning is not
     *         enabled
     */
    @Operation
    public String copyObject(@Parameter(optional = false) String sourceBucketName,
                             @Parameter(optional = false) String sourceKey,
                             @Parameter(optional = true) String sourceVersionId,
                             @Parameter(optional = true) String destinationBucketName,
                             @Parameter(optional = false) String destinationKey,
                             @Parameter(optional = true, defaultValue = "Private") String destinationAcl,
                             @Parameter(optional = true, defaultValue = "Standard") String destinationStorageClass)
    {
        return client.copyObject(new S3ObjectId(sourceBucketName, sourceKey, sourceVersionId),
            new S3ObjectId(coalesce(destinationBucketName, sourceBucketName), destinationKey),
            toAcl(destinationAcl), toStorageClass(destinationStorageClass));
    }

    @Operation
    public URI createPresignedUri(@Parameter(optional = false) String bucketName,
                                  @Parameter(optional = false) String key,
                                  @Parameter(optional = true) String versionId,
                                  @Parameter(optional = true) Date expiration,
                                  @Parameter(optional = true, defaultValue = "PUT") String method)
    {
        return client.createPresignedUri(new S3ObjectId(bucketName, key, versionId), expiration,
            toHttpMethod(method));
    }

    private HttpMethod toHttpMethod(String method)
    {
        return method != null ? HttpMethod.valueOf(method) : null;
    }

    @Operation
    public InputStream getObjectContent(@Parameter(optional = false) String bucketName,
                                        @Parameter(optional = false) String key,
                                        @Parameter(optional = true) String versionId)

    {
        return client.getObjectContent(new S3ObjectId(bucketName, key, versionId));
    }

    @Operation
    public ObjectMetadata getObjectMetadata(@Parameter(optional = false) String bucketName,
                                            @Parameter(optional = false) String key,
                                            @Parameter(optional = true) String versionId)

    {
        return client.getObjectMetadata(new S3ObjectId(bucketName, key, versionId));
    }

    @Operation
    public S3Object getObject(@Parameter(optional = false) String bucketName,
                              @Parameter(optional = false) String key,
                              @Parameter(optional = true) String versionId)
    {
        return client.getObject(new S3ObjectId(bucketName, key, versionId));
    }

    public void initialise() throws InitialisationException
    {
        if (client == null)
        {
            client = new SimpleAmazonS3AmazonDevKitImpl(createAmazonS3());
        }
    }

    /**
     * Creates an {@link AmazonS3} client. If accessKey and secretKey are not set,
     * the resulting client is annonymous
     * 
     * @return a new {@link AmazonS3}
     */
    private AmazonS3Client createAmazonS3()
    {
        if (StringUtils.isEmpty(accessKey) && StringUtils.isEmpty(secretKey))
        {
            return new AmazonS3Client();
        }
        return new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
    }

    public String getAccessKey()
    {
        return accessKey;
    }

    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public void setClient(SimpleAmazonS3 client)
    {
        this.client = client;
    }

    /**
     * Creates the {@link S3ObjectContent}. If content is a String or file, the
     * content length parameter is ignored. Also contentMD5 is ignored if content is
     * a file, too.
     */
    private static S3ObjectContent createContent(Object content, Long contentLength, String contentMd5)
    {
        if (content instanceof InputStream)
        {
            return new InputStreamS3ObjectContent((InputStream) content, contentLength, contentMd5);
        }
        if (content instanceof String)
        {
            String stringContent = (String) content;
            return new InputStreamS3ObjectContent(new ByteArrayInputStream(stringContent.getBytes()),
                (long) stringContent.length(), contentMd5);
        }
        if (content instanceof byte[])
        {
            return new InputStreamS3ObjectContent(new ByteArrayInputStream((byte[]) content), contentLength,
                contentMd5);
        }
        if (content instanceof File)
        {
            return new FileS3ObjectContent((File) content);
        }
        throw new IllegalArgumentException("Wrong input");
    }
}
