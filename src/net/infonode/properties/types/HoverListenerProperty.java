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


// $Id: HoverListenerProperty.java,v 1.4 2005/02/16 11:28:15 jesper Exp $
package net.infonode.properties.types;

import net.infonode.gui.hover.HoverListener;
import net.infonode.properties.base.PropertyGroup;
import net.infonode.properties.util.PropertyValueHandler;
import net.infonode.properties.util.ValueHandlerProperty;

/**
 * A property of type {@link HoverListener}.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
public class HoverListenerProperty extends ValueHandlerProperty {
  /**
   * Constructor.
   *
   * @param group        the property group
   * @param name         the property name
   * @param description  the property description
   * @param valueHandler handles values for this property
   */
  public HoverListenerProperty(PropertyGroup group,
                               String name,
                               String description,
                               PropertyValueHandler valueHandler) {
    super(group, name, HoverListener.class, description, valueHandler);
  }

  /**
   * Returns the hover listener value of this property in a value container.
   *
   * @param valueContainer the value container
   * @return the hover listener value of this property
   */
  public HoverListener get(Object valueContainer) {
    return (HoverListener) getValue(valueContainer);
  }

  /**
   * Sets the hover listener value of this property in a value container.
   *
   * @param valueContainer the value container
   * @param listener       the hover listener value
   */
  public void set(Object valueContainer, HoverListener listener) {
    setValue(valueContainer, listener);
  }
}
