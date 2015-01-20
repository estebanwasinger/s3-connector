/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.common.io.ByteSource;
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GetObjectContentTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
    }


    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetInputStreamObjectContent() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("getInputStreamObjectContentTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            getObjectContentVerifications();

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetByteArrayObjectContent() {

        upsertBeanFromContextOnTestRunMessage("getByteArrayObjectContentTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        getObjectContentVerifications();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetFileObjectContent() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("getFileObjectContentTestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            getObjectContentVerifications();

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
    public void testGetStringObjectContent() {

        upsertBeanFromContextOnTestRunMessage("getStringObjectContentTestData");

        getObjectContentVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetByteArrayObjectContentOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("getByteArrayObjectContentTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getByteArrayObjectContentUpdatedUserMetadata");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        getObjectContentOptionalAttributesVerifications(updatedUserMetadata);

    }


    @Category({RegressionTests.class})
    @Test
    public void testGetFileObjectContentOptionalAttributes() {

        File temp = null;
        upsertBeanFromContextOnTestRunMessage("getFileObjectContentTestData");

        try {
            HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getFileObjectContentUpdatedUserMetadata");

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            getObjectContentOptionalAttributesVerifications(updatedUserMetadata);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }


    }

    @Category({RegressionTests.class})
    @Test
    public void testGetInputStreamObjectContentOptionalAttributes() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("getInputStreamObjectContentTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getInputStreamObjectContentUpdatedUserMetadata");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", IOUtils.toByteArray(inputStream));

            getObjectContentOptionalAttributesVerifications(updatedUserMetadata);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testGetStringObjectContentOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("getStringObjectContentTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getStringObjectContentUpdatedUserMetadata");

        getObjectContentOptionalAttributesVerifications(updatedUserMetadata);

    }

    private void getObjectContentVerifications() {
        try {

            runFlowAndGetPayload("create-object-child-elements-from-message");

            S3Object s3object = runFlowAndGetPayload("get-object");
            S3ObjectInputStream expectedObjectContent = s3object.getObjectContent();

            S3ObjectInputStream actualObjectContent = runFlowAndGetPayload("get-object-content");

            assertTrue(IOUtils.contentEquals(expectedObjectContent, actualObjectContent));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    /**
     * Utility method to compare InputStreams without modifying their content. Use this instead
     * of IOUtils.contentEquals.
     */
    private void assertEqualStreams(InputStream stream1, InputStream stream2) throws IOException {
        ByteSource bytes1 = ByteSource.wrap(IOUtils.toByteArray(stream1));
        ByteSource bytes2 = ByteSource.wrap(IOUtils.toByteArray(stream2));
        if (!bytes1.contentEquals(bytes2)) {
            throw new AssertionError("Stream contents were not equal. Stream lengths were " + bytes1.size() + " and " + bytes2.size() + " respectively.");
        }
    }

    private void getObjectContentOptionalAttributesVerifications(HashMap<String, Object> updatedUserMetadata) {

        S3ObjectInputStream expectedObjectContent;
        S3ObjectInputStream actualObjectContent;

        ByteSource expectedBytes;
        ByteSource actualBytes;

        upsertOnTestRunMessage("versioningStatus", "ENABLED");

        try {

            runFlowAndGetPayload("set-bucket-versioning-status");

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-from-message").toString());

            S3Object s3object = runFlowAndGetPayload("get-object");
            expectedObjectContent = s3object.getObjectContent();
            expectedBytes = ByteSource.wrap(IOUtils.toByteArray(expectedObjectContent));

            // get-object-content-optional-attributes-unmodified-since

            Date lastModified = s3object.getObjectMetadata().getLastModified();
            upsertOnTestRunMessage("unmodifiedSince", lastModified);

            actualObjectContent = runFlowAndGetPayload("get-object-content-optional-attributes-unmodified-since");
            actualBytes = ByteSource.wrap(IOUtils.toByteArray(actualObjectContent));

            assertTrue(expectedBytes.contentEquals(actualBytes));

            // get-object-content-optional-attributes-version-id

            actualObjectContent = runFlowAndGetPayload("get-object-content-optional-attributes-version-id");
            actualBytes = ByteSource.wrap(IOUtils.toByteArray(actualObjectContent));

            assertTrue(expectedBytes.contentEquals(actualBytes));

            // update the object

            upsertOnTestRunMessage("userMetadata", updatedUserMetadata);
            runFlowAndGetPayload("create-object-child-elements-from-message");
            Thread.sleep(5000);

            // get-object-content-optional-attributes-modified-since

            upsertOnTestRunMessage("modifiedSince", lastModified);

            actualObjectContent = runFlowAndGetPayload("get-object-content-optional-attributes-modified-since");
            actualBytes = ByteSource.wrap(IOUtils.toByteArray(actualObjectContent));

            assertTrue(expectedBytes.contentEquals(actualBytes));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }
}
