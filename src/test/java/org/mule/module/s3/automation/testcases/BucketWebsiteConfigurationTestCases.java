/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
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

public class BucketWebsiteConfigurationTestCases extends S3TestParent {

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("bucketWebsiteConfigurationTestData");
        runFlowAndGetPayload("create-bucket");
    }


    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testSetAndGetBucketWebsiteConfiguration() {

        try {
            runFlowAndGetPayload("set-bucket-website-configuration");

            Thread.sleep(5000);

            BucketWebsiteConfiguration bucketWebsiteConfiguration = runFlowAndGetPayload("get-bucket-website-configuration");

            assertEquals(((BucketWebsiteConfiguration) getTestRunMessageValue("bucketWebsiteConfiguration")).getIndexDocumentSuffix(),
                    bucketWebsiteConfiguration.getIndexDocumentSuffix());
            assertEquals(((BucketWebsiteConfiguration) getTestRunMessageValue("bucketWebsiteConfiguration")).getErrorDocument(),
                    bucketWebsiteConfiguration.getErrorDocument());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteBucketWebsiteConfiguration() {

        try {

            runFlowAndGetPayload("set-bucket-website-configuration");

            runFlowAndGetPayload("delete-bucket-website-configuration");

            assertEquals(NULLPAYLOAD, runFlowAndGetPayload("get-bucket-website-configuration").toString());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket");
    }
}