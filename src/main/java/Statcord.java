import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.JDA;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Timer;
import java.util.TimerTask;

public class Statcord {
    private static boolean statcordActive = false;
    private static int servers = 0;
    private static int users = 0;
    private static int commandsRun = 0;
    private static String key;
    private static String id;
    private static int memactive = 0; // need help pls
    private static int memload = 0; // need help pls
    private static int cpuload = 0; // need help pls
    private static String bandwidth = "0"; // need help pls

    private static JDA jda = null;
    private static JSONArray popcmd = new JSONArray();
    private static JSONArray activeuser = new JSONArray();
    private static boolean autopost = false;

    public static void start(String id, String key, JDA jda, boolean autopost) {
        System.out.println("\u001B[33mStatcord started with this: " + id + " " + key + " " + jda.toString() + "\u001B[0m");

        //save important stuff
        Statcord.jda = jda;
        Statcord.key = key;
        Statcord.id = id;

        //make it active
        statcordActive = true;

        if (autopost) {
            autorun();
            System.out.println("\u001B[33m!!! [Statcord] autorun activated!\u001B[0m");
            Statcord.autopost = true;
        }
    }

    //some booleans for users
    public static boolean isStatcordActive() {
        return statcordActive;
    }

    public static boolean isAutopostActive() {
        return autopost;
    }

    //manually updating Stats
    public static void updateStats() throws IOException, InterruptedException {
        System.out.println("\u001B[33m!!! [Statcord] manually updating Statcord!\u001B[0m");

        servers = jda.getGuilds().size();
        users = jda.getUsers().size();
        memactive = (int) Runtime.getRuntime().totalMemory();
        memload = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        int memperc = (memload / memactive) * 100;
        System.out.println(popcmd.length());

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);

        double processload = osBean.getSystemCpuLoad();
        cpuload = (int) (processload * 100);


        JSONObject post = new JSONObject();
        post.put("id", id);
        post.put("key", key);
        post.put("servers", String.valueOf(servers));
        post.put("users", String.valueOf(users));
        post.put("active", activeuser);
        post.put("commands", String.valueOf(commandsRun));
        post.put("popular", popcmd);
        post.put("memactive", String.valueOf(memload));
        post.put("memload", String.valueOf(memperc));
        post.put("cpuload", String.valueOf(cpuload));
        post.put("bandwidth", "0");

        String body = post.toString();

        System.out.println(body);
        post(body);

        commandsRun = 0;
        popcmd = new JSONArray();
        activeuser = new JSONArray();
    }

    // command metrics with active users
    public static void commandPost(String command, String author) {
        if (!statcordActive) {
            System.out.println("\u001B[33m[Statcord]You can not use 'commandPost' because Statcord is not active!\u001B[0m");
            return;
        }
        System.out.println("Doing it!");

        // popular cmds
        JSONObject obj = new JSONObject();
        if(!userexists(popcmd,command)) {
            obj.put("name", command);
            obj.put("count", 1);
            popcmd.put(obj);
            commandsRun++;
        }else{
            for(int i=0; i< popcmd.length(); i++){
                if(popcmd.getJSONObject(i).getString("name").equalsIgnoreCase(command)){
                    int test = popcmd.getJSONObject(i).getInt("count") + 1;
                    popcmd.getJSONObject(i).put("count", test);
                    commandsRun++;
                    break;
                }
            }
        }

        //active users
        if (!activeuser.toString().contains(author)) {
            activeuser.put(author);
        }

        //When popular cmds are higher than 5, it gets shortened because Statcord only accepts 5 commands but! it is still working with more than 5 cmds, sooo im not going to delete them rn:)
    }

    //boolean if a value is existing in a jsonarray (for popular cmds)
    private static boolean userexists(JSONArray jsonArray, String usernameToFind) {
        return jsonArray.toString().contains("\"name\":\""+usernameToFind+"\"");
    }

    //http post to statcord
    public static void post(String body) throws IOException, InterruptedException {
        String url = "https://statcord.com/logan/stats";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type","application/json")
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.body().contains("Success")) {
            System.out.println("\u001B[33m[Statcord] Updated Stats on Statcord!\u001B[0m");
        } else {
            System.out.println("Man its not working");
            System.out.println(response.body());
        }
    }

    //autorun set to 1h
    public static void autorun() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() throws NullPointerException {
                System.out.println("\u001B[33m[Statcord] Automatic update!\u001B[0m");
                try {
                    updateStats();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 5000, 3600 * 1000);
    }
}
