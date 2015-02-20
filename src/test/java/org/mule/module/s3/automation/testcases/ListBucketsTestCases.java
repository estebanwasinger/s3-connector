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
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ListBucketsTestCases extends S3TestParent {

    private List<String> generatedBucketNames = new ArrayList<String>();
    private List<String> retrievedBucketNames = new ArrayList<String>();
    private int bucketAmount;

    @Before
    public void setUp() throws Exception {

        String bucketName;

        initializeTestRunMessage("listBucketsTestData");
        bucketAmount = Integer.parseInt(getTestRunMessageValue("bucketAmount").toString());

        try {

            for (int i = 1; i <= bucketAmount; i++) {

                bucketName = UUID.randomUUID().toString();
                generatedBucketNames.add(bucketName);

                upsertOnTestRunMessage("bucketName", bucketName);

                runFlowAndGetPayload("create-bucket");
            }

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }

    @Category({SmokeTests.class, RegressionTests.class})
    @Test
    public void testListBuckets() {

        try {

            List<Bucket> buckets = (List<Bucket>) runFlowAndGetPayload("list-buckets");

            assertTrue(buckets.size() >= generatedBucketNames.size());

            Iterator<Bucket> iterator = buckets.iterator();

            while (iterator.hasNext()) {

                Bucket bucket = iterator.next();
                retrievedBucketNames.add(bucket.getName());

            }

            assertTrue(retrievedBucketNames.containsAll(generatedBucketNames));

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }

    }


    @After
    public void tearDown() throws Exception {

        for (int i = 0; i < generatedBucketNames.size(); i++) {

            upsertOnTestRunMessage("bucketName", generatedBucketNames.get(i).toString());

            runFlowAndGetPayload("delete-bucket");

        }

    }

}