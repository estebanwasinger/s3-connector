/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
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

public class BucketVersioningConfigurationTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("bucketVersioningConfigurationTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testBucketVersioningConfigurationTestCases() {

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        try {

            BucketVersioningConfiguration bucketVersioningConfiguration =
                    runFlowAndGetPayload("get-bucket-versioning-configuration");

            assertEquals("OFF", bucketVersioningConfiguration.getStatus().toUpperCase());

            upsertOnTestRunMessage("versioningStatus", "ENABLED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            bucketVersioningConfiguration = runFlowAndGetPayload("get-bucket-versioning-configuration");

            assertEquals("ENABLED", bucketVersioningConfiguration.getStatus().toUpperCase());

            upsertOnTestRunMessage("versioningStatus", "SUSPENDED");

            runFlowAndGetPayload("set-bucket-versioning-status");

            bucketVersioningConfiguration = runFlowAndGetPayload("get-bucket-versioning-configuration");

            assertEquals("SUSPENDED", bucketVersioningConfiguration.getStatus().toUpperCase());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-bucket-optional-attributes");
    }

}