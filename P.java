

public class P {
    public static void main(String[] args) {
        Node<Integer> node = new Node<>(2);
        for(int i = 0; i < 4; i++) {
            node.addChildren(new Node<>(i));
        }

        node.traverseToNode(node);

    }
}
