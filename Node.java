import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node<T> {
        public List<Node<T>> children;
        public T dataType;
        public Node<T> parent;
        public int parentVisit;
        public int childVisit;
        public double evaluationValue;

        public Node(T dataType) {
            this.children = new LinkedList<>();
            this.parent = this;
            this.parentVisit = 0;
            this.childVisit = 0;
            this.dataType = dataType;
            this.evaluationValue = 0;
        }

        public Node<T> getParent() {
            return parent;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public T getType() {
            return dataType;
        }

        public void addChildren(Node<T> child) {
            children.add(child);
        }

        public void traverseToNode(Node<T> node) {
            Queue<Node<T>> queue = new LinkedList<>();
            
            while(!queue.isEmpty()) {
                System.out.println(queue.remove().dataType);
            }
        }

        public Node<T> getRoot(Node<T> node) {
            return null;
        }
    }

