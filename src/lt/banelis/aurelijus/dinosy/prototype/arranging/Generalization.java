package lt.banelis.aurelijus.dinosy.prototype.arranging;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import lt.banelis.aurelijus.dinosy.prototype.ClassRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.PhpUml;
import lt.banelis.aurelijus.dinosy.prototype.Tree;

/**
 * Arranging by which class extend which.
 * Arranging from top (parent) to down (childs).
 * Not connected elements are arrange susing SimpleArrange
 *
 * @author Aurelijus Banelis
 */
public class Generalization extends SimpleArranging {
    LinkedList<Tree<ClassRepresentation>> trees;
    LinkedList<ClassRepresentation> used;
    LinkedList<ClassRepresentation> bare;
    List<ClassRepresentation> models;
    int lastHeight = 0;
    private int space = 50;
    private LinkedList<Tree<ClassBox>> classTrees = new LinkedList<Tree<ClassBox>>();

    public Generalization(PhpUml phpUml) {
        super(phpUml);
        reset();
    }

    
    private void reset() {
        trees = new LinkedList<Tree<ClassRepresentation>>();
        used = new LinkedList<ClassRepresentation>();
        bare = new LinkedList<ClassRepresentation>();
    }

    @Override
    public void arrange() {
        arrange(getModels());
    }

    @Override
    public int arrange(List<ClassRepresentation> models) {
        putToTrees(models);
        separateBareClasses();
        rearangeBare();
        rearangeTrees();
        getPanel().repaint();
        return lastHeight;
    }

    private void putToTrees(List<ClassRepresentation> models) {
        this.models = models;
        reset();
        for (ClassRepresentation model : models) {
            if (!used.contains(model)) {
                used.add(model);
                ClassRepresentation parentModel = get(model.whatExtends());
                Tree<ClassRepresentation> parentTree = get(parentModel);
                List<Tree<ClassRepresentation>> childTrees = getAll(model.getClassName());

                if (parentTree == null && childTrees == null) {

                    /* Bare (could be connected later) */
                    trees.add(new Tree<ClassRepresentation>(model));

                } else if (parentTree != null && childTrees == null) {

                    /* Connecting child to existing tree */
                    parentTree.addChild(model);

                } else if (parentTree == null && childTrees != null) {

                    /* Connecting all childs to one head */
                    Tree<ClassRepresentation> newTree = null;
                    boolean first = true;
                    for (Tree<ClassRepresentation> childTree : childTrees) {
                        if (first) {
                             newTree = childTree.addParent(model);
                             trees.add(newTree);
                             first = false;
                        } else {
                            newTree.addChild(childTree);
                        }
                        trees.remove(childTree);
                    }

                } else {

                    /* Connecting two subtrees */
                    parentTree.addChild(model);
                    Tree<ClassRepresentation> newBranch = parentTree.get(model);
                    for (Tree<ClassRepresentation> childTree : childTrees) {
                        newBranch.addChild(childTree);
                        trees.remove(childTree);
                    }

                }
            }
        }
    }

    private ClassRepresentation get(String object) {
        if (object == null) {
            return null;
        }
        for (ClassRepresentation model : models) {
            if (model.getClassName().equals(object)) {
                return model;
            }
        }
        return null;
    }

