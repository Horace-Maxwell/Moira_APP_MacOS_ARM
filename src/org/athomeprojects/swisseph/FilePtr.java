/*
 * This is a port of the Swiss Ephemeris Free Edition, Version 1.64.01 of
 * Astrodienst AG, Switzerland from the original C Code to Java. For copyright
 * see the original copyright notices below and additional copyright notes in
 * the file named LICENSE, or - if this file is not available - the copyright
 * notes at http://www.astro.ch/org.athomeprojects.swisseph/ and following. For
 * any questions or comments regarding this port to Java, you should ONLY
 * contact me and not Astrodienst, as the Astrodienst AG is not involved in this
 * port in any way. Thomas Mack, mack@idb.cs.tu-bs.de, 23th of April 2001
 */
/*
 * Copyright (C) 1997 - 2000 Astrodienst AG, Switzerland. All rights reserved.
 * This file is part of Swiss Ephemeris Free Edition. Swiss Ephemeris is
 * distributed with NO WARRANTY OF ANY KIND. No author or distributor accepts
 * any responsibility for the consequences of using it, or for whether it serves
 * any particular purpose or works at all, unless he or she says so in writing.
 * Refer to the Swiss Ephemeris Public License ("SEPL" or the "License") for
 * full details. Every copy of Swiss Ephemeris must include a copy of the
 * License, normally in a plain ASCII text file named LICENSE. The License
 * grants you the right to copy, modify and redistribute Swiss Ephemeris, but
 * only under certain conditions described in the License. Among other things,
 * the License requires that the copyright notices and this notice be preserved
 * on all copies. For uses of the Swiss Ephemeris which do not fall under the
 * definitions laid down in the Public License, the Swiss Ephemeris Professional
 * Edition must be purchased by the developer before he/she distributes any of
 * his software or makes available any product or service built upon the use of
 * the Swiss Ephemeris. Authors of the Swiss Ephemeris: Dieter Koch and Alois
 * Treindl The authors of Swiss Ephemeris have no control or influence over any
 * of the derived works, i.e. over software or services created by other
 * programmers which use Swiss Ephemeris functions. The names of the authors or
 * of the copyright holder (Astrodienst) must not be used for promoting any
 * software, product or service which uses or contains the Swiss Ephemeris. This
 * copyright notice is the ONLY place where the names of the authors can legally
 * appear, except in cases where they have given special permission in writing.
 * The trademarks 'Swiss Ephemeris' and 'Swiss Ephemeris inside' may be used for
 * promoting such software, products or services.
 */
package org.athomeprojects.swisseph;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import org.athomeprojects.base.Message;

/**
 * This class is meant to be a wrapper to some read functionality of the
 * RandomAccessFile class with the addition to read (and search and seek) in
 * files specified with a http://... access. This is needed to allow applets
 * read access to files, in this case the Swiss Ephemeris and JPL data files.
 */
// jimmy: modify http support to cache file locally
public class FilePtr {
    static private final int MAX_CACHE = 6;

    private final int MAX_FAILURES = 100;

    private final int DATA_CACHE_SIZE = 4096;

    private final int BUFSIZE = 20;

    static private int sequence = 0;

    private class CacheRecord {
        String name;

        boolean in_used;

        int used_seq;

        int length;

        Hashtable table;

        byte[][] cache;
    }

    static private CacheRecord[] cache_array = new CacheRecord[MAX_CACHE];

    RandomAccessFile fp;

    int cache_index;

    private Hashtable read_table;

    private long fpos = 0;

    // Holds max. 1 BUFSIZE byte chunks of read data: startidx, endidx, data:
    private long[] startIdx = new long[1]; // long, as it holds the file pointer

    // pos.
    private long[] endIdx = new long[1];

    private byte[][] data;

    private byte inbuf[];

    private int idx = 0; // What to fill next.

    private long savedLength = -1;

