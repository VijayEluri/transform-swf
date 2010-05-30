/*
 * DefineMovieClip.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.movieclip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.MovieTag;
import com.flagstone.transform.SWF;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * DefineMovieClip defines a movie clip that animates shapes within a movie. It
 * contains an array of movie objects that define the placement of shapes,
 * buttons, text and images and the order in which they are displayed through a
 * time-line that is separate from the parent movie.
 *
 * <p>
 * Although a movie clip contains the commands that instructs the Flash Player
 * on how to animate the clip it cannot contain any new definitions of objects.
 * All definitions must be in the main movie. All objects referred to by the
 * movie clip must be also defined in the main movie before they can be used.
 * </p>
 *
 * <p>
 * When using the DefineMovieClip object can only contain objects from the
 * following classes: ShowFrame, PlaceObject, PlaceObject2, RemoveObject,
 * RemoveObject2, DoAction, StartSound, FrameLabel, SoundStreamHead,
 * SoundStreamHead2 or SoundStreamBlock. Other objects are not allowed.
 * </p>
 */
//TODO(class)
public final class DefineMovieClip implements DefineTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "DefineMovieClip: { identifier=%d;"
    		+ " objects=%s }";

    /** The unique identifier for this object. */
    private int identifier;
    /** The list of objects that describe how the movie clip is animated. */
    private List<MovieTag> objects;

    /** The number of frames in the movie clip, when it is encoded. */
    private transient int frameCount;
    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a DefineMovieClip object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public DefineMovieClip(final SWFDecoder coder, final Context context)
            throws IOException {
        length = coder.readUnsignedShort() & Coder.LENGTH_FIELD;
        if (length == Coder.IS_EXTENDED) {
            length = coder.readInt();
        }
        identifier = coder.readUnsignedShort();
        frameCount = coder.readUnsignedShort();
        objects = new ArrayList<MovieTag>();

        final SWFFactory<MovieTag> decoder = context.getRegistry()
                .getMovieDecoder();

        while (coder.scanUnsignedShort() >> 6 != MovieTypes.END) {
           objects.add(decoder.getObject(coder, context));
        }
    }

    /**
     * Creates a DefineMovieClip object with the unique identifier and array of
     * movie objects.
     *
     * @param uid
     *            a unique identifier for the movie clip. Must be in the range
     *            1..65535,
     * @param anArray
     *            the array of movie objects. Must not be null.
     */
    public DefineMovieClip(final int uid, final List<MovieTag> anArray) {
        setIdentifier(uid);
        setObjects(anArray);
    }

    /**
     * Creates and initialises a DefineMovieClip object using the values copied
     * from another DefineMovieClip object.
     *
     * @param object
     *            a DefineMovieClip object from which the values will be
     *            copied.
     */
    public DefineMovieClip(final DefineMovieClip object) {
        identifier = object.identifier;
        objects = new ArrayList<MovieTag>(object.objects.size());
        for (final MovieTag tag : object.objects) {
            objects.add(tag.copy());
        }
    }

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }

    /**
     * Adds the movie object to the array of objects that update the display
     * list. See description above for the list of acceptable types.
     *
     * @param obj
     *            a Movie object. Must not be null
     *
     * @return this object.
     */
    public DefineMovieClip add(final MovieTag obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        objects.add(obj);
        return this;
    }

    /**
     * Get the array of movie objects that describe how the movie clip is
     * animated.
     *
     * @return the list of objects for the movie clip.
     */
    public List<MovieTag> getObjects() {
        return objects;
    }

    /**
     * Sets the array of movie objects.
     *
     * @param anArray
     *            the array of movie objects. Must not be null.
     */
    public void setObjects(final List<MovieTag> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        objects = anArray;
    }

    /** {@inheritDoc} */
    public DefineMovieClip copy() {
        return new DefineMovieClip(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, objects);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {

        frameCount = 0;
        length = 6;

        for (final MovieTag object : objects) {
            length += object.prepareToEncode(context);

            if (object instanceof ShowFrame) {
                frameCount += 1;
            }
        }
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {

        coder.writeHeader(MovieTypes.DEFINE_MOVIE_CLIP, length);
        coder.mark();
        coder.writeI16(identifier);
        coder.writeI16(frameCount);

        for (final MovieTag object : objects) {
            object.encode(coder, context);
        }
        coder.writeI16(0);
        coder.unmark(length);
    }
}
