package com.hibernatus.hibmobtech.model;

import com.hibernatus.hibmobtech.R;

/**
 * Created by Eric on 24/11/2015.
 */

public class SiteCategoryIcon {
    public static int getIconColor(Category cat){
        if (cat != null) {
            int id = (int)cat.getId();
            switch (id) {
                case 1:
                    return R.color.md_teal_A700; // Hôtel
                case 2:
                    return R.color.md_blue_800; // Hôpital, Clinique, Santé
                case 3:
                    return R.color.md_light_blue_600; // Foyer, Association
                case 4:
                    return R.color.md_red_900; // Restaurants, Fast-food, Restauration
                case 5:
                    return R.color.md_indigo_A100; // Laverie, Pressing, Blanchisserie
                case 6:
                    return R.color.md_green_700; // Institut, Ambassade
                case 7:
                    return R.color.md_deep_purple_A700; // Mairie
                case 8:
                    return R.color.md_lime_800; // Lycée, Collège, Ecole
                case 9:
                    return R.color.md_amber_A400; // Boulangerie, Boucherie, Alimentation
                case 10:
                    return R.color.md_brown_500; // Communauté religieuse, Spiritualité
                case 11:
                    return R.color.md_pink_A400; // Salon de Coiffure, Beauté, Soins
                case 12:
                    return R.color.md_pink_A100; // Crèches
                case 13:
                    return R.color.md_brown_900; // Foyer
            }
        }
        return R.color.md_blue_grey_400; // Non définie
    }
    public static String getIcon(Category cat){
        if (cat != null) {
            int id = (int)cat.getId();
            switch (id) {
                case 1:
                    return "\uf236"; // Hôtel
                case 2:
                    return "\uf0fd"; // Hôpital, Clinique, Santé
                case 3:
                    return "\uf015"; // Foyer, Association
                case 4:
                    return "\uf0f5"; // Restaurants, Fast-food, Restauration
                case 5:
                    return "\uf27e"; // Laverie, Pressing, Blanchisserie
                case 6:
                    return "\uf132"; // Institut, Ambassade
                case 7:
                    return "\uf19c"; // Mairie
                case 8:
                    return "\uf19d"; // Lycée, Collège, Ecole
                case 9:
                    return "\uf07a"; // Boulangerie, Boucherie, Alimentation
                case 10:
                    return "\uf0a2"; // Communauté religieuse, Spiritualité
                case 11:
                    return "\uf0c4"; // Salon de Coiffure, Beauté, Soins
                case 12:
                    return "\uf1a1"; // Crèches
                case 13:
                    return "\uf015"; // Foyer
            }
        }
        return "\uf059"; // Non définie
    }
}
