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

#include "com_trilead_ssh2_signature_Ed25519Verify.h"

#include "android/log.h"
#include "log_compat.h"

#include "ed25519/crypto_api.h"
#include "ScopedPrimitiveArray.h"
#include "ScopedLocalRef.h"
#include "JNIHelp.h"

#define LOG_TAG "Ed25519Verify"
#define LOG(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jbyteArray JNICALL Java_com_trilead_ssh2_signature_Ed25519Verify_crypto_1sign_1ed25519(JNIEnv *env, jclass, jbyteArray msgArray, jbyteArray keyArray) {
	ScopedByteArrayRO msg(env, msgArray);
	if (msg.get() == NULL) {
		return NULL;
	}

	ScopedByteArrayRO key(env, keyArray);
	if (key.get() == NULL) {
        	return NULL;
	}

	unsigned char sigBytes[crypto_sign_ed25519_BYTES];
	unsigned long long sigLen;
	int ret = crypto_sign_ed25519(sigBytes, &sigLen,
				      reinterpret_cast<const unsigned char*>(msg.get()), msg.size(),
				      reinterpret_cast<const unsigned char*>(key.get()));
	if (ret != 0) {
		jniThrowException(env, "java/io/IOException", "Could not sign Ed25519 message");
		return NULL;
	}

	ScopedLocalRef<jbyteArray> sigArray(env, env->NewByteArray(sigLen));
	if (sigArray.get() == NULL) {
		return NULL;
	}

	ScopedByteArrayRW sig(env, sigArray.get());
	if (sig.get() == NULL) {
		return NULL;
	}

	memcpy(sig.get(), sigBytes, sigLen);

	return sigArray.release();
}

JNIEXPORT jbyteArray JNICALL Java_com_trilead_ssh2_signature_Ed25519Verify_crypto_1sign_1ed25519_1open(JNIEnv *env, jclass, jbyteArray sigArray, jbyteArray keyArray) {
	ScopedByteArrayRO sig(env, sigArray);
	if (sig.get() == NULL) {
		return 0;
	}

	ScopedByteArrayRO key(env, keyArray);
	if (key.get() == NULL) {
		return 0;
	}

	unsigned char msgBytes[sig.size()];
	unsigned long long msgLen = sig.size();
	int ret = crypto_sign_ed25519_open(msgBytes, &msgLen,
				      reinterpret_cast<const unsigned char*>(sig.get()), sig.size(),
				      reinterpret_cast<const unsigned char*>(key.get()));
	if (ret != 0) {
		ALOGD("crypto_sign_ed25519_open returned %d", ret);
		return NULL;
	}

	ScopedLocalRef<jbyteArray> msgArray(env, env->NewByteArray(msgLen));
	if (msgArray.get() == NULL) {
		return NULL;
	}

	ScopedByteArrayRW msg(env, msgArray.get());
	if (msg.get() == NULL) {
		return NULL;
	}

	memcpy(msg.get(), msgBytes, msgLen);

	return msgArray.release();
}