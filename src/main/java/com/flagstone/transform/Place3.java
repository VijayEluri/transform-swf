/*
 * Place3.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.coder.SWFFactory;
import com.flagstone.transform.datatype.Blend;
import com.flagstone.transform.datatype.ColorTransform;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.exception.IllegalArgumentRangeException;
import com.flagstone.transform.filter.Filter;
import com.flagstone.transform.movieclip.MovieClipEventHandler;

/**
 * PlaceObject2 is used to add and manipulate objects (shape, button, etc.) on
 * the Flash Player's display list.
 *
 * <p>
 * PlaceObject2 supersedes the PlaceObject class providing more functionality
 * and easier manipulation of objects in the display list through the following
 * operations:
 * </p>
 *
 * <ul>
 * <li>Place a new shape on the display list.</li>
 * <li>Change an existing shape by moving it to new location or changing its
 * appearance.</li>
 * <li>Replace an existing shape with a another.</li>
 * <li>
 * Define clipping layers to mask objects displayed in front of a shape.</li>
 * <li>Control the morphing process that changes one shape into another.</li>
 * <li>Assign names to objects rather than using their identifiers.</li>
 * <li>Define the sequence of actions that are executed when an event occurs in
 * movie clip.</li>
 * </ul>
 *
 * <p>
 * <b>Clipping Depth</b><br/>
 * With the introduction of Flash 3 the display list supported a clipping layer.
 * This allowed the outline of an object to define a clipping path that is used
 * to mask other objects placed in front of it. The clipping depth can be set to
 * mask objects between the layer containing the clipping path and a specified
 * layer.
 * </p>
 *
 * <p>
 * <b>Shape Morphing</b><br/>
 * Shapes that will be morphed are defined using the DefineMorphShape class
 * which defines a start and end shape. The Flash Player performs the
 * interpolation that transforms one shape into another. The progress of the
 * morphing process is controlled by a ratio which ranges from 0.0 to 1.0, where
 * 0 generates a shape identical to the starting shape in the DefineMorphShape
 * object and 1.0 generates the shape at the end of the morphing process.
 * </p>
 *
 * <p>
 * <b>Movie Clip Event Handlers</b><br/>
 * With the introduction of Flash 5, movie clips (defined using the
 * DefineMovieClip class) could specify sequences of actions that would be
 * performed in response to mouse or keyboard events. The actions are specified
 * using ClipEvent objects and the PlaceObject2 class is used to register the
 * actions in response to a particular event with the Flash player. Multiple
 * events can be handled by defining an ClipEvent for each type of event. For
 * more information see the ClipEvent class.
 * </p>
 *
 * <p>
 * Since only one object can be placed on a given layer an existing object on
 * the display list can be identified by the layer it is displayed on rather
 * than its identifier. Therefore Layer is the only required attribute. The
 * remaining attributes are optional according to the different operation being
 * performed:
 * </p>
 *
 * <ul>
 * <li>If an existing object on the display list is being modified then only the
 * layer number is required. Previously in the PlaceObject class both the
 * identifier and the layer number were required.</li>
 * <li>If no coordinate transform is applied to the shape (the default is a
 * unity transform that does not change the shape) then it is not encoded.</li>
 * <li>Similarly if no colour transform is applied to the shape (the default is
 * a unity transform that does not change the shape's colour) then it is not
 * encoded.</li>
 * <li>If a shape is not being morphed then the ratio attribute may be left at
 * its default value (-1.0).</li>
 * <li>If a shape is not used to define a clipping area then the depth attribute
 * may be left at its default value (0).</li>
 * <li>If a name is net assigned to an object the name attribute may be left its
 * default value (an empty string).</li>
 * <li>If no events are being defined for a movie clip then the array of
 * ClipEvent object may be left empty.</li>
 * </ul>
 *
 * <p>
 * The Layer class provides a simple API for manipulating objects on the display
 * list. While it is relatively simple to create instances of PlaceObject2
 * object that perform the same steps the API provided by Player is easier to
 * use and much more readable.
 * </p>
 *
 * @see com.flagstone.transform.util.movie.Layer
 */
