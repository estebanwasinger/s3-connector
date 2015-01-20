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
import org.mule.modules.tests.ConnectorTestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static org.junit.Assert.*;

public class ListObjectsTestCases extends S3TestParent {

    private List<String> objectsKeyValues;
    private String bucketName;


    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("listObjectsTestData");
        objectsKeyValues = new ArrayList<String>();
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
    }


    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testListObjects() {

        int objectCount = 0;

        File tempFile = null;
        InputStream inputStream = null;

        try {

            createObject(initializeByteArrayTestData());
            createObject(initializeFileTestData(tempFile));
            createObject(initializeInputStreamTestData(inputStream));
            createObject(initializeStringTestData());

            Iterable<S3ObjectSummary> s3ObjectsSummaries = (Iterable<S3ObjectSummary>) runFlowAndGetPayload("list-objects");

            Iterator<S3ObjectSummary> iterator = s3ObjectsSummaries.iterator();

            while (iterator.hasNext()) {

                S3ObjectSummary s3ObjectSummary = iterator.next();
                objectCount++;

                assertEquals(bucketName, s3ObjectSummary.getBucketName());
                assertTrue(objectsKeyValues.contains(s3ObjectSummary.getKey()));

            }

            assertEquals(Integer.parseInt("4"), objectCount);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Category({RegressionTests.class})
    @Test
    public void testListObjectsOptionalAttributes() {

        int objectCount = 0;

        File tempFile = null;
        InputStream inputStream = null;

        try {

            createObject(initializeByteArrayTestData());
            createObject(initializeFileTestData(tempFile));
            createObject(initializeInputStreamTestData(inputStream));
            createObject(initializeStringTestData());

            Iterable<S3ObjectSummary> s3ObjectsSummaries = (Iterable<S3ObjectSummary>) runFlowAndGetPayload("list-objects-optional-attributes");

            Iterator<S3ObjectSummary> iterator = s3ObjectsSummaries.iterator();

            while (iterator.hasNext()) {

                S3ObjectSummary s3ObjectSummary = iterator.next();
                objectCount++;

                assertEquals(bucketName, s3ObjectSummary.getBucketName());
                assertTrue(objectsKeyValues.contains(s3ObjectSummary.getKey()));

            }

            assertEquals(Integer.parseInt(getTestRunMessageValue("expectedResults").toString()), objectCount);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            IOUtils.closeQuietly(inputStream);
        }

    }

    private HashMap<String, Object> initializeByteArrayTestData() {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectsByteArrayObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        byte data[] = bucketName.getBytes();
        initializationValues.put("contentRef", data);

        return initializationValues;

    }

    private HashMap<String, Object> initializeStringTestData() {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectsStringObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        return initializationValues;

    }

    private HashMap<String, Object> initializeInputStreamTestData(InputStream inputStream) {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectsInputStreamObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        String host = initializationValues.get("host").toString();
        String path = initializationValues.get("path").toString();
        String urlString = String.format("http://%s/%s", host, path);

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();

            initializationValues.put("contentRef", inputStream);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

        return initializationValues;

    }

    private HashMap<String, Object> initializeFileTestData(File tempFile) {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectsFileObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        try {

            tempFile = tempFile.createTempFile("temp-file-name", ".tmp");
            initializationValues.put("contentRef", tempFile);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

        return initializationValues;

    }

    private void createObject(HashMap<String, Object> initializationData) {

        try {

            upsertOnTestRunMessage(initializationData);
            runFlowAndGetPayload("create-object-child-elements-from-message");

            objectsKeyValues.add(initializationData.get("key").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }


    @After
    public void tearDown() throws Exception {

        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }


}