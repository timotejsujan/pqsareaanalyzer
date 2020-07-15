package model;

import controller.Main;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Class drawing 2D objects.
 *
 * @author Timotej Sujan
 */
public final class cluster_logo extends JFrame {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int PANEL_WIDTH = (int) screenSize.getWidth();
    private final int PANEL_HEIGHT = 800;
    private int BLOCK_WIDTH;
    private final BufferedImage img_g = ImageIO.read(new File(Main.jar_folder_path + "/images/G.png"));
    private final BufferedImage img_a = ImageIO.read(new File(Main.jar_folder_path + "/images/A.png"));
    private final BufferedImage img_c = ImageIO.read(new File(Main.jar_folder_path + "/images/C.png"));
    private final BufferedImage img_t = ImageIO.read(new File(Main.jar_folder_path + "/images/T.png"));
    private Graphics graphics;
    private final model.cluster cluster;

    public cluster_logo(model.cluster c) throws IOException {
        cluster = c;
        BLOCK_WIDTH = PANEL_WIDTH / (c.max_length + 2);
        setBounds(350, 250, PANEL_WIDTH, PANEL_HEIGHT);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ClusterVisualization");
    }

    public void show_sequence() {
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                graphics = g;
                paint_cluster();
            }
        };
        panel.setBackground(Color.WHITE);
        add(panel);
        setVisible(true);
    }

    public BufferedImage get_buffered_image() {
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphics = bi.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        graphics.setColor(Color.BLACK);
        this.paint_cluster();
        return bi;
    }

    private ArrayList<bar> get_logo(ArrayList<String> sequences) {
        sequences.sort((xs1, xs2) -> xs2.length() - xs1.length());
        ArrayList<StringBuilder> transformedStringBuilder = new ArrayList<>();
        for (int j = 0; j < sequences.size(); j++) {
            String s = sequences.get(j);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (j == 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    transformedStringBuilder.add(sb);
                } else {
                    transformedStringBuilder.get(i).append(c);
                }
            }
        }
        ArrayList<bar> logo = new ArrayList<>();
        double e = 1.0 / Math.log(2) * (4 - 1) / (2 * sequences.size());
        for (StringBuilder s : transformedStringBuilder) {
            bar b = new bar();
            int A = 0, C = 0, T = 0, G = 0, N = 0;
            for (int i = 0; i < s.toString().length(); i++) {
                char c =  s.toString().charAt(i);
                if (c == 'A') A++;
                else if (c == 'C') C++;
                else if (c == 'T') T++;
                else if (c == 'G') G++;
                else if (c == 'N') N++;
            }
            for (int n = 0; n < N; n++){
                A++;C++;T++;G++;
            }
            b.bases.put('A', A);
            b.bases.put('C', C);
            b.bases.put('T', T);
            b.bases.put('G', G);
            b.bases = sortByValue(b.bases);
            double H = -(b.h('A') + b.h('T') + b.h('C') + b.h('G'));
            b.r = Math.max(0, 2 - (H + e));
            logo.add(b);
        }
        return logo;
    }

    private void paint_cluster() {
        ArrayList<bar> left_area = get_logo(cluster.left_area);
        ArrayList<bar> pqs = get_logo(cluster.pqs);
        ArrayList<bar> right_area = get_logo(cluster.right_area);

        int marginLeft =  60;
        graphics.drawString("left area", marginLeft, 10);
        int AMP = 100;
        paint_line(marginLeft - 5, 20, AMP * 2);
        graphics.drawString("PQS", marginLeft, 260);
        paint_line(marginLeft - 5, 270, AMP * 2);
        graphics.drawString("right area", marginLeft, 510);
        paint_line(marginLeft - 5, 520, AMP * 2);

        draw_baseline(1, left_area.size(), 230,  marginLeft);
        draw_baseline(left_area.size() + 1, left_area.size() * 2, 730,  marginLeft);

        draw_bars(pqs, AMP, marginLeft, 470);
        draw_bars(left_area, AMP, marginLeft, 220);
        draw_bars(right_area, AMP, marginLeft, 720);
    }

    private void draw_bars(ArrayList<bar> bars, int AMP, int marginLeft, int y_offset){
        int counter = 0;
        for (bar b : bars) {
            double total_height = AMP * b.r;
            int height = 0;
            double curr_height;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                curr_height = AMP * b.rel_freq(entry.getValue()) * b.r;
                int x = marginLeft + (counter) * (BLOCK_WIDTH);
                int y = (int) (y_offset - total_height + height);
                draw_letter(x, y, (int) curr_height, AMP, entry.getKey());
                height += curr_height;
            }
            counter++;
        }
    }

    private void draw_baseline(int from, int to, int y, int marginLeft){
        int counter = 0;
        for (int j = from; j <= to; j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer) j).toString(), marginLeft + (counter++) * (BLOCK_WIDTH) + BLOCK_WIDTH / 2, y);
        }
    }

    private void draw_letter(int x, int y, int curr_height, int AMP, Character key){
        if (curr_height >= AMP / (double) 8) {
            graphics.drawImage(get_img(key), x, y, BLOCK_WIDTH, curr_height, null);
        } else {
            graphics.setColor(get_color(key));
            graphics.fillRect(x, y, BLOCK_WIDTH, curr_height);
        }
    }

    private void paint_line(int x, int y, int height) {
        int m_x = 30;
        int m_y = 5;
        graphics.drawLine(x, y, x, y + height);
        graphics.drawLine(x - 5, y, x, y);
        graphics.drawString("2", x + 5 - m_x, y + m_y);
        graphics.drawLine(x - 5, y + height / 4, x, y + height / 4);
        graphics.drawString("1.5", x - 5 - m_x, y + height / 4 + m_y);
        graphics.drawLine(x - 5, y + height / 2, x, y + height / 2);
        graphics.drawString("1", x + 5 - m_x, y + height / 2 + m_y);
        graphics.drawLine(x - 5, y + (height * 3 / 4), x, y + (height * 3 / 4));
        graphics.drawString("0.5", x - 5 - m_x, y + (height * 3 / 4) + m_y);
        graphics.drawLine(x - 5, y + height, x, y + height);
        graphics.drawString("0", x + 5 - m_x, y + height + m_y);
    }

    private Image get_img(char c) {
        switch (c) {
            case 'T':
                return img_t;
            case 'G':
                return img_g;
            case 'C':
                return img_c;
            case 'A':
            default:
                return img_a;
        }
    }

    private Color get_color(char c) {
        switch (c) {
            case 'A':
                return new Color(238, 66, 102);
                //return Color.getHSBColor(347, 72, 93);
                //return Color.getColor("#00EE4266");
            case 'T':
                return new Color(68, 118, 4);
                //return Color.getHSBColor(347, 72, 93);
                //return Color.getColor("#00447604");
            case 'G':
                return new Color(236, 195, 11);
                //return Color.getHSBColor(347, 72, 93);
                //return Color.getColor("#00ECC30B");
            case 'C':
                return new Color(42, 30, 92);
                //return Color.getHSBColor(347, 72, 93);
                //return Color.getColor("#002A1E5C");
            default:
                return Color.BLACK;
        }
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private class bar {
        Map<Character, Integer> bases = new HashMap<>();
        double r = 0;

        double rel_freq(int n) {
            return (double) n / count();
        }

        int count() {
            return bases.values().stream().reduce(0, Integer::sum);
        }

        double h(Character key) {
            double rf = rel_freq(bases.get(key));
            return rf * log2(rf);
        }

        private double log2(double x) {
            return (x == 0 ? 0 : Math.log(x) / Math.log(2));
        }
    }
}

