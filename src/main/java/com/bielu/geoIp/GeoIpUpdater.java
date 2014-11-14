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

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <code>GeoIpUpdater</code> TODO provide description
 *
 * @author Przemyslaw Bielicki
 */
public class GeoIpUpdater {
	
	private static final Logger LOG = Logger.getLogger(GeoIpUpdater.class);
	
	public static final String LONGITUDE = "Longitude:";
	public static final String LATITUDE = "Latitude:";
	public static final String CITY = "City:";
	public static final String COUNTRY = "Country:";
	
	public static IpInfo getIpInfo(String ip, String proxyUrl, String proxyPort) {
		IpInfo info = new IpInfo();
		try {
			URL url = new URL("http://api.hostip.info/get_html.php?ip=" + ip + "&position=true");
			
			Proxy proxy = Proxy.NO_PROXY;
			if (proxyUrl != null && proxyPort != null) {
				proxy = new Proxy(Proxy.Type.HTTP, 
							new InetSocketAddress(proxyUrl, Integer.parseInt(proxyPort)));
			}
			
			LineNumberReader in = new LineNumberReader(new InputStreamReader(url.openConnection(proxy).getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.startsWith(COUNTRY)) {
					info.setCountry(line.split(COUNTRY)[1].trim());
					
				} else if (line.startsWith(CITY)) {
					info.setCity(line.split(CITY)[1].trim());
					
				} else if (line.startsWith(LATITUDE)) {
					info.setLatitude(line.split(LATITUDE)[1].trim());
					
				} else if (line.startsWith(LONGITUDE)) {
					info.setLongitude(line.split(LONGITUDE)[1].trim());
				}
			}
		} catch (Exception e) {
			LOG.info(e);
		}
		return info;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] argz) throws Exception {
		//Args args = readArgs(argz);
		String proxyUrl = argz.length > 0 ? argz[0] : null;
		String proxyPort = argz.length > 1 ? argz[1] : null;
		
		Properties p = new Properties();
		p.load(GeoIpUpdater.class.getClassLoader().getResourceAsStream("jdbc.properties"));
		Class.forName(p.getProperty("jdbc.driverClassName"));
		Connection conn = DriverManager.getConnection(p.getProperty("jdbc.url"), p.getProperty("jdbc.username"), p.getProperty("jdbc.password"));
		conn.setAutoCommit(false);
		
		PreparedStatement stmt = conn.prepareStatement("select remoteAddr from statistics where country is null or city is null group by remoteAddr");
		ResultSet rs = stmt.executeQuery();
		
		PreparedStatement batch = conn.prepareStatement("update statistics set country = ?, city = ? where remoteAddr = ?");
		while (rs.next()) {
			String ip = rs.getString("remoteAddr");
			IpInfo info = getIpInfo(ip, proxyUrl, proxyPort);
			
			batch.clearParameters();
			batch.setString(1, info.getCounrtyCode());
			batch.setString(2, info.getCity());
			batch.setString(3, ip);
			batch.addBatch();
			
			LOG.info("Added location [" + info.getCity() + "], [" + info.getCounrtyCode() + "] info for [" + ip + "] IP address");
			batch.execute();
		}
		stmt.close();
		batch.close();
		conn.commit();
		conn.close();
		LOG.info("Transaction comitted.");
	}

}
