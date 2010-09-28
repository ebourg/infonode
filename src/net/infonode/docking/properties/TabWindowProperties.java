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


// $Id: TabWindowProperties.java,v 1.8 2004/07/05 12:56:37 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.propertymap.*;
import net.infonode.tabbedpanel.TabbedPanelProperties;

/**
 * Properties and property values for tab windows.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.8 $
 */
public class TabWindowProperties extends PropertyMapContainer {
  /**
   * Property group containing all tab window properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Tab Window Properties", "");

  /**
   * Property values for the tabbed panel in the tab window.
   */
  public static final PropertyMapProperty TABBED_PANEL_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Tabbed Panel Properties",
                              "Property values for the tabbed panel in the tab window.",
                              TabbedPanelProperties.PROPERTIES);

  /**
   * Default property values for the window tabs in the tab window.
   */
  public static final PropertyMapProperty TAB_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Tab Properties",
                              "Default property values for the window tabs in the tab window.",
                              WindowTabProperties.PROPERTIES);


  /**
   * Creates an empty property object.
   */
  public TabWindowProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property object containing the map.
   *
   * @param map the property map
   */
  public TabWindowProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public TabWindowProperties(TabWindowProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public TabWindowProperties addSuperObject(TabWindowProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Returns the property values for the tabbed panel in the tab window.
   * @return the property values for the tabbed panel in the tab window
   */
  public TabbedPanelProperties getTabbedPanelProperties() {
    return new TabbedPanelProperties(TABBED_PANEL_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the default property values for the window tabs in the tab window.
   * @return the default property values for the window tabs in the tab window
   */
  public WindowTabProperties getTabProperties() {
    return new WindowTabProperties(TAB_PROPERTIES.get(getMap()));
  }

}
