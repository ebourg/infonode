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


// $Id: TabbedPanelHoverPolicyProperty.java,v 1.3 2005/02/16 11:28:15 jesper Exp $

package net.infonode.tabbedpanel;

import net.infonode.properties.base.PropertyGroup;
import net.infonode.properties.types.EnumProperty;
import net.infonode.properties.util.PropertyValueHandler;

/**
 * Property for TabbedPanelHoverPolicy
 *
 * @author johan
 * @version $Revision: 1.3 $
 * @see TabbedPanelHoverPolicy
 * @since ITP 1.3.0
 */
public class TabbedPanelHoverPolicyProperty extends EnumProperty {
  /**
   * Constructs a TabbedPanelHoverPolicyProperty object
   *
   * @param group        property group
   * @param name         property name
   * @param description  property description
   * @param valueStorage storage for property
   */
  public TabbedPanelHoverPolicyProperty(PropertyGroup group,
                                        String name,
                                        String description,
                                        PropertyValueHandler valueStorage) {
    super(group,
          name,
          TabbedPanelHoverPolicy.class,
          description,
          valueStorage,
          TabbedPanelHoverPolicy.values());
  }

  /**
   * Gets the TabbedPanelHoverPolicy
   *
   * @param object storage object for property
   * @return the TabbedPanelHoverPolicy
   */
  public TabbedPanelHoverPolicy get(Object object) {
    return (TabbedPanelHoverPolicy) getValue(object);
  }

  /**
   * Sets the TabbedPanelHoverPolicy
   *
   * @param object storage object for property
   * @param policy the TabbedPanelHoverPolicy
   */
  public void set(Object object, TabbedPanelHoverPolicy policy) {
    setValue(object, policy);
  }
}