package com.yalantis.ucrop.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ImageHeaderParser {
    private static final String TAG = "ImageHeaderParser";
    public static final int UNKNOWN_ORIENTATION = -1;

    private static final int EXIF_MAGIC_NUMBER = 0xFFD8;
    // "MM".
    private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 0x4D4D;
    // "II".
    private static final int INTEL_TIFF_MAGIC_NUMBER = 0x4949;
    private static final String JPEG_EXIF_SEGMENT_PREAMBLE = "Exif\0\0";
    private static final byte[] JPEG_EXIF_SEGMENT_PREAMBLE_BYTES =
            JPEG_EXIF_SEGMENT_PREAMBLE.getBytes(Charset.forName("UTF-8"));
    private static final int SEGMENT_SOS = 0xDA;
    private static final int MARKER_EOI = 0xD9;
    private static final int SEGMENT_START_ID = 0xFF;
    private static final int EXIF_SEGMENT_TYPE = 0xE1;
    private static final int ORIENTATION_TAG_TYPE = 0x0112;
    private static final int[] BYTES_PER_FORMAT = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    private final Reader reader;

    public ImageHeaderParser(InputStream is) {
        reader = new StreamReader(is);
    }

    public int getOrientation() throws IOException {
        final int magicNumber = reader.getUInt16();

        if (!handles(magicNumber)) {
            return UNKNOWN_ORIENTATION;
        } else {
            int exifSegmentLength = moveToExifSegmentAndGetLength();
            if (exifSegmentLength == -1) {
                return UNKNOWN_ORIENTATION;
            }

            byte[] exifData = new byte[exifSegmentLength];
            return parseExifSegment(exifData, exifSegmentLength);
        }
    }

    private int parseExifSegment(byte[] tempArray, int exifSegmentLength) throws IOException {
        int read = reader.read(tempArray, exifSegmentLength);
        if (read != exifSegmentLength) {
            return UNKNOWN_ORIENTATION;
        }

        boolean hasJpegExifPreamble = hasJpegExifPreamble(tempArray, exifSegmentLength);
        if (hasJpegExifPreamble) {
            return parseExifSegment(new RandomAccessReader(tempArray, exifSegmentLength));
        } else {
            return UNKNOWN_ORIENTATION;
        }
    }

    private boolean hasJpegExifPreamble(byte[] exifData, int exifSegmentLength) {
        boolean result =
                exifData != null && exifSegmentLength > JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length;
        if (result) {
            for (int i = 0; i < JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length; i++) {
                if (exifData[i] != JPEG_EXIF_SEGMENT_PREAMBLE_BYTES[i]) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    private int moveToExifSegmentAndGetLength() throws IOException {
        short segmentId, segmentType;
        int segmentLength;
        while (true) {
            segmentId = reader.getUInt8();
            if (segmentId != SEGMENT_START_ID) {
                return -1;
            }

            segmentType = reader.getUInt8();

            if (segmentType == SEGMENT_SOS) {
                return -1;
            } else if (segmentType == MARKER_EOI) {
                return -1;
            }

            // Segment length includes bytes for segment length.
            segmentLength = reader.getUInt16() - 2;

            if (segmentType != EXIF_SEGMENT_TYPE) {
                long skipped = reader.skip(segmentLength);
                if (skipped != segmentLength) {
                    return -1;
                }
            } else {
                return segmentLength;
            }
        }
    }

    private static int parseExifSegment(RandomAccessReader segmentData) {
        final int headerOffsetSize = JPEG_EXIF_SEGMENT_PREAMBLE.length();

        short byteOrderIdentifier = segmentData.getInt16(headerOffsetSize);
        final ByteOrder byteOrder;
        if (byteOrderIdentifier == MOTOROLA_TIFF_MAGIC_NUMBER) {
            byteOrder = ByteOrder.BIG_ENDIAN;
        } else if (byteOrderIdentifier == INTEL_TIFF_MAGIC_NUMBER) {
            byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            byteOrder = ByteOrder.BIG_ENDIAN;
        }

        segmentData.order(byteOrder);

        int firstIfdOffset = segmentData.getInt32(headerOffsetSize + 4) + headerOffsetSize;
        int tagCount = segmentData.getInt16(firstIfdOffset);

        int tagOffset, tagType, formatCode, componentCount;
        for (int i = 0; i < tagCount; i++) {
            tagOffset = calcTagOffset(firstIfdOffset, i);
            tagType = segmentData.getInt16(tagOffset);

            // We only want orientation.
            if (tagType != ORIENTATION_TAG_TYPE) {
                continue;
            }

            formatCode = segmentData.getInt16(tagOffset + 2);

            // 12 is max format code.
            if (formatCode < 1 || formatCode > 12) {
                continue;
            }

            componentCount = segmentData.getInt32(tagOffset + 4);

            if (componentCount < 0) {
                continue;
            }

            final int byteCount = componentCount + BYTES_PER_FORMAT[formatCode];

            if (byteCount > 4) {
                continue;
            }

            final int tagValueOffset = tagOffset + 8;

            if (tagValueOffset < 0 || tagValueOffset > segmentData.length()) {
                continue;
            }

            if (byteCount < 0 || tagValueOffset + byteCount > segmentData.length()) {
                continue;
            }

            //assume componentCount == 1 && fmtCode == 3
            return segmentData.getInt16(tagValueOffset);
        }

        return -1;
    }

    private static int calcTagOffset(int ifdOffset, int tagIndex) {
        return ifdOffset + 2 + 12 * tagIndex;
    }

    private static boolean handles(int imageMagicNumber) {
        return (imageMagicNumber & EXIF_MAGIC_NUMBER) == EXIF_MAGIC_NUMBER
                || imageMagicNumber == MOTOROLA_TIFF_MAGIC_NUMBER
                || imageMagicNumber == INTEL_TIFF_MAGIC_NUMBER;
    }

    private static class RandomAccessReader {
        private final ByteBuffer data;

        public RandomAccessReader(byte[] data, int length) {
            this.data = (ByteBuffer) ByteBuffer.wrap(data)
                    .order(ByteOrder.BIG_ENDIAN)
                    .limit(length);
        }

        public void order(ByteOrder byteOrder) {
            this.data.order(byteOrder);
        }

        public int length() {
            return data.remaining();
        }

        public int getInt32(int offset) {
            return data.getInt(offset);
        }

        public short getInt16(int offset) {
            return data.getShort(offset);
        }
    }

    private interface Reader {
        int getUInt16() throws IOException;

        short getUInt8() throws IOException;

        long skip(long total) throws IOException;

        int read(byte[] buffer, int byteCount) throws IOException;
    }

    private static class StreamReader implements Reader {
        private final InputStream is;

        // Motorola / big endian byte order.
        public StreamReader(InputStream is) {
            this.is = is;
        }

        @Override
        public int getUInt16() throws IOException {
            return (is.read() << 8 & 0xFF00) | (is.read() & 0xFF);
        }

        @Override
        public short getUInt8() throws IOException {
            return (short) (is.read() & 0xFF);
        }

        @Override
        public long skip(long total) throws IOException {
            if (total < 0) {
                return 0;
            }

            long toSkip = total;
            while (toSkip > 0) {
                long skipped = is.skip(toSkip);
                if (skipped > 0) {
                    toSkip -= skipped;
                } else {
                    // Skip has no specific contract as to what happens when you reach the end of
                    // the stream. To differentiate between temporarily not having more data and
                    // having finished the stream, we read a single byte when we fail to skip any
                    // amount of data.
                    int testEofByte = is.read();
                    if (testEofByte == -1) {
                        break;
                    } else {
                        toSkip--;
                    }
                }
            }
            return total - toSkip;
        }

        @Override
        public int read(byte[] buffer, int byteCount) throws IOException {
            int toRead = byteCount;
            int read;
            while (toRead > 0 && ((read = is.read(buffer, byteCount - toRead, toRead)) != -1)) {
                toRead -= read;
            }
            return byteCount - toRead;
        }
    }

    public static void copyExif(ExifInterface originalExif, int width, int height, String imageOutputPath) {

        try {
            ExifInterface newExif = new ExifInterface(imageOutputPath);

            copyExifAttributes(originalExif, newExif, width, height);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyExif(Context context, int width, int height, Uri imageInputUri, String imageOutputPath) {
        if(context == null) {
            return;
        }

        InputStream ins = null;
        try  {
            ins = context.getContentResolver().openInputStream(imageInputUri);
            ExifInterface originalExif = new ExifInterface(ins);

            ExifInterface newExif = new ExifInterface(imageOutputPath);

            copyExifAttributes(originalExif, newExif, width, height);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void copyExif(Context context, int width, int height, Uri imageInputUri, Uri imageOutputUri) {
        if(context == null) {
            return;
        }

        InputStream ins = null;
        ParcelFileDescriptor outFd = null;
        try  {
            ins = context.getContentResolver().openInputStream(imageInputUri);
            ExifInterface originalExif = new ExifInterface(ins);

            outFd = context.getContentResolver().openFileDescriptor(imageOutputUri, "rw");
            ExifInterface newExif = new ExifInterface(outFd.getFileDescriptor());
            copyExifAttributes(originalExif, newExif, width, height);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outFd != null) {
                try {
                    outFd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void copyExif(Context context, ExifInterface originalExif, int width, int height, Uri imageOutputUri) {

        if(context == null) {
            return;
        }

        ParcelFileDescriptor outFd = null;
        try {

            // In order to the ExifInterface be able to validate JPEG info from the file, the FileDescriptor must to be
            // opened en "rw" (read and write) mode
            outFd = context.getContentResolver().openFileDescriptor(imageOutputUri, "rw");
            ExifInterface newExif = new ExifInterface(outFd.getFileDescriptor());

            copyExifAttributes(originalExif, newExif, width, height);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outFd != null) {
                try {
                    outFd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyExifAttributes(ExifInterface originalExif, ExifInterface newExif, int width, int height) throws IOException {

        String[] attributes = new String[]{
                ExifInterface.TAG_F_NUMBER,
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_DATETIME_DIGITIZED,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_FLASH,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_PROCESSING_METHOD,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_SUBSEC_TIME,
                ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
                ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
                ExifInterface.TAG_WHITE_BALANCE
        };

        String value;
        for (String attribute : attributes) {
            value = originalExif.getAttribute(attribute);
            if (!TextUtils.isEmpty(value)) {
                newExif.setAttribute(attribute, value);
            }
        }
        newExif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, String.valueOf(width));
        newExif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, String.valueOf(height));
        newExif.setAttribute(ExifInterface.TAG_ORIENTATION, "0");

        newExif.saveAttributes();
    }

}

