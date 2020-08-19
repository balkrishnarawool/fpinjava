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
public abstract class Tree<A> {
}