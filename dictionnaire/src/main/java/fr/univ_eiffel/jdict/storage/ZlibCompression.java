package fr.univ_eiffel.jdict.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/** Compression zlib avec les classes Deflater (compression) et Inflater (décompression) du JDK. */
public class ZlibCompression implements Compression {
    @Override
    public String name() {
        return "zlib";
    }

    @Override
    public byte[] compress(byte[] data) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        try {
            deflater.setInput(data);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream(data.length / 2 + 16);
            byte[] buffer = new byte[4096];
            while (!deflater.finished()) {
                int n = deflater.deflate(buffer);
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } finally {
            deflater.end();
        }
    }

    @Override
    public byte[] decompress(byte[] data) throws IOException {
        Inflater inflater = new Inflater();
        try {
            inflater.setInput(data);
            ByteArrayOutputStream out = new ByteArrayOutputStream(data.length * 3);
            byte[] buffer = new byte[4096];
            while (!inflater.finished()) {
                int n = inflater.inflate(buffer);
                if (n == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
                    throw new IOException("entrée compressée incomplète");
                }
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (DataFormatException e) {
            throw new IOException("entrée compressée invalide", e);
        } finally {
            inflater.end();
        }
    }
}
