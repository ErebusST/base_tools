/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * The type Base 64.
 *
 * @author 司徒彬
 * @date 2020 /7/17 10:55
 */
public class Base64 {
    private static final char[] S_BASE64CHAR = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final char S_BASE64PAD = '=';
    private static final byte[] S_DECODETABLE = new byte[128];

    /**
     * Instantiates a new Base 64.
     */
    public Base64() {
    }

    private static int decode0(char[] ibuf, byte[] obuf, int wp) {
        int outlen = 3;
        if (ibuf[3] == '=') {
            outlen = 2;
        }

        if (ibuf[2] == '=') {
            outlen = 1;
        }

        int b0 = S_DECODETABLE[ibuf[0]];
        int b1 = S_DECODETABLE[ibuf[1]];
        int b2 = S_DECODETABLE[ibuf[2]];
        int b3 = S_DECODETABLE[ibuf[3]];
        switch(outlen) {
            case 1:
                obuf[wp] = (byte)(b0 << 2 & 252 | b1 >> 4 & 3);
                return 1;
            case 2:
                obuf[wp++] = (byte)(b0 << 2 & 252 | b1 >> 4 & 3);
                obuf[wp] = (byte)(b1 << 4 & 240 | b2 >> 2 & 15);
                return 2;
            case 3:
                obuf[wp++] = (byte)(b0 << 2 & 252 | b1 >> 4 & 3);
                obuf[wp++] = (byte)(b1 << 4 & 240 | b2 >> 2 & 15);
                obuf[wp] = (byte)(b2 << 6 & 192 | b3 & 63);
                return 3;
            default:
                throw new RuntimeException("Couldn't decode.");
        }
    }

