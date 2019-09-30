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
        nodeGroup1.addRandomNodes(200, NodeType.NATURAL);

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
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            if (nodeGroup1.selectedNode.left != null) {
                nodeGroup1.selectedNode = nodeGroup1.selectedNode.left;
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (nodeGroup1.selectedNode.right != null) {
                nodeGroup1.selectedNode = nodeGroup1.selectedNode.right;
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            if (nodeGroup1.selectedNode.top != null) {
                nodeGroup1.selectedNode = nodeGroup1.selectedNode.top;
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            if (nodeGroup1.selectedNode.bottom != null) {
                nodeGroup1.selectedNode = nodeGroup1.selectedNode.bottom;
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
        nodes.put(key, new Node(0, 0, NodeType.NATURAL));
        selectedNode = nodes.get(key);

    }

    public void addGrid(int rows, int columns) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                String key = c + ":" + r;

                if (!nodes.containsKey(key)) {
                    Node node = new Node(c, r, NodeType.NATURAL);
                    nodes.put(key, node);
                    node.link(nodes);
                }
            }
        }

        selectedNode = nodes.get("0:0");
    }

    public void addRandomNodes(int amount, NodeType type) {
        getRandomNode().addRandomNodes(amount, nodes, type);
    }

    public void addRandomNodes(Node startNode, int amount, NodeType type) {
        startNode.addRandomNodes(amount, nodes, type);
    }

    public void addRandomRectangles(int amount) {
        for (int i = 0; i < amount; i++) {
            int direction = RandomUtils.randomIntRange(1, 4);

            int rows = 0;
            int columns = 0;

            if (RandomUtils.randomIntRange(1, 100) > 50) {
                rows = RandomUtils.randomIntRange(1, 2);
                columns = RandomUtils.randomIntRange(1, 20);
            } else {
                rows = RandomUtils.randomIntRange(1, 20);
                columns = RandomUtils.randomIntRange(1, 2);
            }

            Node startingNode = getRandomNode();

            int startingx = startingNode.x;
            int startingy = startingNode.y;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {

                    int newx = startingx;
                    int newy = startingy;

                    // South Right
                    if (direction == 1) {
                        newx += c;
                        newy += r;
                    }

                    // North Right
                    if (direction == 2) {
                        newx += c;
                        newy -= r;
                    }

                    // South Left
                    if (direction == 3) {
                        newx -= c;
                        newy += r;
                    }

                    // North Left
                    if (direction == 4) {
                        newx -= c;
                        newy -= r;
                    }

                    String key = newx + ":" + newy;

                    if (!nodes.containsKey(key)) {
                        Node node = new Node(newx, newy, NodeType.NATURAL);
                        nodes.put(key, node);
                        node.link(nodes);
                    }
                }
            }
        }
    }

    public Node getRandomNode() {
        return (Node)nodes.values().toArray()[RandomUtils.randomIntRange(0, nodes.size() - 1)];
    }

    public Node getRandomOutsideNode() {

        Node result = null;

        while (result == null) {
            Node node = getRandomNode();

            if (node.top == null || node.bottom == null || node.right == null) {
                result = node;
            }
        }

        return result;
    }

    public void draw(Graphics2D g2d, int x, int y) {

        for (Node node : nodes.values()) {
            node.drawn = false;
        }

//        try {
//            selectedNode.img = ImageIO.read(Test.class.getResourceAsStream("wooden-crate.png"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        selectedNode.draw(g2d, x, y, size);

        g2d.setColor(selectedNode.lineColor.brighter());
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

    NodeType type;

    public Node(int x, int y, NodeType type) {

        this.x = x;
        this.y = y;
        this.type = type;

        if (type.equals(NodeType.NATURAL)) {
            this.fillColor = Test.BACKGROUND1;
        } else {
            this.fillColor = Test.BLUE.darker().darker().darker();
        }
        this.lineColor = Test.BACKGROUND1.brighter();

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
            g2d.drawImage(img, x - ((Math.max(size - 10, 1)) / 2), y - (Math.max(size - 10, 1) / 2), Math.max(size - 10, 1), Math.max(size - 10, 1), null);
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

    public void addRandomNodes(int depth, Map<String, Node> nodes, NodeType type) {

        int currentDepth = 1;
        this.addRandomNode(depth, currentDepth, nodes, type);

    }

    public void addRandomNode(int depth, int currentDepth, Map<String, Node> nodes, NodeType type) {

        if (currentDepth < depth) {

            int direction = RandomUtils.randomIntRange(1, 4);

            if (direction == 1) {

                if (addNodeTop(nodes, type)) {
                    currentDepth++;
                }

                top.addRandomNode(depth, currentDepth, nodes, type);

            } else if (direction == 2) {

                if (addNodeBottom(nodes, type)) {
                    currentDepth++;
                }

                bottom.addRandomNode(depth, currentDepth, nodes, type);
            } else if (direction == 3) {

                if (addNodeLeft(nodes, type)) {
                    currentDepth++;
                }

                left.addRandomNode(depth, currentDepth, nodes, type);
            } else if (direction == 4) {

                if (addNodeRight(nodes, type)) {
                    currentDepth++;
                }

                right.addRandomNode(depth, currentDepth, nodes, type);

            }
        }
    }

    public boolean addNodeTop(Map<String, Node> nodes, NodeType type) {
        return addNode(x, y - 1, nodes, type);
    }

    public boolean addNodeBottom(Map<String, Node> nodes, NodeType type) {
        return addNode(x, y + 1, nodes, type);
    }

    public boolean addNodeLeft(Map<String, Node> nodes, NodeType type) {
        return addNode(x - 1, y, nodes, type);
    }

    public boolean addNodeRight(Map<String, Node> nodes, NodeType type) {
        return addNode(x + 1, y, nodes, type);
    }

    private boolean addNode(int x, int y, Map<String, Node> nodes, NodeType type) {

        String key = x + ":" + y;
        boolean result = true;
        Node node = null;

        if (nodes.containsKey(key)) {
            node = nodes.get(key);
            result = false;
        }

        if (node == null) {
            node = new Node(x, y, type);
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

enum NodeType {
    NATURAL, WATER
}
