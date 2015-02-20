/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BucketPolicyTestCases extends S3TestParent {

    private String policyText;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("bucketPolicyTestData");
        runFlowAndGetPayload("create-bucket");

        policyText = ((String) getTestRunMessageValue("policyTemplate")).replace("test_bucket_name", (String) getTestRunMessageValue("bucketName"));
        upsertOnTestRunMessage("policyText", policyText);
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testSetAndGetBucketPolicy() {
        try {
            runFlowAndGetPayload("set-bucket-policy");

            String bucketPolicy = runFlowAndGetPayload("get-bucket-policy");

            assertEquals(policyText, bucketPolicy);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteBucketPolicy() {
        try {
            runFlowAndGetPayload("set-bucket-policy");

            Thread.sleep(5000);

            runFlowAndGetPayload("delete-bucket-policy");

            Thread.sleep(5000);

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("get-bucket-policy").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket");
    }
}