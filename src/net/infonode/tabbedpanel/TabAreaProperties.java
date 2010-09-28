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


// $Id: TabAreaProperties.java,v 1.16 2004/07/06 15:08:32 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.properties.gui.util.ComponentProperties;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapContainer;
import net.infonode.properties.propertymap.PropertyMapGroup;
import net.infonode.properties.propertymap.PropertyMapProperty;

/**
 * TabAreaProperties holds all visual properties for a tabbed panel's tab area.
 * TabbedPanelProperties contains TabAreaProperties.
 *
 * @see TabbedPanel
 * @see TabbedPanelProperties
 * @author $Author: jesper $
 * @version $Revision: 1.16 $
 */
public class TabAreaProperties extends PropertyMapContainer {
  /**
   * A property group for all properties in TabAreaProperties
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Tab Area Properties", "Properties for the TabbedPanel class.");

  /**
   * Properties for the component
   *
   * @see #getComponentProperties
   */
  public static final PropertyMapProperty COMPONENT_PROPERTIES = new PropertyMapProperty(
          PROPERTIES, "Component Properties", "Properties for tab area component.", ComponentProperties.PROPERTIES);

  /**
   * Constructs a TabAreaProperties object with the given object
   * as property storage
   *
   * @param object object to store properties in
   */
  public TabAreaProperties(PropertyMap object) {
    super(object);
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param superObject the object from which to inherit property values
   * @return this
   */
  public TabAreaProperties addSuperObject(TabAreaProperties superObject) {
  	getMap().addSuperMap(superObject.getMap());
  	return this;
  }
  
  /**
   * Gets the component properties
   *
   * @return component properties
   */
  public ComponentProperties getComponentProperties() {
    return new ComponentProperties(COMPONENT_PROPERTIES.get(getMap()));
  }
}
