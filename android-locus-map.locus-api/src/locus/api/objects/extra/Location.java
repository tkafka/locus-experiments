/*  
 * Copyright 2012, Asamm Software, s. r. o.
 * 
 * This file is part of LocusAPI.
 * 
 * LocusAPI is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *  
 * LocusAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *  
 * You should have received a copy of the Lesser GNU General Public
 * License along with LocusAPI. If not, see 
 * <http://www.gnu.org/licenses/lgpl.html/>.
 */

package locus.api.objects.extra;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import locus.api.objects.Storable;
import locus.api.utils.Utils;

public class Location extends Storable {

	// location unique ID
	private long id;
	// provider for location source
	String provider;
	// location time
	long time;
	
	// latitude of location in WGS coordinates
	double latitude;
	// longitude of location in WGS coordinates
	double longitude;

	// flag if altitude is set
	boolean hasAltitude;
	// altitude value
	double altitude;

	// container for basic values
    private ExtraBasic extraBasic;
    // container for ANT sensor values
    private ExtraAnt extraSensor;
    
    private class ExtraBasic implements Cloneable {

    	boolean hasSpeed;
    	float speed;

    	boolean hasBearing;
    	float bearing;
        
    	boolean hasAccuracy;
        float accuracy;
        
        ExtraBasic() {
            hasSpeed = false;
            speed = 0.0f;
            hasBearing = false;
            bearing = 0.0f;
            hasAccuracy = false;
            accuracy = 0.0f;
        }
        
        @Override
        public ExtraBasic clone() {
        	ExtraBasic newExtra = new ExtraBasic();
        	newExtra.hasSpeed = hasSpeed;
        	newExtra.speed = speed;
        	newExtra.hasBearing = hasBearing;
        	newExtra.bearing = bearing;
        	newExtra.hasAccuracy = hasAccuracy;
        	newExtra.accuracy = accuracy;
			return newExtra;
        }
        
        boolean hasData() {
        	return hasSpeed || hasBearing || hasAccuracy;
        }
    }
    
    private class ExtraAnt implements Cloneable {

    	boolean hasHr;
    	int hr;

    	boolean hasCadence;
    	int cadence;
        
    	boolean hasSpeed;
        float speed;
        
        boolean hasPower;
        float power;
        
        ExtraAnt() {
        	hasHr = false;
        	hr = 0;
        	hasCadence = false;
        	cadence = 0;
            hasSpeed = false;
            speed = 0.0f;
            hasPower = false;
            power = 0.0f;
        }
        
        @Override
        public ExtraAnt clone() {
        	ExtraAnt newExtra = new ExtraAnt();
        	newExtra.hasHr = hasHr;
        	newExtra.hr = hr;
        	newExtra.hasCadence = hasCadence;
        	newExtra.cadence = cadence;
        	newExtra.hasSpeed = hasSpeed;
        	newExtra.speed = speed;
        	newExtra.hasPower = hasPower;
        	newExtra.power = power;
			return newExtra;
        }
        
        boolean hasData() {
        	return hasHr || hasCadence || hasSpeed || hasPower;
        }
    }
    
    /**
     * Constructs a new Location.
     * @param provider the name of the location provider that generated this
     * location fix.
     */
    public Location(String provider) {
        super();
        this.provider = provider;
    }
    
    public Location(String provider, double lat, double lon) {
    	super();
    	this.provider = provider;
    	this.latitude = lat;
    	this.longitude = lon;
    }

	/**
	 * Empty constructor used for {@link Storable}
	 * <br />
	 * Do not use directly!
	 */
    public Location() {
    	this("");
    }
    
    public Location(DataInputStream dis) throws IOException {
        super(dis);
    }

    public Location(Location loc) {
        set(loc);
    }
    
    public Location(byte[] data) throws IOException {
        super(data);
    }

