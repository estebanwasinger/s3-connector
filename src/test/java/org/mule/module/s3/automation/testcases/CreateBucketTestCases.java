/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.Region;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.*;

public class CreateBucketTestCases extends S3TestParent {

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("createBucketTestData");
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testCreateBucket() {
        try {
            Bucket bucket = runFlowAndGetPayload("create-bucket");

            assertEquals(getTestRunMessageValue("bucketName"), bucket.getName());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({RegressionTests.class})
    @Test
    public void testCreateBucketOptionalAttributes() {

        try {
            Bucket bucket = runFlowAndGetPayload("create-bucket-optional-attributes");

            assertEquals(getTestRunMessageValue("bucketName"), bucket.getName());

            String location = runFlowAndGetPayload("get-bucket-location");

            assertTrue(Region.fromValue(location).name().equalsIgnoreCase((String) getTestRunMessageValue("region")));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket");
    }

}