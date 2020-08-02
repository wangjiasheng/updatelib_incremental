package com.nothome.delta;

import static com.nothome.delta.GDiffWriter.COPY_INT_INT;
import static com.nothome.delta.GDiffWriter.COPY_INT_UBYTE;
import static com.nothome.delta.GDiffWriter.COPY_INT_USHORT;
import static com.nothome.delta.GDiffWriter.COPY_LONG_INT;
import static com.nothome.delta.GDiffWriter.COPY_USHORT_INT;
import static com.nothome.delta.GDiffWriter.COPY_USHORT_UBYTE;
import static com.nothome.delta.GDiffWriter.COPY_USHORT_USHORT;
import static com.nothome.delta.GDiffWriter.DATA_INT;
import static com.nothome.delta.GDiffWriter.DATA_MAX;
import static com.nothome.delta.GDiffWriter.DATA_USHORT;
import static com.nothome.delta.GDiffWriter.EOF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
public class GDiffPatcher {
    
    private ByteBuffer buf = ByteBuffer.allocate(1024);
    private byte buf2[] = buf.array();
    public GDiffPatcher() {
    }
    public void patch(File sourceFile, File patchFile, File outputFile)
		throws IOException
	{
        RandomAccessFileSeekableSource source =new RandomAccessFileSeekableSource(new RandomAccessFile(sourceFile, "r")); 
        InputStream patch = new FileInputStream(patchFile);
        OutputStream output = new FileOutputStream(outputFile);
        try {
            patch(source, patch, output);
        } catch (IOException e) {
            throw e;
        } finally {
            source.close();
            patch.close();
            output.close();
        }
    }
    public void patch(byte[] source, InputStream patch, OutputStream output) throws IOException {
        patch(new ByteBufferSeekableSource(source), patch, output);
    }

    public byte[] patch(byte[] source, byte[] patch) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        patch(source, new ByteArrayInputStream(patch), os);
        return os.toByteArray();
    }


    public void patch(SeekableSource source, InputStream patch, OutputStream out) throws IOException {
        
        DataOutputStream outOS = new DataOutputStream(out);
        DataInputStream patchIS = new DataInputStream(patch);

        if (patchIS.readUnsignedByte() != 0xd1 ||
                patchIS.readUnsignedByte() != 0xff ||
                patchIS.readUnsignedByte() != 0xd1 ||
                patchIS.readUnsignedByte() != 0xff ||
                patchIS.readUnsignedByte() != 0x04) {

            throw new PatchException("magic string not found, aborting!");
        }

        while (true) {
            int command = patchIS.readUnsignedByte();
            if (command == EOF)
                break;
            int length;
            int offset;
            
            if (command <= DATA_MAX) {
                append(command, patchIS, outOS);
                continue;
            }
            
            switch (command) {
            case DATA_USHORT:
                length = patchIS.readUnsignedShort();
                append(length, patchIS, outOS);
                break;
            case DATA_INT:
                length = patchIS.readInt();
                append(length, patchIS, outOS);
                break;
            case COPY_USHORT_UBYTE:
                offset = patchIS.readUnsignedShort();
                length = patchIS.readUnsignedByte();
                copy(offset, length, source, outOS);
                break;
            case COPY_USHORT_USHORT:
                offset = patchIS.readUnsignedShort();
                length = patchIS.readUnsignedShort();
                copy(offset, length, source, outOS);
                break;
            case COPY_USHORT_INT:
                offset = patchIS.readUnsignedShort();
                length = patchIS.readInt();
                copy(offset, length, source, outOS);
                break;
            case COPY_INT_UBYTE:
                offset = patchIS.readInt();
                length = patchIS.readUnsignedByte();
                copy(offset, length, source, outOS);
                break;
            case COPY_INT_USHORT:
                offset = patchIS.readInt();
                length = patchIS.readUnsignedShort();
                copy(offset, length, source, outOS);
                break;
            case COPY_INT_INT:
                offset = patchIS.readInt();
                length = patchIS.readInt();
                copy(offset, length, source, outOS);
                break;
            case COPY_LONG_INT:
                long loffset = patchIS.readLong();
                length = patchIS.readInt();
                copy(loffset, length, source, outOS);
                break;
            default: 
                throw new IllegalStateException("command " + command);
            }
        }
		outOS.flush();
    }

    private void copy(long offset, int length, SeekableSource source, OutputStream output)
		throws IOException
	{
        source.seek(offset);
        while (length > 0) {
            int len = Math.min(buf.capacity(), length);
            buf.clear().limit(len);
            int res = source.read(buf);
            if (res == -1)
                throw new EOFException("in copy " + offset + " " + length);
            output.write(buf.array(), 0, res);
            length -= res;
        }
    }

    private void append(int length, InputStream patch, OutputStream output) throws IOException {
        while (length > 0) {
            int len = Math.min(buf2.length, length);
    	    int res = patch.read(buf2, 0, len);
    	    if (res == -1)
    	        throw new EOFException("cannot read " + length);
            output.write(buf2, 0, res);
            length -= res;
        }
    }
    public static void main(String argv[]) {

        if (argv.length != 3) {
            System.err.println("usage GDiffPatch source patch output");
            System.err.println("aborting..");
            return;
        }
        try {
            File sourceFile = new File(argv[0]);
            File patchFile = new File(argv[1]);
            File outputFile = new File(argv[2]);

            if (sourceFile.length() > Integer.MAX_VALUE ||
            patchFile.length() > Integer.MAX_VALUE) {
                System.err.println("source or patch is too large, max length is " + Integer.MAX_VALUE);
                System.err.println("aborting..");
                return;
            }
            GDiffPatcher patcher = new GDiffPatcher();
            patcher.patch(sourceFile, patchFile, outputFile);

            System.out.println("finished patching file");

        } catch (Exception ioe) {
            System.err.println("error while patching: " + ioe);
        }
    }
}

