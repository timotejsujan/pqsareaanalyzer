package external;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Timotej Sujan
 */
public class blast_api {
    // private
    private final Pattern p_id = Pattern.compile("(RID = )(.*)(\n)");
    private final Pattern p_time = Pattern.compile("(RTOE = )(.*)(\n)");
    private String database = "nt";
    private String megablast = "on";

    // public
    public String id = "";
    public String time = "";

    public void set_database(String database) {
        this.database = database;
    }

    public void set_megablast(String megablast) {
        this.megablast = megablast;
    }

    public void send(String sequence) throws Exception {

        URL url = new URL("https://blast.ncbi.nlm.nih.gov/blast/Blast.cgi");

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("CMD", "Put");
        params.put("PROGRAM", "blastn");
        if (megablast.equals("on")) params.put("MEGABLAST", megablast);
        params.put("DATABASE", database);
        params.put("QUERY", sequence);
        params.put("FORMAT_TYPE", "XML");

        StringBuilder post_data = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (post_data.length() != 0) post_data.append('&');
            post_data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            post_data.append('=');
            post_data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] post_data_bytes = post_data.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(post_data_bytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(post_data_bytes);
        /*
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        for (int c; (c = in.read()) >= 0;)
            System.out.print((char)c);
        */
        try (InputStream instream = conn.getInputStream()) {

            Scanner s = new Scanner(instream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            Matcher m = p_id.matcher(result);
            if (m.find()) id = m.group(2);

            m = p_time.matcher(result);
            if (m.find()) time = m.group(2);
        }

    }
    /*
    public void Send(String sequence) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://blast.ncbi.nlm.nih.gov/blast/Blast.cgi");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("CMD", "Put"));
        params.add(new BasicNameValuePair("PROGRAM", "blastn"));
        if (megablast.equals("on")) {
            params.add(new BasicNameValuePair("MEGABLAST", megablast));
        }
        params.add(new BasicNameValuePair("DATABASE", database));
        params.add(new BasicNameValuePair("QUERY", sequence));
        params.add(new BasicNameValuePair("FORMAT_TYPE", "XML"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {

                Scanner s = new Scanner(instream).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

                Matcher m = p_id.matcher(result);
                if (m.find()) id = m.group(2);

                m = p_time.matcher(result);
                if (m.find()) time = m.group(2);
            }
        }
    }*/
}