//TODO(class)
@SuppressWarnings({"PMD.TooManyFields", "PMD.TooManyMethods" })
public final class Place3 implements MovieTag {

    /**
     * Place a new object on the display list.
     * @param identifier the unique identifier for the object.
     * @param layer the layer where it will be displayed.
     * @param xCoord the x-coordinate where the object's origin will be.
     * @param yCoord the y-coordinate where the object's origin will be.
     * @return the Place3 object to update the display list.
     */
    public static Place3 show(final int identifier, final int layer,
            final int xCoord, final int yCoord) {
        final Place3 object = new Place3();
        object.setType(PlaceType.NEW);
        object.setLayer(layer);
        object.setIdentifier(identifier);
        object.setTransform(CoordTransform.translate(xCoord, yCoord));
        return object;
    }

    /**
     * Change the position of a displayed object.
     *
     * @param layer the display list layer where the object is displayed.
     * @param xCoord the x-coordinate where the object's origin will be moved.
     * @param yCoord the y-coordinate where the object's origin will be moved.
     * @return the Place3 object to change the position of the object.
     */
    public static Place3 move(final int layer, final int xCoord,
            final int yCoord) {
        final Place3 object = new Place3();
        object.setType(PlaceType.MODIFY);
        object.setLayer(layer);
        object.setTransform(CoordTransform.translate(xCoord, yCoord));
        return object;
    }

    /**
     * Replace an existing object with another.
     *
     * @param identifier the unique identifier of the new object.
     * @param layer the display list layer of the existing object.
     * @return the Place3 object to update the display list.
     */
    public static Place3 replace(final int identifier, final int layer) {
        final Place3 object = new Place3();
        object.setType(PlaceType.REPLACE);
        object.setLayer(layer);
        object.setIdentifier(identifier);
        return object;
    }

    /**
     * Replace an existing object with another.
     *
     * @param identifier the unique identifier of the new object.
     * @param layer the display list layer of the existing object.
     * @param xCoord the x-coordinate where the new object's origin will be.
     * @param yCoord the y-coordinate where the new object's origin will be.
     * @return the Place3 object to update the display list.
     */
    public static Place3 replace(final int identifier, final int layer,
            final int xCoord, final int yCoord) {
        final Place3 object = new Place3();
        object.setType(PlaceType.REPLACE);
        object.setLayer(layer);
        object.setIdentifier(identifier);
        object.setTransform(CoordTransform.translate(xCoord, yCoord));
        return object;
    }

    /** Format string used in toString() method. */
    private static final String FORMAT = "PlaceObject3: { type=%s; layer=%d;"
            + " bitmapCache=%d; identifier=%d; transform=%s; "
            + " colorTransform=%s; ratio=%d; clippingDepth=%d; "
            + " name=%s; className=%s; "
            + " filters=%s; blend=%s; clipEvents=%s}";

    /** How the display list will be updated. */
    private PlaceType type;
    /** The display list layer number. */
    private int layer;
    private String className;
    /** Whether the displayed object will be cached as a bitmap. */
    private Integer bitmapCache;
    /** The unique identifier of the object that will be displayed. */
    private int identifier;
    /** The coordinate transform applied to the displayed object. */
    private CoordTransform transform;
    /** The color transform applied to the displayed object. */
    private ColorTransform colorTransform;
    /** The progression of the morphing process. */
    private Integer ratio;
    /** The name assigned to an object. */
    private String name;
    /** The number of layers to clip. */
    private Integer depth;
    /** The set of effects filters applied to the object. */
    private List<Filter> filters;
    /** How the object is blended with its background. */
    private Integer blend;
    /** The set of event handlers for movie clips. */
    private List<MovieClipEventHandler> events;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;
    /** Indicates whether the encoded object contains a blend. */
    private transient boolean hasBlend;
    /** Indicates whether the encoded object contains filters. */
    private transient boolean hasFilters;
    /** Indicates whether the encoded object contains an image. */
    private transient boolean hasImage;

