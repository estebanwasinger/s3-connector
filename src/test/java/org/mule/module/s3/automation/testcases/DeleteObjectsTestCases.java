/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.module.s3.simpleapi.KeyVersion;
import org.mule.modules.tests.ConnectorTestUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class DeleteObjectsTestCases extends S3TestParent {

    private String bucketName;
    private List<KeyVersion> keyVersionList;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
        keyVersionList = new ArrayList<KeyVersion>();
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteInputStreamObjectsVersioningEnabled() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("deleteInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            createDeleteAndCheckBucketContents(true);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteInputStreamObjects() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("deleteInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            createDeleteAndCheckBucketContents(false);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteStringObjects() {

        upsertBeanFromContextOnTestRunMessage("deleteStringObjectTestData");
        createDeleteAndCheckBucketContents(false);

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteStringObjectsVersioningEnabled() {

        upsertBeanFromContextOnTestRunMessage("deleteStringObjectTestData");
        try {
            createDeleteAndCheckBucketContents(true);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteByteArrayObjects() {

        upsertBeanFromContextOnTestRunMessage("deleteByteArrayObjectTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        createDeleteAndCheckBucketContents(false);

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteByteArrayObjectsVersioningEnabled() {

        upsertBeanFromContextOnTestRunMessage("deleteByteArrayObjectTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        createDeleteAndCheckBucketContents(true);

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteFileObjects() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("deleteFileObjectTestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            createDeleteAndCheckBucketContents(false);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteFileObjectsVersioningEnabled() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("deleteFileObjectTestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            createDeleteAndCheckBucketContents(true);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }

    }

    private void createDeleteAndCheckBucketContents(boolean versioning) {

        KeyVersion keyVersion = new KeyVersion();

        try {
            if (versioning) {
                upsertOnTestRunMessage("versioningStatus", "ENABLED");
                runFlowAndGetPayload("set-bucket-versioning-status");
                keyVersion.setVersion(runFlowAndGetPayload("create-object-child-elements-from-message").toString());
            }

            keyVersion.setValue(getTestRunMessageValue("key").toString());
            keyVersionList.add(keyVersion);

            upsertOnTestRunMessage("keysReference", keyVersionList);

            runFlowAndGetPayload("delete-objects");

            Iterable<S3ObjectSummary> s3ObjectsSummaries = (Iterable<S3ObjectSummary>) runFlowAndGetPayload("list-objects");

            Iterator<S3ObjectSummary> iterator = s3ObjectsSummaries.iterator();

            while (iterator.hasNext()) {

                S3ObjectSummary s3ObjectSummary = iterator.next();
                assertFalse(s3ObjectSummary.getKey().equals(getTestRunMessageValue("key").toString()));
            }
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }

}