    private Tree<ClassRepresentation> get(ClassRepresentation model) {
        if (model == null) {
            return null;
        }
        for (Tree<ClassRepresentation> tree : trees) {
            if (tree.getHead().getClassName().equals(model.getClassName())) {
                return tree;
            } else {
                Tree<ClassRepresentation> candidate = tree.getDeep(model, new Comparator<ClassRepresentation>() {
                    public int compare(ClassRepresentation o1, ClassRepresentation o2) {
                        if (o1.getClassName().equals(o2.getClassName())) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private List<Tree<ClassRepresentation>> getAll(String extendsClass) {
        LinkedList<Tree<ClassRepresentation>> result = new LinkedList<Tree<ClassRepresentation>>();
        if (extendsClass == null) {
            return null;
        }
        for (Tree<ClassRepresentation> tree : trees) {
            if (tree.getHead().whatExtends() != null &&
                tree.getHead().whatExtends().equals(extendsClass)) {
                result.add(tree);
            }
        }
        if (result.size() > 0) {
            return result;
        } else {
            return null;
        }
    }

    private void separateBareClasses() {
        LinkedList<Tree<ClassRepresentation>> bareTress = new LinkedList<Tree<ClassRepresentation>>();
        for (Tree<ClassRepresentation> tree : trees) {
            if (tree.getChilds().size() < 1) {
                bareTress.add(tree);
            }
        }
        for (Tree<ClassRepresentation> tree : bareTress) {
            bare.add(tree.getHead());
            trees.remove(tree);
        }
    }

    public LinkedList<Tree<ClassRepresentation>> getTrees() {
        return trees;
    }

    public LinkedList<ClassRepresentation> getBare() {
        return bare;
    }

    private void rearangeBare() {
        lastHeight = super.arrange(bare) + space;
    }

    private void rearangeTrees() {
        classTrees = new LinkedList<Tree<ClassBox>>();
        for (Tree<ClassRepresentation> tree : trees) {
//            ClassTree classTree = new ClassTree(tree);
            Tree<ClassBox> box = rearangeCreateBoxes(null, tree);
            rearangeTranslate(box, 0, lastHeight);
//            classTree.setTop(lastHeight);
            lastHeight += box.getHead().height + space;
            classTrees.add(box);
        }
    }

    /**
     * Recursively goes down to create Tree of ClassBox'es.
     * Recursively goes up to update box width and height
     */
    private Tree<ClassBox> rearangeCreateBoxes(Tree<ClassBox> head, Tree<ClassRepresentation> tree) {
        /* Head */
        Tree<ClassBox> parent;
        if (head == null) {
            head = new Tree<ClassBox>(new ClassBox(tree.getHead()));
        }
        parent = head;

        /* Convert ClassRepresentaion to ClassBox */
        for (Tree<ClassRepresentation> subTree : tree.getChilds()) {
            ClassBox child = new ClassBox(subTree.getHead());
            rearangeCreateBoxes(head.addChild(child), subTree);
        }

        /* Update Width and Height */
        int maxChildsHeight = 0;
        int width = 0;
        int i = 0;
        for (Tree<ClassBox> subTree : head.getChilds()) {
            width += subTree.getHead().width;
            maxChildsHeight = Math.max(maxChildsHeight, subTree.getHead().height + space);
        }
        if (head.getHead().representation.getWidth() > width) {
            head.getHead().width = head.getHead().representation.getWidth() + space;
        } else {
            head.getHead().width = width;
        }
        head.getHead().height = head.getHead().representation.getHeight() + maxChildsHeight;

        /* Parent */
        return parent;
    }

    /**
     * Recursively goes down to to translate (left and top) and
     * set head horizontal position to center.
     */
    private void rearangeTranslate(Tree<ClassBox> head, int left, int top) {
        head.getHead().top = top;
        head.getHead().left = left;
        int oldLeft = left;
        top += head.getHead().representation.getHeight() + space;
        for (Tree<ClassBox> subTree : head.getChilds()) {
            rearangeTranslate(subTree, left, top);
            subTree.getHead().left = left;
            subTree.getHead().top = top;
            left += subTree.getHead().width;
        }
        int horizontalSpace = head.getHead().width - head.getHead().representation.getWidth();
        head.getHead().headLeft = oldLeft + (horizontalSpace / 2);
        head.getHead().FIXME_LEFT = (horizontalSpace / 2);
        head.getHead().savePosition();
    }

    public LinkedList<Tree<ClassBox>> getClassTrees() {
        return classTrees;
    }


    public class ClassBox {
        ClassRepresentation representation;
        public int width;
        public int height;
        public int top;
        public int headLeft;
        public int FIXME_LEFT;
        public int left;

        ClassBox(ClassRepresentation representation) {
            this.representation = representation;
        }

        public void savePosition() {
            getPanel().getZoomableComponent(representation).setLocation(headLeft, top);
        }

        @Override
        public String toString() {
            return representation.getClassName() + " -> " + representation.whatExtends() + " | " + left + "x" + top + " ! " + width + "x" + height + " h " + headLeft + " & " + representation.getWidth() + "x" + representation.getHeight();
        }
    }
}
