/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import com.amazonaws.services.s3.model.Bucket;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.module.s3.automation.RegressionTests;
import org.mule.module.s3.automation.S3TestParent;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DeleteBucketTestCases extends S3TestParent {

    String bucketName;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("deleteBucketTestData");
        bucketName = ((Bucket) runFlowAndGetPayload("create-bucket")).getName();

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteBucket() {

        boolean bucketNotFound = true;

        try {

            runFlowAndGetPayload("delete-bucket");

            List<Bucket> buckets = runFlowAndGetPayload("list-buckets");
            Iterator<Bucket> iterator = buckets.iterator();

            while (iterator.hasNext() && !bucketNotFound) {

                Bucket bucket = iterator.next();
                if (bucket.getName().equals(bucketName)) {
                    bucketNotFound = false;
                }
            }
            assertTrue(bucketNotFound);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testDeleteBucketOptionalAttributes() {

        boolean bucketNotFound = true;

        HashMap<String, Object> updatedUserMetadata = getBeanFromContext("deleteBucketUpdatedUserMetadata");

        byte data[] = bucketName.getBytes();
        upsertOnTestRunMessage("contentRef", data);

        upsertOnTestRunMessage("versioningStatus", "ENABLED");

        try {

            runFlowAndGetPayload("set-bucket-versioning-status");

            runFlowAndGetPayload("create-object-child-elements-from-message");

            upsertOnTestRunMessage("userMetadata", updatedUserMetadata);

            runFlowAndGetPayload("create-object-child-elements-from-message");

            runFlowAndGetPayload("delete-bucket-optional-attributes");

            List<Bucket> buckets = (List<Bucket>) runFlowAndGetPayload("list-buckets");
            Iterator<Bucket> iterator = buckets.iterator();

            while (iterator.hasNext() && !bucketNotFound) {

                Bucket bucket = iterator.next();
                if (bucket.getName().equals(bucketName)) {
                    bucketNotFound = false;
                }

            }

            assertTrue(bucketNotFound);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

}