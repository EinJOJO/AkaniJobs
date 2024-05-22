package it.einjojo.jobs.util;

public class AkaniUtil {
    public static boolean isAkaniCoreAvailable() {
        try {
            Class.forName("it.einjojo.akani.core.api.AkaniCore");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
