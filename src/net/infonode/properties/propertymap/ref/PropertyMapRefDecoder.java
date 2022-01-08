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


// $Id: PropertyMapRefDecoder.java,v 1.4 2004/09/22 14:32:50 jesper Exp $
package net.infonode.properties.propertymap.ref;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
public class PropertyMapRefDecoder {
  public static final int PARENT = 0;
  public static final int THIS = 1;
  public static final int PROPERTY_OBJECT_PROPERTY = 2;
  public static final int COMPOSITE = 3;

  private PropertyMapRefDecoder() {
  }

  public static PropertyMapRef decode(ObjectInputStream in) throws IOException {
    int type = in.readInt();

    switch (type) {
      case PARENT:
        return ParentMapRef.INSTANCE;

      case THIS:
        return ThisPropertyMapRef.INSTANCE;

      case PROPERTY_OBJECT_PROPERTY:
        return PropertyMapPropertyRef.decode(in);

      case COMPOSITE:
        return CompositeMapRef.decode(in);

      default:
        throw new IOException("Invalid property object ref type!");
    }
  }
}
