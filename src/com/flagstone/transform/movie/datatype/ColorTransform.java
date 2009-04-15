/*
 * ColorTransform.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movie.datatype;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.Encodeable;
import com.flagstone.transform.movie.Copyable;

/**
 * <p>
 * A ColorTransform is used to change the colour of a shape or button without
 * changing the values in the original definition of the object.
 * </p>
 * 
 * <p>
 * Two types of transformation are supported: Add and Multiply. In Add
 * transformations a value is added to each colour channel:
 * </p>
 * 
 * <pre>
 * newRed = red + addRedTerm
 * newGreen = green + addGreenTerm
 * newBlue = blue + addBlueTerm
 * newAlpha = alpha + addAlphaTerm
 * </pre>
 * 
 * <p>
 * In Multiply transformations each colour channel is multiplied by a given
 * value:
 * </p>
 * 
 * <pre>
 * newRed = red * multiplyRedTerm
 * newGreen = green * multiplyGreenTerm
 * newBlue = blue * multiplyBlueTerm
 * newAlpha = alpha * multiplyAlphaTerm
 * </pre>
 * 
 * <p>
 * Add and Multiply transforms may be combined in which case the multiply terms
 * are applied to the colour channel before the add terms.
 * </p>
 * 
 * <pre>
 * newRed = (red * multiplyRedTerm) + addRedTerm
 * newGreen = (green * multiplyGreenTerm) + addGreenTerm
 * newBlue = (blue * multiplyBlueTerm) + addBlueTerm
 * newAlpha = (alpha * multiplyAlphaTerm) + addAlphaTerm
 * </pre>
 * 
 * <p>
 * For each type of transform the result of the calculation is limited to the
 * range 0..255. If the result is less than 0 or greater than 255 then it is
 * clamped at 0 and 255 respectively.
 * </p>
 * 
 * <p>
 * Not all objects containing a colour transform use the add or multiply terms
 * defined for the alpha channel. The colour objects defined in an DefineButton,
 * ButtonColorTransform or PlaceObject object do not use the alpha channel while
 * DefineButton2 and PlaceObject2 do. The "parent" object is stored in a Context
 * when objects are encoded or decoded allowing the alpha terms to be
 * selectively encoded or decoded.
 * </p>
 * 
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class ColorTransform implements Encodeable, Copyable<ColorTransform> {

	private static final String FORMAT = 
		"ColorTransform: { multiply=[%f, %f, %f, %f]; add=[%d, %d, %d, %d] }";

	private int multiplyRed;
	private int multiplyGreen;
	private int multiplyBlue;
	private int multiplyAlpha;

	private int addRed;
	private int addGreen;
	private int addBlue;
	private int addAlpha;

	private transient int size;
	private transient boolean hasMultiply;
	private transient boolean hasAdd;
	private transient boolean hasAlpha;

	public ColorTransform(final SWFDecoder coder) throws CoderException {

		coder.alignToByte();

		hasAdd = coder.readBits(1, false) != 0;
		hasMultiply = coder.readBits(1, false) != 0;
		hasAlpha = coder.getContext().isTransparent();
		size = coder.readBits(4, false);

		if (hasMultiply) {
			multiplyRed = coder.readBits(size, true);
			multiplyGreen = coder.readBits(size, true);
			multiplyBlue = coder.readBits(size, true);
			multiplyAlpha = hasAlpha ? coder.readBits(size, true) : 256;
		} else {
			multiplyRed = 256;
			multiplyGreen = 256;
			multiplyBlue = 256;
			multiplyAlpha = 256;
		}

		if (hasAdd) {
			addRed = coder.readBits(size, true);
			addGreen = coder.readBits(size, true);
			addBlue = coder.readBits(size, true);

			if (hasAlpha) {
				addAlpha = coder.readBits(size, true);
			}
		}

		coder.alignToByte();
	}

	/**
	 * Creates an add colour transform.
	 * 
	 * @param addRed
	 *            value to add to the red colour channel.
	 * @param addGreen
	 *            value to add to the green colour channel.
	 * @param addBlue
	 *            value to add to the blue colour channel.
	 * @param addAlpha
	 *            value to add to the alpha colour channel.
	 */
	public ColorTransform(final int addRed, final int addGreen,
			final int addBlue, final int addAlpha) {
		multiplyRed = 256;
		multiplyGreen = 256;
		multiplyBlue = 256;
		multiplyAlpha = 256;

		this.addRed = addRed;
		this.addGreen = addGreen;
		this.addBlue = addBlue;
		this.addAlpha = addAlpha;
	}

	/**
	 * Creates a multiply colour transform that will apply the colour channels.
	 * 
	 * @param mulRed
	 *            value to multiply the red colour channel by.
	 * @param mulGreen
	 *            value to multiply the green colour channel by.
	 * @param mulBlue
	 *            value to multiply the blue colour channel by.
	 * @param mulAlpha
	 *            value to multiply the alpha colour channel by.
	 */
	public ColorTransform(final float mulRed, final float mulGreen,
			final float mulBlue, final float mulAlpha) {
		multiplyRed = (int) (mulRed * 256);
		multiplyGreen = (int) (mulGreen * 256);
		multiplyBlue = (int) (mulBlue * 256);
		multiplyAlpha = (int) (mulAlpha * 256);

		addRed = 0;
		addGreen = 0;
		addBlue = 0;
		addAlpha = 0;
	}

	/**
	 * Create a copy of a ColorTransform object.
	 * 
	 * @param object
	 *            the ColorTransform object used to initialise this one.
	 */
	public ColorTransform(final ColorTransform object) {

		multiplyRed = object.multiplyRed;
		multiplyGreen = object.multiplyGreen;
		multiplyBlue = object.multiplyBlue;
		multiplyAlpha = object.multiplyAlpha;

		addRed = object.addRed;
		addGreen = object.addGreen;
		addBlue = object.addBlue;
		addAlpha = object.addAlpha;
	}

	/**
	 * Returns true if the colour of an object will be unchanged by the
	 * transform.
	 */
	public boolean isUnityTransform() {
		return (multiplyRed == 256) && (multiplyGreen == 256)
				&& (multiplyBlue == 256) && (multiplyAlpha == 256)
				&& (addRed == 0) && (addGreen == 0) && (addBlue == 0)
				&& (addAlpha == 0);
	}

	/**
	 * Returns the value of the multiply term for the red channel.
	 */
	public float getMultiplyRed() {
		return multiplyRed / 256.0f;
	}

	/**
	 * Returns the value of the multiply term for the green channel.
	 */
	public float getMultiplyGreen() {
		return multiplyGreen / 256.0f;
	}

	/**
	 * Returns the value of the multiply term for the blue channel.
	 */
	public float getMultiplyBlue() {
		return multiplyBlue / 256.0f;
	}

	/**
	 * Returns the value of the multiply term for the alpha channel.
	 */
	public float getMultiplyAlpha() {
		return multiplyAlpha / 256.0f;
	}

	/**
	 * Returns the value of the add term for the red channel.
	 */
	public int getAddRed() {
		return addRed;
	}

	/**
	 * Returns the value of the add term for the green channel.
	 */
	public int getAddGreen() {
		return addGreen;
	}

	/**
	 * Returns the value of the add term for the blue channel.
	 */
	public int getAddBlue() {
		return addBlue;
	}

	/**
	 * Returns the value of the add term for the alpha channel.
	 */
	public int getAddAlpha() {
		return addAlpha;
	}

	/**
	 * Sets the value for the multiplyTerm which will be applied to the red
	 * colour channel.
	 * 
	 * @param aNumber
	 *            the value to be multiplied with the red colour channel's
	 *            value.
	 */
	public void setMultiplyRed(final float aNumber) {
		multiplyRed = (int) (aNumber * 256);
	}

	/**
	 * Sets the value for the multiplyTerm which will be applied to the green
	 * colour channel.
	 * 
	 * @param aNumber
	 *            the value to be multiplied with the green colour channel's
	 *            value.
	 */
	public void setMultiplyGreen(final float aNumber) {
		multiplyGreen = (int) (aNumber * 256);
	}

	/**
	 * Sets the value for the multiplyTerm which will be applied to the blue
	 * colour channel.
	 * 
	 * @param aNumber
	 *            the value to be multiplied with the blue colour channel's
	 *            value.
	 */
	public void setMultiplyBlue(final float aNumber) {
		multiplyBlue = (int) (aNumber * 256);
	}

	/**
	 * Sets the value for the multiplyTerm which will be applied to the alpha
	 * colour channel.
	 * 
	 * @param aNumber
	 *            the value to be multiplied with the alpha colour channel's
	 *            value.
	 */
	public void setMultiplyAlpha(final float aNumber) {
		multiplyAlpha = (int) (aNumber * 256);
	}

	/**
	 * Sets the values for the multiply terms for each of the colour channels
	 * 
	 * @param mulRed
	 *            value to multiply the red colour channel by.
	 * @param mulGreen
	 *            value to multiply the green colour channel by.
	 * @param mulBlue
	 *            value to multiply the blue colour channel by.
	 * @param mulAlpha
	 *            value to multiply the alpha colour channel by.
	 */
	public void setMultiplyTerms(final float mulRed, final float mulGreen,
			final float mulBlue, final float mulAlpha) {
		multiplyRed = (int) (mulRed * 256);
		multiplyGreen = (int) (mulGreen * 256);
		multiplyBlue = (int) (mulBlue * 256);
		multiplyAlpha = (int) (mulAlpha * 256);
	}

	/**
	 * Sets the value for the addTerm which will be applied to the red colour
	 * channel.
	 * 
	 * @param aNumber
	 *            the value to be added to the red colour channel's value.
	 */
	public void setAddRed(final int aNumber) {
		addRed = aNumber;
	}

	/**
	 * Sets the value for the addTerm which will be applied to the green colour
	 * channel.
	 * 
	 * @param aNumber
	 *            the value to be added to the green colour channel's value.
	 */
	public void setAddGreen(final int aNumber) {
		addGreen = aNumber;
	}

	/**
	 * Sets the value for the addTerm which will be applied to the blue colour
	 * channel.
	 * 
	 * @param aNumber
	 *            the value to be added to the blue colour channel's value.
	 */
	public void setAddBlue(final int aNumber) {
		addBlue = aNumber;
	}

	/**
	 * Sets the value for the addTerm which will be applied to the alpha colour
	 * channel.
	 * 
	 * @param aNumber
	 *            the value to be added to the alpha colour channel's value.
	 */
	public void setAddAlpha(final int aNumber) {
		addAlpha = aNumber;
	}

	/**
	 * Sets the values for the add terms for each of the colour channels.
	 * 
	 * @param addRed
	 *            value to add to the red colour channel.
	 * @param addGreen
	 *            value to add to the green colour channel.
	 * @param addBlue
	 *            value to add to the blue colour channel.
	 * @param addAlpha
	 *            value to add to the alpha colour channel.
	 */
	public void setAddTerms(final int addRed, final int addGreen,
			final int addBlue, final int addAlpha) {
		this.addRed = addRed;
		this.addGreen = addGreen;
		this.addBlue = addBlue;
		this.addAlpha = addAlpha;
	}

	public ColorTransform copy() {
		return new ColorTransform(this);
	}

	@Override
	public String toString() {
		return String.format(FORMAT, multiplyRed / 256.0f,
				multiplyGreen / 256.0f, multiplyBlue / 256.0f,
				multiplyAlpha / 256.0f, addRed, addGreen, addBlue, addAlpha);
	}

	public int prepareToEncode(final SWFEncoder coder) {

		int numberOfBits = 13; // include extra 7 bits for byte alignment

		hasMultiply = containsMultiplyTerms(coder);
		hasAdd = containsAddTerms(coder);
		hasAlpha = coder.getContext().isTransparent();
		size = fieldSize(coder);

		if (hasMultiply) {
			numberOfBits += size * (hasAlpha ? 4 : 3);
		}

		if (hasAdd) {
			numberOfBits += size * (hasAlpha ? 4 : 3);
		}

		return numberOfBits >> 3;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void encode(final SWFEncoder coder) throws CoderException {

		coder.alignToByte();

		coder.writeBits(hasAdd ? 1 : 0, 1);
		coder.writeBits(hasMultiply ? 1 : 0, 1);
		coder.writeBits(size, 4);

		if (hasMultiply) {
			coder.writeBits(multiplyRed, size);
			coder.writeBits(multiplyGreen, size);
			coder.writeBits(multiplyBlue, size);

			if (hasAlpha) {
				coder.writeBits(multiplyAlpha, size);
			}
		}

		if (hasAdd) {
			coder.writeBits(addRed, size);
			coder.writeBits(addGreen, size);
			coder.writeBits(addBlue, size);

			if (hasAlpha) {
				coder.writeBits(addAlpha, size);
			}
		}

		coder.alignToByte();
	}

	private boolean containsAddTerms(final SWFEncoder coder) {
		return (addRed != 0) || (addGreen != 0) || (addBlue != 0)
				|| (coder.getContext().isTransparent() && addAlpha != 0);
	}

	private boolean containsMultiplyTerms(final SWFEncoder coder) {
		return multiplyRed != 256 || multiplyGreen != 256
				|| multiplyBlue != 256
				|| (coder.getContext().isTransparent() && multiplyAlpha != 256);
	}

	private int addFieldSize(final SWFEncoder coder) {

		int size;

		if (coder.getContext().isTransparent()) {
			size = Encoder.maxSize(addRed, addGreen, addBlue, addAlpha);
		} else {
			size = Encoder.maxSize(addRed, addGreen, addBlue);
		}
		return size;
	}

	private int multiplyFieldSize(final SWFEncoder coder) {

		int size;

		if (coder.getContext().isTransparent()) {
			size = Encoder.maxSize(multiplyRed, multiplyGreen, multiplyBlue,
					multiplyAlpha);
		} else {
			size = Encoder.maxSize(multiplyRed, multiplyGreen, multiplyBlue);
		}

		return size;
	}

	private int fieldSize(final SWFEncoder coder) {
		int numberOfBits;

		if (hasAdd && !hasMultiply) {
			numberOfBits = addFieldSize(coder);
		} else if (!hasAdd && hasMultiply) {
			numberOfBits = multiplyFieldSize(coder);
		} else if (hasAdd && hasMultiply) {
			numberOfBits = Math.max(addFieldSize(coder),
					multiplyFieldSize(coder));
		} else {
			numberOfBits = 1;
		}

		return numberOfBits;
	}
}