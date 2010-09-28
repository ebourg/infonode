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


// $Id: WindowTabProperties.java,v 1.6 2004/08/11 13:47:58 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.base.Property;
import net.infonode.properties.propertymap.*;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.tabbedpanel.titledtab.TitledTabStateProperties;

/**
 * Properties and property values for window tabs.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public class WindowTabProperties extends PropertyMapContainer {
  /**
   * Property group containing all window tab properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Tab Properties", "");

  /**
   * Property values for the titled tab used in the tab.
   */
  public static final PropertyMapProperty TITLED_TAB_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Titled Tab Properties",
                              "Property values for the titled tab used in the tab.",
                              TitledTabProperties.PROPERTIES);

  /**
   * Property values for the titled tab when it is focused or a component in the tab's content component has focus.
   */
  public static final PropertyMapProperty FOCUSED_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Focused Properties",
                              "Property values for the titled tab when is focused or a component in the tab's content component has focus.\n" +
                              "The property values are inherited from '" + TITLED_TAB_PROPERTIES + "." +
                              TitledTabProperties.HIGHLIGHTED_PROPERTIES + "'.",
                              TitledTabStateProperties.PROPERTIES);

  /**
   * Property values for the tab buttons when the tab is in the normal state.
   */
  public static final PropertyMapProperty NORMAL_BUTTON_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Normal Button Properties",
                              "Property values for the tab buttons when the tab is in the normal state.",
                              WindowTabStateProperties.PROPERTIES);

  /**
   * Property values for the tab buttons when the tab is highlighted.
   */
  public static final PropertyMapProperty HIGHLIGHTED_BUTTON_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Highlighted Button Properties",
                              "Property values for the tab buttons when the tab is highlighted.",
                              WindowTabStateProperties.PROPERTIES);

  /**
   * Property values for the tab buttons when the tab is focused or a component in the tab's content component has focus.
   */
  public static final PropertyMapProperty FOCUSED_BUTTON_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Focused Button Properties",
                              "Property values for the tab buttons when the tab is focused or a component in the tab's content component has focus.",
                              WindowTabStateProperties.PROPERTIES);

  static {
    WindowTabProperties properties = new WindowTabProperties(PROPERTIES.getDefaultMap());
    PropertyMapProperty[] buttonProperties = {WindowTabStateProperties.CLOSE_BUTTON_PROPERTIES,
                                              WindowTabStateProperties.MINIMIZE_BUTTON_PROPERTIES,
                                              WindowTabStateProperties.RESTORE_BUTTON_PROPERTIES};

    for (int i = 0; i < buttonProperties.length; i++) {
      for (int j = 0; j < WindowTabButtonProperties.PROPERTIES.getPropertyCount(); j++) {
        Property property = WindowTabButtonProperties.PROPERTIES.getProperty(j);

        // Highlighted properties inherits from normal properties
        buttonProperties[i].get(properties.getHighlightedButtonProperties().getMap()).createRelativeRef(
            property,
            buttonProperties[i].get(properties.getNormalButtonProperties().getMap()),
            property);

        // Focus properties inherits from highlight properties
        buttonProperties[i].get(properties.getFocusedButtonProperties().getMap()).createRelativeRef(
            property,
            buttonProperties[i].get(properties.getHighlightedButtonProperties().getMap()),
            property);
      }
    }
  }

  /**
   * Creates an empty property object.
   */
  public WindowTabProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property object containing the map.
   *
   * @param map the property map
   */
  public WindowTabProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public WindowTabProperties(WindowTabProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public WindowTabProperties addSuperObject(WindowTabProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Returns the property values for the titled tab used in the tab.
   * @return the property values for the titled tab used in the tab
   */
  public TitledTabProperties getTitledTabProperties() {
    return new TitledTabProperties(TITLED_TAB_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the property values for the titled tab when it is focused or a component in the tab's content component has focus.
   * @return the property values for the titled tab when it is focused or a component in the tab's content component has focus
   */
  public TitledTabStateProperties getFocusedProperties() {
    return new TitledTabStateProperties(FOCUSED_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the property values for the tab buttons when the tab is in the normal state.
   * @return the property values for the tab buttons when the tab is in the normal state
   */
  public WindowTabStateProperties getNormalButtonProperties() {
    return new WindowTabStateProperties(NORMAL_BUTTON_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the property values for the tab buttons when the tab is highlighted.
   * @return the property values for the tab buttons when the tab is highlighted
   */
  public WindowTabStateProperties getHighlightedButtonProperties() {
    return new WindowTabStateProperties(HIGHLIGHTED_BUTTON_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the property values for the tab buttons when the tab is focused or a component in the tab's content
   * component has focus.
   * @return the property values for the tab buttons when the tab is focused or a component in the tab's content
   *          component has focus
   */
  public WindowTabStateProperties getFocusedButtonProperties() {
    return new WindowTabStateProperties(FOCUSED_BUTTON_PROPERTIES.get(getMap()));
  }
}
