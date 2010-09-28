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

// $Id: ViewProperties.java,v 1.10 2004/07/07 15:54:22 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.BooleanProperty;
import net.infonode.properties.types.IconProperty;
import net.infonode.properties.types.StringProperty;

import javax.swing.*;

/**
 * Properties and property values for views.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.10 $
 */
public class ViewProperties extends PropertyMapContainer {
  /**
   * Property group containing all view properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("View Properties", "");

  /**
   * True if the view shows it's title even though it's not in a tabbed panel with other windows.
   */
  public static final BooleanProperty ALWAYS_SHOW_TITLE =
      new BooleanProperty(PROPERTIES,
                          "Always Show Title",
                          "If true the view will always be placed in a tab window so that it's title is shown.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * The view title.
   */
  public static final StringProperty TITLE = new StringProperty(PROPERTIES,
                                                                "Title",
                                                                "The view title.",
                                                                PropertyMapValueHandler.INSTANCE);

  /**
   * The view icon.
   */
  public static final IconProperty ICON = new IconProperty(PROPERTIES,
                                                           "Icon",
                                                           "The view icon.",
                                                           PropertyMapValueHandler.INSTANCE);

  static {
    new ViewProperties(PROPERTIES.getDefaultMap()).setAlwaysShowTitle(true);
  }

  /**
   * Creates an empty property object.
   */
  public ViewProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property object containing the map.
   *
   * @param map the property map
   */
  public ViewProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public ViewProperties(ViewProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   *
   * @return this
   */
  public ViewProperties addSuperObject(ViewProperties properties) {
    getMap().addSuperMap(properties.getMap());

    return this;
  }

  /**
   * Returns true if the view shows it's title even though it's not in a tabbed panel with other windows.
   *
   * @return true if the view shows it's title even though it's not in a tabbed panel with other windows
   */
  public boolean getAlwaysShowTitle() {
    return ALWAYS_SHOW_TITLE.get(getMap());
  }

  /**
   * Set to true if the view shows it's title even though it's not in a tabbed panel with other windows.
   *
   * @param showTitle true if the view shows it's title even though it's not in a tabbed panel with other windows.
   *
   * @return this
   */
  public ViewProperties setAlwaysShowTitle(boolean showTitle) {
    ALWAYS_SHOW_TITLE.set(getMap(), showTitle);

    return this;
  }

  /**
   * Sets the view title.
   *
   * @param title the view title
   *
   * @return this
   */
  public ViewProperties setTitle(String title) {
    TITLE.set(getMap(), title);

    return this;
  }

  /**
   * Sets the view icon.
   *
   * @param icon the view icon
   *
   * @return this
   */
  public ViewProperties setIcon(Icon icon) {
    ICON.set(getMap(), icon);

    return this;
  }

  /**
   * Returns the view title.
   *
   * @return the view title
   */
  public String getTitle() {
    return TITLE.get(getMap());
  }

  /**
   * Returns the view icon.
   *
   * @return the view icon
   */
  public Icon getIcon() {
    return ICON.get(getMap());
  }
}
