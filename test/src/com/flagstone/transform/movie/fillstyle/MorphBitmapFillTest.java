/*
 * MorphBitmapFillTest.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.flagstone.transform.movie.fillstyle;

import org.junit.Test;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.datatype.CoordTransform;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

@SuppressWarnings( {
	"PMD.LocalVariableCouldBeFinal",
	"PMD.JUnitAssertionsShouldIncludeMessage" })
public final class MorphBitmapFillTest {
	
	private transient final boolean tiled = false;
	private transient final boolean smoothed = false;
	private transient final int identifier = 1;
	private transient final CoordTransform start = 
		CoordTransform.translate(1,2);
	private transient final CoordTransform end = 
		CoordTransform.translate(1,2);

	private transient MorphBitmapFill fixture;

	private transient final byte[] empty = new byte[] { 0x43,
			0x00, 0x00, 0x00, 0x00 };
	
	private transient final byte[] encoded = new byte[] { 0x43,
			0x01, 0x00, 0x06, 0x50, 0x06, 0x50 };
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithLowerBound() {
		fixture = new MorphBitmapFill(tiled, smoothed, 0, start, end);
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForIdentifierWithUpperBound() {
		fixture = new MorphBitmapFill(tiled, smoothed, 65536, start, end);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForStartTransformWithNull() {
		fixture = new MorphBitmapFill(tiled, smoothed, 1, null, end);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void checkAccessorForEndTransformWithNull() {
		fixture = new MorphBitmapFill(tiled, smoothed, 1, start, null);
	}

	@Test
	public void checkCopy() {
		fixture = new MorphBitmapFill(tiled, smoothed, identifier, start, end);
		assertEquals(fixture.getIdentifier(), fixture.copy().getIdentifier());
		assertSame(fixture.getStartTransform(), fixture.copy().getStartTransform());
		assertSame(fixture.getEndTransform(), fixture.copy().getEndTransform());
		assertEquals(fixture.toString(), fixture.toString());
	}
	
	@Test
	public void encode() throws CoderException {

		SWFEncoder encoder = new SWFEncoder(encoded.length);
		SWFContext context = new SWFContext();

		fixture = new MorphBitmapFill(tiled, smoothed, identifier, start, end);
		assertEquals(encoded.length, fixture.prepareToEncode(encoder, context));		
		fixture.encode(encoder, context);
		
		assertTrue(encoder.eof());
		assertArrayEquals(encoded, encoder.getData());
	}

	@Test
	public void decode() throws CoderException {

		SWFDecoder decoder = new SWFDecoder(encoded);
		SWFContext context = new SWFContext();

		fixture = new MorphBitmapFill(decoder, context);
		
		assertTrue(decoder.eof());
		assertEquals(identifier, fixture.getIdentifier());
		assertEquals(start.getTranslateX(), fixture.getStartTransform().getTranslateX());
		assertEquals(start.getTranslateY(), fixture.getStartTransform().getTranslateY());
		assertEquals(end.getTranslateX(), fixture.getEndTransform().getTranslateX());
		assertEquals(end.getTranslateY(), fixture.getEndTransform().getTranslateY());
	}
}
