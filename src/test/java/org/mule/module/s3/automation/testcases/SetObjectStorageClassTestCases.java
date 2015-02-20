/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
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

import static org.junit.Assert.fail;

public class SetObjectStorageClassTestCases extends S3TestParent {

    private String bucketName;


    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testSetInputStreamObjectStorageClass() {
        InputStream inputStream = null;

        try {
            initializeInputStreamTestData(inputStream);
            createObject(false);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testSetInputStreamObjectStorageClassVersioningEnabled() {
        InputStream inputStream = null;

        try {
            initializeInputStreamTestData(inputStream);
            createObject(true);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testSetByteArrayObjectStorageClass() {
        try {
            initializeByteArrayTestData();
            createObject(false);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testSetByteArrayObjectStorageClassVersioningEnabled() {
        try {
            initializeByteArrayTestData();
            createObject(true);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testSetFileObjectStorageClass() {
        File tempFile = null;
        try {

            initializeFileTestData(tempFile);
            createObject(false);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testSetFileObjectStorageClassVersioningEnabled() {

        File tempFile = null;

        try {

            initializeFileTestData(tempFile);
            createObject(true);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testSetStringObjectStorageClass() {
        try {
            initializeStringTestData();
            createObject(false);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testSetStringObjectStorageClassVersioningEnabled() {
        try {
            initializeStringTestData();
            createObject(true);
            setReducedRedundancyStorageClass();
            setStandardStorageClass();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }


    private void initializeStringTestData() {

        upsertBeanFromContextOnTestRunMessage("setStringObjectStorageClassTestData");
    }

    private void initializeByteArrayTestData() {

        upsertBeanFromContextOnTestRunMessage("setByteArrayObjectStorageClassTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

    }

    private void enableVersioning() throws Exception {

        upsertOnTestRunMessage("versioningStatus", "ENABLED");
        runFlowAndGetPayload("set-bucket-versioning-status");
    }

    private void setReducedRedundancyStorageClass() throws Exception {

        upsertOnTestRunMessage("storageClass", "REDUCED_REDUNDANCY");
        runFlowAndGetPayload("set-object-storage-class");

    }

    private void setStandardStorageClass() throws Exception {

        upsertOnTestRunMessage("storageClass", "STANDARD");
        runFlowAndGetPayload("set-object-storage-class");

    }

    private void createObject(boolean versioning) throws Exception {

        if (versioning) {
            enableVersioning();

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-none").toString());
        } else {
            runFlowAndGetPayload("create-object-child-elements-none");
        }
    }

    private void initializeFileTestData(File tempFile) {

        upsertBeanFromContextOnTestRunMessage("setFileObjectStorageClassTestData");

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

        upsertBeanFromContextOnTestRunMessage("setInputStreamObjectStorageClassTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }

}