/** 
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


// $Id: SimplePropertyValue.java,v 1.3 2004/07/06 15:07:17 jesper Exp $
package net.infonode.properties.propertymap.value;

import net.infonode.properties.propertymap.PropertyMapImpl;
import net.infonode.util.Printer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
public class SimplePropertyValue implements PropertyValue {
  private Object value;

  public SimplePropertyValue(Object value) {
    this.value = value;
  }

  public PropertyValue getParent() {
    return null;
  }

  public Object get(PropertyMapImpl object) {
    return value;
  }

  public Object getWithDefault(PropertyMapImpl object) {
    return value;
  }

  public PropertyValue getSubValue(PropertyMapImpl object) {
    return null;
  }

  public void unset() {
  }

  public String toString() {
    return value.toString();
  }

  public void dump(Printer printer) {
    printer.println(toString());
  }

  public boolean equals(Object obj) {
    return obj != null && obj instanceof SimplePropertyValue && ((SimplePropertyValue) obj).value.equals(value);
  }

  public int hashCode() {
    return value.hashCode();
  }

  public void write(ObjectOutputStream out) throws IOException {
    out.writeInt(ValueDecoder.SIMPLE);
    out.writeObject(value);
  }

  public static PropertyValue decode(ObjectInputStream in) throws IOException {
    try {
      return new SimplePropertyValue(in.readObject());
    }
    catch (ClassNotFoundException e) {
      throw new IOException(e.getMessage());
    }
  }
}