    /**
     * Sets the contents of the location to the values from the given location.
     */
    public void set(Location l) {
    	id = l.id;
        provider = new String(l.provider);
        time = l.time;
        latitude = l.latitude;
        longitude = l.longitude;
        hasAltitude = l.hasAltitude();
        altitude = l.getAltitude();

        // set extra basic data
        if (l.extraBasic != null && l.extraBasic.hasData()) {
        	extraBasic = l.extraBasic.clone();
            if (!extraBasic.hasData()) {
            	extraBasic = null;
            }
        } else {
        	extraBasic = null;
        }
        
        // set extra ant data
        if (l.extraSensor != null && l.extraSensor.hasData()) {
        	extraSensor = l.extraSensor.clone();
            if (!extraSensor.hasData()) {
            	extraSensor = null;
            }
        } else {
        	extraSensor = null;
        }
    }

    /*******************************************/
    /*             OVERWRITE PART              */
    /*******************************************/
    
	@Override
	protected int getVersion() {
		return 1;
	}

	@Override
	protected void readObject(int version, DataInputStream dis) throws IOException {
		id = dis.readLong();
		provider = readStringUTF(dis);
		time = dis.readLong();
		latitude = dis.readDouble();
		longitude = dis.readDouble();
		hasAltitude = dis.readBoolean();
		altitude = dis.readDouble();

		// red basic data
		if (dis.readBoolean()) {
			extraBasic = new ExtraBasic();
			extraBasic.hasAccuracy = dis.readBoolean();
			extraBasic.accuracy = dis.readFloat();
			extraBasic.hasBearing = dis.readBoolean();
			extraBasic.bearing = dis.readFloat();
			extraBasic.hasSpeed = dis.readBoolean();
			extraBasic.speed = dis.readFloat();
	    	if (!extraBasic.hasData())
	    		extraBasic = null;
		}
		
		// end VERSION 0
		if (version < 1) {
			return;
		}
		
		// read ant data
		if (dis.readBoolean()) {
			extraSensor = new ExtraAnt();
			extraSensor.hasHr = dis.readBoolean();
        	extraSensor.hr = dis.readInt();
        	extraSensor.hasCadence = dis.readBoolean();
        	extraSensor.cadence = dis.readInt();
        	extraSensor.hasSpeed = dis.readBoolean();
            extraSensor.speed = dis.readFloat();
            extraSensor.hasPower = dis.readBoolean();
            extraSensor.power = dis.readFloat();
	    	if (!extraSensor.hasData())
	    		extraSensor = null;
		}
	}

	@Override
	protected void writeObject(DataOutputStream dos) throws IOException {
		dos.writeLong(id);
		writeStringUTF(dos, provider);
		dos.writeLong(time);
		dos.writeDouble(latitude);
		dos.writeDouble(longitude);
		dos.writeBoolean(hasAltitude);
		dos.writeDouble(altitude);

		// write basic data
		if (extraBasic == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeBoolean(extraBasic.hasAccuracy);
			dos.writeFloat(extraBasic.accuracy);
			dos.writeBoolean(extraBasic.hasBearing);
			dos.writeFloat(extraBasic.bearing);
			dos.writeBoolean(extraBasic.hasSpeed);
			dos.writeFloat(extraBasic.speed);
		}
		
		// write ant data
		if (extraSensor == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeBoolean(extraSensor.hasHr);
			dos.writeInt(extraSensor.hr);
			dos.writeBoolean(extraSensor.hasCadence);
			dos.writeInt(extraSensor.cadence);
			dos.writeBoolean(extraSensor.hasSpeed);
			dos.writeFloat(extraSensor.speed);
			dos.writeBoolean(extraSensor.hasPower);
			dos.writeFloat(extraSensor.power);
		}
	}
	
    @Override
    public void reset() {
    	id = -1L;
        provider = null;
        time = 0L;
        latitude = 0.0;
        longitude = 0.0;
        extraBasic = null;
        extraSensor = null;
    }
    
    /**************************************************/
    /*                 GETTER & SETTERS               */
    /**************************************************/
    
	public long getId() {
		return id;
	}

	public void setId(long mId) {
		this.id = mId;
	}
	
