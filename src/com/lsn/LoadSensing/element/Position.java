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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


public class Position implements Parcelable
{
	private Double latitude;
	private Double longitude;
	private Double altitude;

	public Position() {
		super();
		this.latitude = 0.0;
		this.longitude = 0.0;
		this.altitude = 0.0;
	}

	public Position(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = 0.0;
	}

	public Position(Double latitude, Double longitude, Double altitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public Position(Location location) {
		super();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.altitude = location.getAltitude();
	}

	public Position(Parcel in) {

		readFromParcel(in);
	}

	public Double getLatitude() {
		
		return latitude;
	}
	
	public String getLatitudeStr() {
		NumberFormat formatter = new DecimalFormat("#0.000000");
		return formatter.format(latitude);
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public String getLongitudeStr() {
		NumberFormat formatter = new DecimalFormat("#0.000000");
		return formatter.format(longitude);
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getAltitude() {
		return altitude;
	}
	public String getAltitudeStr() {
		NumberFormat formatter = new DecimalFormat("#0.000000");
		return formatter.format(altitude);
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public void setPosition(Location location)
	{
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.altitude = location.getAltitude();
	}

	public float milesDistanceTo(Position position)
	{
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(position.latitude - latitude);
		double dLon = Math.toRadians(position.longitude - longitude);

		double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
				Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(position.latitude)) *
				Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
		double dist = earthRadius * c;

		return new Float(dist).floatValue();
	}

	public float metersDistanceTo(Position position)
	{
		double dist = milesDistanceTo(position);
		int meterConversion = 1609;

		return new Float(dist*meterConversion).floatValue();
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeDouble(altitude);
	}

	private void readFromParcel(Parcel in) {


		latitude = in.readDouble();
		longitude = in.readDouble();
		altitude = in.readDouble();
	}

	public static final Parcelable.Creator<Position> CREATOR =
			new Parcelable.Creator<Position>() {
		public Position createFromParcel(Parcel in) {
			return new Position(in);
		}

		public Position[] newArray(int size) {
			return new Position[size];
		}
	};

}
