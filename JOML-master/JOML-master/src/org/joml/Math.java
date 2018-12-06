/*
 * (C) Copyright 2015-2018 JOML

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml;

import org.joml.internal.Options;

/**
 * Contains fast approximations of some {@link java.lang.Math} operations.
 * <p>
 * By default, {@link java.lang.Math} methods will be used by all other JOML classes. In order to use the approximations in this class, start the JVM with the parameter <code>-Djoml.fastmath</code>.
 * <p>
 * There are two algorithms for approximating sin/cos:
 * <ol>
 * <li>arithmetic <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815">polynomial approximation</a> contributed by roquendm 
 * <li>theagentd's <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/346213/view.html#msg346213">linear interpolation</a> variant of Riven's algorithm from
 * <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/view.html">http://www.java-gaming.org/</a>
 * </ol>
 * By default, the first algorithm is being used. In order to use the second one, start the JVM with <code>-Djoml.sinLookup</code>. The lookup table bit length of the second algorithm can also be adjusted
 * for improved accuracy via <code>-Djoml.sinLookup.bits=&lt;n&gt;</code>, where &lt;n&gt; is the number of bits of the lookup table.
 * 
 * @author Kai Burjack
 */
public class Math {

    /*
     * The following implementation of an approximation of sine and cosine was
     * thankfully donated by Riven from http://java-gaming.org/.
     * 
     * The code for linear interpolation was gratefully donated by theagentd
     * from the same site.
     */
    public static final double PI = java.lang.Math.PI;
    static final double PI2 = PI * 2.0;
    static final double PIHalf = PI * 0.5;
    static final double PI_4 = PI * 0.25;
    static final double PI_INV = 1.0 / PI;
    private static final int lookupBits = Options.SIN_LOOKUP_BITS;
    private static final int lookupTableSize = 1 << lookupBits;
    private static final int lookupTableSizeMinus1 = lookupTableSize - 1;
    private static final int lookupTableSizeWithMargin = lookupTableSize + 1;
    private static final double pi2OverLookupSize = PI2 / lookupTableSize;
    private static final double lookupSizeOverPi2 = lookupTableSize / PI2;
    private static final float sinTable[];
    static {
        if (Options.FASTMATH && Options.SIN_LOOKUP) {
            sinTable = new float[lookupTableSizeWithMargin];
            for (int i = 0; i < lookupTableSizeWithMargin; i++) {
                double d = i * pi2OverLookupSize;
                sinTable[i] = (float) java.lang.Math.sin(d);
            }
        } else {
            sinTable = null;
        }
    }

    private static final double c1 = Double.longBitsToDouble(-4628199217061079772L);
    private static final double c2 = Double.longBitsToDouble(4575957461383582011L);
    private static final double c3 = Double.longBitsToDouble(-4671919876300759001L);
    private static final double c4 = Double.longBitsToDouble(4523617214285661942L);
    private static final double c5 = Double.longBitsToDouble(-4730215272828025532L);
    private static final double c6 = Double.longBitsToDouble(4460272573143870633L);
    private static final double c7 = Double.longBitsToDouble(-4797767418267846529L);

    /**
     * @author theagentd
     */
    static double sin_theagentd_arith(double x){
        double xi = floor((x + PI_4) * PI_INV);
        double x_ = x - xi * PI;
        double sign = ((int)xi & 1) * -2 + 1;
        double x2 = x_ * x_;
        double sin = x_;
        double tx = x_ * x2;
        sin += tx * c1; tx *= x2;
        sin += tx * c2; tx *= x2;
        sin += tx * c3; tx *= x2;
        sin += tx * c4; tx *= x2;
        sin += tx * c5; tx *= x2;
        sin += tx * c6; tx *= x2;
        sin += tx * c7;
        return sign * sin;
    }

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361718/view.html#msg361718">http://www.java-gaming.org/</a>
     */
    static double sin_roquen_arith(double x) {
        double xi = Math.floor((x + PI_4) * PI_INV);
        double x_ = x - xi * PI;
        double sign = ((int)xi & 1) * -2 + 1;
        double x2 = x_ * x_;

        // code from sin_theagentd_arith:
        // double sin = x_;
        // double tx = x_ * x2;
        // sin += tx * c1; tx *= x2;
        // sin += tx * c2; tx *= x2;
        // sin += tx * c3; tx *= x2;
        // sin += tx * c4; tx *= x2;
        // sin += tx * c5; tx *= x2;
        // sin += tx * c6; tx *= x2;
        // sin += tx * c7;
        // return sign * sin;

        double sin;
        x_  = sign*x_;
        sin =          c7;
        sin = sin*x2 + c6;
        sin = sin*x2 + c5;
        sin = sin*x2 + c4;
        sin = sin*x2 + c3;
        sin = sin*x2 + c2;
        sin = sin*x2 + c1;
        return x_ + x_*x2*sin;
    }

