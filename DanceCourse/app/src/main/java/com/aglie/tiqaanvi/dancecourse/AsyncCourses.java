package app15.dansapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by umyhafzaqa on 2016-05-12.
 */
public class AsyncCourses extends AsyncTask<String, Void, ArrayList<Course>> {
    CoursesDelegate delegate; //callback interface
    ArrayList<Course> lists;
    HttpURLConnection connection;
    URL url;
    private static String URLEN =  "http://192.168.56.1:80/";  //"http://192.168.42.203:80/";
    //"http://10.160.16.143:80/"; //"http://localhost/dansapp/";

    public AsyncCourses(){

    }
    public AsyncCourses(CoursesDelegate delegate) //Activity myContext);
    {
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute(){
        lists = new ArrayList<Course>();
        // tasks = new ArrayList<Task>();
    }
    @Override
    protected ArrayList<Course> doInBackground(String... params) {
        StringBuffer response = null, responsePost=null, responsePut = null;
        String result = null;
        JSONArray list = null;
        try {
            url = new URL(this.URLEN + params[1]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Log.i("TAGURL", url.toString());
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod(params[0]); //setting RequestMethod to GET or POST

            switch(params[0]){
                //GET Code here
                case "GET":
                    connection.setDoOutput(false);  //skicka data till servern, POST    //no body
                    connection.setDoInput(true);    //få svar in från servern, GET
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    Log.i("TAG", "Response Code for GET is: " + responseCode);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    response = new StringBuffer();  //response är StringBuffer
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.i("TAG", "Response text for lists : " + response.toString());
                    list = new JSONArray(response.toString());
                    connection.disconnect();
                    for (int i = 0; i < list.length(); i++) {
                        Course course = new Course();
                        JSONObject listObject = (JSONObject) list.get(i);
                        int courseId = listObject.getInt("courseId");
                        String title = listObject.getString("title");
                        String description = listObject.getString("description");

                        course.setCourseId(courseId);
                        course.setTitle(title);
                        course.setDescription(description);
                        Log.i("Tag", "course id: " + courseId + ", " + title + ", " + description);
                        lists.add(course);
                    }

                    break;
                //POST, add new list
                case "POST":
                    connection.setDoOutput(true);  //skicka data till servern, POST
                    connection.setDoInput(true);    //få svar in från servern, GET
                    connection.setRequestProperty("Content-type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();


                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    //skapa en json sträng
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("title", params[2]);
                    String outputString = jsonObj.toString(); //"\"{ \"title\":\"aNewCheckList\"}\"";
                    Log.i("TAG", "outputString innehåller = : " + outputString);

                    writer.write(outputString);
                    writer.flush();
                    writer.close();
                    os.close();

                    int resCode = connection.getResponseCode();
                    Log.i("TAG", "Response code for POST: " + resCode);
                    BufferedReader brPost = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String inputLinePost = null;
                    responsePost = new StringBuffer();  //response är StringBuffer
                    while ((inputLinePost = brPost.readLine()) != null) {
                        responsePost.append(inputLinePost);
                    }
                    brPost.close();
                    connection.disconnect();
                    Log.i("TAGPOST", "ResponsePost text is : " + responsePost.toString());

                    //calling GET after POST, not good, so optimize this code later on
                    /*JSONObject jObj = new JSONObject(responsePost.toString());
                    Note note1 = new Note();
                    note1.setDate(jObj.getString("created_at"));
                    note1.setTitle(jObj.getString("title"));
                    note1.setId(jObj.getInt("id"));
                    lists.add(note1);*/
                    break;
                case "PUT":
                    connection.setDoOutput(true);  //skicka data till servern, POST
                    connection.setDoInput(true);    //få svar in från servern, GET
                    connection.setRequestProperty("Content-type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();

                    OutputStream osPut = connection.getOutputStream();
                    BufferedWriter writerPut = new BufferedWriter(new OutputStreamWriter(osPut, "UTF-8"));
                    //skapa en json sträng
                    JSONObject jObj = new JSONObject();
                    jObj.put("title", params[2]);
                    String putString = jObj.toString(); //"\"{ \"title\":\"aNewCheckList\"}\"";
                    Log.i("TAG", "outputString innehåller = : " + putString);

                    writerPut.write(putString);
                    writerPut.flush();
                    writerPut.close();
                    osPut.close();

                    int putResponseCode = connection.getResponseCode();
                    Log.i("TAG", "Response code for POST: " + putResponseCode);
                    BufferedReader inPut = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String inputLinePut = null;
                    responsePut = new StringBuffer();  //responsePut är StringBuffer
                    while ((inputLinePut = inPut.readLine()) != null) {
                        responsePut.append(inputLinePut);
                    }
                    inPut.close();
                    connection.disconnect();
                    Log.i("TAGPUT", "ResponsePut text is : " + responsePut.toString());
                    break;

                case "DELETE":
                    connection.setDoOutput(false);  //skicka data till servern, POST    //no body
                    connection.setDoInput(true);    //få svar in från servern, GET
                    Log.i("TAGUri", params[1]);
                    connection.setRequestMethod("DELETE");
                    connection.connect();
                    int deleteResponse = connection.getResponseCode();
                    Log.i("TAGDel", "Delete response code " + deleteResponse);
                    connection.disconnect();
                    break;
                default:
                    break;
            }
            //lists.clear();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }

    @Override
    protected void onPostExecute(ArrayList<Course> result){

    }
}
