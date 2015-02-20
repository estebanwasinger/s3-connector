/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
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
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

public class GetObjectTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetInputStreamObject() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("getInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            getObjectVerifications();

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetByteArrayObject() {

        upsertBeanFromContextOnTestRunMessage("getByteArrayObjectTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        getObjectVerifications();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetFileObject() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("getFileObjectTestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            getObjectVerifications();

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetStringObject() {

        upsertBeanFromContextOnTestRunMessage("getStringObjectContentTestData");

        getObjectVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetByteArrayObjectOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("getByteArrayObjectTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getByteArrayObjectUpdatedUserMetadata");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        getObjectOptionalAttributesVerifications(updatedUserMetadata);

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetFileObjectOptionalAttributes() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("getFileObjectTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getFileObjectUpdatedUserMetadata");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            getObjectOptionalAttributesVerifications(updatedUserMetadata);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetInputStreamObjectOptionalAttributes() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("getInputStreamObjectTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getInputStreamObjectUpdatedUserMetadata");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            getObjectOptionalAttributesVerifications(updatedUserMetadata);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testGetStringObjectOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("getStringObjectTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getStringObjectUpdatedUserMetadata");

        getObjectOptionalAttributesVerifications(updatedUserMetadata);

    }

    private void getObjectVerifications() {

        try {

            runFlowAndGetPayload("create-object-child-elements-from-message");

            S3Object s3object = runFlowAndGetPayload("get-object");
            ObjectMetadata objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("key").toString(), s3object.getKey());
            assertTrue(objectMetadata.getUserMetadata().equals(getTestRunMessageValue("userMetadata")));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    private void getObjectOptionalAttributesVerifications(HashMap<String, Object> updatedUserMetadata) {

        S3Object s3object;
        ObjectMetadata objectMetadata;

        upsertOnTestRunMessage("versioningStatus", "ENABLED");

        try {

            runFlowAndGetPayload("set-bucket-versioning-status");

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-from-message").toString());
            HashMap<String, Object> firstVersionMetadata = (HashMap<String, Object>) getTestRunMessageValue("userMetadata");

            s3object = runFlowAndGetPayload("get-object");
            objectMetadata = s3object.getObjectMetadata();

            upsertOnTestRunMessage("modifiedSince", (Date) objectMetadata.getLastModified());
            upsertOnTestRunMessage("unmodifiedSince", (Date) objectMetadata.getLastModified());

            // get-object-optional-attributes-unmodified-since

            s3object = runFlowAndGetPayload("get-object-optional-attributes-unmodified-since");
            objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("key").toString(), s3object.getKey());
            assertTrue(objectMetadata.getUserMetadata().equals(getTestRunMessageValue("userMetadata")));

            // update the object

            upsertOnTestRunMessage("userMetadata", updatedUserMetadata);

            runFlowAndGetPayload("create-object-child-elements-from-message");

            // get-object-optional-attributes-version-id

            s3object = runFlowAndGetPayload("get-object-optional-attributes-version-id");
            objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("key").toString(), s3object.getKey());
            assertTrue(objectMetadata.getUserMetadata().equals(firstVersionMetadata));

            // get-object-optional-attributes-modified-since

            s3object = runFlowAndGetPayload("get-object-optional-attributes-modified-since");
            objectMetadata = s3object.getObjectMetadata();

            assertEquals(bucketName, s3object.getBucketName());
            assertEquals(getTestRunMessageValue("key").toString(), s3object.getKey());
            assertTrue(objectMetadata.getUserMetadata().equals(getTestRunMessageValue("userMetadata")));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }
}