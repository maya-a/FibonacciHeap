/**
 * submitted by:
 *   1. Name: Maya Aderka
 *      University username: mayaaderka
 *      ID: 318850245
 *   2. Name: Tali Remenick
 *      University username: taliremenick
 *      ID: 207595794
 */

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    public HeapNode minNode;
    public static int totalLinks = 0;
    public static int totalCuts = 0; //includes cascading cuts
    public int size; //total amount of HeapNodes in the Heap
    public int trees;
    public int markedNodes;
    public HeapNode leftNode;
    public static final int minValue = (int) Double.NEGATIVE_INFINITY;

    public FibonacciHeap() {
        this.minNode = null;
        this.leftNode = null;
        this.size = 0;
        this.markedNodes = 0;
    }

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     * O(1) complexity
     */
    public boolean isEmpty() { return minNode == null; }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     *
     * O(1) complexity
     */
    public HeapNode insert(int key) {
        HeapNode newNode = new HeapNode(key);
        size++;
        trees++;
        if (isEmpty()) {
            leftNode = newNode;
            minNode = newNode;
            return newNode;
        }
        if (minNode.getKey() > key) {
            minNode = newNode;
        }
        HeapNode tempLeftPrev = leftNode.prev;
        leftNode.prev = newNode;
        newNode.next = leftNode;
        newNode.prev = tempLeftPrev;
        tempLeftPrev.next = newNode;
        leftNode = newNode;
        return newNode;
    }
    /**
     * private void insertFromRight(FibonacciHeap H, HeapNode root)
     *
     * Inserts input HeapNode root into FibonacciHeap H as the right root.
     *
     * O(1) complexity
     */
    private void insertFromRight(FibonacciHeap H, HeapNode root) {
        if (H.isEmpty()) {
            H.leftNode = root;
            H.minNode = root;
            H.leftNode.next = H.leftNode;
            H.leftNode.prev = H.leftNode;
            H.trees++;
        }
        else {
            HeapNode last = H.leftNode.prev;
            last.next = root;
            root.prev = last;
            root.next = H.leftNode;
            H.leftNode.prev = root;
            H.trees++;
            if (H.minNode.getKey() > root.getKey()) {
                H.minNode = root;
            }
        }
    }


    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     * O(n) complexity
     */
    public void deleteMin() {
        if (minNode == leftNode) {
            leftNode = leftNode.next;
            if (size == 1) {
                this.minNode = null;
                this.leftNode = null;
                this.size = 0;
                return;
            }
        }

        if (minNode.rank == 0) {
            minNode.prev.next = minNode.next;
            minNode.next.prev = minNode.prev;
            trees--;
        }

        else if (trees == 1) {
            trees = minNode.rank;
            leftNode = minNode.child;
            int childNum = minNode.rank;
            minNode = leftNode;
            HeapNode x = leftNode;
            for (int i = 0; i < childNum; i++) {
                x.parent = null;
                removeMark(x);
                if (x.getKey() < minNode.getKey()) {
                    minNode = x;
                }
                x = x.next;
            }
        }
        else {
            HeapNode x = minNode.child;
            do {
                removeMark(x);
                x.parent = null;
                x = x.next;
            } while (x.getKey() != minNode.child.getKey());

            trees = trees + minNode.rank - 1;
            HeapNode tempChildPrev = minNode.child.prev;
            minNode.child.prev = minNode.prev;
            minNode.prev.next = minNode.child;
            tempChildPrev.next = minNode.next;
            minNode.next.prev = tempChildPrev;

            minNode.child = null;
        }
        size--;
        if (trees > 1) {
            consolidate();
        } else {
            minNode = leftNode;
        }
    }
    /**
     * private void removeMark(HeapNode x)
     *
     * removes the mark from input HeapNode x
     *
     * O(1) complexity
     */
    private void removeMark(HeapNode x) {
        if (x.mark) {
            markedNodes--;
            x.mark = false;
        }
    }
    /**
     * private void consolidate()
     *
     * Turns heap into a valid Binomial heap
     *
     * O(n) complexity
     */
    private void consolidate() {
        fromBuckets(toBuckets());
    }
    /**
     * private HeapNode[] toBuckets()
     *
     * Returns a Bucket array - an array that contains all of the heap's nodes organizes in binomial trees.
     * In index i of the output array there is either nothing or there is a pointer to a root of a binomial tree with i children.
     *
     * O(n) complexity
     */
    private HeapNode[] toBuckets() {
        int ranks = (int) (1.4404*(Math.log(size) / Math.log(2)));
        HeapNode[] treeArray = new HeapNode[ranks + 1];

        HeapNode x = leftNode;
        x.prev.next = null;
        HeapNode y;
        HeapNode z;
        while (x != null) {
            y = x;
            x = x.next;
            while (treeArray[y.rank] != null) {
                z = treeArray[y.rank];
                if (z.getKey() < y.getKey()) {
                    y = Link(z, y); //link (smaller,larger)
                } else {
                    y = Link(y, z); //link (smaller,larger)
                }
                treeArray[y.rank-1] = null;
                }
            treeArray[y.rank] = y;
            }
        return treeArray;
    }
    /**
     * private void fromBuckets(HeapNode[] treeArray)
     *
     * Receives a Bucket array - an array that contains all of the heap's nodes organizes in binomial trees.
     * In index i of the output array there is either nothing or there is a pointer to a root of a binomial tree with i children.
     * Inserts all roots from treeArray to a fibonacciHeap
     *
     * O(n) complexity
     */
    private void fromBuckets(HeapNode[] treeArray) {
        FibonacciHeap goodHeap = new FibonacciHeap();
        for (HeapNode root : treeArray) {
            if (root != null) {
                insertFromRight(goodHeap, root);
            }
        }
        leftNode = goodHeap.leftNode;
        minNode = goodHeap.minNode;
        trees = goodHeap.trees;
    }

    /**
     * private HeapNode Link(HeapNode smaller, HeapNode larger)
     *
     * Links input trees into a single binomial tree
     * Inserts all roots from treeArray to a fibonacciHeap
     *
     * O(1) complexity
     */
    private HeapNode Link(HeapNode smaller, HeapNode larger) {
        if (larger.next == null) { //larger is rightNode
            leftNode.prev = larger.prev;
            larger.prev.next = null;
        }

        else if (larger.getKey() == leftNode.getKey()) { //larger is leftNode
            larger.next.prev = larger.prev;
            leftNode = larger.next;
        }
        else {
            larger.next.prev = larger.prev;
            larger.prev.next = larger.next;
        }

        if (smaller.rank == 0) {
            smaller.child = larger;
            larger.parent = smaller;
            larger.next = larger;
            larger.prev = larger;
        } else {
            HeapNode tempChild = smaller.child;
            smaller.child = larger;
            larger.parent = smaller;
            larger.next = tempChild;
            HeapNode tempChildPrev = tempChild.prev;
            tempChild.prev = larger;
            tempChildPrev.next = larger;
            larger.prev = tempChildPrev;
        }
        smaller.rank++;
        totalLinks++;
        return smaller;
    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     * O(1) complexity
     */
    public HeapNode findMin() { return minNode; } //assuming that the heap maintains the heap law

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     * O(1) complexity
     */
    public void meld(FibonacciHeap heap2) {
        if (isEmpty()) {
            this.leftNode = heap2.leftNode;
            this.minNode = heap2.minNode;
            this.size = heap2.size;
            this.trees = heap2.trees;
            return;
        }
        if (heap2.isEmpty()) {
            return;
        }
        if (minNode.getKey()>heap2.minNode.getKey()) {
            minNode = heap2.minNode;
        }
        size += heap2.size;
        trees += heap2.trees;
        markedNodes += heap2.markedNodes;

        HeapNode right = leftNode.prev;
        right.next = heap2.leftNode;
        HeapNode tempLeftPrev = heap2.leftNode.prev;
        heap2.leftNode.prev = right;
        leftNode.prev = tempLeftPrev;
        tempLeftPrev.next = leftNode;
    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     * O(1) complexity
     */
    public int size() { return this.size; }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     * O(n) complexity
     */
    public int[] countersRep() {
        int counterSize = (int) (1.4404*(Math.log(size) / Math.log(2)));
        int[] counter = new int[counterSize + 1];
        counter[leftNode.rank]++;
        HeapNode x = leftNode.next;
        while (x.getKey() != leftNode.getKey()) {
            counter[x.rank]++;
            x = x.next;
        }
        return counter;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     * O(n) complexity
     */
    public void delete(HeapNode x) {
        decreaseKey(x,minValue);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     *
     * O(logn) WC complexity
     * O(1) amortized complexity
     */
    public void decreaseKey(HeapNode x, int delta) {
        x.key -= delta;
        if (x.getKey() < minNode.getKey()){
            minNode = x;
        }
        if (x.parent != null && x.parent.getKey()>x.getKey()) {
            cascadingCut(x, x.parent);
        }

    }
    /**
     * private void cut(HeapNode x, HeapNode parent)
     *
     * cuts HeapNode x out of its parent and adds it as a tree to the heap
     *
     * O(1) complexity
     */
    private void cut(HeapNode x, HeapNode parent) {
        x.parent = null;
        if (parent.child.getKey() == x.getKey()) {
            if (x.next.getKey() == x.getKey()) {
                parent.child = null;
            } else {
                parent.child = x.next;
            }
        }

        x.prev.next = x.next;
        x.next.prev = x.prev;

        HeapNode last = leftNode.prev;
        last.next = x;
        x.prev = last;
        x.next = leftNode;
        leftNode.prev = x;
        leftNode = x;

        removeMark(x);
        parent.rank -= 1;
        totalCuts++;
        trees++;
    }
    /**
     * private void cascadingCut(HeapNode x, HeapNode parent)
     *
     * performs a cut and maintains the markings of the nodes
     *
     * O(logn) WC complexity
     * O(1) amortized complexity
     */
    private void cascadingCut(HeapNode x, HeapNode parent) {
        cut(x,parent);
        if (parent.parent != null) { //parent is not root
            if (!parent.mark) {
                parent.mark = true;
                markedNodes++;
            } else {
                cascadingCut(parent,parent.parent);
            }
        }
    }

//    private void heapifyUp(HeapNode node) {
//        HeapNode currParent = node.parent;
//        while (currParent != null && currParent.getKey() > node.getKey()) {
//            HeapNode tempParent = currParent.parent;
//            HeapNode tempChild = node.child;
//            HeapNode tempNext = currParent.next;
//            HeapNode tempPrev = currParent.prev;
//
//            currParent.parent = node;
//            currParent.child = tempChild;
//            tempChild.parent = currParent;
//            currParent.next = node.next;
//            currParent.prev = node.prev;
//
//            node.parent = tempParent;
//            node.child = currParent;
//            node.next = tempNext;
//            node.prev = tempPrev;
//
//            currParent = node.parent;
//        }
//    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     *
     * O(1) complexity
     */
    public int potential() { return trees + (2*markedNodes); }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     *
     * tree which has larger value in its root under the other tree.
     * O(1) complexity
     */
    public static int totalLinks() { return totalLinks;}

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     *
     *  O(1) complexity
     */
    public static int totalCuts() { return totalCuts; }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     *
     * O(k*deg(H)) complexity (deg(H) is the degree of the only tree in H.)
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        FibonacciHeap minHeap = new FibonacciHeap();
        int[] keyArray = new int[k];

        HeapNode x = H.minNode;
        x.info = x;
        HeapNode y;
        for (int i = 0; i < k; i++) {
            keyArray[i] = x.getKey();
            if (x.info.child != null) {
                y = x.info.child;
                do {
                    minHeap.insert(y.getKey());
                    minHeap.leftNode.info = y;
                    y = y.next;
                }
                while (y.getKey() != x.info.child.getKey());
            }
            x = minHeap.findMin();
            minHeap.deleteMin();
        }

        return keyArray;
    }

    /**
     * public class HeapNode
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{
        public int key;
        public HeapNode info;
        public int rank; //number of children
        public boolean mark;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;

        public HeapNode(int key) {
            this.key = key;
            this.rank = 0;
            this.mark = false;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.child = null;
        }

        public int getKey() {
            return this.key;
        }
    }
}
