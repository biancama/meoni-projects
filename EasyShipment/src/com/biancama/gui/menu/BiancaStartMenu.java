package com.biancama.gui.menu;

import javax.swing.JMenu;

import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class BiancaStartMenu extends JMenu {

    private static final long serialVersionUID = -7833871754471332953L;

    public BiancaStartMenu(String name, String icon) {
        super(BiancaL.L(name, null));
        this.setIcon(BiancaTheme.II(icon, 16, 16));
    }

}