    /**
     * Creates and initialises a Place3 object using values encoded
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
    // TODO(optimise)
    public Place3(final SWFDecoder coder, final Context context)
            throws IOException {
        context.put(Context.TRANSPARENT, 1);
        length = coder.readUnsignedShort() & Coder.LENGTH_FIELD;
        if (length == Coder.IS_EXTENDED) {
            length = coder.readInt();
        }
        coder.mark();
        int bits = coder.readByte();
        final boolean hasEvents = (bits & Coder.BIT7) != 0;
        final boolean hasDepth = (bits & Coder.BIT6) != 0;
        final boolean hasName = (bits & Coder.BIT5) != 0;
        final boolean hasRatio = (bits & Coder.BIT4) != 0;
        final boolean hasColorTransform = (bits & Coder.BIT3) != 0;
        final boolean hasTransform = (bits & Coder.BIT2) != 0;

        switch (bits & 0x03) {
        case 2:
            type = PlaceType.NEW;
            break;
        case 3:
            type = PlaceType.REPLACE;
            break;
        default: // 0,1
            type = PlaceType.MODIFY;
            break;
        }

        bits = coder.readByte();
        hasImage = (bits & Coder.BIT4) != 0;
        final boolean hasClassName = (bits & Coder.BIT3) != 0;
        final boolean hasBitmapCache = (bits & Coder.BIT2) != 0;
        hasBlend = (bits & Coder.BIT1) != 0;
        hasFilters = (bits & Coder.BIT0) != 0;

        layer = coder.readUnsignedShort();

        /* The following line implements the logic as described in the SWF 9
         * specification but it appears to be incorrect. The class name is not
         * given when hasImage is set.
         *
         * if (hasClassName || ((type == PlaceType.NEW
         * || type == PlaceType.REPLACE) && hasImage)) {
         */
        if (hasClassName) {
            className = coder.readString();
        }

        if ((type == PlaceType.NEW) || (type == PlaceType.REPLACE)) {
            identifier = coder.readUnsignedShort();
        }

        if (hasTransform) {
            transform = new CoordTransform(coder);
        }

        if (hasColorTransform) {
            colorTransform = new ColorTransform(coder, context);
        }

        if (hasRatio) {
            ratio = coder.readUnsignedShort();
        }

        if (hasName) {
            name = coder.readString();
        }

        if (hasDepth) {
            depth = coder.readUnsignedShort();
        }

        filters = new ArrayList<Filter>();

        if (hasFilters) {
            final SWFFactory<Filter> decoder = context.getRegistry()
                    .getFilterDecoder();

            final int count = coder.readByte();

            for (int i = 0; i < count; i++) {
                filters.add(decoder.getObject(coder, context));
            }
        }

        if (hasBlend) {
            blend = coder.readByte();
        }

        if (hasBitmapCache) {
            bitmapCache = coder.readByte();
        }

        events = new ArrayList<MovieClipEventHandler>();

