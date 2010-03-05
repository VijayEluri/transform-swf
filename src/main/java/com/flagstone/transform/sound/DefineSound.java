/*
 * DefineSound.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.sound;

import java.util.Arrays;


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.exception.IllegalArgumentValueException;

/**
 * DefineSound is used to define a sound that will be played when a given event
 * occurs.
 *
 * <p>
 * Three different types of object are used to play an event sound:
 * </p>
 *
 * <ul>
 * <li>The DefineSound object that contains the sampled sound.</li>
 * <li>A SoundInfo object that defines how the sound fades in and out, whether
 * it repeats and also defines an envelope for more sophisticated control over
 * how the sound is played.</li>
 * <li>A StartSound object that signals the Flash Player to begin playing the
 * sound.</li>
 * </ul>
 *
 * <p>
 * Five encoded formats for the sound data are supported: NATIVE_PCM, PCM,
 * ADPCM, MP3 and NELLYMOSER.
 * </p>
 *
 * @see SoundInfo
 * @see StartSound
 */
public final class DefineSound implements DefineTag {

    private static final String FORMAT = "DefineSound: { identifier=%d;"
            + " format=%s; rate=%d; channelCount=%d; sampleSize=%d "
            + " sampleCount=%d }";

    private int format;
    private int rate;
    private int channelCount;
    private int sampleSize;
    private int sampleCount;
    private byte[] sound;
    private int identifier;

    private transient int length;

    /**
     * Creates and initialises a DefineSound object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineSound(final SWFDecoder coder) throws CoderException {

        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        format = coder.readBits(4, false);

        switch (coder.readBits(2, false)) {
        case 0:
            rate = 5512;
            break;
        case 1:
            rate = 11025;
            break;
        case 2:
            rate = 22050;
            break;
        case 3:
            rate = 44100;
            break;
        default:
            rate = 0;
            break;
        }

        sampleSize = coder.readBits(1, false) + 1;
        channelCount = coder.readBits(1, false) + 1;
        sampleCount = coder.readWord(4, false);

        sound = coder.readBytes(new byte[length - 7]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineSound object specifying the unique identifier and all the
     * parameters required to describe the sound.
     *
     * @param uid
     *            the unique identifier for this sound. Must be in the range
     *            1..65535.
     * @param aFormat
     *            the encoding format for the sound. For Flash 1 the formats may
     *            be one of the format: NATIVE_PCM, PCM or ADPCM. For Flash 4 or
     *            later include MP3 and Flash 6 or later include NELLYMOSER.
     * @param rate
     *            the number of samples per second that the sound is played at ,
     *            either 5512, 11025, 22050 or 44100.
     * @param channels
     *            the number of channels in the sound, must be either 1 (Mono)
     *            or 2 (Stereo).
     * @param sampleSize
     *            the size of an uncompressed sound sample in bits, must be
     *            either 8 or 16.
     * @param count
     *            the number of samples in the sound data.
     * @param bytes
     *            the sound data.
     */
    public DefineSound(final int uid, final SoundFormat aFormat,
            final int rate, final int channels, final int sampleSize,
            final int count, final byte[] bytes) {
        setIdentifier(uid);
        setFormat(aFormat);
        setRate(rate);
        setChannelCount(channels);
        setSampleSize(sampleSize);
        setSampleCount(count);
        setSound(bytes);
    }

