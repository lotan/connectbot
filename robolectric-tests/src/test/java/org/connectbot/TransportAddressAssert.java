/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2014 Kenny Root, Jeffrey Sharkey
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

package org.connectbot;

import android.net.Uri;

import org.connectbot.transport.AbsTransport;
import org.connectbot.transport.TransportAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A series of assertions to help with unit testing {@link android.net.Uri} objects.
 */
public class TransportAddressAssert {
	/**
	 * To prevent instantiation.
	 */
	protected TransportAddressAssert() {
	}

	public static void assertAddress(Class<? extends AbsTransport> klass, String expectedUsername, String expectedHostname, int expectedPort, TransportAddress actualAddress) {
		if (actualAddress == null) {
			fail("Uri is null");
		} else if (expectedHostname == null) {
			fail("Hostname is null");
		} else if (expectedUsername == null) {
			fail("Username is null");
		}

		assertEquals(klass, actualAddress.getTransport());
		assertEquals(expectedUsername, actualAddress.getUsername());
		assertEquals(expectedHostname, actualAddress.getHostname());
		assertEquals(expectedPort, actualAddress.getPort());
	}
}