    /**
     * Returns the name of the provider that generated this fix,
     * or null if it is not associated with a provider.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the name of the provider that generated this fix.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * Returns the UTC time of this fix, in milliseconds since January 1,
     * 1970.
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the UTC time of this fix, in milliseconds since January 1,
     * 1970.
     */
    public void setTime(long time) {
    	this.time = time;
    }

    /**
     * Returns the latitude of this fix.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of this fix.
     */
    public void setLatitude(double latitude) {
    	this.latitude = latitude;
    }

    /**
     * Returns the longitude of this fix.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of this fix.
     */
    public void setLongitude(double longitude) {
    	this.longitude = longitude;
    }

    /**
     * Returns true if this fix contains altitude information, false
     * otherwise.
     */
    public boolean hasAltitude() {
        return hasAltitude;
    }

    /**
     * Returns the altitude of this fix.  If {@link #hasAltitude} is false,
     * 0.0f is returned.
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude of this fix.  Following this call,
     * hasAltitude() will return true.
     */
    public void setAltitude(double altitude) {
    	this.altitude = altitude;
    	this.hasAltitude = true;
    }

    /**
     * Clears the altitude of this fix.  Following this call,
     * hasAltitude() will return false.
     */
    public void removeAltitude() {
    	this.altitude = 0.0f;
    	this.hasAltitude = false;
    }

    /**************************************************/
    /*                BASIC EXTRA DATA                */
    /**************************************************/
    
    // SPEED
    
    /**
     * Returns true if this fix contains speed information, false
     * otherwise.  The default implementation returns false.
     */
    public boolean hasSpeed() {
    	if (extraBasic == null) {
    		return false;
    	}
        return extraBasic.hasSpeed;
    }

    /**
     * Returns the speed of the device over ground in meters/second.
     * If hasSpeed() is false, 0.0f is returned.
     */
    public float getSpeed() {
    	if (hasSpeed()) {
    		return extraBasic.speed;
    	}
    	return 0.0f;
    }

    /**
     * Sets the speed of this fix, in meters/second.  Following this
     * call, hasSpeed() will return true.
     */
    public void setSpeed(float speed) {
    	if (extraBasic == null) {
    		extraBasic = new ExtraBasic();
    	}
    	extraBasic.speed = speed;
    	extraBasic.hasSpeed = true;
    }

    /**
     * Clears the speed of this fix.  Following this call, hasSpeed()
     * will return false.
     */
    public void removeSpeed() {
    	if (extraBasic == null) {
    		return;
    	}
    	extraBasic.speed = 0.0f;
    	extraBasic.hasSpeed = false;
    	checkExtraBasic();
    }

    // BEARING
    
    /**
     * Returns true if the provider is able to report bearing information,
     * false otherwise.  The default implementation returns false.
     */
    public boolean hasBearing() {
    	if (extraBasic == null) {
    		return false;
    	}
        return extraBasic.hasBearing;
    }

    /**
     * Returns the direction of travel in degrees East of true
     * North. If hasBearing() is false, 0.0 is returned.
     */
    public float getBearing() {
    	if (hasBearing()) {
    		return extraBasic.bearing;
    	}
    	return 0.0f;
    }

    /**
     * Sets the bearing of this fix.  Following this call, hasBearing()
     * will return true.
     */
    public void setBearing(float bearing) {
        while (bearing < 0.0f) {
            bearing += 360.0f;
        }
        while (bearing >= 360.0f) {
            bearing -= 360.0f;
        }
        
        if (extraBasic == null) {
    		extraBasic = new ExtraBasic();
        }
    	extraBasic.bearing = bearing;
        extraBasic.hasBearing = true;
    }

    /**
     * Clears the bearing of this fix.  Following this call, hasBearing()
     * will return false.
     */
    public void removeBearing() {
    	if (extraBasic == null) {
    		return;
    	}
    	extraBasic.bearing = 0.0f;
    	extraBasic.hasBearing = false;
    	checkExtraBasic();
    }

    // ACCURACY
    
    /**
     * Returns true if the provider is able to report accuracy information,
     * false otherwise.  The default implementation returns false.
     */
    public boolean hasAccuracy() {
    	if (extraBasic == null) {
    		return false;
    	}
        return extraBasic.hasAccuracy;
    }

