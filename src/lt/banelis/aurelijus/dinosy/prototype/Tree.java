package lt.banelis.aurelijus.dinosy.prototype;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Tree data structure optimised to store clases by extention
 *
 * @author Aurelijus Banelis
 */
public class Tree<T> {
    private T head;
    private List<Tree<T>> childs;

    public Tree(T head) {
        this(head, new LinkedList<Tree<T>>());
    }

    public Tree(T head, Tree<T> child) {
        this(head);
        childs.add(child);
    }

    public Tree(T head, T ... childs) {
        this(head);
        for (T argument : childs) {
            this.childs.add(new Tree<T>(argument));
        }
    }

    public Tree(T head, List<Tree<T>> childs) {
        this.head = head;
        this.childs = childs;
    }

    public Tree(Tree<T> clone) {
        this(clone.getHead(), clone.getChilds());
    }

    public T getHead() {
        return head;
    }

    public List<Tree<T>> getChilds() {
        return childs;
    }

    public boolean hasChilds() {
        return childs.size() > 0;
    }

    public Tree<T> addChild(T child) {
        Tree<T> childTree = new Tree<T>(child);
        addChild(childTree);
        return childTree;
    }

    public void addChild(Tree<T> child) {
        childs.add(child);
    }

    public void addChilds(T ... childs) {
        for (T argument : childs) {
            this.childs.add(new Tree<T>(argument));
        }
    }

    public void addChilds(List<Tree<T>> childs) {
        this.childs.addAll(childs);
    }

    public Tree<T> addParent(T parent) {
        return new Tree<T>(parent, this);
    }

    public boolean contains(T child) {
        return get(child) != null;
    }

    public boolean containsDeep(T child) {
        return getDeep(child) != null;
    }

    public Tree<T> get(T child) {
        for (Tree<T> tree : childs) {
            if (tree.getHead().equals(child)) {
                return tree;
            }
        }
        return null;
    }

    public Tree<T> getDeep(T child) {
        Tree<T> candidate = get(child);
        if (candidate != null) {
            return candidate;
        } else {
            for (Tree<T> tree : childs) {
                candidate = tree.getDeep(child);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public Tree<T> get(T child, Comparator<T> comparator) {
        for (Tree<T> tree : childs) {
            if (comparator.compare(tree.getHead(), child) == 0) {
                return tree;
            }
        }
        return null;
    }

    public Tree<T> getDeep(T child, Comparator<T> comparator) {
        Tree<T> candidate = get(child, comparator);
        if (candidate != null) {
            return candidate;
        } else {
            for (Tree<T> tree : childs) {
                candidate = tree.getDeep(child, comparator);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public List<T> getAll() {
        LinkedList<T> result = new LinkedList<T>();
        addChildsToList(result, this);
        return result;
    }

    private void addChildsToList(List<T> result, Tree<T> tree) {
        result.add(tree.getHead());
        for (Tree<T> tree1 : tree.getChilds()) {
            addChildsToList(result, tree1);
        }
    }

    public void remove(T child) {
        Tree<T> candidate = get(child);
        if (candidate != null) {
            childs.remove(candidate);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Tree<T> tree : childs) {
            result.append(addTabsToLines(tree.toString()));
        }
        return head.toString() + "\n" + result.toString();
    }

    protected String addTabsToLines(String multiline) {
        String[] lines = multiline.split("\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append("\t").append(line).append("\n");
        }
        return result.toString();
    }
}
