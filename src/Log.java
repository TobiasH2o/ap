import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class Log extends Thread{

    public static boolean ENABLED = true;

    public static void logLine(java.io.Serializable a){
        StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
        Class<?> callerClass = walker.getCallerClass();
        String b = callerClass.getName();
        logLine(b, a);
    }
    public static void logLine(Object[] a) {
        StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
        Class<?> callerClass = walker.getCallerClass();
        String b = callerClass.getName();
        logLine(b, a);
    }
    public static void logLine(Object[][] a){
        StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
        Class<?> callerClass = walker.getCallerClass();
        String b = callerClass.getName();
        logLine(b, a);
    }

    private static void logLine(String a, java.io.Serializable b){
        if(ENABLED) {
            System.out.println("[" + time() + "|" + a + "]" + " " + b);
        }

    }
    private static void logLine(String a, Object[] b){
        Log.logLine("#############################################################");
        for(int c = 0; c < b.length; c++){
            Log.logLine(a, "[" + c + "/" + (b.length - 1) + "]" + b[c]);
        }
        Log.logLine("#############################################################");
    }
    private static void logLine(String a, Object[][] b){
        String[] c = new String[b.length];
        StringBuilder d = new StringBuilder();
        for(int e = 0; e < b.length; e++){
            for(int f = 0; f < b[e].length; f++){
                d.append("{").append(b[e][f]).append("}, ");
            }
            d.replace(d.length()-2, d.length(), "");
            c[e] = d.toString();
            d.delete(0, d.length());
        }
        logLine(a, c);
    }
    static String time(){
        return ("" + java.time.LocalTime.now()).substring(0, 12);
    }
}
