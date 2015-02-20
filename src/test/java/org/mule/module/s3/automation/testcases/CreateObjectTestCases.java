/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import static org.junit.Assert.*;

public class CreateObjectTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();

    }


    @Category({RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectChildElementsNone() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-none").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectChildElementsNoneVersioningEnabled() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectTestData");

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

            assertNotSame(runFlowAndGetPayload("create-object-child-elements-none").toString(), NULLPAYLOAD);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectChildElementsFromMessageVersioningEnabled() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectTestData");

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

            assertNotSame(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-from-message").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectChildElementsFromMessage() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-from-message").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectChildElementsCreateObjectManually() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-create-object-manually").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectChildElementsNone() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-none").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateStringObjectChildElementsNone() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectTestData");
        try {
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-none").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateStringObjectChildElementsFromMessage() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectTestData");
        try {
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-from-message").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateStringObjectChildElementsCreateObjectManually() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectTestData");
        try {
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-create-object-manually").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateStringObjectChildElementsNoneVersioningEnabled() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectTestData");
        try {
            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            assertNotSame(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-none").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateStringObjectChildElementsFromMessageVersioningEnabled() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectTestData");
        try {
            upsertOnTestRunMessage("versioningStatus", "ENABLED");
            runFlowAndGetPayload("set-bucket-versioning-status");
            assertNotSame(NULLPAYLOAD, runFlowAndGetMessage("create-object-child-elements-from-message").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectChildElementsFromMessage() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectTestData");
        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-from-message").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectChildElementsNoneVersioningEnabled() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);
        try {
            upsertOnTestRunMessage("versioningStatus", "ENABLED");
            runFlowAndGetPayload("set-bucket-versioning-status");
            assertNotSame(NULLPAYLOAD, runFlowAndGetMessage("create-object-child-elements-none").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectChildElementsFromMessageVersioningEnabled() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectTestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);
        try {
            upsertOnTestRunMessage("versioningStatus", "ENABLED");
            runFlowAndGetPayload("set-bucket-versioning-status");
            assertNotSame(NULLPAYLOAD, runFlowAndGetMessage("create-object-child-elements-from-message").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectChildElementsCreateObjectsManually() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectTestData");
        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);
        try {
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-create-object-manually").toString());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateFileObjectChildElementsNone() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectTestData");

        try {
            temp = File.createTempFile("temp-file-name", ".tmp");
            upsertOnTestRunMessage("contentRef", temp);
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-none").toString());

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
    public void testCreateFileObjectChildElementsNoneVersioningEnabled() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectTestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");
            upsertOnTestRunMessage("contentRef", temp);
            upsertOnTestRunMessage("versioningStatus", "ENABLED");
            runFlowAndGetPayload("set-bucket-versioning-status");
            assertNotSame(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-none").toString());
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
    public void testCreateFileObjectChildElementsFromMessage() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectTestData");
        try {
            temp = File.createTempFile("temp-file-name", ".tmp");
            upsertOnTestRunMessage("contentRef", temp);
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-from-message").toString());
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
    public void testCreateFileObjectChildElementsCreateObjectManually() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectTestData");

        try {
            temp = File.createTempFile("temp-file-name", ".tmp");
            upsertOnTestRunMessage("contentRef", temp);
            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-create-object-manually").toString());
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
    public void testCreateFileObjectChildElementsFromMessageVersioningEnabled() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectTestData");

        try {
            temp = File.createTempFile("temp-file-name", ".tmp");
            upsertOnTestRunMessage("contentRef", temp);
            upsertOnTestRunMessage("versioningStatus", "ENABLED");
            runFlowAndGetPayload("set-bucket-versioning-status");
            assertNotSame(NULLPAYLOAD, runFlowAndGetPayload("create-object-child-elements-from-message").toString());
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
    public void testCreateStringObjectOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectTestData");

        String content = getTestRunMessageValue("contentRef").toString();

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(content.getBytes("UTF8"));
            byte[] encodedByteData = Base64.encodeBase64(messageDigest.digest());

            upsertOnTestRunMessage("contentMd5", new String(encodedByteData, "UTF-8"));
            upsertOnTestRunMessage("contentLength", Long.valueOf(content.getBytes("UTF-8").length));

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-optional-attributes").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Ignore("Works 95% of the time, but fails randomly !!!")
    @Category({RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectOptionalAttributes() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectTestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);
            upsertOnTestRunMessage("contentLength", new Long(inputStream.available()));

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("create-object-optional-attributes").toString());
            Thread.sleep(10000);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }

}