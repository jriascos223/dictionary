package tech.jriascos.utilities;

public class Tools {
    public static String getClasspathDir() {
        String classpath = System.getProperty("java.class.path", ".");
        String[] splitClasspathDir = classpath.split(":");
        String classpathDirectory = "";
        for (String s : splitClasspathDir) {
            if (s.matches(".*lib/.*")) {
                classpathDirectory = s;
            }
        }
    }

    return classpathDirectory;
}