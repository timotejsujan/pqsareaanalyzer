package internal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Timotej Sujan
 */
public class BlastApi {
    private static final Pattern p_id = Pattern.compile("(RID = )(.*)(\n)");
    private static final Pattern p_time = Pattern.compile("(RTOE = )(.*)(\n)");
    public String id = "test";
    public String time = "test";
    public void Send(String sequence) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://blast.ncbi.nlm.nih.gov/blast/Blast.cgi");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("CMD", "Put"));
        params.add(new BasicNameValuePair("PROGRAM", "blastn"));
        params.add(new BasicNameValuePair("MEGABLAST", "on"));
        params.add(new BasicNameValuePair("DATABASE", "nt"));
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
                if (m.find()) {
                    id = m.group(2);
                }
                m = p_time.matcher(result);
                if (m.find()) {
                    time = m.group(2);
                }
                System.out.println(result);
            }
        }
    }
}
