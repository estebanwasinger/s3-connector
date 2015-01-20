/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.module.s3.simpleapi;

import org.apache.commons.lang.ObjectUtils;

import java.net.URI;
import java.net.URISyntaxException;

import static com.amazonaws.services.s3.model.Region.*;

public enum Region {

    US_STANDARD(US_Standard, "s3.amazonaws.com"),

    US_WEST(US_West, "s3-us-west-1.amazonaws.com"),

    US_WEST_2(US_West_2, "s3-us-west-2.amazonaws.com"),

    US_GOV_CLOUD(US_GovCloud, "s3-us-gov-west-1.amazonaws.com"),

    EU_IRELAND(EU_Ireland, "s3-eu-west-1.amazonaws.com"),

    EU_FRANKFURT(EU_Frankfurt, "s3-eu-central-1.amazonaws.com"),

    AP_SINGAPORE(AP_Singapore, "s3-ap-southeast-1.amazonaws.com"),

    AP_SYDNEY(AP_Sydney, "s3-ap-southeast-2.amazonaws.com"),

    AP_TOKYO(AP_Tokyo, "s3-ap-northeast-1.amazonaws.com"),

    SA_SAO_PAULO(SA_SaoPaulo, "s3-sa-east-1.amazonaws.com"),

    CN_BEIJING(CN_Beijing, "s3.cn-north-1.amazonaws.com.cn");

    private final com.amazonaws.services.s3.model.Region s3Equivalent;

    private final String domain;

    private Region(com.amazonaws.services.s3.model.Region s3Equivalent, String domain) {
        this.s3Equivalent = s3Equivalent;
        this.domain = domain;
    }

    public static Region getDefaultRegion() {
        return US_STANDARD;
    }

    public static Region from(String location) {
        for (Region region : Region.values()) {
            if (ObjectUtils.equals(location, region.toS3Equivalent().toString())) {
                return region;
            }
        }
        return US_STANDARD;
    }

    public com.amazonaws.services.s3.model.Region toS3Equivalent() {
        return s3Equivalent;
    }

    public URI getObjectUri(S3ObjectId objectId, boolean secure) {
        String scheme = secure ? "https" : "http";
        try {
            return new URI(String.format("%s://%s.%s/%s", scheme, objectId.getBucketName(), domain,
                    objectId.getKey()));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}
