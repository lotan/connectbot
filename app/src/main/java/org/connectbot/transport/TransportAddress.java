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

package org.connectbot.transport;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an address used to connect to a remote host.
 */
public final class TransportAddress implements Parcelable {
	private final Class<? extends AbsTransport> transport;
	private final String username;
	private final String hostname;
	private final int port;
	private final String input;

	public TransportAddress(Class<? extends AbsTransport> transport, String username, String hostname, int port, String input) {
		if (transport == null) {
			throw new NullPointerException("transport == null");
		} else if (input == null) {
			throw new NullPointerException("input == null");
		}

		this.transport = transport;
		this.username = username;
		this.hostname = hostname;
		this.port = port;
		this.input = input;
	}

	public Class<? extends AbsTransport> getTransport() {
		return this.transport;
	}

	public String getUsername() {
		return username;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getInput() {
		return input;
	}

	@Override
	public String toString() {
		return "TransportAddress{" +
				"transport=" + transport +
				", username='" + username + '\'' +
				", hostname='" + hostname + '\'' +
				", port=" + port +
				", input='" + input + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TransportAddress that = (TransportAddress) o;

		if (port != that.port) return false;
		if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null)
			return false;
		if (!input.equals(that.input)) return false;
		if (!transport.equals(that.transport)) return false;
		if (username != null ? !username.equals(that.username) : that.username != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = transport.hashCode();
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
		result = 31 * result + port;
		result = 31 * result + input.hashCode();
		return result;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(transport.getName());
		parcel.writeString(username);
		parcel.writeString(hostname);
		parcel.writeInt(port);
		parcel.writeString(input);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public TransportAddress createFromParcel(Parcel in) {
			String transportClassName = in.readString();
			String username = in.readString();
			String hostname = in.readString();
			int port = in.readInt();
			String input = in.readString();

			final Class<?> transportClass;
			try {
				transportClass = Class.forName(transportClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Non-existent transport class: " + transportClassName);
			}

			if (!AbsTransport.class.isAssignableFrom(transportClass)) {
				throw new RuntimeException("Class is not a transport: " + transportClass.getName());
			}
			return new TransportAddress((Class<? extends AbsTransport>) transportClass, username, hostname, port, input);
		}

		public TransportAddress[] newArray(int i) {
			return new TransportAddress[i];
		}
	};
}
