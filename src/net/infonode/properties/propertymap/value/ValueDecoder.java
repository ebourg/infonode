/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R) 
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA 02111-1307, USA.
 */


// $Id: ValueDecoder.java,v 1.7 2005/02/16 11:28:15 jesper Exp $
package net.infonode.properties.propertymap.value;

import net.infonode.properties.base.Property;
import net.infonode.properties.propertymap.PropertyMapImpl;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.7 $
 */
public class ValueDecoder {
  public static final int SIMPLE = 0;
  public static final int REF = 1;

  private ValueDecoder() {
  }

  public static PropertyValue decode(ObjectInputStream in, PropertyMapImpl propertyObject, Property property) throws IOException {
    int type = in.readInt();

    switch (type) {
      case SIMPLE:
        return SimplePropertyValue.decode(in);

      case REF:
        return PropertyRefValue.decode(in, propertyObject, property);

      default:
        throw new IOException("Invalid value type!");
    }
  }

  public static void skip(ObjectInputStream in) throws IOException {
    int type = in.readInt();

    switch (type) {
      case SIMPLE:
        SimplePropertyValue.skip(in);
        break;

      case REF:
        PropertyRefValue.skip(in);
        break;

      default:
        throw new IOException("Invalid value type!");
    }
  }
}
