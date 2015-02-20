/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3;

import com.amazonaws.services.s3.model.CannedAccessControlList;

import static com.amazonaws.services.s3.model.CannedAccessControlList.*;

/**
 * A wrapper over {@link CannedAccessControlList}, as a workaround over mule cloud
 * connector api issue regarding enums processing
 */
public enum AccessControlList {
    PRIVATE(Private),

    PUBLIC_READ(PublicRead),

    PUBLIC_READ_WRITE(PublicReadWrite),

    AUTHENTICATED_READ(AuthenticatedRead),

    LOG_DELIVERY_WRITE(LogDeliveryWrite),

    BUCKET_OWNER_READ(BucketOwnerRead),

    BUCKET_OWNER_FULL_CONTROL(BucketOwnerFullControl);

    private CannedAccessControlList s3Equivalent;

    private AccessControlList(CannedAccessControlList s3Equivalent) {
        this.s3Equivalent = s3Equivalent;
    }

    public CannedAccessControlList toS3Equivalent() {
        return s3Equivalent;
    }

}
