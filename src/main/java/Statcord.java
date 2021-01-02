import net.dv8tion.jda.api.JDA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


public class Statcord {
    public static boolean statcordActive = false;
    public static int servers = 0;
    public static int users = 0;
    public static int commandsRun = 0;
    public static String key;
    public static String id;
    public static int memactive = 0;
    public static int memload= 0;
    public static int cpuload = 0;
    public static String bandwidth = "0";
    public static JDA jda = null;
    public static JSONArray popcmd = new JSONArray();
    public static JSONArray activeuser = new JSONArray();

    public static void start(String id, String key, JDA jda, boolean autopost) {
        
        System.out.println("Statcord started with this: "+id+" "+key+" "+jda.toString());

        //save important stuff
        Statcord.jda = jda;
        Statcord.key = key;
        Statcord.id = id;

        //make it active
        statcordActive = true;

        if (autopost) {
            autorun();
            System.out.println("[Statcord] autorun activated!");
        }

    }
    //manually updating Stats
    public static void updateStats() throws IOException, InterruptedException {
        System.out.println("manually updating Statcord!");

        servers = jda.getGuilds().size();
        users = jda.getUsers().size();
        memactive = (int) Runtime.getRuntime().totalMemory();
        memload = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

        JSONObject post = new JSONObject();
        post.put("id", id);
        post.put("key", key);
        post.put("servers" , String.valueOf(servers));
        post.put("users", String.valueOf(users));
        post.put("active",activeuser);
        post.put("commands",String.valueOf(commandsRun));
        post.put("popular",popcmd);
        post.put("memactive",String.valueOf(memactive));
        post.put("memload","0");
        post.put("cpuload","0");
        post.put("bandwidth","0");

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
            System.out.println("[Statcord]You can not use 'commandPost' because it is not active!");
            return;
        }
        System.out.println("Doing it!");

        // popular cmds
        JSONObject obj = new JSONObject();
        if(!userexists(popcmd,command)) {
            obj.put("name", command);
            obj.put("count", 1);
        }else{
            for(int i=0; i< popcmd.length();i++){
                if(popcmd.getJSONObject(i).getString("name").equalsIgnoreCase(command)){
                    int test = popcmd.getJSONObject(i).getInt("count") +1;
                    popcmd.getJSONObject(i).put("count", test);
                    break;
                }
            }
        }
        popcmd.put(obj);
        commandsRun++;

        //active users
        if(!activeuser.isEmpty()) {
            if (activeuser.toString().contains(author)) {
                activeuser.put(author);
            }
        }

        //When popular cmds are higher than 5, it gets shortened because Statcords only accepts 5 commands
        if(popcmd.length() <5){
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < popcmd.length(); i++) {
                jsonValues.add(popcmd.getJSONObject(i));
            }
            jsonValues.sort(new Comparator<JSONObject>() {
                private static final String KEY_NAME = "count";

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = "";
                    String valB = "";

                    try {
                        valA = (String) a.get(KEY_NAME);
                        valB = (String) b.get(KEY_NAME);
                    } catch (JSONException ignored) {
                    }
                    return valA.compareTo(valB);

                }
            });
            popcmd=new JSONArray();
            for (int i = 0; i < jsonValues.size(); i++) {
                popcmd.put(jsonValues.get(i));
            }

        }

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

        if(response.body().contains("Success")){
            System.out.println("Updated Stats on Statcord!");
        }
    }

    //autorun set to 1m
    public static void autorun() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() throws NullPointerException {
                System.out.println("[Statcord] Automatic update!");
                servers = jda.getGuilds().size();
                users = jda.getUsers().size();
                memactive = (int) Runtime.getRuntime().totalMemory();
                memload = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

                JSONObject post = new JSONObject();
                post.put("id", id);
                post.put("key", key);
                post.put("servers" , String.valueOf(servers));
                post.put("users", String.valueOf(users));
                post.put("active",activeuser);
                post.put("commands",String.valueOf(commandsRun));
                post.put("popular",popcmd);
                post.put("memactive",String.valueOf(memactive));
                post.put("memload","0");
                post.put("cpuload","0");
                post.put("bandwidth","0");

                String body = post.toString();

                try {
                    post(body);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                commandsRun = 0;
                popcmd = new JSONArray();
                activeuser = new JSONArray();
            }
        }, 5000, 60*1000);
    }
}
