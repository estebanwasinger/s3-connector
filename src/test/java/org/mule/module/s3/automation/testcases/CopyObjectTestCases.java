/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import static org.junit.Assert.*;

public class CopyObjectTestCases extends S3TestParent {

    private String bucketName;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("copyObjectTestData");
        runFlowAndGetPayload("create-bucket");
        bucketName = getTestRunMessageValue("bucketName");
        upsertOnTestRunMessage("sourceBucketName", bucketName);
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCopyInputStreamObjectChildElementsFromMessage() {

        InputStream inputStream = null;

        try {
            initializeInputStreamTestData(inputStream);
            copyObjectVersioningDisabled("copy-object-child-elements-from-message");
            copyObjectChildElementsFromMessageGetVerifications();

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyInputStreamObjectChildElementsCreateObjectManually() {

        InputStream inputStream = null;

        try {

            initializeInputStreamTestData(inputStream);
            copyObjectVersioningDisabled("copy-object-child-elements-create-object-manually");
            copyObjectChildElementsCreateObjectManuallyGetVerifications();

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyInputStreamObjectChildElementsNone() {

        InputStream inputStream = null;
        try {
            initializeInputStreamTestData(inputStream);
            copyObjectVersioningDisabled("copy-object-child-elements-none");
            copyObjectChildElementsNoneGetVerifications();

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyByteArrayObjectChildElementsNone() {

        initializeByteArrayTestData();
        copyObjectVersioningDisabled("copy-object-child-elements-none");
        copyObjectChildElementsNoneGetVerifications();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCopyByteArrayObjectChildElementsFromMessage() {

        initializeByteArrayTestData();
        copyObjectVersioningDisabled("copy-object-child-elements-from-message");
        copyObjectChildElementsFromMessageGetVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyByteArrayObjectChildElementsCreateObjectManually() {

        initializeByteArrayTestData();
        copyObjectVersioningDisabled("copy-object-child-elements-create-object-manually");
        copyObjectChildElementsCreateObjectManuallyGetVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyFileObjectChildElementsNone() {

        File tempFile = null;

        try {

            initializeFileTestData(tempFile);
            copyObjectVersioningDisabled("copy-object-child-elements-none");
            copyObjectChildElementsNoneGetVerifications();

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCopyFileObjectChildElementsFromMessage() {

        File tempFile = null;

        try {

            initializeFileTestData(tempFile);
            copyObjectVersioningDisabled("copy-object-child-elements-from-message");
            copyObjectChildElementsFromMessageGetVerifications();

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyFileObjectChildElementsCreateObjectManually() {

        File tempFile = null;

        try {

            initializeFileTestData(tempFile);
            copyObjectVersioningDisabled("copy-object-child-elements-create-object-manually");
            copyObjectChildElementsCreateObjectManuallyGetVerifications();

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyStringObjectChildElementsNone() {

        initializeStringTestData();
        copyObjectVersioningDisabled("copy-object-child-elements-none");
        copyObjectChildElementsNoneGetVerifications();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCopyStringObjectChildElementsFromMessage() {

        initializeStringTestData();
        copyObjectVersioningDisabled("copy-object-child-elements-from-message");
        copyObjectChildElementsFromMessageGetVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyStringObjectChildElementsCreateObjectManually() {

        initializeStringTestData();
        copyObjectVersioningDisabled("copy-object-child-elements-create-object-manually");
        copyObjectChildElementsCreateObjectManuallyGetVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyObjectOptionalAttributesVersionioningEnabled() throws InterruptedException {

        initializeByteArrayTestData();
        createObject(true);
        Thread.sleep(5000);
        copyObjectOptionalAttributesVersioningEnabledVerifications();
        copyObjectChildElementsFromMessageGetVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testCopyObjectOptionalAttributesVersionioningDisabled() throws InterruptedException {

        initializeByteArrayTestData();
        createObject(false);
        Thread.sleep(5000);
        copyObjectOptionalAttributesVersioningDisabledVerifications();
        copyObjectChildElementsFromMessageGetVerifications();
    }


    private void initializeStringTestData() {
        upsertBeanFromContextOnTestRunMessage("copyStringObjectTestData");
    }

    private void initializeByteArrayTestData() {
        upsertBeanFromContextOnTestRunMessage("copyByteArrayObjectTestData");
        upsertOnTestRunMessage("contentRef", (bucketName).getBytes());
    }

    private void copyObjectChildElementsNoneGetVerifications() {
        try {
            upsertOnTestRunMessage("key", getTestRunMessageValue("destinationKey").toString());

            S3Object s3object = runFlowAndGetPayload("get-object");
            ObjectMetadata objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("destinationKey").toString(), s3object.getKey());

            assertTrue(objectMetadata.getUserMetadata().equals(getTestRunMessageValue("userMetadata")));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    private void copyObjectVersioningDisabled(String flowName) {
        try {
            runFlowAndGetPayload("create-object-child-elements-from-message");
            String sourceKey = getTestRunMessageValue("key").toString();

            String destinationKey = sourceKey + "Copy";

            upsertOnTestRunMessage("sourceKey", sourceKey);
            upsertOnTestRunMessage("destinationKey", destinationKey);

            assertTrue(runFlowAndGetPayload(flowName).toString().equals(NULLPAYLOAD));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    private void createObject(boolean versioning) {
        try {
            if (versioning) {
                upsertOnTestRunMessage("versioningStatus", "ENABLED");
                runFlowAndGetPayload("set-bucket-versioning-status");
            }

            upsertOnTestRunMessage("sourceVersionId", runFlowAndGetPayload("create-object-child-elements-from-message").toString());
            upsertOnTestRunMessage("sourceKey", getTestRunMessageValue("key").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    private void copyObjectChildElementsCreateObjectManuallyGetVerifications() {

        HashMap<String, Object> destinationUserMetadata = new HashMap<String, Object>();
        destinationUserMetadata.put("usermetadatakey", "destinationUserMetadataValue");

        try {
            upsertOnTestRunMessage("key", getTestRunMessageValue("destinationKey").toString());

            S3Object s3object = runFlowAndGetPayload("get-object");
            ObjectMetadata objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("destinationKey").toString(), s3object.getKey());
            assertTrue(objectMetadata.getUserMetadata().equals(destinationUserMetadata));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    private void copyObjectChildElementsFromMessageGetVerifications() {
        try {
            upsertOnTestRunMessage("key", getTestRunMessageValue("destinationKey").toString());

            S3Object s3object = runFlowAndGetPayload("get-object");
            ObjectMetadata objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("destinationKey").toString(), s3object.getKey());
            assertTrue(objectMetadata.getUserMetadata().equals(getTestRunMessageValue("destinationUserMetadata")));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    private void copyObjectOptionalAttributesVersioningEnabledVerifications() {

        S3Object s3object;
        ObjectMetadata objectMetadata;

        String sourceVersionId = getTestRunMessageValue("sourceVersionId").toString();
        String destinationKey = getTestRunMessageValue("key").toString();

        try {
            s3object = runFlowAndGetPayload("get-object");
            objectMetadata = s3object.getObjectMetadata();

            upsertOnTestRunMessage("modifiedSince", objectMetadata.getLastModified());
            upsertOnTestRunMessage("unmodifiedSince", objectMetadata.getLastModified());

            // copy-object-optional-attributes-unmodified-since

            upsertOnTestRunMessage("destinationKey", destinationKey + "UnmodifiedSinceCopy");

            String copyByUnmodifiedSinceVersionId = runFlowAndGetPayload("copy-object-optional-attributes-unmodified-since").toString();

            assertFalse(copyByUnmodifiedSinceVersionId.contains(NULLPAYLOAD));
            assertFalse(copyByUnmodifiedSinceVersionId.contains(sourceVersionId));

            // update the object

            upsertOnTestRunMessage("userMetadata", new HashMap<String, Object>());
            runFlowAndGetPayload("create-object-child-elements-from-message");

            // copy-object-optional-attributes-modified-since

            upsertOnTestRunMessage("destinationKey", destinationKey + "ModifiedSinceCopy");

            String copyByModifiedSinceVersionId = runFlowAndGetPayload("copy-object-optional-attributes-modified-since").toString();

            assertFalse(copyByModifiedSinceVersionId.contains(NULLPAYLOAD));
            assertFalse(copyByModifiedSinceVersionId.contains(sourceVersionId));

            // copy-object-optional-attributes-version-id

            upsertOnTestRunMessage("destinationKey", destinationKey + "VersionIdCopy");

            String copyBySourceVersionIdVersionId = runFlowAndGetPayload("copy-object-optional-attributes-source-version-id").toString();

            assertFalse(copyBySourceVersionIdVersionId.contains(NULLPAYLOAD));
            assertFalse(copyBySourceVersionIdVersionId.contains(sourceVersionId));

            // copy-object-optional-attributes-destination-bucket

            upsertOnTestRunMessage("destinationKey", destinationKey + "DestinationBucketCopy");
            upsertOnTestRunMessage("destinationBucketName", getTestRunMessageValue("sourceBucketName").toString());

            String copyByDestinationBucketVersionId = runFlowAndGetPayload("copy-object-optional-attributes-destination-bucket").toString();

            assertFalse(copyByDestinationBucketVersionId.contains(NULLPAYLOAD));
            assertFalse(copyByDestinationBucketVersionId.contains(sourceVersionId));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    private void copyObjectOptionalAttributesVersioningDisabledVerifications() {

        S3Object s3object;
        ObjectMetadata objectMetadata;

        String destinationKey = getTestRunMessageValue("key").toString();

        try {
            s3object = runFlowAndGetPayload("get-object");
            objectMetadata = s3object.getObjectMetadata();

            upsertOnTestRunMessage("modifiedSince", objectMetadata.getLastModified());
            upsertOnTestRunMessage("unmodifiedSince", objectMetadata.getLastModified());

            // copy-object-optional-attributes-unmodified-since

            upsertOnTestRunMessage("destinationKey", destinationKey + "UnmodifiedSinceCopy");

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("copy-object-optional-attributes-unmodified-since").toString());

            // update the object

            upsertOnTestRunMessage("userMetadata", new HashMap<String, Object>());

            runFlowAndGetPayload("create-object-child-elements-from-message");

            // copy-object-optional-attributes-modified-since

            upsertOnTestRunMessage("destinationKey", destinationKey + "ModifiedSinceCopy");

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("copy-object-optional-attributes-modified-since").toString());

            // copy-object-optional-attributes-destination-bucket

            upsertOnTestRunMessage("destinationKey", destinationKey + "DestinationBucketCopy");
            upsertOnTestRunMessage("destinationBucketName", getTestRunMessageValue("sourceBucketName").toString());

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("copy-object-optional-attributes-destination-bucket").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }


    private void initializeFileTestData(File tempFile) {

        upsertBeanFromContextOnTestRunMessage("copyFileObjectTestData");
        try {
            tempFile = tempFile.createTempFile("temp-file-name", ".tmp");
            upsertOnTestRunMessage("contentRef", tempFile);
        } catch (IOException e) {
            if (tempFile != null) {
                tempFile.delete();
            }
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    private void initializeInputStreamTestData(InputStream inputStream) {

        upsertBeanFromContextOnTestRunMessage("copyInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }
}