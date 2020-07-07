package external;

import home.Main;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class drawing 2D objects.
 *
 * @author Timotej Sujan
 */
public final class cluster_visualization extends JFrame {

    private final int PANEL_WIDTH = 1400;
    private final int PANEL_HEIGHT = 800;
    private final int BLOCK_WIDTH;
    private final BufferedImage img_g = ImageIO.read(new File(Main.jar_folder_path + "/images/G.png"));
    private final BufferedImage img_a = ImageIO.read(new File(Main.jar_folder_path + "/images/A.png"));
    private final BufferedImage img_c = ImageIO.read(new File(Main.jar_folder_path + "/images/C.png"));
    private final BufferedImage img_t = ImageIO.read(new File(Main.jar_folder_path + "/images/T.png"));
    private Graphics graphics;
    private final external.cluster cluster;

    public cluster_visualization(external.cluster c) throws IOException {
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
                switch (s.toString().charAt(i)) {
                    case 'A':
                        A++;
                        break;
                    case 'C':
                        C++;
                        break;
                    case 'T':
                        T++;
                        break;
                    case 'G':
                        G++;
                        break;
                    default:
                        N++;
                        break;
                }
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

        int marginLeft = 2 * (BLOCK_WIDTH) + BLOCK_WIDTH / 3;
        graphics.drawString("left area", marginLeft, 10);
        int AMP = 100;
        paint_line(marginLeft - 25, 20, AMP * 2 - 20);
        graphics.drawString("quadruplex", marginLeft, 260);
        paint_line(marginLeft - 25, 270, AMP * 2 - 20);
        graphics.drawString("right area", marginLeft, 510);
        paint_line(marginLeft - 25, 520, AMP * 2 - 20);

        int counter = 2;
        for (int j = 1; j <= left_area.size(); j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer) j).toString(), (counter++) * (BLOCK_WIDTH) + BLOCK_WIDTH / 3, 210);
        }
        counter = 2;
        for (int j = left_area.size() + 1; j <= left_area.size() * 2; j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer) j).toString(), (counter++) * (BLOCK_WIDTH) + BLOCK_WIDTH / 3, 710);
        }

        counter = 2;
        int height;
        for (bar b : pqs) {
            double total_height = AMP * b.r;
            height = 0;
            double curr_height;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                curr_height = AMP * b.rel_freq(entry.getValue()) * b.r;
                //graphics.setColor(get_color(entry.getKey()));

                graphics.drawImage(get_img(entry.getKey()), (counter) * (BLOCK_WIDTH),
                        (int) (450 - total_height + height), BLOCK_WIDTH, (int) curr_height, null);
                height += curr_height;
            }
            counter++;
        }

        counter = 2;
        for (bar b : left_area) {
            double total_height = AMP * b.r;
            height = 0;
            double curr_height;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                curr_height = AMP * b.rel_freq(entry.getValue()) * b.r;
                //graphics.setColor(get_color(entry.getKey()));
                graphics.drawImage(get_img(entry.getKey()), (counter) * (BLOCK_WIDTH),
                        (int) (200 - total_height + height), BLOCK_WIDTH, (int) curr_height, null);
                height += curr_height;
            }
            counter++;
        }

        counter = 2;
        for (bar b : right_area) {
            double total_height = AMP * b.r;
            height = 0;
            double curr_height;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                curr_height = AMP * b.rel_freq(entry.getValue()) * b.r;
                //graphics.setColor(get_color(entry.getKey()));

                graphics.drawImage(get_img(entry.getKey()), (counter) * (BLOCK_WIDTH),
                        (int) (700 - total_height + height), BLOCK_WIDTH, (int) curr_height, null);
                height += curr_height;
            }
            counter++;
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
                return Color.getColor("#8C001A");
            case 'T':
                return Color.getColor("#FF9000");
            case 'G':
                return Color.getColor("#26547C");
            case 'C':
                return Color.getColor("#047632");
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
}

