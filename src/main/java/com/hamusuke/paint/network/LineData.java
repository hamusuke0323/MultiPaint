package com.hamusuke.paint.network;

import com.hamusuke.paint.network.channel.IntelligentByteBuf;

import java.awt.*;
import java.util.Objects;

public class LineData {
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;
    public final Color color;
    public final float width;

    public LineData(int x1, int y1, int x2, int y2, Color color, float width) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.width = width;
    }

    public static LineData unpack(IntelligentByteBuf byteBuf) {
        return new LineData(byteBuf.readVariableInt(), byteBuf.readVariableInt(), byteBuf.readVariableInt(), byteBuf.readVariableInt(), byteBuf.readColor(), byteBuf.readFloat());
    }

    public void pack(IntelligentByteBuf byteBuf) {
        byteBuf.writeVariableInt(this.x1);
        byteBuf.writeVariableInt(this.y1);
        byteBuf.writeVariableInt(this.x2);
        byteBuf.writeVariableInt(this.y2);
        byteBuf.writeColor(this.color);
        byteBuf.writeFloat(this.width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LineData data = (LineData) o;
        return this.x1 == data.x1 && this.y1 == data.y1 && this.x2 == data.x2 && this.y2 == data.y2 && Float.compare(data.width, this.width) == 0 && Objects.equals(this.color, data.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x1, this.y1, this.x2, this.y2, this.color, this.width);
    }
}
