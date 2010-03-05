package com.flagstone.transform.coder;

import com.flagstone.transform.shape.Curve;
import com.flagstone.transform.shape.Line;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.shape.ShapeStyle;

/**
 * Factory is the default implementation of an SWFFactory which used to create
 * instances of Transform classes.
 */
//TODO(class)
public final class ShapeDecoder implements SWFFactory<ShapeRecord> {

    /** TODO(method). */
    public SWFFactory<ShapeRecord> copy() {
        return new ShapeDecoder();
    }

    /** TODO(method). */
    public ShapeRecord getObject(final SWFDecoder coder, final Context context)
            throws CoderException {

        ShapeRecord record = null;

        final int type = coder.readBits(6, false);

        if (type != 0) {
            coder.adjustPointer(-6);

            if ((type & 0x20) > 0) {
                if ((type & 0x10) > 0) {
                    record = new Line(coder);
                } else {
                    record = new Curve(coder);
                }
            } else {
                record = new ShapeStyle(coder, context);
            }
        }

        return record;
    }
}
