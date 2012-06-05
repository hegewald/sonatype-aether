package org.sonatype.aether.util.version;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;
import org.sonatype.aether.version.InvalidVersionSpecificationException;
import org.sonatype.aether.version.Version;
import org.sonatype.aether.version.VersionRange;

public class GenericVersionRangeTest
{

    private Version newVersion( String version )
    {
        return new GenericVersion( version );
    }

    private VersionRange parseValid( String range )
    {
        try
        {
            return new GenericVersionRange( range );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            AssertionError error =
                new AssertionError( range + " should be valid but failed to parse due to: " + e.getMessage() );
            error.initCause( e );
            throw error;
        }
    }	

    private void parseInvalid( String range )
    {
        try
        {
            new GenericVersionRange( range );
            fail( range + " should be invalid" );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            assertTrue( true );
        }
    }

    private void assertContains( VersionRange range, String version )
    {
        assertTrue( range + " should contain " + version, range.containsVersion( newVersion( version ) ) );
    }

    private void assertNotContains( VersionRange range, String version )
    {
        assertFalse( range + " should not contain " + version, range.containsVersion( newVersion( version ) ) );
    }

	/* */
	@BeforeClass
	public static final void setSystemProp() {
		System.setProperty("snapshots", "true");
	}
	/* */

    @Test
    public void testLowerBoundInclusiveUpperBoundInclusive()
    {
        VersionRange range = parseValid( "[1,2]" );
        assertContains( range, "1" );
        assertContains( range, "1.1-SNAPSHOT" );
        assertContains( range, "2" );
        assertEquals( range, parseValid( range.toString() ) );
    }

    @Test
    public void testLowerBoundInclusiveUpperBoundExclusive()
    {
        VersionRange range = parseValid( "[1.2.3.4.5,1.2.3.4.6)" );
        assertContains( range, "1.2.3.4.5" );
        assertNotContains( range, "1.2.3.4.6" );
        assertEquals( range, parseValid( range.toString() ) );
    }

    @Test
    public void testLowerBoundExclusiveUpperBoundInclusive()
    {
        VersionRange range = parseValid( "(1a,1b]" );
        assertNotContains( range, "1a" );
        assertContains( range, "1b" );
        assertEquals( range, parseValid( range.toString() ) );
    }

    @Test
    public void testLowerBoundExclusiveUpperBoundExclusive()
    {
        VersionRange range = parseValid( "(1,3)" );
        assertNotContains( range, "1" );
        assertContains( range, "2-SNAPSHOT" );
        assertNotContains( range, "3" );
        assertEquals( range, parseValid( range.toString() ) );
    }

    @Test
    public void testSingleVersion()
    {
        VersionRange range = parseValid( "[1]" );
        assertContains( range, "1" );
        assertEquals( range, parseValid( range.toString() ) );

        range = parseValid( "[1,1]" );
        assertContains( range, "1" );
        assertEquals( range, parseValid( range.toString() ) );
    }

    @Test
    public void testMissingOpenCloseDelimiter()
    {
        parseInvalid( "1.0" );
    }

    @Test
    public void testMissingOpenDelimiter()
    {
        parseInvalid( "1.0]" );
        parseInvalid( "1.0)" );
    }

    @Test
    public void testMissingCloseDelimiter()
    {
        parseInvalid( "[1.0" );
        parseInvalid( "(1.0" );
    }

    @Test
    public void testTooManyVersions()
    {
        parseInvalid( "[1,2,3]" );
        parseInvalid( "(1,2,3)" );
        parseInvalid( "[1,2,3)" );
    }
	
		/* */
	@Test
	public void testSnapshotExclude() {
		System.setProperty("snapshots", "false");
		VersionRange range = parseValid("[1,2]");
		assertContains(range, "1");
		assertNotContains(range, "1.1-SNAPSHOT");
		assertContains(range, "2");
		assertEquals(range, parseValid(range.toString()));
		setSystemProp();
	}
	/* */

}
