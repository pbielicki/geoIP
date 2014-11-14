/* 
 *  Copyright 2007 Przemyslaw Bielicki All Rights Reserved.
 *
 *  The source code contained or described herein and all documents related to
 *  the source code ("Material") are owned by Przemyslaw Bielicki or its suppliers 
 *  or licensors. Title to the Material remains with Przemyslaw Bielicki or its 
 *  suppliers and licensors. The Material contains trade secrets and proprietary 
 *  and confidential information of Przemyslaw Bielicki or its suppliers and licensors. 
 *  The Material is protected by worldwide copyright and trade secret laws and treaty 
 *  provisions. No part of the Material may be used, copied, reproduced, modified, 
 *  published, uploaded, posted, transmitted, distributed, or disclosed in any way 
 *  without Przemyslaw Bielicki's prior express written permission.
 *
 *  No license under any patent, copyright, trade secret or other intellectual 
 *  property right is granted to or conferred upon you by disclosure or delivery 
 *  of the Materials, either expressly, by implication, inducement, estoppel 
 *  or otherwise. Any license under such intellectual property rights must be express 
 *  and approved by Przemyslaw Bielicki in writing.
 */
package com.bielu.geoIp;

/**
 * <code>IpInfo</code> TODO provide description
 * 
 * @author Przemyslaw Bielicki
 */
public class IpInfo {
	
	private String latitude = "";
	private String longitude = "";
	private String country = "";
	private String counrtyCode;
	private String city = "";

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
		if (country != null) {
			int leftIdx = country.indexOf("(");
			int rightIdx = country.indexOf(")");
			if (leftIdx > -1 && leftIdx < rightIdx) {
				counrtyCode = country.substring(leftIdx + 1, rightIdx);
				if (counrtyCode.length() != 2) {
					counrtyCode = null;
				}
			}
		}
	}

	public String getCounrtyCode() {
		return counrtyCode;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