    /**
     * Returns the accuracy of the fix in meters. If hasAccuracy() is false,
     * 0.0 is returned.
     */
    public float getAccuracy() {
    	if (hasAccuracy()) {
    		return extraBasic.accuracy;
    	}
    	return 0.0f;
    }

    /**
     * Sets the accuracy of this fix.  Following this call, hasAccuracy()
     * will return true.
     */
    public void setAccuracy(float accuracy) {
    	if (extraBasic == null) {
    		extraBasic = new ExtraBasic();
    	}
    	extraBasic.accuracy = accuracy;
    	extraBasic.hasAccuracy = true;
    }

    /**
     * Clears the accuracy of this fix.  Following this call, hasAccuracy()
     * will return false.
     */
    public void removeAccuracy() {
    	if (extraBasic == null) {
    		return;
    	}
    	extraBasic.accuracy = 0.0f;
    	extraBasic.hasAccuracy = false;
    	checkExtraBasic();
    }
    
    private void checkExtraBasic() {
    	if (!extraBasic.hasData()) {
    		extraBasic = null;
    	}
    }
    
    /**************************************************/
    /*                 EXTRA ANT DATA                 */
    /**************************************************/
    
    // HEART RATE
    
    /**
     * Returns true if the provider is able to report Heart rate information,
     * false otherwise.  The default implementation returns false.
     */
    public boolean hasSensorHeartRate() {
    	if (extraSensor == null) {
    		return false;
    	}
        return extraSensor.hasHr;
    }

    /**
     * Returns the Heart rate value in BMP. If hasSensorHeartRate() is false,
     * 0.0 is returned.
     */
    public int getSensorHeartRate() {
    	if (hasSensorHeartRate()) {
    		return extraSensor.hr;
    	}
    	return 0;
    }

    /**
     * Sets the Heart rate of this fix. Following this call, hasSensorHeartRate()
     * will return true.
     */
    public void setSensorHeartRate(int heartRate) {
    	if (extraSensor == null) {
    		extraSensor = new ExtraAnt();
    	}
    	extraSensor.hr = heartRate;
    	extraSensor.hasHr = true;
    }

    /**
     * Clears the accuracy of this fix.  Following this call, hasSensorHeartRate()
     * will return false.
     */
    public void removeSensorHeartRate() {
    	if (extraSensor == null) {
    		return;
    	}
    	extraSensor.hr = 0;
    	extraSensor.hasHr = false;
    	checkExtraSensor();
    }
    
    // CADENCE
    
    /**
     * Returns true if the provider is able to report cadence information,
     * false otherwise.  The default implementation returns false.
     */
    public boolean hasSensorCadence() {
    	if (extraSensor == null) {
    		return false;
    	}
        return extraSensor.hasCadence;
    }

    /**
     * Returns the cadence value. If hasCadence() is false, 0 is returned.
     */
    public int getSensorCadence() {
    	if (hasSensorCadence()) {
    		return extraSensor.cadence;
    	}
    	return 0;
    }

    /**
     * Sets the cadence of this fix.  Following this call, hasCadence()
     * will return true.
     */
    public void setSensorCadence(int cadence) {
    	if (extraSensor == null) {
    		extraSensor = new ExtraAnt();
    	}
    	extraSensor.cadence = cadence;
    	extraSensor.hasCadence = true;
    }

    /**
     * Clears the cadence of this fix.  Following this call, hasCadence()
     * will return false.
     */
    public void removeSensorCadence() {
    	if (extraSensor == null) {
    		return;
    	}
    	extraSensor.cadence = 0;
    	extraSensor.hasCadence = false;
    	checkExtraSensor();
    }
    
    // SPEED
    
    /**
     * Returns true if the provider is able to report speed value,
     * false otherwise.  The default implementation returns false.
     */
    public boolean hasSensorSpeed() {
    	if (extraSensor == null) {
    		return false;
    	}
        return extraSensor.hasSpeed;
    }

