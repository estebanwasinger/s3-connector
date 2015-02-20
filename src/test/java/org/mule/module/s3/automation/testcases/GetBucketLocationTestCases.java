/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.module.s3.simpleapi.Region;
import org.mule.modules.tests.ConnectorTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class GetBucketLocationTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("getBucketLocationTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket-optional-attributes")).getName();
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testGetBucketLocation() {
        try {
            
            String bucketLocation = runFlowAndGetPayload("get-bucket-location");
            Region region = Region.valueOf(getTestRunMessageValue("region").toString());
            assertEquals(bucketLocation, region.toS3Equivalent().getFirstRegionId());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket");
    }

}
