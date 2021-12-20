public class experiment {
    public static void main(String[] args) {

        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        int[] mArray = {10,15,20,25};
        for (int M : mArray) {
            double startTime = System.nanoTime();
            int m = (int) Math.pow(2,M);
            FibonacciHeap.HeapNode[] keys = new FibonacciHeap.HeapNode[m+1];
            for (int i = m-1; i>-2; i--) {
                keys[i+1] = fibonacciHeap.insert(i);
            }
            fibonacciHeap.deleteMin();
            for (int j = M; j>0;j--) {
                int p = (int)(m-Math.pow(2,j)) + 1;
                fibonacciHeap.decreaseKey(keys[p + 1],m+1);
            }
            double endTime   = System.nanoTime();
            double totalTime = Math.pow(10,-6)*(endTime - startTime);
            System.out.println("for M = "+M);
            System.out.println("Run-time "+totalTime);
            System.out.println("Total links "+ FibonacciHeap.totalLinks());
            System.out.println("Total cuts "+ FibonacciHeap.totalCuts());
            System.out.println("Potential "+ fibonacciHeap.potential());
        }
    }
}
