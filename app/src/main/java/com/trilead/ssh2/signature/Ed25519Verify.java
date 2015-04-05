/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2015 Kenny Root
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trilead.ssh2.signature;

import java.io.IOException;

import com.trilead.ssh2.crypto.key.Ed25519PrivateKey;
import com.trilead.ssh2.crypto.key.Ed25519PublicKey;
import com.trilead.ssh2.log.Logger;
import com.trilead.ssh2.packets.TypesReader;
import com.trilead.ssh2.packets.TypesWriter;

/**
 * @author Kenny Root
 */
public class Ed25519Verify {
	private static final Logger log = Logger.getLogger(Ed25519Verify.class);

	/** Identifies this as an Ed25519 key in the protocol. */
	public static final String ED25519_ID = "ssh-ed25519";

	private static final int ED25519_PK_SIZE_BYTES = 32;
	private static final int ED25519_SK_SIZE_BYTES = 64;
	private static final int ED25519_SIG_SIZE_BYTES = 64;

	static {
		// Poorly named!
		System.loadLibrary("com_google_ase_Exec");
	}

	public static native byte[] crypto_sign_ed25519(byte[] data, byte[] key);

	public static native byte[] crypto_sign_ed25519_open(byte[] sig, byte[] key);

	public static byte[] encodeSSHEd25519PublicKey(Ed25519PublicKey key) {
		TypesWriter tw = new TypesWriter();

		tw.writeString(ED25519_ID);
		tw.writeBytes(key.getEncoded());

		return tw.getBytes();
	}

	public static Ed25519PublicKey decodeSSHEd25519PublicKey(byte[] key) throws IOException {
		TypesReader tr = new TypesReader(key);

		String key_format = tr.readString();
		if (key_format.equals(ED25519_ID) == false) {
			throw new IOException("This is not an Ed25519 key");
		}

		byte[] keyBytes = tr.readByteString();

		if (tr.remain() != 0) {
			throw new IOException("Padding in Ed25519 public key! " + tr.remain() + " bytes left.");
		}

		if (keyBytes.length != ED25519_PK_SIZE_BYTES) {
			throw new IOException("Ed25519 was not of correct length: " + keyBytes.length + " vs " + ED25519_PK_SIZE_BYTES);
		}

		return Ed25519PublicKey.getInstance(keyBytes);
	}

	public static byte[] generateSignature(byte[] msg, Ed25519PrivateKey pk) {
		return crypto_sign_ed25519(msg, pk.getEncoded());
	}

	public static boolean verifySignature(byte[] data, byte[] sig, Ed25519PublicKey key) throws IOException {
		byte[] keyBytes = key.getEncoded();
		if (keyBytes.length != ED25519_PK_SIZE_BYTES) {
			throw new IOException("Invalid Ed25519 key lentgh " + keyBytes.length);
		}
		byte[] verify = new byte[data.length + sig.length];
		System.arraycopy(sig, 0, verify, 0, sig.length);
		System.arraycopy(data, 0, verify, sig.length, data.length);
		byte[] msg = crypto_sign_ed25519_open(verify, key.getEncoded());
		return msg != null && msg.length == data.length;
	}

	public static byte[] encodeSSHEd25519Signature(byte[] sig) {
		TypesWriter tw = new TypesWriter();

		tw.writeString(ED25519_ID);
		tw.writeBytes(sig);

		return tw.getBytes();
	}

	public static byte[] decodeSSHEd25519Signature(byte[] sig) throws IOException {
		byte[] rsArray;

		TypesReader tr = new TypesReader(sig);

		String sig_format = tr.readString();
		if (sig_format.equals(ED25519_ID) == false) {
			throw new IOException("Peer sent wrong signature format");
		}

		rsArray = tr.readByteString();

		if (tr.remain() != 0) {
			throw new IOException("Padding in Ed25519 signature!");
		}

		if (rsArray.length > ED25519_SIG_SIZE_BYTES) {
			throw new IOException("Ed25519 signature was " + rsArray.length + " bytes (" + ED25519_PK_SIZE_BYTES + " expected)");
		}

		return rsArray;
	}
}
