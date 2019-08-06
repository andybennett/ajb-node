import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class Test extends JPanel implements KeyListener {

    public static Color BACKGROUND = Color.decode("#002b36");
    public static Color BACKGROUND1 = Color.decode("#073642");
    public static Color GRAY = Color.decode("#93a1a1");
    public static Color BLUE = Color.decode("#268bd2");
    public static Color YELLOW = Color.decode("#b58900");
    public static Color GREEN = Color.decode("#859900");
    public static Color RED = Color.decode("#dc322f");
    public static Color CYAN = Color.decode("#2aa198");
    public static Color MAGENTA = Color.decode("#d33682");
    public static Color VIOLET = Color.decode("#6c71c4");
    public static Color ORANGE = Color.decode("#cb4b16");

    NodeGroup nodeGroup1 = new NodeGroup();

    int scale = 10;

    public static void main(String [] args) {

        new Test();

    }

    public Test() {

        JFrame frame = new JFrame();
        frame.setSize(1024, 768);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setBackground(BACKGROUND);

        frame.add(this);
        frame.addKeyListener(this);

        nodeGroup1.size = scale;
        //nodeGroup1.addGrid(500, 500);
        nodeGroup1.addRandomNodes(10000);
        nodeGroup1.addRandomNodes(10000);
        nodeGroup1.addRandomNodes(10000);
        nodeGroup1.addRandomNodes(10000);

        frame.setVisible(true);

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(BACKGROUND);
        g2d.fillRect(0,0,this.getWidth(), this.getHeight());

        nodeGroup1.draw(g2d, this.getWidth() / 2, this.getHeight() / 2);

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode() == 61) {
            scale++;
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_MINUS) {
            scale--;
            if (scale < 1) {
                scale = 1;
            }
        }

        nodeGroup1.size = scale;
        this.repaint();

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

}

class RandomUtils {

    public static int randomIntRange(final int min, final int max) {

        return min + (int) (Math.random() * ((max - min) + 1));

    }

}

class NodeGroup {

    int size = 1;
    Map<String, Node> nodes = new HashMap<>();
    Node selectedNode;

    public NodeGroup() {

        String key = "0:0";
        nodes.put(key, new Node(0, 0));
        selectedNode = nodes.get(key);

    }

    public void addGrid(int rows, int columns) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                String key = c + ":" + r;

                if (!nodes.containsKey(key)) {
                    Node node = new Node(c, r);
                    nodes.put(key, node);
                    node.link(nodes);
                }
            }
        }

        selectedNode = nodes.get(rows / 2 + ":" + columns / 2);
    }

    public void addRandomNodes(int amount) {
        getRandomNode().addRandomNodes(amount, nodes);
    }

    public void addRandomNodes(Node startNode, int amount) {
        startNode.addRandomNodes(amount, nodes);
    }

    public Node getRandomNode() {
        return (Node)nodes.values().toArray()[RandomUtils.randomIntRange(0, nodes.size() - 1)];
    }

    public void draw(Graphics2D g2d, int x, int y) {

        for (Node node : nodes.values()) {
            node.drawn = false;
        }

        try {
            selectedNode.img = ImageIO.read(Test.class.getResourceAsStream("swap-bag.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        selectedNode.draw(g2d, x, y, size);

        g2d.setColor(Test.RED);
        g2d.draw(selectedNode.getBounds(x, y, size));

    }
}

class Node {

    int x, y, screenx, screeny;

    Node top = null;
    Node left = null;
    Node right = null;
    Node bottom = null;

    Color fillColor;
    Color lineColor;

    boolean drawn = false;

    Image img;

    public Node(int x, int y) {

        this.x = x;
        this.y = y;
        this.fillColor = Test.BACKGROUND1;
        this.lineColor = Test.GRAY;

    }

    public void draw(Graphics2D g2d, int x, int y, int size) {

        this.screenx = x;
        this.screeny = y;

        Rectangle2D bounds = getBounds(screenx, screeny, size);

        g2d.setColor(fillColor);
        g2d.fill(bounds);

        g2d.setColor(lineColor);
        g2d.draw(bounds);

        if (img != null) {
            g2d.drawImage(img, x - (size / 2), y - (size / 2), size, size, null);
        }

        drawn = true;

        if (top != null && !top.drawn) {
            top.draw(g2d, x, y - size, size);
        }

        if (bottom != null && !bottom.drawn) {
            bottom.draw(g2d, x, y + size, size);
        }

        if (left != null && !left.drawn) {
            left.draw(g2d, x - size, y, size);
        }

        if (right != null && !right.drawn) {
            right.draw(g2d, x + size, y, size);
        }

    }

    public void addRandomNodes(int depth, Map<String, Node> nodes) {

        int currentDepth = 1;
        this.addRandomNode(depth, currentDepth, nodes);

    }

    public void addRandomNode(int depth, int currentDepth, Map<String, Node> nodes) {

        if (currentDepth < depth) {

            int direction = RandomUtils.randomIntRange(1, 4);

            if (direction == 1) {

                if (addNodeTop(nodes)) {
                    currentDepth++;
                }

                top.addRandomNode(depth, currentDepth, nodes);

            } else if (direction == 2) {

                if (addNodeBottom(nodes)) {
                    currentDepth++;
                }

                bottom.addRandomNode(depth, currentDepth, nodes);

            } else if (direction == 3) {

                if (addNodeLeft(nodes)) {
                    currentDepth++;
                }

                left.addRandomNode(depth, currentDepth, nodes);

            } else if (direction == 4) {

                if (addNodeRight(nodes)) {
                    currentDepth++;
                }

                right.addRandomNode(depth, currentDepth, nodes);

            }
        }
    }

    public boolean addNodeTop(Map<String, Node> nodes) {
        return addNode(x, y - 1, nodes);
    }

    public boolean addNodeBottom(Map<String, Node> nodes) {
        return addNode(x, y + 1, nodes);
    }

    public boolean addNodeLeft(Map<String, Node> nodes) {
        return addNode(x - 1, y, nodes);
    }

    public boolean addNodeRight(Map<String, Node> nodes) {
        return addNode(x + 1, y, nodes);
    }

    private boolean addNode(int x, int y, Map<String, Node> nodes) {

        String key = x + ":" + y;
        boolean result = true;
        Node node = null;

        if (nodes.containsKey(key)) {
            node = nodes.get(key);
            result = false;
        }

        if (node == null) {
            node = new Node(x, y);
            nodes.put(key, node);
        }

        link(nodes);

        return result;
    }

    public Rectangle2D getBounds(int x, int y, int size) {
        return new Rectangle2D.Double(x - (size / 2), y - (size / 2), size, size);
    }

    public void link(Map<String, Node> nodes) {
        String topKey = x + ":" + (y - 1);
        String bottomKey = x + ":" + (y + 1);
        String leftKey = (x - 1) + ":" + y;
        String rightKey = (x + 1) + ":" + y;

        if (nodes.containsKey(topKey)) {
            Node node = nodes.get(topKey);
            this.top = node;
            node.bottom = this;
        }

        if (nodes.containsKey(bottomKey)) {
            Node node = nodes.get(bottomKey);
            this.bottom = node;
            node.top = this;
        }

        if (nodes.containsKey(leftKey)) {
            Node node = nodes.get(leftKey);
            this.left = node;
            node.right = this;
        }

        if (nodes.containsKey(rightKey)) {
            Node node = nodes.get(rightKey);
            this.right = node;
            node.left = this;
        }
    }
}
