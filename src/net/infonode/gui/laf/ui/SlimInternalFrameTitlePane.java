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


// $Id: SlimInternalFrameTitlePane.java,v 1.4 2005/02/16 11:28:11 jesper Exp $
package net.infonode.gui.laf.ui;

import net.infonode.gui.ButtonFactory;
import net.infonode.gui.border.EdgeBorder;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
public class SlimInternalFrameTitlePane extends BasicInternalFrameTitlePane {
  public SlimInternalFrameTitlePane(JInternalFrame f) {
    super(f);
    setBorder(new EdgeBorder(UIManager.getColor("controlDkShadow"), false, true, false, false));
  }

  protected void createButtons() {
    iconButton = ButtonFactory.createFlatHighlightButton(iconIcon,
                                                         UIManager.getString("InternalFrame.iconButtonToolTip"),
                                                         0,
                                                         iconifyAction);
    iconButton.setFocusable(false);

    closeButton = ButtonFactory.createFlatHighlightButton(closeIcon,
                                                          UIManager.getString("InternalFrame.closeButtonToolTip"),
                                                          0,
                                                          closeAction);
    closeButton.setFocusable(false);

    maxButton = ButtonFactory.createFlatHighlightButton(maxIcon,
                                                        UIManager.getString("InternalFrame.maxButtonToolTip"),
                                                        0,
                                                        maximizeAction);
    maxButton.setFocusable(false);
  }

}
