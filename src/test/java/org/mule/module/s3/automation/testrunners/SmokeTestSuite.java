/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.automation.testrunners;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.module.s3.automation.SmokeTests;
import org.mule.module.s3.automation.testcases.*;

@RunWith(Categories.class)
@IncludeCategory(SmokeTests.class)

@SuiteClasses({

        BucketPolicyTestCases.class,
        BucketVersioningConfigurationTestCases.class,
        BucketWebsiteConfigurationTestCases.class,
        CopyObjectTestCases.class,
        CreateBucketTestCases.class,
        CreateObjectPresignedURITestCases.class,
        CreateObjectTestCases.class,
        CreateObjectURITestCases.class,
        DeleteBucketTestCases.class,
        DeleteObjectTestCases.class,
        GetObjectContentTestCases.class,
        GetObjectMetadataTestCases.class,
        GetObjectTestCases.class,
        ListBucketsTestCases.class,
        ListObjectVersionsTestCases.class,
        ListObjectsTestCases.class,
        SetObjectStorageClassTestCases.class,
        GetBucketLocationTestCases.class
})

public class SmokeTestSuite {

}