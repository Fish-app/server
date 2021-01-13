package no.maoyi.app.user.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class KeyManager {

	private static final String KEYPAIR_FILENAME = "jwtkeys.ser";

	private KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);

	private boolean keyPairFileExist() {
		return Files.exists(Paths.get(KEYPAIR_FILENAME));
	}

	private KeyPair createKeyPair() {
		return Keys.keyPairFor(SignatureAlgorithm.RS256);
	}

	private KeyPair readKeyPair() {
		KeyPair result = null;

		try (FileInputStream fis = new FileInputStream(KEYPAIR_FILENAME);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(bis)) {
			result = (KeyPair) ois.readObject();
		} catch (IOException | ClassNotFoundException ex) {
		}

		return result != null ? result : createKeyPair();
	}

	private void writeKeyPair(KeyPair keyPair) {
		try (FileOutputStream fos = new FileOutputStream(KEYPAIR_FILENAME);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(keyPair);
		} catch (IOException ex) {
		}
	}

	private void evaluateKeyFile() {
		if (keyPairFileExist()) {
			keyPair = readKeyPair();
		} else {
			keyPair = createKeyPair();
			writeKeyPair(keyPair);
		}
	}

	public PublicKey getPublicKey() {
		return this.keyPair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.keyPair.getPrivate();
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public KeyManager() {
		evaluateKeyFile();
	}
}