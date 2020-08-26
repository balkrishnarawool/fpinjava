package fpinjava.chapter10;

import fpinjava.chapter7.Result;
import fpinjava.chapter8.List;

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
// Merging two trees gives a tree with a size (number of elements) that can be smaller than the sum of the sizes of the original trees
// The height of the result is higher than the optimal height (the smallest power of 2 higher than the resulting size which is log2(size))
public abstract class Tree<A extends Comparable<A>> {

    public abstract A value();
    abstract Tree<A> left();
    abstract Tree<A> right();

    public abstract Tree<A> insert(A a);
    public abstract boolean member(A a);
    public abstract int size();
    public abstract int height();
    public abstract Result<A> max();
    public abstract Result<A> min();
    public abstract boolean isEmpty();

    public abstract Tree<A> remove(A a);
    protected abstract Tree<A> removeMerge(Tree<A> ta);

    public abstract Tree<A> merge(Tree<A> a);

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

        @Override
        public Tree<A> insert(A insertedValue) {
            return new T<>(empty(), insertedValue, empty());
        }

        @Override
        public boolean member(A a){
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int height() {
            return -1;
        }

        @Override
        public Result<A> max() {
            return Result.empty();
        }

        @Override
        public Result<A> min() {
            return Result.empty();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Tree<A> remove(A a) {
            return this;
        }

        @Override
        protected Tree<A> removeMerge(Tree<A> ta) {
            return this;
        }

        @Override
        public Tree<A> merge(Tree<A> a) {
            return a;
        }

    }

    public static class T<A extends Comparable<A>> extends Tree<A> {

        private A value;
        private Tree<A> left;
        private Tree<A> right;

        private T(Tree<A> left, A value, Tree<A> right){
            this.left = left;
            this.value = value;
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

        @Override
        public Tree<A> insert(A insertedValue) {
            return insertedValue.compareTo(this.value) < 0
                    ? new T<>(left.insert(insertedValue), this.value, right)
                    : insertedValue.compareTo(this.value) > 0
                        ? new T<>(left, this.value, right.insert(insertedValue))
                        : new T<>(this.left, insertedValue, this.right);
        }

        @Override
        public boolean member(A value) {
            return value.compareTo(this.value) < 0
                    ? left.member(value)
                    : value.compareTo(this.value) > 0
                        ? right.member(value)
                        : true;
        }

        @Override
        public int size() {
            return 1 + left.size() + right.size();
        }

        @Override
        public int height() {
            return 1 + Math.max(left.height(), right.height());
        }

        @Override
        public Result<A> max() {
            return right.max().orElse(() -> Result.success(value));
        }

        @Override
        public Result<A> min() {
            return left.min().orElse(() -> Result.success(value));
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        protected Tree<A> removeMerge(Tree<A> ta) {
            if (ta.isEmpty()) {
                return this;
            }
            if (ta.value().compareTo(value) < 0) {
                return new T<>(left.removeMerge(ta), value, right);
            } else if (ta.value().compareTo(value) > 0) {
                return new T<>(left, value, right.removeMerge(ta));
            }
            throw new IllegalStateException("We shouldn't be here");
        }

        @Override
        public Tree<A> remove(A a) {
            if (a.compareTo(this.value) < 0) {
                return new T<>(left.remove(a), value, right);
            } else if (a.compareTo(this.value) > 0) {
                return new T<>(left, value, right.remove(a));
            } else {
                return left.removeMerge (right);
            }
        }

        @Override
        public Tree<A> merge(Tree<A> a) {
            if (a.isEmpty()) {
                return this;
            }
            if (a.value().compareTo(this.value) > 0) {
                return new T<>(left, value, right.merge(new T<>(empty(),
                        a.value(), a.right()))).merge(a.left());
            }
            if (a.value().compareTo(this.value) < 0) {
                return new T<>(left.merge(new T<>(a.left(), a.value(),
                        empty())), value, right).merge(a.right());
            }
            return new T<>(left.merge(a.left()), value, right.merge(a.right()));
        }
    }

    public static <A extends Comparable<A>> Tree<A> tree(List<A> list) {
        return list.foldLeft(empty(), t -> t::insert);
    }

    @SafeVarargs
    public static <A extends Comparable<A>> Tree<A> tree(A... as) {
        return tree(List.list(as));
    }

    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Tree<A> empty() {
        return EMPTY;
    }

}