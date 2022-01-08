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


// $Id: ReleaseInfoDialog.java,v 1.16 2005/12/04 13:46:04 jesper Exp $

package net.infonode.gui;

import net.infonode.util.ReleaseInfo;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ReleaseInfoDialog {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

  static {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public static void showDialog(ReleaseInfo info, String text) {
    showDialog(new ReleaseInfo[]{info}, text == null ? null : new String[]{text});
  }

  public static void showDialog(ReleaseInfo[] info, String[] text) {
    final JComponent message = constructMessage(info, text);
    JScrollPane scrollPane = new JScrollPane(message,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
      public Dimension getPreferredSize() {
        Dimension d = message.getPreferredSize();
        int height = (int) d.getHeight();
        return new Dimension((int) d.getWidth() + 50, height < 300 ? (int) super.getPreferredSize().getHeight() : 400);
      }
    };
    message.setBorder(new EmptyBorder(10, 20, 10, 20));
    scrollPane.getViewport().setBackground(Color.white);
    JOptionPane.showMessageDialog(null, scrollPane, "Product Release Information", JOptionPane.INFORMATION_MESSAGE);
  }

  private static JComponent constructMessage(ReleaseInfo[] info, String[] text) {
    Box box = new Box(BoxLayout.Y_AXIS);
    for (int i = 0; i < info.length; i++) {
      JLabel l = new JLabel("<html><body>" + (text == null || text[i] == null ? "" : text[i] + "<br>") + "<table>" +
                            "<tr><td style='font-weight: bold;'>Product Name:</td><td>"
                            + info[i].getProductName() + "</td></tr>" + "<tr><td style='font-weight: bold;'>Version:</td><td>" +
                            info[i].getProductVersion()
                            .toString() +
                            "</td></tr>"
                            + "<tr><td style='font-weight: bold;'>Build Time:</td><td>" + DATE_FORMAT.format(
                                new Date(info[i].getBuildTime())) + "</td></tr>"
                            + "<tr><td style='font-weight: bold;'>License:</td><td>" + info[i].getLicense() + "</td></tr>" +
                            "<tr><td style='font-weight: bold;'>Vendor:</td><td>" +
                            info[i].getProductVendor()
                            + "</td></tr>" + "<tr><td style='font-weight: bold;'>Homepage:</td><td>" + info[i].getHomepage() +
                            "</td></tr>" +
                            "</table></body></html>");
      l.setFont(l.getFont().deriveFont(Font.PLAIN));
      l.setBorder(new CompoundBorder(new EmptyBorder(0, 0, i == info.length - 1 ? 0 : 10, 0),
                                     new TitledBorder(" " + info[i].getProductName() + " ")));
      box.add(l);
    }
    return box;
  }
}