    /**
     * Returns the speed of the fix in meters per sec. If hasSensorSpeed() is false,
     * 0.0 is returned.
     */
    public float getSensorSpeed() {
    	if (hasSensorSpeed()) {
    		return extraSensor.speed;
    	}
    	return 0.0f;
    }

    /**
     * Sets the speed of this fix.  Following this call, hasSensorSpeed()
     * will return true.
     */
    public void setSensorSpeed(float speed) {
    	if (extraSensor == null) {
    		extraSensor = new ExtraAnt();
    	}
    	extraSensor.speed = speed;
    	extraSensor.hasSpeed = true;
    }

    /**
     * Clears the speed of this fix.  Following this call, hasSensorSpeed()
     * will return false.
     */
    public void removeSensorSpeed() {
    	if (extraSensor == null) {
    		return;
    	}
    	extraSensor.speed = 0.0f;
    	extraSensor.hasSpeed = false;
    	checkExtraSensor();
    }
    
    // POWER
    
    /**
     * Returns true if the provider is able to report power value,
     * false otherwise.  The default implementation returns false.
     */
    public boolean hasSensorPower() {
    	if (extraSensor == null) {
    		return false;
    	}
        return extraSensor.hasPower;
    }

    /**
     * Returns the power of the fix in W. If hasSensorPower() is false,
     * 0.0 is returned.
     */
    public float getSensorPower() {
    	if (hasSensorPower()) {
    		return extraSensor.power;
    	}
    	return 0.0f;
    }

    /**
     * Sets the power of this fix.  Following this call, hasSensorPower()
     * will return true.
     */
    public void setSensorPower(float power) {
    	if (extraSensor == null) {
    		extraSensor = new ExtraAnt();
    	}
    	extraSensor.power = power;
    	extraSensor.hasPower = true;
    }

    /**
     * Clears the power of this fix.  Following this call, hasSensorPower()
     * will return false.
     */
    public void removeSensorPower() {
    	if (extraSensor == null) {
    		return;
    	}
    	extraSensor.power = 0.0f;
    	extraSensor.hasPower = false;
    	checkExtraSensor();
    }
    
    private void checkExtraSensor() {
    	if (!extraSensor.hasData()) {
    		extraSensor = null;
    	}
    }
    
    /**************************************************/
    /*                    UTILS PART                  */
    /**************************************************/
    
    public boolean hasSpeedExport() {
    	return hasSpeed() || hasSensorSpeed();
    }
    
    public float getSpeedExport() {
    	if (hasSensorSpeed()) {
    		return getSensorSpeed();
    	}
    	return getSpeed();
    }
    
    // cache the inputs and outputs of computeDistanceAndBearing
    // so calls to distanceTo() and bearingTo() can share work
    private double mLat1 = 0.0;
    private double mLon1 = 0.0;
    private double mLat2 = 0.0;
    private double mLon2 = 0.0;
    private float mDistance = 0.0f;
    private float mInitialBearing = 0.0f;
    private float[] mResults = new float[2];
    
