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

package locus.api.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import locus.api.utils.Logger;
import locus.api.utils.Utils;

public abstract class Storable {

	private static final String TAG = "Storable";
	
	public Storable() {
		reset();
	}
	
	public Storable(byte[] data) throws IOException {
		this();
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(
					new ByteArrayInputStream(data));
			read(dis);
		} finally {
			Utils.closeStream(dis);
		}
	}
	
	public Storable(DataInputStream dis) throws IOException {
		this();
		read(dis);
	}
	
	/**
	 * Current object version used for storing
	 * @return
	 */
	protected abstract int getVersion();
	
	/**
	 * This function is called from {@link read} function. Do not call it directly until you know what you're doing
	 * @param version
	 * @param dis
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected abstract void readObject(int version, DataInputStream dis) throws IOException;
	
	/**
	 * This function is called from {@link write} function. Do not call it directly until you know what you're doing
	 * @param dos
	 * @throws IOException
	 */
	protected abstract void writeObject(DataOutputStream dos) throws IOException;
	
	/**
	 * Reset all values to default once
	 */
	public abstract void reset();
	
	private static class BodyContainer {
		int version;
		byte[] data;
	}
	
	public static Storable read(Class<? extends Storable> claz, DataInputStream dis) 
			throws IOException, InstantiationException, IllegalAccessException {
    	// read header
		BodyContainer bc = readHeader(dis);

    	// now initialize object. Data are already loaded, so error will not break data flow
		Storable storable = claz.newInstance();
		storable.readBody(bc);
		return storable;
	}
	
	public void read(DataInputStream input) throws IOException {
    	// read header
		BodyContainer bc = readHeader(input);
    	// read body
    	readBody(bc);
	}
	
	private static BodyContainer readHeader(DataInputStream dis) throws IOException {
		// initialize container
		BodyContainer bc = new BodyContainer();
//Logger.d(TAG, "avail:" + input.available());
		// read basic data
    	bc.version = dis.readInt();
    	int size = dis.readInt();
//Logger.d(TAG, "avail2:" + input.available());
    	// read object data
    	bc.data = new byte[size];
    	dis.read(bc.data);
//Logger.d(TAG, "ver:" + bc.version + ", size:" + size + ", read:" + read);
    	// return filled container
    	return bc;
	}
	
	private void readBody(BodyContainer bc) throws IOException {
    	// prepare streams
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bc.data));
    	// read object
    	readObject(bc.version, dis);
    	// close streams
    	Utils.closeStream(dis);
	}
    
	public void write(DataOutputStream dos) throws IOException {
		// prepare data handler
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos2 = new DataOutputStream(baos);
		// write object itself
		writeObject(dos2);
		// flush all writes
		dos2.flush();
		// get raw data
		byte[] data = baos.toByteArray();
		Utils.closeStream(dos2);

		// write basic data
		dos.writeInt(getVersion());
		dos.writeInt(data.length);
		// write object data
		dos.write(data);
	}
	
	/**
	 * Create precise copy of current object. 
	 * Method is that object is stored into byte stream and then restored
	 * as a new object.
	 * 
	 * @return exact clone of this object
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Storable getCopy() throws IOException, InstantiationException, IllegalAccessException {
		byte[] data = getAsBytes();
		return read(this.getClass(), new DataInputStream(
				new ByteArrayInputStream(data)));
	}

	public byte[] getAsBytes() {
		// define stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {
			// write current object
			write(dos);
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			Logger.e(TAG, "getAsBytes()", e);
			return null;
		} finally {
			Utils.closeStream(dos);
		}
	}
	
    /**********************************************/
    /*            LIST READING/WRITING            */
    /**********************************************/
	
	public static ArrayList<? extends Storable> readList(Class<? extends Storable> claz,
			DataInputStream dis) throws IOException {
		// prepare container
		ArrayList<Storable> objs = new ArrayList<Storable>();
				
		// read size
		int count = dis.readInt();
//Logger.d(TAG, "readList(" + claz + ", " + dis + "), count:" + count);
		if (count == 0)
			return objs;
		
		// read locations
		for (int i = 0; i < count; i++) {
			try {
				Storable item = read(claz, dis);
				objs.add(item);
			} catch (InstantiationException e) {
				Logger.e(TAG, "readList(" + claz + ", " + dis + ")", e);
			} catch (IllegalAccessException e) {
				Logger.e(TAG, "readList(" + claz + ", " + dis + ")", e);
			}
		}
		return objs;
	}
	
	public static void writeList(ArrayList<? extends Storable> objs, DataOutputStream dos)
			throws IOException {
		// get size of list
		int size;
		if (objs == null) {
			size = 0;
		} else {
			size = objs.size();
		}

		// write size of list
		dos.writeInt(size);
//Logger.d(TAG, "writeList(" + objs + "), size:" + size);
		if (size == 0)
			return;

		// write objects
		for (Storable obj : objs) {
			obj.write(dos);
		}
	}
	
	public static ArrayList<? extends Storable> getList(Class<? extends Storable> claz, byte[] data)
			throws IOException {
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(
					new ByteArrayInputStream(data));
			return readList(claz, dis);			
		} finally {
			Utils.closeStream(dis);
		}
	}
	
	public static byte[] getAsBytes(ArrayList<? extends Storable> data) {
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			Storable.writeList(data, dos);
			dos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			Logger.e(TAG, "getAsBytes(" + data + ")", e);
		} finally {
			Utils.closeStream(dos);
		}
		return null;
	}
	
    /**********************************************/
    /*           STRING READING/WRITING           */
    /**********************************************/
    
    public static String readStringUTF(DataInput dis) throws IOException {
		int textLength = dis.readInt();
		if (textLength == 0) {
			return "";
		} else {
			byte[] buffer = new byte[textLength];				
			dis.readFully(buffer);
//Logger.w(TAG, "readString(), readed:" + new String(buffer, 0, textLength));
			return new String(buffer, "UTF-8");
		}
	}
    
	public static void writeStringUTF(DataOutput dos, String string) throws IOException {
		if (string == null || string.length() == 0) {
			dos.writeInt(0);
		} else {
			byte[] bytes = string.getBytes("UTF-8");
//Logger.w(TAG, "writeStringUTF(" + dos + "), length:" + bytes.length);
			dos.writeInt(bytes.length);
			dos.write(bytes);
		}
	}
}
