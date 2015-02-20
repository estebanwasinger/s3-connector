/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GetObjectMetadataTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetInputStreamObjectMetadata() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("getInputStreamObjectMetadataTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            getObjectMetadataVerifications();

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetByteArrayObjectMetadata() {
        upsertBeanFromContextOnTestRunMessage("getByteArrayObjectMetadataTestData");
        try {
            byte data[] = bucketName.getBytes();
            upsertOnTestRunMessage("contentRef", data);

            getObjectMetadataVerifications();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetFileObjectMetadata() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("getFileObjectMetadataTestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            getObjectMetadataVerifications();

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
    public void testGetStringObjectMetadata() {

        upsertBeanFromContextOnTestRunMessage("getStringObjectMetadataTestData");

        getObjectMetadataVerifications();

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetByteArrayObjectMetadataOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("getByteArrayObjectMetadataTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getByteArrayObjectMetadataUpdatedUserMetadata");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        getObjectMetadataOptionalAttributesVerifications(updatedUserMetadata);

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetFileObjectMetadataOptionalAttributes() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("getFileObjectMetadataTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getFileObjectMetadataUpdatedUserMetadata");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            getObjectMetadataOptionalAttributesVerifications(updatedUserMetadata);

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
    public void testGetInputStreamObjectMetadataOptionalAttributes() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("getInputStreamObjectMetadataTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getInputStreamObjectMetadataUpdatedUserMetadata");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            getObjectMetadataOptionalAttributesVerifications(updatedUserMetadata);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testGetStringObjectMetadataOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("getStringObjectMetadataTestData");
        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("getStringObjectUpdatedMetadataUserMetadata");

        getObjectMetadataOptionalAttributesVerifications(updatedUserMetadata);

    }

    private void getObjectMetadataVerifications() {

        try {

            runFlowAndGetPayload("create-object-child-elements-from-message");

            ObjectMetadata objectMetadata = runFlowAndGetPayload("get-object-metadata");

            assertTrue(objectMetadata.getUserMetadata().equals(getTestRunMessageValue("userMetadata")));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    private void getObjectMetadataOptionalAttributesVerifications(HashMap<String, Object> updatedUserMetadata) {

        upsertOnTestRunMessage("versioningStatus", "ENABLED");

        try {

            runFlowAndGetPayload("set-bucket-versioning-status");

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-from-message").toString());
            HashMap<String, Object> firstVersionMetadata = (HashMap<String, Object>) getTestRunMessageValue("userMetadata");

            // update the object

            upsertOnTestRunMessage("userMetadata", updatedUserMetadata);

            runFlowAndGetPayload("create-object-child-elements-from-message");
            Thread.sleep(5000);

            // get-object-optional-attributes-version-id

            ObjectMetadata objectMetadata = runFlowAndGetPayload("get-object-metadata-optional-attributes-version-id");

            assertTrue(objectMetadata.getUserMetadata().equals(firstVersionMetadata));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }
}