//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.gui.swing.interfaces;

import com.biancama.gui.swing.borders.InsideShadowBorder;

/**
 * A JPanel with an Dropshadow border on top
 */
public abstract class DroppedPanel extends SwitchPanel {

    private static final long serialVersionUID = -4849858185626557726L;

    public DroppedPanel() {
        InsideShadowBorder border = new InsideShadowBorder(5, 0, 0, 0);
        border.setBorderInsets(0, 3, 0, 0);
        this.setBorder(border);
    }
}
