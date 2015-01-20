/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.s3.automation;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.mule.modules.tests.ConnectorTestCase;


public class S3TestParent extends ConnectorTestCase {

    // Set global timeout of tests to 5 minutes
    @Rule
    public Timeout globalTimeout = new Timeout(300000);


   /* protected static final String[] SPRING_CONFIG_FILES = new String[]{"AutomationSpringBeans.xml"};
    protected static ApplicationContext context;
    protected Map<String, Object> setupData;
    protected Map<String, Object> testObjects;


    @Override
    protected String getConfigResources() {
        return "automation-test-flows.xml";
    }

    protected MessageProcessor lookupMessageProcessor(String name) {
        return (MessageProcessor) muleContext.getRegistry().lookupFlowConstruct(name);
    }

    @BeforeClass
    public static void beforeClass() {

        context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);

    }*/

}