    /**
     * Creates a new FilePtr instance. Well, the parameters are rather
     * &quot;funny&quot; for now, but there were reasons for it. I will change
     * it later (hopefully)... <br>
     * If you do not need to read randomly and you have access to the file
     * directly, you should use the BufferedInputStream etc. -classes, as they
     * are MUCH faster than the RandomAccessFile class that is used here.
     */
    public FilePtr(RandomAccessFile file_p, String fnamp) throws IOException
    {
        data = new byte[BUFSIZE][1];
        inbuf = new byte[BUFSIZE];
        for (int i = 0; i < data[0].length; i++) {
            startIdx[i] = -1; // Means: no data at this index.
        }
        fp = file_p;
        // look into cache
        for (cache_index = 0; cache_index < MAX_CACHE; cache_index++) {
            if (cache_array[cache_index] != null
                    && cache_array[cache_index].name.equals(fnamp)) {
                cache_array[cache_index].in_used = true;
                read_table = cache_array[cache_index].table;
                return;
            }
        }
        cache_index = -1;
        read_table = null;
        // find unallocated slot
        for (int i = 0; i < MAX_CACHE; i++) {
            if (cache_array[i] == null) {
                cache_array[i] = new CacheRecord();
                cache_index = i;
                break;
            }
        }
        if (cache_index < 0) {
            // use unused slot with lowest sequence
            int best_seq = Integer.MAX_VALUE;
            for (int i = 0; i < MAX_CACHE; i++) {
                if (!cache_array[i].in_used
                        && cache_array[i].used_seq < best_seq) {
                    cache_index = i;
                    best_seq = cache_array[i].used_seq;
                }
            }
        }
        if (cache_index >= 0) {
            cache_array[cache_index].length = 0;
            cache_array[cache_index].cache = null;
            if (fp != null) {
                cache_array[cache_index].name = fnamp;
                cache_array[cache_index].in_used = true;
                cache_array[cache_index].used_seq = ++sequence;
                cache_array[cache_index].table = read_table = new Hashtable();
                return;
            } else {
                cache_array[cache_index].table = null;
                try {
                    URL url = new URL(fnamp);
                    InputStream in = url.openStream();
                    Message.info("Loading data from Internet.  Please wait...");
                    LinkedList head = new LinkedList();
                    byte[] buf = new byte[DATA_CACHE_SIZE];
                    int offset = 0, len = DATA_CACHE_SIZE;
                    for (;;) {
                        int n = in.read(buf, offset, len);
                        if (n == -1) {
                            in.close();
                            if (len < DATA_CACHE_SIZE)
                                head.addLast(buf);
                            else
                                buf = null;
                            cache_array[cache_index].cache = (byte[][]) head
                                    .toArray(new byte[1][]);
                            cache_array[cache_index].name = fnamp;
                            cache_array[cache_index].in_used = true;
                            cache_array[cache_index].table = read_table = new Hashtable();
                            cache_array[cache_index].used_seq = ++sequence;
                            Message.info("");
                            return;
                        }
                        len -= n;
                        offset += n;
                        cache_array[cache_index].length += n;
                        if (len == 0) {
                            head.addLast(buf);
                            buf = new byte[DATA_CACHE_SIZE];
                            offset = 0;
                            len = DATA_CACHE_SIZE;
                        }
                    }
                } catch (IOException e) {
                    cache_array[cache_index].table = null;
                    cache_array[cache_index].cache = null;
                    cache_array[cache_index] = null;
                }
            }
        }
        cache_index = -1;
        Message.info("");
        throw new IOException("Cannot cache data");
    }

    /**
     * Reads one (signed) byte.
     * 
     * @return One signed 8 bit byte.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws EOFException
     *             if the end of file is reached <i>before </i> the byte could
     *             be read.
     */
    public byte readByte() throws IOException, EOFException
    {
        if (startIdx[idx] < 0 || fpos < startIdx[idx] || fpos > endIdx[idx]) {
            readToBuffer();
        }
        fpos++;
        return data[(int) (fpos - 1 - startIdx[idx])][idx];
    }

    /**
     * Reads one <i>unsigned </i> byte.
     * 
     * @return One unsigned 8 bit byte as an integer.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws EOFException
     *             if the end of file is reached <i>before </i> the byte could
     *             be read.
     */
    public int readUnsignedByte() throws IOException, EOFException
    {
        return ((int) readByte()) & 0xff;
    }

    /**
     * Reads a (signed) short value. This is a two byte read (16 bits) with
     * highest byte first.
     * 
     * @return A signed 2 byte (16 bit) value as a short.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws EOFException
     *             if the end of file is reached before the 2 bytes could be
     *             read completely.
     */
    public short readShort() throws IOException, EOFException
    {
        return (short) ((readUnsignedByte() << 8) + readUnsignedByte());
    }

    /**
     * Reads a (signed) integer value. This is a four byte read (32 bits) with
     * highest bytes first.
     * 
     * @return A signed 4 byte (32 bit) value as an integer.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws EOFException
     *             if the end of file is reached before the 4 bytes could be
     *             read completely.
     */
    public int readInt() throws IOException, EOFException
    {
        return (((int) readUnsignedByte()) << 24)
                + (((int) readUnsignedByte()) << 16)
                + (((int) readUnsignedByte()) << 8) + (int) readUnsignedByte();
    }

    /**
     * Reads a double value. This is an 8 byte read (64 bits) with highest bytes
     * first.
     * 
     * @return A double value.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws EOFException
     *             if the end of file is reached before the 8 bytes could be
     *             read completely.
     */
    public double readDouble() throws IOException, EOFException
    {
        long ldb = (((long) readUnsignedByte()) << 56)
                + (((long) readUnsignedByte()) << 48)
                + (((long) readUnsignedByte()) << 40)
                + (((long) readUnsignedByte()) << 32)
                + (((long) readUnsignedByte()) << 24)
                + (((long) readUnsignedByte()) << 16)
                + (((long) readUnsignedByte()) << 8)
                + (long) readUnsignedByte();
        return Double.longBitsToDouble(ldb);
    }

