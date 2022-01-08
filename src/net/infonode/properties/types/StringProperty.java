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


// $Id: StringProperty.java,v 1.4 2004/09/22 14:32:50 jesper Exp $
package net.infonode.properties.types;

import net.infonode.properties.base.PropertyGroup;
import net.infonode.properties.util.PropertyValueHandler;
import net.infonode.properties.util.ValueHandlerProperty;

/**
 * A {@link String} property.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
public class StringProperty extends ValueHandlerProperty {
  /**
   * Constructor.
   *
   * @param group        the property group
   * @param name         the property name
   * @param description  the property description
   * @param valueHandler handles values for this property
   */
  public StringProperty(PropertyGroup group, String name, String description, PropertyValueHandler valueHandler) {
    super(group, name, String.class, description, valueHandler);
  }

  /**
   * Returns the string value of this property in a value container.
   *
   * @param valueContainer the value container
   * @return the string value of this property
   */
  public String get(Object valueContainer) {
    Object value = getValue(valueContainer);
    return value == null ? null : value.toString();
  }

  /**
   * Sets the string value of this property in a value container.
   *
   * @param valueContainer the value container
   * @param value          the string value
   */
  public void set(Object valueContainer, String value) {
    setValue(valueContainer, value);
  }
}