    private static final double s5 = Double.longBitsToDouble(4523227044276562163L);
    private static final double s4 = Double.longBitsToDouble(-4671934770969572232L);
    private static final double s3 = Double.longBitsToDouble(4575957211482072852L);
    private static final double s2 = Double.longBitsToDouble(-4628199223918090387L);
    private static final double s1 = Double.longBitsToDouble(4607182418589157889L);

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815">http://www.java-gaming.org/</a>
     */
    static double sin_roquen_9(double v) {
      double i  = java.lang.Math.rint(v*PI_INV);
      double x  = v - i * Math.PI;
      double qs = 1-2*((int)i & 1);
      double x2 = x*x;
      double r;
      x = qs*x;
      r =        s5;
      r = r*x2 + s4;
      r = r*x2 + s3;
      r = r*x2 + s2;
      r = r*x2 + s1;
      return x*r;
    }

    private static final double k1 = Double.longBitsToDouble(-4628199217061079959L);
    private static final double k2 = Double.longBitsToDouble(4575957461383549981L);
    private static final double k3 = Double.longBitsToDouble(-4671919876307284301L);
    private static final double k4 = Double.longBitsToDouble(4523617213632129738L);
    private static final double k5 = Double.longBitsToDouble(-4730215344060517252L);
    private static final double k6 = Double.longBitsToDouble(4460268259291226124L);
    private static final double k7 = Double.longBitsToDouble(-4798040743777455072L);

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815">http://www.java-gaming.org/</a>
     */
    static double sin_roquen_newk(double v) {
      double i  = java.lang.Math.rint(v*PI_INV);
      double x  = v - i * Math.PI;
      double qs = 1-2*((int)i & 1);
      double x2 = x*x;
      double r;
      x = qs*x;
      r =        k7;
      r = r*x2 + k6;
      r = r*x2 + k5;
      r = r*x2 + k4;
      r = r*x2 + k3;
      r = r*x2 + k2;
      r = r*x2 + k1;
      return x + x*x2*r;
    }

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/349515/view.html#msg349515">http://www.java-gaming.org/</a>
     */
    static double sin_theagentd_lookup(double rad) {
        float index = (float) (rad * lookupSizeOverPi2);
        int ii = (int)java.lang.Math.floor(index);
        float alpha = index - ii;
        int i = ii & lookupTableSizeMinus1;
        float sin1 = sinTable[i];
        float sin2 = sinTable[i + 1];
        return sin1 + (sin2 - sin1) * alpha;
    }

    public static double sin(double rad) {
        if (Options.FASTMATH) {
            if (Options.SIN_LOOKUP)
                return sin_theagentd_lookup(rad);
            return sin_roquen_newk(rad);
        }
        return java.lang.Math.sin(rad);
    }

    public static double cos(double rad) {
        if (Options.FASTMATH)
            return sin(rad + PIHalf);
        return java.lang.Math.cos(rad);
    }

    public static double cosFromSin(double sin, double angle) {
        if (Options.FASTMATH)
            return sin(angle + PIHalf);
        // sin(x)^2 + cos(x)^2 = 1
        double cos = sqrt(1.0 - sin * sin);
        double a = angle + PIHalf;
        double b = a - (int)(a / PI2) * PI2;
        if (b < 0.0)
            b = PI2 + b;
        if (b >= PI)
            return -cos;
        return cos;
    }

    /* Other math functions not yet approximated */

    public static double sqrt(double r) {
        return java.lang.Math.sqrt(r);
    }

    public static double tan(double r) {
        return java.lang.Math.tan(r);
    }

    public static double acos(double r) {
        return java.lang.Math.acos(r);
    }

    public static double atan2(double y, double x) {
        return java.lang.Math.atan2(y, x);
    }

    public static double asin(double r) {
        return java.lang.Math.asin(r);
    }

    public static double abs(double r) {
        return java.lang.Math.abs(r);
    }

    public static float abs(float r) {
        return java.lang.Math.abs(r);
    }

    public static int abs(int r) {
        return java.lang.Math.abs(r);
    }

    public static int max(int x, int y) {
        return java.lang.Math.max(x, y);
    }

    public static int min(int x, int y) {
        return java.lang.Math.min(x, y);
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static double min(double a, double b) {
        return a < b ? a : b;
    }

    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    public static double toRadians(double angles) {
        return java.lang.Math.toRadians(angles);
    }

    public static double toDegrees(double angles) {
        return java.lang.Math.toDegrees(angles);
    }

    public static double floor(double v) {
        return java.lang.Math.floor(v);
    }

    public static float floor(float v) {
        return (float) java.lang.Math.floor(v);
    }

    public static double ceil(double v) {
        return java.lang.Math.ceil(v);
    }

    public static float ceil(float v) {
        return (float) java.lang.Math.ceil(v);
    }

    public static long round(double v) {
        return java.lang.Math.round(v);
    }

    public static int round(float v) {
        return java.lang.Math.round(v);
    }

    public static double exp(double a) {
    	return java.lang.Math.exp(a);
    }

    public static boolean isFinite(double d) {
        return abs(d) <= Double.MAX_VALUE;
    }

    public static boolean isFinite(float f) {
        return abs(f) <= Float.MAX_VALUE;
    }
}