    /**
     * Creates and initialises a DefineSound object using the values copied
     * from another DefineSound object.
     *
     * @param object
     *            a DefineSound object from which the values will be
     *            copied.
     */
    public DefineSound(final DefineSound object) {
        identifier = object.identifier;
        format = object.format;
        rate = object.rate;
        channelCount = object.channelCount;
        sampleSize = object.sampleSize;
        sampleCount = object.sampleCount;
        sound = object.sound;
    }

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
             throw new IllegalArgumentRangeException(1, 65536, uid);
        }
        identifier = uid;
    }

    /**
     * Returns the compression format used.
     */
    public SoundFormat getFormat() {    
        SoundFormat value;
        
        switch (format) {
        case 0: 
            value = SoundFormat.NATIVE_PCM;
            break;
        case 1: 
            value = SoundFormat.ADPCM;
            break;
        case 2: 
            value = SoundFormat.MP3;
            break;
        case 3: 
            value = SoundFormat.PCM;
            break;
        case 5: 
            value = SoundFormat.NELLYMOSER_8K;
            break;
        case 6: 
            value = SoundFormat.NELLYMOSER;
            break;
        default:
            throw new IllegalStateException("Unsupported sound format.");
        }
        return value;
    }

    /**
     * Returns the rate at which the sound will be played, in Hz: 5512, 11025,
     * 22050 or 44100.
     */
    public int getRate() {
        return rate;
    }

    /**
     * Returns the number of sound channels, 1 (Mono) or 2 (Stereo).
     */
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * Returns the size of an uncompressed sample in bytes.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Returns the number of samples in the sound data.
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Returns a copy of the sound data.
     */
    public byte[] getSound() {
        return Arrays.copyOf(sound, sound.length);
    }

    /**
     * Sets the compression format used.
     *
     * @param encoding
     *            the format for the sound.
     */
    public void setFormat(final SoundFormat encoding) {
        switch (encoding) {
        case NATIVE_PCM: 
            format = 0;
            break;
        case ADPCM: 
            format = 1;
            break;
        case MP3: 
            format = 2;
            break;
        case PCM: 
            format = 3;
            break;
        case NELLYMOSER_8K: 
            format = 5;
            break;
        case NELLYMOSER: 
            format = 6;
            break;
        default:
            throw new IllegalArgumentException("Unsupported sound format.");
        }
    }

    /**
     * Sets the sampling rate in Hertz.
     *
     * @param rate
     *            the rate at which the sounds is played in Hz. Must be one of:
     *            5512, 11025, 22050 or 44100.
     */
    public void setRate(final int rate) {
        if ((rate != 5512) && (rate != 11025) && (rate != 22050)
                && (rate != 44100)) {
            throw new IllegalArgumentValueException(new int[] {5512, 11025, 22050, 44100}, rate);
        }
        this.rate = rate;
    }

    /**
     * Sets the number of channels defined in the sound.
     *
     * @param channels
     *            the number of channels in the sound, must be either 1 (Mono)
     *            or 2 (Stereo).
     */
    public void setChannelCount(final int channels) {
        if ((channels < 1) || (channels > 2)) {
            throw new IllegalArgumentRangeException(1, 2, channels);
        }
        channelCount = channels;
    }

    /**
     * Sets the sample size in bytes.
     *
     * @param size
     *            the size of sound samples in bytes. Must be either 1 or 2.
     */
    public void setSampleSize(final int size) {
        if ((size < 1) || (size > 2)) {
            throw new IllegalArgumentRangeException(1, 2, size);
        }
        sampleSize = size;
    }

    /**
     * Sets the number of samples in the sound data.
     *
     * @param count
     *            the number of samples for the sound.
     */
    public void setSampleCount(final int count) {
        if (count < 1) {
            throw new IllegalArgumentRangeException(1, Integer.MAX_VALUE, count);
        }
        sampleCount = count;
    }

    /**
     * Sets the sound data.
     *
     * @param bytes
     *            the sound data. Must not be null.
     */
    public void setSound(final byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }
        sound = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public DefineSound copy() {
        return new DefineSound(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, format, rate, channelCount,
                sampleSize, sampleCount);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 7;
        length += sound.length;

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_SOUND << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_SOUND << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        coder.writeBits(format, 4);

        switch (rate) {
        case 5512:
            coder.writeBits(0, 2);
            break;
        case 11025:
            coder.writeBits(1, 2);
            break;
        case 22050:
            coder.writeBits(2, 2);
            break;
        case 44100:
            coder.writeBits(3, 2);
            break;
        default:
            break;
        }
        coder.writeBits(sampleSize - 1, 1);
        coder.writeBits(channelCount - 1, 1);
        coder.writeWord(sampleCount, 4);

        coder.writeBytes(sound);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
