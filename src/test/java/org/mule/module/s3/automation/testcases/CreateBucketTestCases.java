/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.fail;

public class CreateBucketTestCases extends S3TestParent {

    @Before
    public void setUp() {

        testObjects = (HashMap<String, Object>) context.getBean("createBucketTestData");

    }

    @After
    public void tearDown() {

        try {

            MessageProcessor flow = lookupMessageProcessor("delete-bucket");
            MuleEvent response = flow.process(getTestEvent(testObjects));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateBucket() {

        testObjects.put("bucketName", UUID.randomUUID().toString());

        MessageProcessor flow = lookupMessageProcessor("create-bucket");

        try {

            MuleEvent response = flow.process(getTestEvent(testObjects));
            Bucket bucket = (Bucket) response.getMessage().getPayload();

            assertEquals(testObjects.get("bucketName").toString(), bucket.getName());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateBucketOptionalAttributes() {

        testObjects.put("bucketName", UUID.randomUUID().toString());

        MessageProcessor flow = lookupMessageProcessor("create-bucket");

        try {

            MuleEvent response = flow.process(getTestEvent(testObjects));
            Bucket bucket = (Bucket) response.getMessage().getPayload();

            assertEquals(testObjects.get("bucketName").toString(), bucket.getName());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

    }

}