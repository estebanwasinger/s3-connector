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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CreateObjectPresignedURITestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectPresignedURI() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectPresignedURITestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri")).toURL());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectPresignedURIOptionalAttributes() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectPresignedURITestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {
            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-none"));

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri-optional-attributes")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectPresignedURI() {
        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectPresignedURITestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {
            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateStringObjectPresignedURI() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectPresignedURITestData");

        try {
            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateFileObjectPresignedURI() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectPresignedURITestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            runFlowAndGetPayload("create-object-child-elements-none");

            runFlowAndGetPayload("create-object-presigned-uri");

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
    public void testCreateByteArrayObjectPresignedURIOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectPresignedURITestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {
            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-none").toString());

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri-optional-attributes")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateStringObjectPresignedURIOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectPresignedURITestData");

        try {

            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-none").toString());

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri-optional-attributes")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateFileObjectPresignedURIOptionalAttributes() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectPresignedURITestData");

        try {

            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            Thread.sleep(5000);
            upsertOnTestRunMessage("versionId", runFlowAndGetPayload("create-object-child-elements-none").toString());

            runFlowAndGetPayload("create-object-child-elements-none");
            Thread.sleep(5000);

            assertNotNull(((URI) runFlowAndGetPayload("create-object-presigned-uri-optional-attributes")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }
}