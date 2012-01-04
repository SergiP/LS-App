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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class LSSensor implements Parcelable {

	private String sensorSituation;
	private String sensorName;
	private String sensorId;
	private String sensorSerial;
	private String sensorNetwork;
	private Position sensorPosition;
	private Measure sensorMeasure;
	private String sensorChannel;
	private String sensorType;
	private Bitmap sensorImage;
	private String sensorDesc;
	private String sensorImageName;
	private int sensorFaves;

	public LSSensor() {

		sensorSituation = "";
		sensorName = "";
		sensorId = "";
		sensorSerial = "";
		sensorNetwork = "";
		sensorPosition = new Position();
		sensorMeasure = new Measure();
		sensorChannel = "";
		sensorType = "";
		sensorImage = null;
		sensorDesc = "";
		sensorImageName = "";
		sensorFaves = 0;
	}

	public LSSensor(Parcel in) {

		sensorPosition = new Position();
		sensorMeasure = new Measure();
		readFromParcel(in);
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getSensorSituation() {
		return sensorSituation;
	}

	public void setSensorSituation(String sensorSituation) {
		this.sensorSituation = sensorSituation;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getSensorNetwork() {
		return sensorNetwork;
	}

	public void setSensorNetwork(String sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
	}

	public Position getSensorPosition() {
		return sensorPosition;
	}

	public void setSensorPosition(Position sensorPosition) {
		this.sensorPosition = sensorPosition;
	}

	public String getSensorChannel() {
		return sensorChannel;
	}

	public void setSensorChannel(String sensorChannel) {
		this.sensorChannel = sensorChannel;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public Double getSensorMeasure() {
		return sensorMeasure.getMeasure();
	}

	public String getSensorMeasureUnit() {
		return sensorMeasure.getMeasureUnit();
	}

	public void setSensorMeasure(Double measure,String unit) {
		this.sensorMeasure.setMeasure(measure);
		this.sensorMeasure.setMeasureUnit(unit);
	}

	public void setSensorMeasure(String measure,String unit) {
		this.sensorMeasure.setMeasure(Double.parseDouble(measure));
		this.sensorMeasure.setMeasureUnit(unit);
	}

	public Double getSensorMaxLoad() {
		return sensorMeasure.getMaxLoad();
	}

	public String getSensorMaxLoadUnit() {
		return sensorMeasure.getMaxLoadUnit();
	}

	public void setSensorMaxLoad(Double maxLoad,String unit) {
		this.sensorMeasure.setMaxLoad(maxLoad);
		this.sensorMeasure.setMaxLoadUnit(unit);
	}

	public void setSensorMaxLoad(String maxLoad,String unit) {
		this.sensorMeasure.setMaxLoad(Double.parseDouble(maxLoad));
		this.sensorMeasure.setMaxLoadUnit(unit);
	}

	public Double getSensorSensitivity() {
		return sensorMeasure.getSensitivity();
	}

	public String getSensorSensitivityUnit() {
		return sensorMeasure.getSensitivityUnit();
	}

	public void setSensorSensitivity(Double sensitivity,String unit) {
		this.sensorMeasure.setSensitivity(sensitivity);
		this.sensorMeasure.setSensitivityUnit(unit);
	}

	public void setSensorSensitivity(String sensitivity,String unit) {
		this.sensorMeasure.setSensitivity(Double.parseDouble(sensitivity));
		this.sensorMeasure.setSensitivityUnit(unit);
	}

	public Double getSensorOffset() {
		return sensorMeasure.getOffset();
	}

	public String getSensorOffsetUnit() {
		return sensorMeasure.getOffsetUnit();
	}

	public void setSensorOffset(Double offset,String unit) {
		this.sensorMeasure.setOffset(offset);
		this.sensorMeasure.setOffsetUnit(unit);
	}

	public void setSensorOffset(String offset,String unit) {
		this.sensorMeasure.setOffset(Double.parseDouble(offset));
		this.sensorMeasure.setOffsetUnit(unit);
	}

	public Double getSensorAlarmAt() {
		return sensorMeasure.getAlarmAt();
	}

	public String getSensorAlarmAtUnit() {
		return sensorMeasure.getAlarmAtUnit();
	}

	public void setSensorAlarmAt(Double alarmAt,String unit) {
		this.sensorMeasure.setAlarmAt(alarmAt);
		this.sensorMeasure.setAlarmAtUnit(unit);
	}

	public void setSensorAlarmAt(String alarmAt,String unit) {
		this.sensorMeasure.setAlarmAt(Double.parseDouble(alarmAt));
		this.sensorMeasure.setAlarmAtUnit(unit);
	}

	public String getSensorLastTare() {
		return sensorMeasure.getLastTare();
	}

	public void setSensorLastTare(String lastTare) {
		this.sensorMeasure.setLastTare(lastTare);
	}

	public Bitmap getSensorImage() {
		return sensorImage;
	}

	public void setSensorImage(Bitmap sensorImage) {
		this.sensorImage = sensorImage;
	}

	public String getSensorDesc() {
		return sensorDesc;
	}

	public void setSensorDesc(String sensorDesc) {
		this.sensorDesc = sensorDesc;
	}

	public String getSensorSerial() {
		return sensorSerial;
	}

	public void setSensorSerial(String sensorSerial) {
		this.sensorSerial = sensorSerial;
	}

	public String getSensorImageName() {
		return sensorImageName;
	}

	public void setSensorImageName(String sensorImageName) {
		this.sensorImageName = sensorImageName;
	}


	public int getSensorFaves() {
		return sensorFaves;
	}

	public void setSensorFaves(int sensorFaves) {
		this.sensorFaves = sensorFaves;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(sensorSituation);
		dest.writeString(sensorName);
		dest.writeString(sensorId);
		dest.writeString(sensorSerial);
		dest.writeString(sensorNetwork);
		dest.writeParcelable(sensorPosition, flags);
		dest.writeParcelable(sensorMeasure, flags);
		dest.writeString(sensorChannel);
		dest.writeString(sensorType);
		dest.writeParcelable(sensorImage,flags);
		dest.writeString(sensorDesc);
		dest.writeString(sensorImageName);
		dest.writeInt(sensorFaves);
	}

	private void readFromParcel(Parcel in) {

		sensorSituation = in.readString();
		sensorName = in.readString();
		sensorId = in.readString();
		sensorSerial = in.readString();
		sensorNetwork = in.readString();
		sensorPosition = in.readParcelable(Position.class.getClassLoader());
		sensorMeasure =  in.readParcelable(Measure.class.getClassLoader());
		sensorChannel = in.readString();
		sensorType = in.readString();
		sensorImage = in.readParcelable(Bitmap.class.getClassLoader());
		sensorDesc = in.readString();
		sensorImageName = in.readString();
		sensorFaves = in.readInt();
	}

	public static final Parcelable.Creator<LSSensor> CREATOR =
			new Parcelable.Creator<LSSensor>() {
		public LSSensor createFromParcel(Parcel in) {
			return new LSSensor(in);
		}

		public LSSensor[] newArray(int size) {
			return new LSSensor[size];
		}
	};
}

