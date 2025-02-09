package com.island.simulation.services;
public class DunaBanner {
    public static void displayBanner(){

        final String ANSI_BOLD = "\033[1m";
        final String ANSI_COLOR = "\033[38;2;218;165;32m";
        final String ANSI_RESET = "\033[0m";

        // ASCII-арт буквы D
        String[] dArt = {
                "  ____  ",
                " |  _ \\ ",
                " | | | |",
                " | |_| |",
                " |____/ "
        };

        // ASCII-арт буквы U
        String[] uArt = {
                " _   _ ",
                "| | | |",
                "| | | |",
                "| |_| |",
                " \\___/ "
        };

        // ASCII-арт буквы N
        String[] nArt = {
                " _   _ ",
                "| \\ | |",
                "|  \\| |",
                "| |\\  |",
                "|_| \\_|"
        };

        // ASCII-арт буквы A (как в предыдущем примере)
        String[] aArt = {
                "    ___  ",
                "   /   \\ ",
                "  / /\\  \\",
                " / ____  \\",
                "/_/    \\_ \\"
        };

        int height = dArt.length; // все массивы имеют 5 строк
        for (int i = 0; i < height; i++) {
            String line = dArt[i] + "   " + uArt[i] + "   " + nArt[i] + "   " + aArt[i];
            System.out.println(ANSI_BOLD + ANSI_COLOR + line + ANSI_RESET);
        }
    }
}


