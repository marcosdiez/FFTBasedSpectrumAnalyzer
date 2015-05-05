package com.marcosdiez.spectrumanalyzer;

/**
 * Created by Marcos on 17-Jan-15.
 */
public class Settings {

    // http://freesense.no-ip.org:8080/ScadaBR/httpds?__device=blah&porta=1

    public static String server_protocol = "http";
    public static String server = "freesense.no-ip.org";
    public static int server_port = 8080;
    public static String server_path = "ScadaBR/httpds";

    public static int seconds_to_sleep_between_publish_attempt = 5;

    public static boolean working_for_real = false;

    public static String server_header = server_protocol + "://" + server + ":" + server_port + "/" + server_path;
}