    /**
     * Reads a complete line from the file. The line is seen to end on a newline
     * character ('\n') or on an end of file. The returned String will include
     * the newline character if any.
     * 
     * @return A String containing a complete line. The line may be 0 characters
     *         long.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws EOFException
     *             if the end of file is reached before even one character
     *             (byte) could be read.
     */
    public String readLine() throws IOException, EOFException
    {
        Long l = null;
        String sout;
        if (read_table != null) {
            l = new Long(fpos);
            sout = (String) read_table.get(l);
            if (sout != null) {
                fpos += sout.length();
                return sout;
            }
        }
        sout = "";
        try {
            char ch;
            while ((ch = (char) readUnsignedByte()) != '\n') {
                sout += ch;
            }
            sout += ch;
        } catch (EOFException e) {
            if (sout.length() == 0) {
                throw e;
            }
        }
        if (read_table != null)
            read_table.put(l, sout);
        return sout;
    }

    /**
     * Closes the connection to the file. This includes any Streams as well as
     * the Socket and the RandomAccessFile from the constructor.
     * 
     * @throws IOException
     *             if an I/O error occurs.
     */
    public void close() throws IOException
    {
        if (cache_index >= 0) {
            cache_array[cache_index].in_used = false;
            cache_index = -1;
        }
        if (fp != null) {
            fp.close();
            fp = null;
        }
    }

    /**
     * Returns the current position of the file pointer.
     * 
     * @return the current position of the file pointer.
     */
    public long getFilePointer()
    {
        return fpos;
    }

    /**
     * Returns the length of the file.
     * 
     * @return the length of the file in bytes.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public long length() throws IOException
    {
        if (fp != null && savedLength < 0) {
            savedLength = fp.length();
        }
        if (fp != null || savedLength >= 0) {
            return savedLength;
        }
        return cache_array[cache_index].length;
    }

    /**
     * Positions the file pointer.
     * 
     * @param pos
     *            the new position in the file. The position is seen zero based.
     */
    public void seek(long pos)
    {
        fpos = pos;
    }

    /**
     * Repositions the file pointer by skipping some bytes from the current
     * position.
     * 
     * @param count
     *            How many bytes to skip.
     * @throws EOFException
     *             if the new position would be beyond the end of the file.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public void skipBytes(int count) throws IOException
    {
        if (fpos + count >= length()) {
            throw new EOFException("Filepointer position " + (fpos + count)
                    + " exceeds " + "file length by "
                    + (fpos + count - length() + 1) + " byte(s).");
        }
        fpos += count;
    }

    // Reads a chunk of data to the buffer data[][idx]
    private void readToBuffer() throws IOException, EOFException
    {
        // Directly reading a file:
        if (fp != null) {
            fp.seek(fpos);
            int cnt = fp.read(inbuf);
            // Probably, RandomAccessFile.read(byte[n]) performes n read
            // operations???
            if (cnt == -1) {
                throw new EOFException("Filepointer position " + fpos
                        + " exceeds file" + " length by "
                        + (fpos - length() + 1) + " byte(s).");
            }
            for (int n = 0; n < cnt; n++) {
                data[n][idx] = inbuf[n];
            }
            startIdx[idx] = fpos;
            endIdx[idx] = fpos + cnt - 1;
            return;
        }
        // Reading via http:
        if (fpos >= length()) {
            throw new EOFException("Filepointer position " + fpos
                    + " exceeds file " + "length by " + (fpos - length() + 1)
                    + " byte(s).");
        }
        int cnt = readFromCache(inbuf);
        for (int n = 0; n < cnt; n++) {
            data[n][idx] = inbuf[n];
        }
        startIdx[idx] = fpos;
        endIdx[idx] = fpos + cnt - 1;
    }

    private int readFromCache(byte[] buf)
    {
        int pos = (int) fpos;
        int cnt = 0, buf_offset = 0, buf_len = buf.length;
        do {
            int remain = cache_array[cache_index].length - pos;
            if (remain <= 0)
                break;
            int entry = pos / DATA_CACHE_SIZE;
            int offset = pos - entry * DATA_CACHE_SIZE;
            byte[] d_buf = cache_array[cache_index].cache[entry];
            int len = d_buf.length - offset;
            len = Math.min(len, buf_len);
            len = Math.min(len, remain);
            for (int i = 0; i < len; i++) {
                buf[i + buf_offset] = d_buf[i + offset];
            }
            pos += len;
            buf_offset += len;
            buf_len -= len;
            cnt += len;
        } while (buf_len > 0);
        return cnt;
    }

    static public void printCacheStat()
    {
        int count = 0, n_byte = 0;
        for (int i = 0; i < MAX_CACHE; i++) {
            if (cache_array[i] == null || cache_array[i].table == null)
                continue;
            Hashtable table = cache_array[i].table;
            for (Enumeration e = table.keys(); e.hasMoreElements();) {
                count++;
                String val = (String) table.get(e.nextElement());
                n_byte += val.length();
            }
        }
        System.out.println(Integer.toString(count) + " entries, "
                + Integer.toString(n_byte) + " bytes.");
    }
}