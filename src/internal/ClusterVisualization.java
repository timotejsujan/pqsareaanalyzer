package internal;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Class drawing 2D objects.
 *
 * @author Timotej Sujan
 */
public final class ClusterVisualization extends JFrame {

    private static final int PANEL_WIDTH = 1400;
    private static final int PANEL_HEIGHT = 800;
    private static final int HALF_WIDTH = PANEL_WIDTH / 2;
    private static final int HALF_HEIGHT = PANEL_HEIGHT / 2;
    private int BLOCK_WIDTH;
    private int BLOCK_HEIGHT;
    private int AMP = 100;
    File fileImgG = new File("/Users/timotej.sujan/Downloads/RestaurantMgtSampleUI-master/src/images/G.png");
    BufferedImage imgG = ImageIO.read(fileImgG);
    File fileImgA = new File("/Users/timotej.sujan/Downloads/RestaurantMgtSampleUI-master/src/images/A.png");
    BufferedImage imgA = ImageIO.read(fileImgA);
    File fileImgC = new File("/Users/timotej.sujan/Downloads/RestaurantMgtSampleUI-master/src/images/C.png");
    BufferedImage imgC = ImageIO.read(fileImgC);
    File fileImgT = new File("/Users/timotej.sujan/Downloads/RestaurantMgtSampleUI-master/src/images/T.png");
    BufferedImage imgT = ImageIO.read(fileImgT);

    private Graphics graphics;

    private Cluster cluster;

    /**
     * Draws 2D objects.
     *
     * @param args command line arguments, will be ignored
     */
    public static void main(String[] args) {
        //SwingUtilities.invokeLater(ClusterVisualization::new);
    }

    public ClusterVisualization(Cluster c) throws IOException {
        this.cluster = c;
        BLOCK_WIDTH = 40;//PANEL_WIDTH/c.sequences.get(0).length();
        BLOCK_HEIGHT = 10;//PANEL_WIDTH/c.sequences.size();

        setBounds(350, 250, PANEL_WIDTH, PANEL_HEIGHT);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ClusterVisualization");
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                try {
                    paintScene(g);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        panel.setBackground(Color.WHITE);
        add(panel);
        setVisible(true);
    }

    private void paintScene(Graphics g) throws IOException {
        graphics = g;

        paintCluster(cluster);
    }

    private void paintCluster(Cluster cluster) throws IOException {
        cluster.sequences.sort((xs1, xs2) -> xs2.length() - xs1.length());
        ArrayList<StringBuilder> transformedStringBuilder = new ArrayList<>();
        for (int j = 0; j < cluster.sequences.size(); j++) {
            String s = cluster.sequences.get(j);
            for (int i = 0; i < s.length(); i++){
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
        Logo logo = new Logo();
        double e = 1/Math.log(2)*(4 - 1)/(2*cluster.sequences.size());
        for (StringBuilder s : transformedStringBuilder) {
            Bar b = new Bar();
            Integer A = 0, C = 0, T = 0, G = 0;
            for (int i = 0; i < s.toString().length(); i++){
                switch (s.toString().charAt(i)){
                    case 'A': A++; break;
                    case 'C': C++; break;
                    case 'T': T++; break;
                    case 'G': G++; break;
                    default: break;
                }
            }
            b.bases.put('A', A);
            b.bases.put('C', C);
            b.bases.put('T', T);
            b.bases.put('G', G);
            b.bases = sortByValue(b.bases);
            double H = - (b.h('A') + b.h('T') + b.h('C') + b.h('G'));
            b.r = 2 - (H + e);
            logo.bars.add(b);
        }
        for (int j = 1; j <= logo.bars.size()/2; j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer)j).toString(), j*(BLOCK_WIDTH)+BLOCK_WIDTH/3, 310);
        }
        int counter = 1;
        for (int j = logo.bars.size()/2 + 1; j <= logo.bars.size(); j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer)j).toString(), counter*(BLOCK_WIDTH)+BLOCK_WIDTH/3, 610);
            counter++;
        }

        counter = 1;
        int height = 0;
        int extraY = 0;
        for (Bar b : logo.bars) {
            double totalHeight = AMP*b.r;
            height = 0;
            double currentHeight = 0;
            if (counter > logo.bars.size()/2) {
                extraY = 300;
                counter = 1;
            }
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                currentHeight = AMP*b.relFreq(entry.getValue())*b.r;
                graphics.setColor(getColor(entry.getKey()));

                //drawLetter(graphics, entry.getKey(), (counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), (int) currentHeight, BLOCK_WIDTH);
                graphics.drawImage(getImage(entry.getKey()), (counter)*(BLOCK_WIDTH), (int) (extraY + 300 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight, null);
                //graphics.fillRect((counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight);
                height += currentHeight;
            }
            counter++;
        }
    }

    private Image getImage(Character c) {
        switch (c) {
            case 'A': return imgA;
            case 'T': return imgT;
            case 'G': return imgG;
            case 'C': return imgC;
            default: return imgA;
        }
    }

    private void drawLetter(Graphics g, Character c, int x, int y, int height, int width) {
        switch (c) {
            case 'A': drawA(g, x, y, height, width);
            case 'T': drawT(g, x, y, height, width);
            case 'G': drawG(g, x, y, height, width);
            case 'C': drawC(g, x, y, height, width);
            default: return;
        }
    }

    private void drawA(Graphics g,  int x, int y, int height, int width) {
        g.drawLine(x, y+height, x + width/2, y);
        g.drawLine(x+width, y+height, x + width/2, y);
        g.drawLine(x+width/4, y+height/2, x+width*3/4, y+height/2);
    }
    private void drawC(Graphics g,  int x, int y, int height, int width) {

    }
    private void drawT(Graphics g,  int x, int y, int height, int width) {

    }
    private void drawG(Graphics g,  int x, int y, int height, int width) {

    }

    private Color getColor(char c) {
        switch (c) {
            case 'A': return Color.RED;
            case 'T': return Color.YELLOW;
            case 'G': return Color.BLUE;
            case 'C': return Color.GREEN;
            default: return Color.BLACK;
        }
    }

    private class Logo
    {
        ArrayList<Bar> bars = new ArrayList<>();
    }

    private class Bar
    {
        Map<Character, Integer> bases = new HashMap<>();
        double r = 0;

        double relFreq(int n) {
            return (double)n/count();
        }

        int count() {
            return bases.values().stream().reduce(0, Integer::sum);
        }

        double h(Character key) {
            return relFreq(bases.get(key))*log2(relFreq(bases.get(key)));
        }
    }

    private static double log2(double x)
    {
        if (x == 0) return 0;
        return (double) (Math.log(x) / Math.log(2));
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
