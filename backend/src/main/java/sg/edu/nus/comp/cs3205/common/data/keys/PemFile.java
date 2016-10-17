package sg.edu.nus.comp.cs3205.common.data.keys;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Key;

// https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
public class PemFile {

    private PemObject pemObject;

    public PemFile (Key key, String description) {
        this.pemObject = new PemObject(description, key.getEncoded());
    }

    public void write(String filename) throws IOException {
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(filename)))) {
            pemWriter.writeObject(this.pemObject);
        }
    }
}