    private static void computeDistanceAndBearing(double lat1, double lon1,
            double lat2, double lon2, float[] results) {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        int MAXITERS = 20;
        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lon1 *= Math.PI / 180.0;
        lon2 *= Math.PI / 180.0;

        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

        double L = lon2 - lon1;
        double A = 0.0;
        double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
        double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma = 0.0;
        double deltaSigma = 0.0;
        double cosSqAlpha = 0.0;
        double cos2SM = 0.0;
        double cosSigma = 0.0;
        double sinSigma = 0.0;
        double cosLambda = 0.0;
        double sinLambda = 0.0;

        double lambda = L; // initial guess
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2; // (14)
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
            sigma = Math.atan2(sinSigma, cosSigma); // (16)
            double sinAlpha = (sinSigma == 0) ? 0.0 :
                cosU1cosU2 * sinLambda / sinSigma; // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 :
                cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
            A = 1 + (uSquared / 16384.0) * // (3)
                (4096.0 + uSquared *
                 (-768 + uSquared * (320.0 - 175.0 * uSquared)));
            double B = (uSquared / 1024.0) * // (4)
                (256.0 + uSquared *
                 (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
            double C = (f / 16.0) *
                cosSqAlpha *
                (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * // (6)
                (cos2SM + (B / 4.0) *
                 (cosSigma * (-1.0 + 2.0 * cos2SMSq) -
                  (B / 6.0) * cos2SM *
                  (-3.0 + 4.0 * sinSigma * sinSigma) *
                  (-3.0 + 4.0 * cos2SMSq)));

            lambda = L +
                (1.0 - C) * f * sinAlpha *
                (sigma + C * sinSigma *
                 (cos2SM + C * cosSigma *
                  (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)

            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }

        float distance = (float) (b * A * (sigma - deltaSigma));
        results[0] = distance;
        if (results.length > 1) {
            float initialBearing = (float) Math.atan2(cosU2 * sinLambda,
                cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
            initialBearing *= 180.0 / Math.PI;
            results[1] = initialBearing;
            if (results.length > 2) {
                float finalBearing = (float) Math.atan2(cosU1 * sinLambda,
                    -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
                finalBearing *= 180.0 / Math.PI;
                results[2] = finalBearing;
            }
        }
    }

    /**
     * Computes the approximate distance in meters between two
     * locations, and optionally the initial and final bearings of the
     * shortest path between them.  Distance and bearing are defined using the
     * WGS84 ellipsoid.
     *
     * <p> The computed distance is stored in results[0].  If results has length
     * 2 or greater, the initial bearing is stored in results[1]. If results has
     * length 3 or greater, the final bearing is stored in results[2].
     *
     * @param startLatitude the starting latitude
     * @param startLongitude the starting longitude
     * @param endLatitude the ending latitude
     * @param endLongitude the ending longitude
     * @param results an array of floats to hold the results
     *
     * @throws IllegalArgumentException if results is null or has length < 1
     */
    public static void distanceBetween(double startLatitude, double startLongitude,
        double endLatitude, double endLongitude, float[] results) {
        if (results == null || results.length < 1) {
            throw new IllegalArgumentException("results is null or has length < 1");
        }
        computeDistanceAndBearing(startLatitude, startLongitude,
            endLatitude, endLongitude, results);
    }

    /**
     * Returns the approximate distance in meters between this
     * location and the given location.  Distance is defined using
     * the WGS84 ellipsoid.
     *
     * @param dest the destination location
     * @return the approximate distance in meters
     */
    public float distanceTo(Location dest) {
        // See if we already have the result
        synchronized (mResults) {
            if (latitude != mLat1 || longitude != mLon1 ||
                dest.latitude != mLat2 || dest.longitude != mLon2) {
                computeDistanceAndBearing(latitude, longitude,
                    dest.latitude, dest.longitude, mResults);
                mLat1 = latitude;
                mLon1 = longitude;
                mLat2 = dest.latitude;
                mLon2 = dest.longitude;
                mDistance = mResults[0];
                mInitialBearing = mResults[1];
            }
            return mDistance;
        }
    }

    /**
     * Returns the approximate initial bearing in degrees East of true
     * North when traveling along the shortest path between this
     * location and the given location.  The shortest path is defined
     * using the WGS84 ellipsoid.  Locations that are (nearly)
     * antipodal may produce meaningless results.
     *
     * @param dest the destination location
     * @return the initial bearing in degrees
     */
    public float bearingTo(Location dest) {
        synchronized (mResults) {
            // See if we already have the result
            if (latitude != mLat1 || longitude != mLon1 ||
                            dest.latitude != mLat2 || dest.longitude != mLon2) {
                computeDistanceAndBearing(latitude, longitude,
                    dest.latitude, dest.longitude, mResults);
                mLat1 = latitude;
                mLon1 = longitude;
                mLat2 = dest.latitude;
                mLon2 = dest.longitude;
                mDistance = mResults[0];
                mInitialBearing = mResults[1];
            }
            return mInitialBearing;
        }
    }
    
    /**************************************************/
    /*                      UTILS                     */
    /**************************************************/
    
    @Override
    public String toString() {
    	return Utils.toString(this);
    }
}
