package fpinjava.chapter10;

import fpinjava.chapter2.Function;
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
// Folding trees:
// - Post order left
// - Pre order left
// - Post order right
// - Pre order right
// - In order left
// - In order right
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

    protected abstract List<A> toListPreOrderLeft();

    public abstract Tree<A> merge(Tree<A> a);
    public abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g);
    public abstract <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g);
    public abstract <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f);
    public abstract <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f);
    public abstract <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f);

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
        protected List<A> toListPreOrderLeft() {
            return List.list();
        }

        @Override
        public Tree<A> merge(Tree<A> a) {
            return a;
        }

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g) {
            return identity;
        }

        @Override
        public <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return identity;
        }

        @Override
        public <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return identity;
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

        @Override
        public <B> B foldLeft(B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> g) { // ote the signature of f is different than foldRight
                                                                                      // Apply g to
            return g.apply(right.foldLeft(identity, f, g))                            // folded right subtree and
                    .apply(f.apply(left.foldLeft(identity, f, g)).apply(this.value)); // result of applying f to folded left subtree and root-value
            // Note that there is other implementation possible as below.
            // This implementation is also correct according to the definition of folding, but result might be different.
            //      Apply g to
            //      folded left subtree and
            //      result of applying f to folded right subtree and root-value

        }

        @Override
        public <B> B foldRight(B identity, Function<A, Function<B, B>> f, Function<B, Function<B, B>> g) { // Note the signature of f is different than foldLeft
                                                                                      // Apply g to
            return g.apply(f.apply(this.value).apply(left.foldRight(identity, f, g))) // result of applying f to root-value and folded left subtree and
                    .apply(right.foldRight(identity, f, g));                          // folded right subtree
            // Note that there is other implementation possible as below.
            // This implementation is also correct according to the definition of folding, but result might be different.
            //      Apply g to
            //      result of applying f to root-value and folded right subtree and
            //      folded left subtree
        }

        @Override
        public <B> B foldInOrder(B identity, Function<B, Function<A, Function<B, B>>> f) {
            return f.apply(left.foldInOrder(identity, f))
                    .apply(value).apply(right.foldInOrder(identity, f));
        }

        @Override
        public <B> B foldPreOrder(B identity, Function<A, Function<B, Function<B, B>>> f) {
            return f.apply(value).apply(left.foldPreOrder(identity, f))
                    .apply(right.foldPreOrder(identity, f));
        }

        @Override
        public <B> B foldPostOrder(B identity, Function<B, Function<B, Function<A, B>>> f) {
            return f.apply(left.foldPostOrder(identity, f))
                    .apply(right.foldPostOrder(identity, f)).apply(value);
        }

        @Override
        protected List<A> toListPreOrderLeft() {
            return left().toListPreOrderLeft()
                    .concat(right().toListPreOrderLeft()).cons(value);
        }

    }

    public static <A extends Comparable<A>> boolean lt(A first, A second) {
        return first.compareTo(second) < 0;
    }

    public static <A extends Comparable<A>> boolean lt(A first, A second,
                                                       A third) {
        return lt(first, second) && lt(second, third);
    }

    public static <A extends Comparable<A>> boolean ordered(Tree<A> left, A a, Tree<A> right) {
        return left.max().flatMap(lMax -> right.min().map(rMin ->
                lt(lMax, a, rMin))).getOrElse(left.isEmpty() && right.isEmpty())
                || left.min().mapEmpty().flatMap(ignore -> right.min().map(rMin ->
                lt(a, rMin))).getOrElse(false)
                || right.min().mapEmpty().flatMap(ignore -> left.max().map(lMax ->
                lt(lMax, a))).getOrElse(false);
    }

    public static <A extends Comparable<A>> Tree<A> tree(Tree<A> t1, A a, Tree<A> t2) {
        return ordered(t1, a, t2)
                ? new T<>(t1, a, t2)
                : ordered(t2, a, t1)
                ? new T<>(t2, a, t1)
                : Tree.<A>empty().insert(a).merge(t1).merge(t2);
    }

    public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
        return toListPreOrderLeft().foldLeft(identity, f);
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