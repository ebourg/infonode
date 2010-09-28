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


//$Id: TabAreaComponentsProperties.java,v 1.9 2004/11/11 14:10:33 jesper Exp $

package net.infonode.tabbedpanel;

import net.infonode.properties.gui.util.ComponentProperties;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.BooleanProperty;

/**
 * TabAreaComponentsProperties holds all visual properties for the area in a
 * tabbed panel's tab area where the tab area components (scroll buttons, tab
 * drop down list and components set by calling setTabAreaComponents in a tabbed
 * panel) are shown. TabbedPanelProperties contains TabAreaComponentsProperties.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.9 $
 * @see TabbedPanel
 * @see TabbedPanelProperties
 * @since ITP 1.1.0
 */
public class TabAreaComponentsProperties extends PropertyMapContainer {
  /**
   * A property group for all properties in TabAreaComponentsProperties
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Tab Area Properties",
                                                                         "Properties for the TabbedPanel class.");

  /**
   * Stretch enabled property
   *
   * @see #setStretchEnabled
   * @see #getStretchEnabled
   */
  public static final BooleanProperty STRETCH_ENABLED = new BooleanProperty(PROPERTIES, "Stretch Enabled", "Stretch components to be as high as tabs if tabs are higher than components.",
                                                                            PropertyMapValueHandler.INSTANCE);

  /**
   * Properties for the component
   *
   * @see #getComponentProperties
   */
  public static final PropertyMapProperty COMPONENT_PROPERTIES = new PropertyMapProperty(PROPERTIES,
                                                                                         "Component Properties",
                                                                                         "Properties for tab area components area.",
                                                                                         ComponentProperties.PROPERTIES);

  /**
   * Properties for the shaped panel
   *
   * @see #getShapedPanelProperties
   * @since ITP 1.2.0
   */
  public static final PropertyMapProperty SHAPED_PANEL_PROPERTIES = new PropertyMapProperty(PROPERTIES,
                                                                                            "Shaped Panel Properties",
                                                                                            "Properties for shaped tab area components area.",
                                                                                            ShapedPanelProperties.PROPERTIES);

  /**
   * Constructs a TabAreaComponentsProperties object with the given object as
   * property storage
   *
   * @param object object to store properties in
   */
  public TabAreaComponentsProperties(PropertyMap object) {
    super(object);
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param superObject the object from which to inherit property values
   * @return this
   */
  public TabAreaComponentsProperties addSuperObject(TabAreaComponentsProperties superObject) {
    getMap().addSuperMap(superObject.getMap());
    return this;
  }

  /**
   * Removes a super object.
   *
   * @return this
   */
  public TabAreaComponentsProperties removeSuperObject() {
    getMap().removeSuperMap();
    return this;
  }

  /**
   * Gets if components should be stretched to same height as tabs if tabs are
   * higher than components.
   *
   * @return true if stretch is enabled, otherwise false
   */
  public boolean getStretchEnabled() {
    return STRETCH_ENABLED.get(getMap());
  }

  /**
   * Sets if components should be stretched to same height as tabs if tabs are
   * higher than components.
   *
   * @param enabled true for stretch, otherwise false
   * @return this TabAreaComponentsProperties
   */
  public TabAreaComponentsProperties setStretchEnabled(boolean enabled) {
    STRETCH_ENABLED.set(getMap(), enabled);
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

  /**
   * Gets the shaped panel properties
   *
   * @return shaped panel properties
   * @since ITP 1.2.0
   */
  public ShapedPanelProperties getShapedPanelProperties() {
    return new ShapedPanelProperties(SHAPED_PANEL_PROPERTIES.get(getMap()));
  }
}