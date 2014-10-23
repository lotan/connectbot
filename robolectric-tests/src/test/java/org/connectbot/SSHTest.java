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

import org.connectbot.transport.SSH;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.connectbot.TransportAddressAssert.*;
import static org.junit.Assert.*;

/**
 * Created by Kenny Root on 10/22/14.
 */
@Config(manifest = "../app/src/main/AndroidManifest.xml", emulateSdk = 16)
@RunWith(RobolectricTestRunner.class)
public class SSHTest {
	@Test
	public void getUri_NoUsername_Failure() throws Exception {
		assertNull(SSH.getUri("localhost"));
		assertNull(SSH.getUri("www.google.com:2222"));
	}

	@Test
	public void getUri_DefaultPort_Success() throws Exception {
		assertAddress(SSH.class, "root", "127.0.0.1", 22, SSH.getUri("root@127.0.0.1"));
	}
	@Test
	public void getUri_IPV6_Success() throws Exception {
		assertAddress(SSH.class, "kenny", "2001:db8::1", 2222, SSH.getUri("kenny@[2001:db8::1]:2222"));
	}
}
