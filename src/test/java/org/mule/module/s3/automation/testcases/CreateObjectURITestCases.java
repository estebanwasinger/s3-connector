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

import static org.junit.Assert.*;

public class CreateObjectURITestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("createBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectURI() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectURITestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-uri")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateInputStreamObjectURIOptionalAttributes() {

        InputStream inputStream = null;

        upsertBeanFromContextOnTestRunMessage("createInputStreamObjectURITestData");

        String host = getTestRunMessageValue("host").toString();
        String path = getTestRunMessageValue("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            upsertOnTestRunMessage("contentRef", inputStream);

            runFlowAndGetPayload("create-object-child-elements-none");

            URI uri = (URI) runFlowAndGetPayload("create-object-uri-optional-attributes");

            assertEquals("https", uri.getScheme());
            assertNotNull(uri.toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateByteArrayObjectURI() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectURITestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-uri")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateStringObjectURI() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectURITestData");

        try {

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-uri")).toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateFileObjectURI() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectURITestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            runFlowAndGetPayload("create-object-child-elements-none");

            assertNotNull(((URI) runFlowAndGetPayload("create-object-uri")).toURL());

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
    public void testCreateByteArrayObjectURIOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("createByteArrayObjectURITestData");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {

            runFlowAndGetPayload("create-object-child-elements-none");

            URI uri = (URI) runFlowAndGetPayload("create-object-uri-optional-attributes");

            assertEquals("https", uri.getScheme());
            assertNotNull(uri.toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateStringObjectURIOptionalAttributes() {

        upsertBeanFromContextOnTestRunMessage("createStringObjectURITestData");

        try {

            runFlowAndGetPayload("create-object-child-elements-none");

            URI uri = (URI) runFlowAndGetPayload("create-object-uri-optional-attributes");

            assertEquals("https", uri.getScheme());
            assertNotNull(uri.toURL());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateFileObjectURIOptionalAttributes() {

        File temp = null;

        upsertBeanFromContextOnTestRunMessage("createFileObjectURITestData");

        try {

            temp = File.createTempFile("temp-file-name", ".tmp");

            upsertOnTestRunMessage("contentRef", temp);

            runFlowAndGetPayload("create-object-child-elements-none");

            URI uri = (URI) runFlowAndGetPayload("create-object-uri-optional-attributes");

            assertEquals("https", uri.getScheme());
            assertNotNull(uri.toURL());

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