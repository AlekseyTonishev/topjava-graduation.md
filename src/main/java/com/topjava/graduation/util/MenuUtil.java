package com.topjava.graduation.util;

import com.topjava.graduation.model.Menu;
import com.topjava.graduation.to.MenuTo;

public class MenuUtil {

    private MenuUtil() {
    }

    public static Menu menuFromTo(MenuTo menuTo) {
        return new Menu(menuTo.getId(), menuTo.getDate());
    }
}