        if (hasEvents) {
            int event;

            coder.readUnsignedShort();
            coder.readInt();

            while ((event = coder.readInt()) != 0) {
                events.add(new MovieClipEventHandler(event,
                        coder, context));
            }
        }
        context.remove(Context.TRANSPARENT);
        coder.unmark(length);
    }

    /**
     * Creates an uninitialised Place3 object.
     */
    public Place3() {
        filters = new ArrayList<Filter>();
        events = new ArrayList<MovieClipEventHandler>();
    }

    /**
     * Creates and initialises a Place3 object using the values copied
     * from another Place3 object.
     *
     * @param object
     *            a Place3 object from which the values will be
     *            copied.
     */
    public Place3(final Place3 object) {
        type = object.type;
        layer = object.layer;
        bitmapCache = object.bitmapCache;
        className = object.className;
        identifier = object.identifier;
        if (object.transform != null) {
            transform = object.transform;
        }
        if (object.colorTransform != null) {
            colorTransform = object.colorTransform;
        }
        ratio = object.ratio;
        depth = object.depth;
        name = object.name;

        filters = new ArrayList<Filter>(object.filters);
        blend = object.blend;
        events = new ArrayList<MovieClipEventHandler>(object.events.size());

        for (final MovieClipEventHandler event : object.events) {
            events.add(event.copy());
        }

    }

    /**
     * Get the type of place operation being performed, either adding a new
     * object, replacing an existing one with another or modifying an existing
     * object.
     *
     * @return the way the object will be placed.
     */
    public PlaceType getType() {
        return type;
    }

    /**
     * Sets the type of placement.
     *
     * @param aType
     *            the type of operation to be performed, either New, Modify or
     *            Replace.
     *
     * @return this object.
     */
    public Place3 setType(final PlaceType aType) {
        type = aType;
        return this;
    }

    /**
     * Get the Layer on which the object will be displayed in the display
     * list.
     *
     * @return the layer where the object will be displayed.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Sets the layer at which the object will be placed.
     *
     * @param aLayer
     *            the layer number on which the object is being displayed. Must
     *            be in the range 1..65535.
     *
     * @return this object.
     */
    public Place3 setLayer(final int aLayer) {
        if ((aLayer < 1) || (aLayer > SWF.MAX_LAYER)) {
            throw new IllegalArgumentRangeException(1, SWF.MAX_LAYER, aLayer);
        }
        layer = aLayer;
        return this;
    }

    /**
     * Get the identifier of the object to be placed. This is only required
     * when placing an object for the first time. Subsequent references to the
     * object on this layer can simply use the layer number.
     *
     * @return the unique identifier of the object to be displayed.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of the object.
     *
     * @param uid
     *            the identifier of a new object to be displayed. Must be in the
     *            range 1..65535.
     *
     * @return this object.
     */
    public Place3 setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
        return this;
    }

    /**
     * Get the coordinate transform. May be null if no coordinate transform
     * was defined.
     *
     * @return the coordinate transform that will be applied to the displayed
     * object.
     */
    public CoordTransform getTransform() {
        return transform;
    }

    /**
     * Sets the coordinate transform that defines the position where the object
     * will be displayed. The argument may be null if the location of the object
     * is not being changed.
     *
     * @param aTransform
     *            an CoordTransform object that will be applied to the object
     *            displayed.
     *
     * @return this object.
     */
    public Place3 setTransform(final CoordTransform aTransform) {
        transform = aTransform;
        return this;
    }

    /**
     * Get the colour transform. May be null if no colour transform was
     * defined.
     *
     * @return the colour transform that will be applied to the displayed
     * object.
     */
    public ColorTransform getColorTransform() {
        return colorTransform;
    }

    /**
     * Sets the colour transform that defines the colour effects applied to the
     * object. The argument may be null if the color of the object is not being
     * changed.
     *
     * @param aTransform
     *            an ColorTransform object that will be applied to the object
     *            displayed.
     *
     * @return this object.
     */
    public Place3 setColorTransform(final ColorTransform aTransform) {
        colorTransform = aTransform;
        return this;
    }

    /**
     * Get the morph ratio, in the range 0..65535 that defines the progress
     * in the morphing process performed by the Flash Player from the defined
     * start and end shapes. A value of 0 indicates the start of the process and
     * 65535 the end. Returns null if no ratio was specified.
     *
     * @return the morphing ratio.
     */
    public Integer getRatio() {
        return ratio;
    }

    /**
     * Sets point of the morphing process for a morph shape in the range
     * 0..65535. May be set to null if the shape being placed is not being
     * morphed.
     *
     * @param aNumber
     *            the progress in the morphing process.
     *
     * @return this object.
     */
    public Place3 setRatio(final Integer aNumber) {
        if ((aNumber != null) && ((aNumber < 0) || (aNumber > SWF.MAX_MORPH))) {
            throw new IllegalArgumentRangeException(1, SWF.MAX_MORPH, aNumber);
        }
        ratio = aNumber;
        return this;
    }

    /**
     * Get the number of layers that will be clipped by the object placed on
     * the layer specified in this object.
     *
     * @return the number of layers to be clipped.
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Sets the number of layers that this object will mask. May be set to zero
     * if the shape being placed does not define a clipping area.
     *
     * @param aNumber
     *            the number of layers clipped.
     *
     * @return this object.
     */
    public Place3 setDepth(final Integer aNumber) {
        if ((aNumber != null) && ((aNumber < 1) || (aNumber > SWF.MAX_LAYER))) {
             throw new IllegalArgumentRangeException(1, SWF.MAX_LAYER, aNumber);
        }
        depth = aNumber;
        return this;
    }

    /**
     * Get the name of the object. May be null if a name was not assigned to
     * the object.
     *
     * @return the name of the object.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of an object to be displayed. If a shape is not being
     * assigned a name then setting the argument to null will omit the attribute
     * when the object is encoded.
     *
     * @param aString
     *            the name assigned to the object.
     *
     * @return this object.
     */
    public Place3 setName(final String aString) {
        name = aString;
        return this;
    }

    /**
     * Get the value indicating whether the display object will be cached as a
     * bitmap (non-zero) or not cached (zero).
     *
     * @return a non-zero value if the object is cached or zer oif not cached.
     */
    public Integer getBitmapCache() {
        return bitmapCache;
    }

    /**
     * Set whether the displayed object should be cached as a bitmap.
     *
     * @param cache set to a non-zero value if the object should be cached as
     * a bitmap or to zero to disable caching.
     *
     * @return this object.
     */
    public Place3 setBitmapCache(final Integer cache) {
        bitmapCache = cache;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public Place3 setClassName(final String aString) {
        className = aString;
        return this;
    }

    /**
     * Get the list of filters that will be applied to the object when it is
     * displayed as a bitmap.
     * @return the list of bitmap filters.
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Set the list of filters that will be applied to the object when it is
     * displayed as a bitmap.
     * @param array the list of bitmap filters.
     */
    public void setFilters(final List<Filter> array) {
        if (array == null) {
            throw new IllegalArgumentException();
        }
        filters = array;
    }


    public Blend getBlend() {
        return Blend.fromInt(blend);
    }


    public void setBlend(final Blend mode) {
        blend = mode.getValue();
    }

    /**
     * Adds a clip event to the array of clip events. If the object already
     * contains a set of encoded clip event objects they will be deleted.
     *
     * @param aClipEvent
     *            a clip event object.
     *
     *            throws NullPointerException of the clip event object is null
     *
     * @return this object.
     */
    public Place3 add(final MovieClipEventHandler aClipEvent) {
        if (aClipEvent == null) {
            throw new IllegalArgumentException();
        }
        events.add(aClipEvent);
        return this;
    }

    /**
     * Get the array of event handlers that define the actions that will
     * be executed in response to events that occur in the movie clip being
     * placed.
     *
     * @return the set of event handlers for the movie clip.
     */
    public List<MovieClipEventHandler> getEvents() {
        return events;
    }

    /**
     * Set the array of Clip events. Clip Events are only valid for movie clips
     * and the argument should be set to null when placing other types of
     * object.
     *
     * If the object already contains a set of encoded clip event objects they
     * will be deleted.
     *
     * @param anArray
     *            an array of ClipEvent objects.
     */
    public void setEvents(final List<MovieClipEventHandler> anArray) {
        if (anArray == null) {
            throw new IllegalArgumentException();
        }
        events = anArray;
    }


    public Place3 add(final Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        filters.add(filter);
        return this;
    }

    /** {@inheritDoc} */
    public Place3 copy() {
        return new Place3(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, type, layer, bitmapCache, identifier,
                transform, colorTransform, ratio, depth, name, className,
                filters, blend, events);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        // CHECKSTYLE:OFF
        context.put(Context.TRANSPARENT, 1);

        hasBlend = blend != null;
        hasFilters ^= filters.isEmpty();

        length = 4;
        length += ((type == PlaceType.NEW) || (type == PlaceType.REPLACE)) ? 2
                : 0;
        length += transform == null ? 0 : transform.prepareToEncode(context);
        length += colorTransform == null ? 0 : colorTransform.prepareToEncode(
                context);
        length += ratio == null ? 0 : 2;
        length += depth == null ? 0 : 2;
        length += name == null ? 0 : context.strlen(name);
        length += className == null ? 0 : context.strlen(className);

        if (hasFilters) {
            length += 1;
            for (final Filter filter : filters) {
                length += filter.prepareToEncode(context);
            }
        }

        if (hasBlend) {
            length += 1;
        }

        if (bitmapCache != null) {
            length += 1;
        }

        if (!events.isEmpty()) {
            length += 6;

            for (final MovieClipEventHandler handler : events) {
                length += handler.prepareToEncode(context);
            }

            length += 6;
        }

        context.remove(Context.TRANSPARENT);

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
        // CHECKSTYLE:ON
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        coder.writeHeader(MovieTypes.PLACE_3, length);
        coder.mark();

        context.put(Context.TRANSPARENT, 1);
        int bits = 0;
        bits |= events.isEmpty() ? 0 : Coder.BIT7;
        bits |= depth == null ? 0 : Coder.BIT6;
        bits |= name == null ? 0 : Coder.BIT5;
        bits |= ratio == null ? 0 : Coder.BIT4;
        bits |= colorTransform == null ? 0 : Coder.BIT3;
        bits |= transform == null ? 0 : Coder.BIT2;

        switch (type) {
        case NEW:
            bits |= 2;
            break;
        case REPLACE:
            bits |= 3;
            break;
        default:
            bits |= 1;
            break;
        }
        coder.writeByte(bits);

        bits = 0;
        bits |= hasImage ? Coder.BIT4 : 0;
        bits |= className == null ? 0 : Coder.BIT3;
        bits |= bitmapCache == null ? 0 : Coder.BIT2;
        bits |= hasBlend ? Coder.BIT1 : 0;
        bits |= hasFilters ? Coder.BIT0 : 0;
        coder.writeByte(bits);
        coder.writeI16(layer);

        if (className != null) {
            coder.writeString(className);
        }
        if ((type == PlaceType.NEW) || (type == PlaceType.REPLACE)) {
            coder.writeI16(identifier);
        }
        if (transform != null) {
            transform.encode(coder, context);
        }
        if (colorTransform != null) {
            colorTransform.encode(coder, context);
        }
        if (ratio != null) {
            coder.writeI16(ratio);
        }
        if (name != null) {
            coder.writeString(name);
        }

        if (depth != null) {
            coder.writeI16(depth);
        }

        if (hasFilters) {
            coder.writeByte(filters.size());
            for (Filter filter : filters) {
                filter.encode(coder, context);
            }
        }

        if (hasBlend) {
            coder.writeByte(blend);
        }

        if (bitmapCache != null) {
            coder.writeByte(bitmapCache);
        }

        if (!events.isEmpty()) {
            int eventMask = 0;

            coder.writeI16(0);

            for (final MovieClipEventHandler handler : events) {
                eventMask |= handler.getEventCode();
            }

            coder.writeI32(eventMask);

            for (final MovieClipEventHandler handler : events) {
                handler.encode(coder, context);
            }

            coder.writeI32(0);
        }
        context.remove(Context.TRANSPARENT);
        coder.unmark(length);
    }
}
