
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */

//Omri Yakir
	/*
	username: omriyakir
	id: 318867199
	 */
//Maya Raytan
	/*
	username: mayaraytan
	id: 209085711
	 */

public class FibonacciHeap
{
    private static int LINKS;
    private static int CUTS;
    private HeapNode first;
    private HeapNode min;
    private int trees;
    private int marked;
    private int size;

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     * Complexity: O(1)
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     * Complexity: O(1)
     */
    public HeapNode insert(int key)
    {
        HeapNode node = new HeapNode(key);
        add_tree_to_first(node);//add tree to first ,update min if needed
        size++; // promote heap size
        trees++; // promote number of trees
        return node;

    }

    /**
     * private void consolidation()
     * consolidate the heap trees
     * when finish:trees< 1.5log(size)
     * Complexity: amortized: O(logn). wc: O(n)
     */
    private void consolidation(){
        if(!isEmpty()){
            HeapNode [] buckets= new HeapNode[(int) Math.ceil((1.5*log2(size+1)+1))] ; // create buckets array size 1.5log(size)
            HeapNode tmp = first;
            do { // fill buckets with the heap's trees
                HeapNode next_tree = tmp.next;
                tmp.next=tmp;
                tmp.prev=tmp;
                while(buckets[tmp.rank]!=null){ // link until there is some empty place
                    tmp=link(tmp,buckets[tmp.rank]);
                    buckets[tmp.rank-1]=null;}
                buckets[tmp.rank]=tmp; // put the tree in that empty space
                tmp = next_tree;  }
            while(tmp!=first);
            first =null;
            int size_keeper = size;
            size=0; // temporary so add_tree_to_first will work correctly
            for (int i = buckets.length-1; i>-1; i--){ // add list to the heap from small rank to high
                if (buckets[i] !=null){
                        add_tree_to_first(buckets[i]);
                        size=size_keeper;
                    }
                }
            }
        }

