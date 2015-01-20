/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.S3VersionSummary;
import org.apache.sshd.common.util.IoUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;

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

    private HashMap<String, Object> initializeByteArrayTestData() {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((HashMap<String, Object>) context.getBean("listObjectVersionsByteArrayObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        byte data[] = bucketName.getBytes();
        initializationValues.put("contentRef", data);

        return initializationValues;

    }

    private HashMap<String, Object> initializeStringTestData() {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((HashMap<String, Object>) context.getBean("listObjectVersionsStringObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        return initializationValues;

    }

    private void createObjectVersioningEnabled(HashMap<String, Object> initializationData) {
        try {

            MessageProcessor createObjectFlow = lookupMessageProcessor("create-object-child-elements-from-message");
            MuleEvent createObjectResponse = createObjectFlow.process(getTestEvent(initializationData));
            objectsVersionIds.add(createObjectResponse.getMessage().getPayload().toString());
            objectsKeyValues.add(initializationData.get("key").toString());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Before
    public void setUp() {

        objectsKeyValues = new ArrayList<String>();
        objectsVersionIds = new ArrayList<String>();
        ;

        bucketName = UUID.randomUUID().toString();

        testObjects = new HashMap<String, Object>();
        testObjects.put("bucketName", bucketName);
        testObjects.putAll((HashMap<String, Object>) context.getBean("listObjectVersionsTestData"));

        try {

            MessageProcessor flow = lookupMessageProcessor("create-bucket");
            flow.process(getTestEvent(testObjects));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

    @After
    public void tearDown() {

        try {

            MessageProcessor flow = lookupMessageProcessor("delete-bucket-optional-attributes");
            flow.process(getTestEvent(testObjects));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

    private HashMap<String, Object> initializeFileTestData(File tempFile) {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((HashMap<String, Object>) context.getBean("listObjectVersionsFileObjectTestData"));
        initializationValues.put("bucketName", bucketName);

        try {

            tempFile = tempFile.createTempFile("temp-file-name", ".tmp");
            initializationValues.put("contentRef", tempFile);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        return initializationValues;

    }

    private HashMap<String, Object> initializeInputStreamTestData(InputStream inputStream) {

        HashMap<String, Object> initializationValues = new HashMap<String, Object>();
        initializationValues.putAll((HashMap<String, Object>) context.getBean("listObjectVersionsInputStreamObjectTestData"));
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
            e.printStackTrace();
            fail();
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

            testObjects.put("versioningStatus", "ENABLED");

            MessageProcessor setBucketVersioningStatusFlow = lookupMessageProcessor("set-bucket-versioning-status");
            setBucketVersioningStatusFlow.process(getTestEvent(testObjects));

            createObjectVersioningEnabled(initializeByteArrayTestData());
            createObjectVersioningEnabled(initializeFileTestData(tempFile));
            createObjectVersioningEnabled(initializeInputStreamTestData(inputStream));
            createObjectVersioningEnabled(initializeStringTestData());

            MessageProcessor listObjectsFlow = lookupMessageProcessor("list-object-versions");
            MuleEvent listObjectsResponse = listObjectsFlow.process(getTestEvent(testObjects));
            Iterable<S3VersionSummary> s3ObjectsSummaries = (Iterable<S3VersionSummary>) listObjectsResponse.getMessage().getPayload();

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
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException logOrIgnore) {
            }
        }

    }

    @Test
    public void testListObjectVersionsOptionalAttributes() throws MuleException, Exception {
        int objectCount = 0;
        File tempFile = null;
        InputStream stream = null;
        Iterable<S3VersionSummary> s3ObjectsSummaries = null;
        Iterator<S3VersionSummary> iterator;

        testObjects.put("versioningStatus", "ENABLED");
        MessageProcessor setBucketVersioningStatusFlow = lookupMessageProcessor("set-bucket-versioning-status");
        setBucketVersioningStatusFlow.process(getTestEvent(testObjects));

        createObjectVersioningEnabled(initializeByteArrayTestData());
        createObjectVersioningEnabled(initializeFileTestData(tempFile));
        createObjectVersioningEnabled(initializeInputStreamTestData(stream));
        createObjectVersioningEnabled(initializeStringTestData());

        testObjects.put("versionIdMarker", objectsVersionIds.get(0));
        testObjects.put("keyMarker", objectsKeyValues.get(0));

        MessageProcessor listObjectsFlow = lookupMessageProcessor("list-object-versions-optional-attributes");
        MuleEvent listObjectsResponse = listObjectsFlow.process(getTestEvent(testObjects));
        try {
            s3ObjectsSummaries = (Iterable<S3VersionSummary>) listObjectsResponse.getMessage().getPayload();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            if (stream != null) {
                IoUtils.closeQuietly(stream);
            }
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

}