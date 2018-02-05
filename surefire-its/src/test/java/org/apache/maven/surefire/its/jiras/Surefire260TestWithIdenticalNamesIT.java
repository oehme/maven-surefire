package org.apache.maven.surefire.its.jiras;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.net.URI;

import org.apache.maven.surefire.its.fixture.*;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test Surefire-570 Multiple report directories
 *
 * @author Kristian Rosenvold
 */
public class Surefire260TestWithIdenticalNamesIT
    extends SurefireJUnit4IntegrationTestCase
{

    @Test
    public void testWithIdenticalNames()
        throws IOException
    {
        SurefireLauncher surefireLauncher = unpack( "surefire-260-testWithIdenticalNames" ).failNever();
        surefireLauncher.executeTest();
        surefireLauncher.reset();
        OutputValidator validator = surefireLauncher.addSurefireReportGoal().executeCurrentGoals();

        TestFile siteFile = validator.getSiteFile( "surefire-report.html" );
        final URI uri = siteFile.toURI();

        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled( true );
        final HtmlPage page = webClient.getPage( uri.toURL() );

        final HtmlAnchor a =
            (HtmlAnchor) page.getByXPath( "//a[@href = \"javascript:toggleDisplay('surefire260.TestB.testDup');\"]" )
                    .get( 0 );
        final HtmlDivision content = (HtmlDivision) page.getElementById( "surefire260.TestB.testDup-failure" );
        assertNotNull( content );
        assertTrue( content.getAttribute( "style" ).contains( "none" ) );
        a.click();
        assertFalse( content.getAttribute( "style" ).contains( "none" ) );
        webClient.closeAllWindows();
    }
}