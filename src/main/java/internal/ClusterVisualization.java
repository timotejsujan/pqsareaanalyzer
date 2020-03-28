package internal;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private File fileImgG = new File("src/main/java/images/G.png");
    private BufferedImage imgG = ImageIO.read(fileImgG);
    private File fileImgA = new File("src/main/java/images/A.png");
    private BufferedImage imgA = ImageIO.read(fileImgA);
    private File fileImgC = new File("src/main/java/images/C.png");
    private BufferedImage imgC = ImageIO.read(fileImgC);
    private File fileImgT = new File("src/main/java/images/T.png");
    private BufferedImage imgT = ImageIO.read(fileImgT);

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

    }

    public void showSequence(){
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

    public void export() throws IOException {
        //showSequence(false);
        BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphics = bImg.createGraphics();
        graphics.setColor (Color.WHITE);
        graphics.fillRect ( 0, 0, bImg.getWidth(), bImg.getHeight() );
        this.paintScene(graphics);
        try {
            ImageIO.write(bImg, "png", new File("./output_image.png"));
            //System.out.println("-- saved");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Logo getLogoFromArray(ArrayList<String> sequences) {
        sequences.sort((xs1, xs2) -> xs2.length() - xs1.length());
        ArrayList<StringBuilder> transformedStringBuilder = new ArrayList<>();
        for (int j = 0; j < sequences.size(); j++) {
            String s = sequences.get(j);
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
        double e = 1/Math.log(2)*(4 - 1)/(2*sequences.size());
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
        return logo;
    }

    private void paintCluster(Cluster cluster) throws IOException {
        Logo leftLogo = getLogoFromArray(cluster.leftArea);
        Logo pqsLogo = getLogoFromArray(cluster.pqs);
        Logo rightLogo = getLogoFromArray(cluster.rightArea);

        for (int j = 1; j <= leftLogo.bars.size(); j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer)j).toString(), j*(BLOCK_WIDTH)+BLOCK_WIDTH/3, 210);
        }
        int counter = 1;
        for (int j = leftLogo.bars.size() + 1; j <= leftLogo.bars.size()*2; j++) {
            graphics.setColor(Color.BLACK);
            graphics.drawString(((Integer)j).toString(), counter*(BLOCK_WIDTH)+BLOCK_WIDTH/3, 710);
            counter++;
        }

        counter = 1;
        int height = 0;
        for (Bar b : pqsLogo.bars) {
            double totalHeight = AMP*b.r;
            height = 0;
            double currentHeight = 0;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                currentHeight = AMP*b.relFreq(entry.getValue())*b.r;
                graphics.setColor(getColor(entry.getKey()));

                //drawLetter(graphics, entry.getKey(), (counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), (int) currentHeight, BLOCK_WIDTH);
                graphics.drawImage(getImage(entry.getKey()), (counter)*(BLOCK_WIDTH), (int) (450 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight, null);
                //graphics.fillRect((counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight);
                height += currentHeight;
            }
            counter++;
        }

        counter = 1;
        for (Bar b : leftLogo.bars) {
            double totalHeight = AMP*b.r;
            height = 0;
            double currentHeight;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                currentHeight = AMP*b.relFreq(entry.getValue())*b.r;
                graphics.setColor(getColor(entry.getKey()));

                //drawLetter(graphics, entry.getKey(), (counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), (int) currentHeight, BLOCK_WIDTH);
                graphics.drawImage(getImage(entry.getKey()), (counter)*(BLOCK_WIDTH), (int) (200 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight, null);
                //graphics.fillRect((counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight);
                height += currentHeight;
            }
            counter++;
        }

        counter = 1;
        for (Bar b : rightLogo.bars) {
            double totalHeight = AMP*b.r;
            height = 0;
            double currentHeight;
            for (Map.Entry<Character, Integer> entry : b.bases.entrySet()) {
                currentHeight = AMP*b.relFreq(entry.getValue())*b.r;
                graphics.setColor(getColor(entry.getKey()));

                //drawLetter(graphics, entry.getKey(), (counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), (int) currentHeight, BLOCK_WIDTH);
                graphics.drawImage(getImage(entry.getKey()), (counter)*(BLOCK_WIDTH), (int) (700 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight, null);
                //graphics.fillRect((counter)*2*(BLOCK_WIDTH), (int) (300 - totalHeight + height), BLOCK_WIDTH, (int) currentHeight);
                height += currentHeight;
            }
            counter++;
        }
    }

    private Image getImage(Character c) {
        switch (c) {
            case 'T': return imgT;
            case 'G': return imgG;
            case 'C': return imgC;
            case 'A':
            default: return imgA;
        }
    }

    private void drawLetter(Graphics g, Character c, int x, int y, int height, int width) {
        switch (c) {
            case 'A': drawA(g, x, y, height, width);
            case 'T': drawT(g, x, y, height, width);
            case 'G': drawG(g, x, y, height, width);
            case 'C': drawC(g, x, y, height, width);
            default:
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

    private static class Logo
    {
        ArrayList<Bar> bars = new ArrayList<>();
    }

    private static class Bar
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
