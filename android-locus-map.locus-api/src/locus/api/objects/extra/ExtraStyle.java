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
import java.util.ArrayList;

import locus.api.objects.Storable;
import locus.api.objects.extra.ExtraStyle.LineStyle.ColorStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.Units;
import locus.api.utils.Logger;

public class ExtraStyle extends Storable {

	private static final String TAG = "ExtraStyle";
	
	// style name - id in style tag
	String id;
	// style name - name tag inside style tag	
	String name;
	
	// BALLON STYLE (not used yet)
	BalloonStyle balloonStyle;
	// ICON STYLE
	IconStyle iconStyle;
	// LABEL STYLE
	LabelStyle labelStyle;
	// LINE STYLE
	LineStyle lineStyle;
	// LIST STYLE (not used yet)
	ListStyle listStyle;
	// POLY STYLE
	PolyStyle polyStyle;
	
	public ExtraStyle() {
		this("");
	}
	
	public ExtraStyle(String name) {
		super();
		this.name = name;
	}

	public ExtraStyle(DataInputStream dis) throws IOException {
		super(dis);
	}
	
	public ExtraStyle(byte[] data) throws IOException {
		super(data);
	}
	
    /*******************************************/
    /*             OVERWRITE PART              */
    /*******************************************/
	
	@Override
	protected int getVersion() {
		return 0;
	}

	@Override
	public void reset() {
		id = "";
		name = "";
		balloonStyle = null;
		iconStyle = null;
		labelStyle = null;
		lineStyle = null;
		listStyle = null;
		polyStyle = null;
	}
	
	@Override
	protected void readObject(int version, DataInputStream dis)
			throws IOException {
		// read core
		id = readStringUTF(dis);
		name = readStringUTF(dis);
		
		// balloon style
		if (dis.readBoolean()) {
			balloonStyle = new BalloonStyle();
			balloonStyle.bgColor = dis.readInt();
			balloonStyle.textColor = dis.readInt();
			balloonStyle.text = readStringUTF(dis);
			balloonStyle.displayMode = BalloonStyle.DisplayMode.values()[dis.readInt()];
		}
		
		// icon style
		if (dis.readBoolean()) {
			iconStyle = new IconStyle();
			iconStyle.color = dis.readInt();
			iconStyle.scale = dis.readFloat();
			iconStyle.heading = dis.readFloat();
			iconStyle.iconHref = readStringUTF(dis);
			iconStyle.hotSpot = KmlVec2.read(dis);
		}
		
		// label style
		if (dis.readBoolean()) {
			labelStyle = new LabelStyle();
			labelStyle.color = dis.readInt();
			labelStyle.scale = dis.readFloat();
		}
		
		// line style
		if (dis.readBoolean()) {
			lineStyle = new LineStyle();
			lineStyle.color = dis.readInt();
			lineStyle.width = dis.readFloat();
			lineStyle.gxOuterColor = dis.readInt();
			lineStyle.gxOuterWidth = dis.readFloat();
			lineStyle.gxPhysicalWidth = dis.readFloat();
			lineStyle.gxLabelVisibility = dis.readBoolean();
			
			lineStyle.colorStyle = LineStyle.ColorStyle.values()[dis.readInt()];
			lineStyle.units = LineStyle.Units.values()[dis.readInt()];
		}
		
		// list style
		if (dis.readBoolean()) {
			listStyle = new ListStyle();
			listStyle.listItemType = ListStyle.ListItemType.values()[dis.readInt()];
			listStyle.bgColor = dis.readInt();
			int itemsCount = dis.readInt();
			for (int i = 0; i < itemsCount; i++) {
				ListStyle.ItemIcon itemIcon = new ListStyle.ItemIcon();
				itemIcon.state = ListStyle.ItemIcon.State.values()[dis.readInt()];
				itemIcon.href = readStringUTF(dis);
				listStyle.itemIcons.add(itemIcon);
			}
		}
		
		// poly style
		if (dis.readBoolean()) {
			polyStyle = new PolyStyle();
			polyStyle.color = dis.readInt();
			polyStyle.fill = dis.readBoolean();
			polyStyle.outline = dis.readBoolean();
		}
	}