    /**
     * Decode byte [ ].
     *
     * @param data the data
     * @param off  the off
     * @param len  the len
     * @return the byte [ ]
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static byte[] decode(char[] data, int off, int len) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[len / 4 * 3 + 3];
        int obufcount = 0;

        for(int i = off; i < off + len; ++i) {
            char ch = data[i];
            if (ch == '=' || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != 127) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    obufcount += decode0(ibuf, obuf, obufcount);
                }
            }
        }

        if (obufcount == obuf.length) {
            return obuf;
        } else {
            byte[] ret = new byte[obufcount];
            System.arraycopy(obuf, 0, ret, 0, obufcount);
            return ret;
        }
    }

    /**
     * Decode byte [ ].
     *
     * @param data the data
     * @return the byte [ ]
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static byte[] decode(String data) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[data.length() / 4 * 3 + 3];
        int obufcount = 0;

        for(int i = 0; i < data.length(); ++i) {
            char ch = data.charAt(i);
            if (ch == '=' || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != 127) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    obufcount += decode0(ibuf, obuf, obufcount);
                }
            }
        }

        if (obufcount == obuf.length) {
            return obuf;
        } else {
            byte[] ret = new byte[obufcount];
            System.arraycopy(obuf, 0, ret, 0, obufcount);
            return ret;
        }
    }

    /**
     * Decode .
     *
     * @param data    the data
     * @param off     the off
     * @param len     the len
     * @param ostream the ostream
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static void decode(char[] data, int off, int len, OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];

        for(int i = off; i < off + len; ++i) {
            char ch = data[i];
            if (ch == '=' || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != 127) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }

    }

    /**
     * Decode .
     *
     * @param data    the data
     * @param ostream the ostream
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static void decode(String data, OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];

        for(int i = 0; i < data.length(); ++i) {
            char ch = data.charAt(i);
            if (ch == '=' || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != 127) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }

    }

    /**
     * Encode string.
     *
     * @param data the data
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static String encode(byte[] data) {
        return encode(data, 0, data.length);
    }

    /**
     * Encode string.
     *
     * @param data the data
     * @param off  the off
     * @param len  the len
     * @return the string
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static String encode(byte[] data, int off, int len) {
        if (len <= 0) {
            return "";
        } else {
            char[] out = new char[len / 3 * 4 + 4];
            int rindex = off;
            int windex = 0;

            int rest;
            int i;
            for(rest = len - off; rest >= 3; rest -= 3) {
                i = ((data[rindex] & 255) << 16) + ((data[rindex + 1] & 255) << 8) + (data[rindex + 2] & 255);
                out[windex++] = S_BASE64CHAR[i >> 18];
                out[windex++] = S_BASE64CHAR[i >> 12 & 63];
                out[windex++] = S_BASE64CHAR[i >> 6 & 63];
                out[windex++] = S_BASE64CHAR[i & 63];
                rindex += 3;
            }

            if (rest == 1) {
                i = data[rindex] & 255;
                out[windex++] = S_BASE64CHAR[i >> 2];
                out[windex++] = S_BASE64CHAR[i << 4 & 63];
                out[windex++] = '=';
                out[windex++] = '=';
            } else if (rest == 2) {
                i = ((data[rindex] & 255) << 8) + (data[rindex + 1] & 255);
                out[windex++] = S_BASE64CHAR[i >> 10];
                out[windex++] = S_BASE64CHAR[i >> 4 & 63];
                out[windex++] = S_BASE64CHAR[i << 2 & 63];
                out[windex++] = '=';
            }

            return new String(out, 0, windex);
        }
    }

    /**
     * Encode .
     *
     * @param data    the data
     * @param off     the off
     * @param len     the len
     * @param ostream the ostream
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static void encode(byte[] data, int off, int len, OutputStream ostream) throws IOException {
        if (len > 0) {
            byte[] out = new byte[4];
            int rindex = off;

            int rest;
            int i;
            for(rest = len - off; rest >= 3; rest -= 3) {
                i = ((data[rindex] & 255) << 16) + ((data[rindex + 1] & 255) << 8) + (data[rindex + 2] & 255);
                out[0] = (byte)S_BASE64CHAR[i >> 18];
                out[1] = (byte)S_BASE64CHAR[i >> 12 & 63];
                out[2] = (byte)S_BASE64CHAR[i >> 6 & 63];
                out[3] = (byte)S_BASE64CHAR[i & 63];
                ostream.write(out, 0, 4);
                rindex += 3;
            }

            if (rest == 1) {
                i = data[rindex] & 255;
                out[0] = (byte)S_BASE64CHAR[i >> 2];
                out[1] = (byte)S_BASE64CHAR[i << 4 & 63];
                out[2] = 61;
                out[3] = 61;
                ostream.write(out, 0, 4);
            } else if (rest == 2) {
                i = ((data[rindex] & 255) << 8) + (data[rindex + 1] & 255);
                out[0] = (byte)S_BASE64CHAR[i >> 10];
                out[1] = (byte)S_BASE64CHAR[i >> 4 & 63];
                out[2] = (byte)S_BASE64CHAR[i << 2 & 63];
                out[3] = 61;
                ostream.write(out, 0, 4);
            }

        }
    }

    /**
     * Encode .
     *
     * @param data   the data
     * @param off    the off
     * @param len    the len
     * @param writer the writer
     * @throws IOException the io exception
     * @author ErebusST
     * @since 2022 -01-07 15:35:58
     */
    public static void encode(byte[] data, int off, int len, Writer writer) throws IOException {
        if (len > 0) {
            char[] out = new char[4];
            int rindex = off;
            int rest = len - off;
            int output = 0;

            int i;
            while(rest >= 3) {
                i = ((data[rindex] & 255) << 16) + ((data[rindex + 1] & 255) << 8) + (data[rindex + 2] & 255);
                out[0] = S_BASE64CHAR[i >> 18];
                out[1] = S_BASE64CHAR[i >> 12 & 63];
                out[2] = S_BASE64CHAR[i >> 6 & 63];
                out[3] = S_BASE64CHAR[i & 63];
                writer.write(out, 0, 4);
                rindex += 3;
                rest -= 3;
                output += 4;
                if (output % 76 == 0) {
                    writer.write("\n");
                }
            }

            if (rest == 1) {
                i = data[rindex] & 255;
                out[0] = S_BASE64CHAR[i >> 2];
                out[1] = S_BASE64CHAR[i << 4 & 63];
                out[2] = '=';
                out[3] = '=';
                writer.write(out, 0, 4);
            } else if (rest == 2) {
                i = ((data[rindex] & 255) << 8) + (data[rindex + 1] & 255);
                out[0] = S_BASE64CHAR[i >> 10];
                out[1] = S_BASE64CHAR[i >> 4 & 63];
                out[2] = S_BASE64CHAR[i << 2 & 63];
                out[3] = '=';
                writer.write(out, 0, 4);
            }

        }
    }

    static {
        int i;
        for(i = 0; i < S_DECODETABLE.length; ++i) {
            S_DECODETABLE[i] = 127;
        }

        for(i = 0; i < S_BASE64CHAR.length; ++i) {
            S_DECODETABLE[S_BASE64CHAR[i]] = (byte)i;
        }

    }
}
