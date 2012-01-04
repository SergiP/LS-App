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

public class LSImage implements Parcelable {

	private Bitmap imageBitmap;
	private String imageName;
	private String imageId;
	private String imageNetwork;
	private String imageSituation;
	private String imageNameFile;

	public LSImage() {

		imageBitmap = null;
		imageName = "";
		imageNetwork = "";
		imageSituation= "";
		imageNameFile = "";
	}

	public LSImage(Parcel in) {

		readFromParcel(in);
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageNetwork() {
		return imageNetwork;
	}

	public void setImageNetwork(String imageNetwork) {
		this.imageNetwork = imageNetwork;
	}

	public String getImageSituation() {
		return imageSituation;
	}

	public void setImageSituation(String imageSituation) {
		this.imageSituation = imageSituation;
	}

	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public Bitmap getImageThumb(int size) {
		return Bitmap.createScaledBitmap(imageBitmap, size, size, false);
	}

	public Bitmap getImageThumb(int sizeX, int sizeY) {
		return Bitmap.createScaledBitmap(imageBitmap, sizeX, sizeY, false);
	}

	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageNameFile() {
		return imageNameFile;
	}

	public void setImageNameFile(String imageNameFile) {
		this.imageNameFile = imageNameFile;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeParcelable(imageBitmap, flags);
		dest.writeString(imageName);
		dest.writeString(imageId);
		dest.writeString(imageNetwork);
		dest.writeString(imageSituation);
		dest.writeString(imageNameFile);
	}

	private void readFromParcel(Parcel in) {

		imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
		imageName = in.readString();
		imageId = in.readString();
		imageNetwork = in.readString();
		imageSituation = in.readString();
		imageNameFile = in.readString();
	}



	public static final Parcelable.Creator<LSImage> CREATOR =
			new Parcelable.Creator<LSImage>() {
		public LSImage createFromParcel(Parcel in) {
			return new LSImage(in);
		}

		public LSImage[] newArray(int size) {
			return new LSImage[size];
		}
	};

}
