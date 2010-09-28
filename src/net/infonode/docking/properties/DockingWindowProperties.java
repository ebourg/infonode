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


// $Id: DockingWindowProperties.java,v 1.8 2004/09/28 15:07:29 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.propertymap.*;

/**
 * Properties and property values common for all docking windows.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.8 $
 */
public class DockingWindowProperties extends PropertyMapContainer {
  /**
   * Property group containing all docking window properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Docking Window Properties", "");

  /**
   * Property values for the window tab which is used in tabbed panels.
   */
  public static final PropertyMapProperty TAB_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Tab Properties",
                              "Property values for the window tab which is used in tabbed panels.",
                              WindowTabProperties.PROPERTIES);


  /**
   * Creates an empty property object.
   */
  public DockingWindowProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property map containing the map.
   *
   * @param map the property map
   */
  public DockingWindowProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public DockingWindowProperties(DockingWindowProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public DockingWindowProperties addSuperObject(DockingWindowProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Removes the last added super object.
   *
   * @return this
   * @since IDW 1.1.0
   */
  public DockingWindowProperties removeSuperObject() {
    getMap().removeSuperMap();
    return this;
  }

  /**
   * Returns the property values for the window tab which is used in tabbed panels.
   *
   * @return the property values for the window tab which is used in tabbed panels
   */
  public WindowTabProperties getTabProperties() {
    return new WindowTabProperties(TAB_PROPERTIES.get(getMap()));
  }

}
