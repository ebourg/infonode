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


// $Id: TabbedPanelContentPanelProperties.java,v 1.21 2004/11/11 14:10:33 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.properties.gui.util.ComponentProperties;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.properties.propertymap.*;

/**
 * TabbedPanelContentPanelProperties holds all properties for a
 * {@link TabbedPanelContentPanel}. These properties affects the
 * content area of a TabbedPanel. TabbedPanelProperties contains
 * TabbedPanelContentPanelProperties.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.21 $
 * @see TabbedPanel
 * @see TabbedPanelProperties
 */
public class TabbedPanelContentPanelProperties extends PropertyMapContainer {
  /**
   * A property group for all properties in TabbedPanelContentPanelProperties
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Tab Content Panel Properties",
                                                                         "Properties for the TabContentPanel class.");

  /**
   * Properties for the component
   *
   * @see #getComponentProperties
   */
  public static final PropertyMapProperty COMPONENT_PROPERTIES = new PropertyMapProperty(PROPERTIES,
                                                                                         "Component Properties",
                                                                                         "Properties for the content area component.",
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
   * Constructs an empty TabbedPanelContentPanelProperties object
   */
  public TabbedPanelContentPanelProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Constructs a TabbedPanelContentPanelProperties map with the given map
   * as property storage
   *
   * @param map map to store properties in
   */
  public TabbedPanelContentPanelProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Constructs a TabbedPanelContentPanelProperties object that inherits its properties
   * from the given TabbedPanelContentPanelProperties object
   *
   * @param inheritFrom TabbedPanelContentPanelProperties object to inherit properties
   *                    from
   */
  public TabbedPanelContentPanelProperties(TabbedPanelContentPanelProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param superObject the object from which to inherit property values
   * @return this
   */
  public TabbedPanelContentPanelProperties addSuperObject(TabbedPanelContentPanelProperties superObject) {
    getMap().addSuperMap(superObject.getMap());
    return this;
  }

  /**
   * Removes a super object.
   *
   * @return this
   */
  public TabbedPanelContentPanelProperties removeSuperObject() {
    getMap().removeSuperMap();
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
