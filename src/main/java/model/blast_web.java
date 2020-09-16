package model;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Timotej Sujan
 */
public class blast_web {
    // private
    private final Pattern p_id = Pattern.compile("(RID = )(.*)(\n)");
    private final Pattern p_time = Pattern.compile("(RTOE = )(.*)(\n)");
    private String database = "nt";
    private String megablast = "on";

    public String filter = "";
    public String format_type = "";
    public String expect = "";
    public String nucl_reward = "";
    public String nucl_penalty = "";
    public String gapcosts = "";
    public String matrix = "";
    public String hitlist_size = "";
    public String descriptions = "";
    public String alignments = "";
    public String ncbi_gi = "";
    public String threshold = "";
    public String word_size = "";
    public String composition_based_statistics = "";
    public String num_threads = "";

    private Map<String, Object> get_params(String sequence){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("CMD", "Put");
        params.put("PROGRAM", "blastn");
        if (megablast == null) megablast = "on";
        if (database == null) database = "nt";
        if (sequence == null || sequence.isEmpty()) sequence = "NOTHING";
        if (megablast.equals("on")) params.put("MEGABLAST", megablast);
        params.put("DATABASE", database);
        params.put("QUERY", sequence);
        if (!filter.isEmpty())
            params.put("FILTER", filter);
        if (!format_type.isEmpty())
            params.put("FORMAT_TYPE", format_type);
        if (!expect.isEmpty())
            params.put("EXPECT", expect);
        if (!nucl_reward.isEmpty())
            params.put("NUCL_REWARD", nucl_reward);
        if (!nucl_penalty.isEmpty())
            params.put("NUCL_PENALTY", nucl_penalty);
        if (!gapcosts.isEmpty())
            params.put("GAPCOSTS", gapcosts);
        if (!matrix.isEmpty())
            params.put("MATRIX", matrix);
        if (!hitlist_size.isEmpty())
            params.put("HITLIST_SIZE", hitlist_size);
        if (!descriptions.isEmpty())
            params.put("DESCRIPTIONS", descriptions);
        if (!alignments.isEmpty())
            params.put("ALIGNMENTS", alignments);
        if (!ncbi_gi.isEmpty())
            params.put("NCBI_GI", ncbi_gi);
        if (!threshold.isEmpty())
            params.put("THRESHOLD", threshold);
        if (!word_size.isEmpty())
            params.put("WORD_SIZE", word_size);
        if (!composition_based_statistics.isEmpty())
            params.put("COMPOSITION_BASED_STATISTICS", composition_based_statistics);
        if (!num_threads.isEmpty())
            params.put("NUM_THREADS", num_threads);

        return params;
    }

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

        Map<String, Object> params = get_params(sequence);

        StringBuilder post_data = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (post_data.length() != 0) post_data.append('&');
            post_data.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            post_data.append('=');
            post_data.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
        }
        byte[] post_data_bytes = post_data.toString().getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(post_data_bytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(post_data_bytes);

        try (InputStream instream = conn.getInputStream()) {

            Scanner s = new Scanner(instream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            Matcher m = p_id.matcher(result);
            if (m.find()) id = m.group(2);

            m = p_time.matcher(result);
            if (m.find()) time = m.group(2);
        }

    }
}
