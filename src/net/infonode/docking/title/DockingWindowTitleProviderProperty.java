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


// $Id: DockingWindowTitleProviderProperty.java,v 1.4 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking.title;

import net.infonode.properties.base.PropertyGroup;
import net.infonode.properties.util.PropertyValueHandler;
import net.infonode.properties.util.ValueHandlerProperty;

/**
 * A property that has a {@link DockingWindowTitleProvider} object as value.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 * @since IDW 1.3.0
 */
public class DockingWindowTitleProviderProperty extends ValueHandlerProperty {
  /**
   * Constructor.
   *
   * @param group        the property group
   * @param name         the property name
   * @param description  the property description
   * @param valueHandler handles values for this property
   */
  public DockingWindowTitleProviderProperty(PropertyGroup group,
                                            String name,
                                            String description,
                                            PropertyValueHandler valueHandler) {
    super(group, name, DockingWindowTitleProvider.class, description, valueHandler);
  }

  /**
   * Gets the {@link DockingWindowTitleProvider} value of this property in a value container.
   *
   * @param valueContainer the value container
   * @return the {@link DockingWindowTitleProvider} value of this property in a value container
   */
  public DockingWindowTitleProvider get(Object valueContainer) {
    return (DockingWindowTitleProvider) getValue(valueContainer);
  }

  /**
   * Sets the {@link DockingWindowTitleProvider} value of this property in a value container.
   *
   * @param valueContainer the value container
   * @param provider       the value
   */
  public void set(Object valueContainer, DockingWindowTitleProvider provider) {
    setValue(valueContainer, provider);
  }
}