	@Override
	protected void writeObject(DataOutputStream dos) throws IOException {
		// write core
		writeStringUTF(dos, id);
		writeStringUTF(dos, name);
		
		// balloon style
		if (balloonStyle == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeInt(balloonStyle.bgColor);
			dos.writeInt(balloonStyle.textColor);
			writeStringUTF(dos, balloonStyle.text);
			dos.writeInt(balloonStyle.displayMode.ordinal());
		}
		
		// icon style
		if (iconStyle == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeInt(iconStyle.color);
			dos.writeFloat(iconStyle.scale);
			dos.writeFloat(iconStyle.heading);
			writeStringUTF(dos, iconStyle.iconHref);
			iconStyle.hotSpot.write(dos);
		}
		
		// label style
		if (labelStyle == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeInt(labelStyle.color);
			dos.writeFloat(labelStyle.scale);
		}
		
		// line style
		if (lineStyle == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeInt(lineStyle.color);
			dos.writeFloat(lineStyle.width);
			dos.writeInt(lineStyle.gxOuterColor);
			dos.writeFloat(lineStyle.gxOuterWidth);
			dos.writeFloat(lineStyle.gxPhysicalWidth);
			dos.writeBoolean(lineStyle.gxLabelVisibility);
			
			dos.writeInt(lineStyle.colorStyle.ordinal());
			dos.writeInt(lineStyle.units.ordinal());
		}
		
		// list style
		if (listStyle == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeInt(listStyle.listItemType.ordinal());
			dos.writeInt(listStyle.bgColor);
			dos.writeInt(listStyle.itemIcons.size());
			for (ListStyle.ItemIcon itemIcon : listStyle.itemIcons) {
				dos.writeInt(itemIcon.state.ordinal());
				writeStringUTF(dos, itemIcon.href);
			}
		}
		
		// poly style
		if (polyStyle == null) {
			dos.writeBoolean(false);
		} else {
			dos.writeBoolean(true);
			dos.writeInt(polyStyle.color);
			dos.writeBoolean(polyStyle.fill);
			dos.writeBoolean(polyStyle.outline);
		}
	}

    /*******************************************/
    /*            SETERS & GETTERS             */
    /*******************************************/
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// ICON STYLE
	
	public String getIconStyleIconUrl() {
		if (iconStyle == null)
			return null;
		return iconStyle.iconHref;
	}

	public void setIconStyle(String iconUrl, float scale) {
    	setIconStyle(iconUrl, COLOR_DEFAULT, 0.0f, scale);
    }
	
    public void setIconStyle(String iconUrl, int color, float heading, float scale) {
    	iconStyle = new IconStyle();
    	iconStyle.iconHref = iconUrl;
    	iconStyle.color = color;
    	iconStyle.heading = heading;
    	iconStyle.scale = scale;
    	// set hot spot
    	setIconStyleHotSpot(HOTSPOT_BOTTOM_CENTER);
    }
	
	// definition of hotSpot of icon to bottom center
	public static final int HOTSPOT_BOTTOM_CENTER = 0;
	public static final int HOTSPOT_TOP_LEFT = 1;
	public static final int HOTSPOT_CENTER_CENTER = 2;

	public void setIconStyleHotSpot(int hotspot) {
		if (iconStyle == null) {
			Logger.e(TAG, "setIconStyleHotSpot(" + hotspot + "), " +
					"initialize IconStyle before settings hotSpot!");
			return;
		}
		
		if (hotspot == HOTSPOT_TOP_LEFT) {
			iconStyle.hotSpot = new KmlVec2(
					0.0f, KmlVec2.Units.FRACTION, 1.0f, KmlVec2.Units.FRACTION);
		} else if (hotspot == HOTSPOT_CENTER_CENTER) {
			iconStyle.hotSpot = new KmlVec2(
					0.5f, KmlVec2.Units.FRACTION, 0.5f, KmlVec2.Units.FRACTION);
		} else {
			// hotspot == HOTSPOT_BOTTOM_CENTER
			iconStyle.hotSpot = generateDefaultHotSpot();
		}
	}
	
	private static KmlVec2 generateDefaultHotSpot() {
		// HOTSPOT_BOTTOM_CENTER
		return new KmlVec2(0.5f, KmlVec2.Units.FRACTION,
				0.0f, KmlVec2.Units.FRACTION);
	}
	
