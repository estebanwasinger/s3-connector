/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3VersionSummary;
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

public class ListObjectVersionsTestCases extends S3TestParent {

    private String bucketName;
    private List<String> objectsKeyValues;
    private List<String> objectsVersionIds;


    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("listObjectVersionsTestData");

        objectsKeyValues = new ArrayList<String>();
        objectsVersionIds = new ArrayList<String>();

        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();

    }

    private HashMap<String, Object> initializeFileTestData(File tempFile) {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectVersionsFileObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        try {

            tempFile = tempFile.createTempFile("temp-file-name", ".tmp");
            initializationValues.put("contentRef", tempFile);

        } catch (IOException e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

        return initializationValues;

    }

    private HashMap<String, Object> initializeInputStreamTestData(InputStream inputStream) {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectVersionsInputStreamObjectTestData"));
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

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testListObjectVersions() {

        int objectCount = 0;

        File tempFile = null;

        InputStream inputStream = null;

        try {

            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            createObjectVersioningEnabled(initializeByteArrayTestData());
            createObjectVersioningEnabled(initializeFileTestData(tempFile));
            createObjectVersioningEnabled(initializeInputStreamTestData(inputStream));
            createObjectVersioningEnabled(initializeStringTestData());

            Iterable<S3VersionSummary> s3ObjectsSummaries = (Iterable<S3VersionSummary>) runFlowAndGetPayload("list-object-versions");

            Iterator<S3VersionSummary> iterator = s3ObjectsSummaries.iterator();

            while (iterator.hasNext()) {

                S3VersionSummary s3VersionSummary = iterator.next();
                objectCount++;

                assertEquals(bucketName, s3VersionSummary.getBucketName());
                assertTrue(objectsVersionIds.contains(s3VersionSummary.getVersionId()));
                assertTrue(objectsKeyValues.contains(s3VersionSummary.getKey()));
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

    @Test
    public void testListObjectVersionsOptionalAttributes() throws Exception {
        int objectCount = 0;
        File tempFile = null;
        InputStream inputStream = null;
        Iterable<S3VersionSummary> s3ObjectsSummaries = null;
        Iterator<S3VersionSummary> iterator;

        upsertOnTestRunMessage("versioningStatus", "ENABLED");
        runFlowAndGetPayload("set-bucket-versioning-status");

        createObjectVersioningEnabled(initializeByteArrayTestData());
        createObjectVersioningEnabled(initializeFileTestData(tempFile));
        createObjectVersioningEnabled(initializeInputStreamTestData(inputStream));
        createObjectVersioningEnabled(initializeStringTestData());

        upsertOnTestRunMessage("versionIdMarker", objectsVersionIds.get(0));
        upsertOnTestRunMessage("keyMarker", objectsKeyValues.get(0));

        try {
            s3ObjectsSummaries = (Iterable<S3VersionSummary>) runFlowAndGetPayload("list-object-versions-optional-attributes");
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            IOUtils.closeQuietly(inputStream);
        }

        iterator = s3ObjectsSummaries.iterator();
        while (iterator.hasNext()) {
            S3VersionSummary versionSummary = iterator.next();
            objectCount++;
            assertEquals(bucketName, versionSummary.getBucketName());
            assertTrue(objectsVersionIds.contains(versionSummary.getVersionId()));
            assertTrue(objectsKeyValues.contains(versionSummary.getKey()));
        }

        assertEquals(3, objectCount);
    }

    private HashMap<String, Object> initializeByteArrayTestData() {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectVersionsByteArrayObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        byte data[] = bucketName.getBytes();
        initializationValues.put("contentRef", data);

        return initializationValues;

    }

    private HashMap<String, Object> initializeStringTestData() {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((Map<String, Object>) getBeanFromContext("listObjectVersionsStringObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        return initializationValues;

    }

    private void createObjectVersioningEnabled(HashMap<String, Object> initializationData) {
        try {

            upsertOnTestRunMessage(initializationData);
            objectsVersionIds.add(runFlowAndGetPayload("create-object-child-elements-from-message").toString());
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