    /**
     * private HeapNode link(HeapNode x , HeapNode y)
     *
     * the function links between the trees x and y.
     * returns the root of the linked tree
     * assume x.rank == y.rank
     * Complexity: O(1)
     */
    private HeapNode link(HeapNode x , HeapNode y){
        // only for debugging
        if (x.rank != y.rank){
            System.out.println("x.rank = " +x.key+" y.rank =" +y.rank);   }
        if (x.key > y.key){ // make x be the node with the smaller key
            HeapNode tmp = x;
            x=y;
            y=tmp; }
        //making the necessary connections
        x.next=x;
        x.prev=x;
        if (x.rank==0){//x doesn't have children
           x.child=y;
           y.parent =x;
        }
        else {
            HeapNode x_start = x.child;
            HeapNode x_last = x.child.prev;
            x.child = y;
            y.parent = x;
            y.next = x_start;
            y.prev = x_last;
            x_start.prev = y;
            x_last.next = y;
        }
        x.rank+=1; // promote the linked tree rank
        trees-=1;//reducing the total number of trees by 1;
        LINKS+=1;
        return x;

    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     * Complexity: amortized: O(logn). wc: O(n)
     */
    public void deleteMin()
    {
        if (!isEmpty()){
            HeapNode first_child=min.child;
            HeapNode tmp = first_child;
            for (int i=0;i<min.rank;i++){ // rank ==number of children
                if (tmp.mark){    // unmark min's children and decrease from total marked
                    marked--;
                    tmp.mark=false;}
                tmp=tmp.next;
                tmp.parent=null; // update min's children parent to null;
            }
            trees += min.rank-1; // update the amount of trees in the heap

            if (size ==1){ // min is the only node in the heap
                first=null;
                min=null;
            }
            else{
                if(min.rank==0){  // min has no children
                    if(first==min){first=min.next;}
                    min.prev.next = min.next;
                    min.next.prev = min.prev;
                }
                else{
                    HeapNode last_child = first_child.prev;
                    if(min.next==min){ // min is the only tree in the heap
                        first = first_child;   }
                    else{ // min has children and brothers
                        if (first==min){ first=first_child;}
                            min.prev.next = first_child;
                            first_child.prev = min.prev;
                            last_child.next = min.next;
                            min.next.prev = last_child;
                    }
                }

            }
            size--; // decrease size by 1
            consolidation();
        }
    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     * Complexity: O(1)
     */
    public HeapNode findMin()
    {
        return min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     * Complexity: O(1)
     */
    public void meld (FibonacciHeap heap2)
    {
        if (this.isEmpty()){ // this heap is empty and will become heap2
            first = heap2.first;
            min = heap2.min;
        }
        else{
            if(!heap2.isEmpty()){
                HeapNode heap1_first = first;
                HeapNode heap1_last = first.prev;
                HeapNode heap2_first = heap2.first;
                HeapNode heap2_last = heap2.first.prev;
                // make the necessary connections
                heap1_first.prev = heap2_last;
                heap1_last.next = heap2_first;
                heap2_first.prev = heap1_last;
                heap2_last.next = first;

                if (heap2.min.key< min.key){ // change min node if needed
                    min = heap2.min;  }
            }
        }
        size += heap2.size; // add the size of heap2 to the current heap size
        trees += heap2.trees; // add the amount of trees in heap2 to the current heap size
        marked += heap2.marked;   // add the amount of marked nodes in heap2 to the current heap sum of marked

    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     * Complexity: O(1)
     */
    public int size()
    {
        return size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     * Complexity: O(n)
     */
    public int[] countersRep()
    {
        if (this.isEmpty()){return new int[0];} //return empty array if heap is empty
        int[] rank_arr = new int[(int) Math.ceil(1.5*log2(size+1))+1];
        HeapNode node = first;
        do { //iterate over all trees in heap
            rank_arr[node.rank]++; // add 1 to number of arrays with this rank
            node = node.next;
        }
        while (node != first);
        int n = get_real_length(rank_arr);
        return copyArrayToSmallerLength(rank_arr, n); //remove redundant information from returned array
    }
    /**
     * private int get_real_length(int[] arr)
     *
     * Return last index in array which is not contain zero.
     * Complexity: O(n)
     */
    private int get_real_length(int[] arr){
        int len = arr.length; //begin in last index
        while (len > 0) {
            if (arr[len - 1] != 0) {break;}
            len--;} //moving to the start of the array and look for non-zero item
        return len;
    }

    /**
     * private int get_real_length(int[] arr)
     *
     * Return input array shorted to new len
     * Complexity: O(n)
     */
    private int[] copyArrayToSmallerLength(int[] arr, int len){
        int[] newArr = new int[len];
        for (int i = 0; i<len; i++){
            newArr[i] = arr[i];
        }
        return newArr;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     * Complexity: amortized: O(logn). wc: O(n).
     */
    public void delete(HeapNode x)
    {   // from the assumption isEmpty()==false -> min!=null
        decreaseKey(x, x.key-Math.abs(min.key)+1); // deacrease x's key so it'll be the min
        deleteMin(); // now removing x
    }

    /**
     * private void add_tree_to_first(HeapNode x)
     *
     * add x to first.
     * doesnt change trees and size
     * updating minimum if needed
     * Complexity: O(1)
     */
    private void add_tree_to_first(HeapNode x){
        {
            HeapNode tmp = first;
            first = x; // new node is now first
            if (isEmpty()){ // size==0
                x.next = x;
                x.prev = x;
                min = x;  }
            else {
                first.next = tmp;
                first.prev = tmp.prev;
                tmp.prev.next = first;
                tmp.prev = x;
                if (x.key < min.key) {
                    min = x; // change min if needed
                }
            }

        }
    }

    /**
     * private void cut(HeapNode x,HeapNode y)
     *
     * disconnecting x from y;
     * changing pointer
     * Complexity: O(1)
     */
    private void cut(HeapNode x,HeapNode y){
        x.parent=null;
        if (x.mark){ marked--;} // if x marked decreasing total marked
        x.mark=false; // roots not marked
        y.rank--;
        if (y.parent!=null && !y.mark){
            y.mark=true;
            marked++;
        }
        if (x.next == x){ // x is the only sun of y
            y.child=null; }
        else{
            y.child=x.next;
            x.prev.next=x.next;
            x.next.prev = x.prev;
        }
        CUTS++;
    }

    private static int log2(int N)
    {
        return (int)(Math.log(N) / Math.log(2));
    }

    /**
     * private void cascading_cut(HeapNode x ,HeapNode y)
     *
     * cut x from y
     * add x tree to left
     * keep on cutting to the root if parents are marked
     * Complexity: amortized: O(1). wc: O(logn).
     */
    private void cascading_cut(HeapNode x ,HeapNode y){
        boolean parent_mark=false;
        do {
            parent_mark = y.mark;
            cut(x,y);
            add_tree_to_first(x);
            trees++;
            x = y;
            y = y.parent;}
        while(parent_mark);
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     * Complexity: amortized: O(1). wc: O(logn).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.key -= delta;
        if (x.key < min.key){min = x;} //update min of heap if needed
        HeapNode y = x.parent;
        if (y!=null) { // if x isn't a root
            while (x.key < y.key) { // while a key is smaller than it's father
                cascading_cut(x, y);
                x = y;
            }
        }
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     * Complexity: O(1)
     */
    public int potential()
    {
        return trees + 2*marked;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     * Complexity: O(1)
     */
    public static int totalLinks()
    {
        return LINKS;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     * Complexity: O(1)
     */
    public static int totalCuts()
    {
        return CUTS;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     * Complexity: O(k*deg(H))
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] min_k = new int[k]; // initialized output array
        FibonacciHeap work_heap = new FibonacciHeap(); // creating help heap
        work_heap.insert(H.min.key); // inserting root
        HeapNode tmp = H.min;
        for (int i = 0; i < k; i++) {
            work_heap.deleteMin();
            min_k[i] = tmp.key; // update output array
            HeapNode first_child = tmp.child;
            HeapNode child = tmp.child;
            if (child != null) {
                do { //insert children
                    work_heap.insert(child.key);
                    child = child.next;
                }
                while (first_child != child);
            }
            tmp=H.min; // update pointer to the new min
        }
        return min_k;
    }


    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{

        public int key;
        private int rank;
        private boolean mark;
        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        private HeapNode pointer;


        public HeapNode(int key) {
            this.key = key;
        }

        public int getKey() {
            return this.key;
        }

    }
}