	public void setIconStyleHotSpot(KmlVec2 vec2) {
		if (iconStyle == null || vec2 == null) {
			Logger.e(TAG, "setIconStyleHotSpot(" + vec2 + "), " +
					"initialize IconStyle before settings hotSpot or hotSpot is null!");
			return;
		}
		
		iconStyle.hotSpot = vec2;
	}
	
	// LINE STYLE

	public LineStyle getLineStyle() {
		return lineStyle;
	}
	
    public void removeLineStyle() {
    	lineStyle = null;
    }

    public void setLineStyle(int color, float width) {
    	setLineStyle(ColorStyle.SIMPLE, color, width, Units.PIXELS);
    }
    
    public void setLineStyle(LineStyle.ColorStyle style, int color,
    		float width, LineStyle.Units units) {
    	lineStyle = new LineStyle();
    	lineStyle.colorStyle = style;
    	lineStyle.color = color;
    	lineStyle.width = width;
    	lineStyle.units = units;
    }
    
    // POLY STYLE
    
    public void setPolyStyle(int color, boolean fill, boolean outline) {
    	polyStyle = new PolyStyle();
    	polyStyle.color = color;
    	polyStyle.fill = fill;
    	polyStyle.outline = outline;
    }
    
    public void removePolyStyle() {
    	polyStyle = null;
    }
	
    /*******************************************/
    /*                 STYLES                  */
    /*******************************************/

    public static final int BLACK       = 0xFF000000;
    public static final int WHITE       = 0xFFFFFFFF;
    
	public static final int COLOR_DEFAULT = WHITE;

	public static class BalloonStyle {
		public enum DisplayMode {
			DEFAULT, HIDE
		}
		
		public int bgColor = WHITE;
		public int textColor = BLACK;
		public String text = "";
		public DisplayMode displayMode = DisplayMode.DEFAULT;
	}
	
	
	public static class IconStyle {
		
		public int color;
		public float scale;
		public float heading;
		public String iconHref;
		public KmlVec2 hotSpot;
		
		// temporary variables for Locus usage that are not serialized
		// and are for private Locus usage only
		public Object icon;
		public int iconW;
		public int iconH;
		
		public IconStyle() {
			color = COLOR_DEFAULT;
			scale = 1.0f;
			heading = 0.0f;
			iconHref = null;
			hotSpot = generateDefaultHotSpot();
			icon = null;
			iconW = -1;
			iconH = -1;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("IconStyle [");
			sb.append("color:").append(color);
			sb.append(", scale:").append(scale);
			sb.append(", heading:").append(heading);
			sb.append(", iconHref:").append(iconHref);
			sb.append(", hotSpot:").append(hotSpot);
			sb.append(", icon:").append(icon);
			sb.append(", iconW:").append(iconW);
			sb.append(", iconH:").append(iconH);
			sb.append("]");
			return sb.toString();
		}
	}
	
	public static class LabelStyle {
		
		public int color = COLOR_DEFAULT;
		public float scale = 1.0f;
	}
	
	public static class LineStyle {
		
		public enum ColorStyle {
			SIMPLE, BY_SPEED, BY_ALTITUDE
		}
		
		public enum Units {
			PIXELS, METRES
		}
		
		// KML styles
		public int color = COLOR_DEFAULT;
		public float width = 1.0f;
		public int gxOuterColor = COLOR_DEFAULT;
		public float gxOuterWidth = 0.0f;
		public float gxPhysicalWidth = 0.0f;
		public boolean gxLabelVisibility = false;
		
		// Locus extension
		public ColorStyle colorStyle = ColorStyle.SIMPLE;
		public Units units = Units.PIXELS;
	}
	
	public static class ListStyle {
		
		public enum ListItemType {
			CHECK, CHECK_OFF_ONLY, CHECK_HIDE_CHILDREN, RADIO_FOLDER
		}
		
		public ListItemType listItemType = ListItemType.CHECK;
		public int bgColor = WHITE;
		public ArrayList<ItemIcon> itemIcons = new ArrayList<ItemIcon>();

		public static class ItemIcon {

			public enum State {
				OPEN, CLOSED, ERROR, FETCHING0, FETCHING1, FETCHING2
			}
			
			public State state = State.OPEN;
			public String href = "";
		}
	}
	
	public static class PolyStyle {
		
		public int color = COLOR_DEFAULT;
		public boolean fill = true;
		public boolean outline = true;
	}
}

