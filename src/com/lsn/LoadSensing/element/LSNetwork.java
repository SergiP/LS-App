//    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
//    
//    Copyright (C) 2011-2012
//    Authors:
//        Sergio González Díez        [sergio.gd@gmail.com]
//        Sergio Postigo Collado      [spostigoc@gmail.com]
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.lsn.LoadSensing.element;

import android.os.Parcel;
import android.os.Parcelable;

public class LSNetwork implements Parcelable {

	private String networkSituation;
	private String networkName;
	private String networkId;
	private Position networkPosition;
	private Integer networkNumSensors;

	public LSNetwork() {

		networkSituation = "";
		networkName = "";
		networkId = "";
		networkPosition = new Position();
		networkNumSensors = 0;
	}

	public LSNetwork(Parcel in) {

		networkPosition = new Position();
		readFromParcel(in);
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getNetworkSituation() {
		return networkSituation;
	}

	public void setNetworkSituation(String networkSituation) {
		this.networkSituation = networkSituation;
	}

	public Integer getNetworkNumSensors() {
		return networkNumSensors;
	}

	public void setNetworkNumSensors(Integer networkNumber) {
		this.networkNumSensors = networkNumber;
	}

	public void setNetworkNumSensors(String networkNumber) {
		this.networkNumSensors = Integer.parseInt(networkNumber);
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public Position getNetworkPosition() {
		return networkPosition;
	}

	public void setNetworkPosition(Position networkPosition) {
		this.networkPosition = networkPosition;
	}

	public void setNetworkPosition(String lat, String lon) {

		this.networkPosition.setLatitude(Double.parseDouble(lat));
		this.networkPosition.setLongitude(Double.parseDouble(lon));
	}

	public void setNetworkPosition(Double lat, Double lon) {
		this.networkPosition.setLatitude(lat);
		this.networkPosition.setLongitude(lon);
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(networkSituation);
		dest.writeString(networkName);
		dest.writeString(networkId);
		dest.writeParcelable(networkPosition, flags);
		dest.writeInt(networkNumSensors);
	}

	private void readFromParcel(Parcel in) {

		networkSituation = in.readString();
		networkName = in.readString();
		networkId = in.readString();
		networkPosition = in.readParcelable(Position.class.getClassLoader());
		networkNumSensors = in.readInt();
	}

	public static final Parcelable.Creator<LSNetwork> CREATOR =
			new Parcelable.Creator<LSNetwork>() {
		public LSNetwork createFromParcel(Parcel in) {
			return new LSNetwork(in);
		}

		public LSNetwork[] newArray(int size) {
			return new LSNetwork[size];
		}
	};

}
