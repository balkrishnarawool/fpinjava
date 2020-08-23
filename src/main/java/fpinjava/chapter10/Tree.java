package fpinjava.chapter10;

// TODO
// List has limitations:
// - The complexity of accessing elements grows proportionally with the number of elements.
// For example, searching for a particular element may necessitate examining all elements
// if it happens that the searched-for element is the last in the list.
// - Among other less efficient operations are sorting, accessing elements by their index,
// and finding the maximal or minimal element.
// Definitions:
// - Tree
// - Element/ Node
// - Binary tree
// - Branch: Left branch, Right branch
// - Subtree
// - Root
// - Leaf
// - Full tree
// - Perfectly balanced tree/ Perfect tree
// - Imperfectly balanced tree
// - Perfectly imbalanced tree
// - Size
// - Height
// - Depth
// Height and depth of an empty tree are equal to -1.
// Leafy tree: A tree that only contains data at the leaf nodes
// Ordered Binary Tree / Binary Search Tree
// A recursive search in a perfectly balanced binary tree will never overflow the stack.
// - Because the standard stack size allows for minimum 1000 recursive calls/ stack frames.
// - A perfectly balanced binary tree of height 1000 contains 2^1000 elements, you’ll never have enough main memory for such data.
// - But the problem is not all trees are perfectly balanced and therefore we need a mechanism to balance them.
// - A perfectly imbalanced tree is in fact a singly linked list
// Insertion order:
// - The the balance of the tree depends on the order in which elements are inserted.
// - Inserting ordered elements will produce a totally unbalanced tree.
// - Many insertion orders will produce identical trees.
// Tree traversal order:
// - A set of 10 elements can be inserted into a tree in 3,628,800 distinct orders, but this will only produce 16,796 distinct trees.
// - Ordered trees are very efficient for storing and retrieving random data, but they’re very bad for storing and retrieving preordered data.
// - Recursive traversal orders
//   - Pre-order, in-order, post-order
//   - Depth first / height first
//   - Depth first pre-order, depth first in-order, depth first post-order
// - No-recursive traversal order
//   - Breadth-first search / level-order traversal
public abstract class Tree<A extends Comparable<A>> {

    public abstract A value();
    abstract Tree<A> left();
    abstract Tree<A> right();

    @SuppressWarnings("rawtypes")
    private static Tree EMPTY = new Empty();

    public static class Empty<A extends Comparable<A>> extends Tree<A> {

        private Empty(){
        }

        @Override
        public A value() {
            throw new IllegalStateException("value called on empty tree");
        }

        @Override
        Tree<A> left() {
            throw new IllegalStateException("left called on empty tree");
        }

        @Override
        Tree<A> right() {
            throw new IllegalStateException("right called on empty tree");
        }

        @Override
        public String toString() {
            return "E";
        }
    }

    public static class T<A extends Comparable<A>> extends Tree<A> {

        private A value;
        private Tree<A> left;
        private Tree<A> right;

        private T(A value, Tree<A> left, Tree<A> right){
            this.value = value;
            this.left = left;
            this.right = right;
        }

        @Override
        public A value() {
            return value;
        }

        @Override
        Tree<A> left() {
            return left;
        }

        @Override
        Tree<A> right() {
            return right;
        }

        @Override
        public String toString() {
            return "T";
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Tree<A> empty() {
        return EMPTY;
    }
}