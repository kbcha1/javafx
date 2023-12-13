package DyanmicPlanningShow1;

import java.util.ArrayList;
import java.util.List;

public class DynamicPrograming {

    public static final String[] TYPES=new String[] {
            "一维节点选择问题",
            "二维节点选择问题",
            "最短路径问题"};

    private final int N;
    private final String optType;
    private final String sep = "/";

    private int T;
    private boolean A[][];    // A: n*n转移矩阵，节点i到节点j是否可以转移或概率
    private Benifit B;    // B：T*n发射矩阵，时刻t访问节点i带来的收益增量，如果与时间无关就是一维向量，必要时换成HMM里的发射概率
    private int pi[];    // pi：初始化概率，第0步可选择的节点，不是必须，可由B[0]和path[0]代表
    private int f[][];    // f：T*n收益矩阵，时刻t访问节点i的前向最大收益
    private String path[][];    // path：T*n与f相对的现阶段最大收益路径

    // 解题步骤：先构造A B，再求f
    // 最终解：f[T-1]最大元素
    // path不是必须，但是可以帮助检查错误
    // 这里“时间”只代表变化的维度，不一定只是时间维度
    // 熟练之后刷题只要看能不能构造A B pi就行了

    private int target = -1;
    private int bestT = -1;
    private int bestN = -1;
    private int bestResult;
    private int[] bestTForNode;

    public DynamicPrograming(int n, int t, String optType) {
        //给定节点数、时间、求解类型
        N = n;
        T = t;
        this.optType = optType;

        try {
            if (!(optType == "max" || optType == "min"))
                throw new RuntimeException();
            else {
                f = new int[t + 1][n + 1];//TxN
                path = new String[t + 1][n + 1];
                path[0][0] = ""; //以虚拟0号节点为时刻0的访问节点
                bestTForNode = new int[n + 1];
            }

        }catch (Exception exception1){
            System.out.println("目标约束错误！请确认求解目标约束是否为max或min");
            System.exit(1);
        }
    }

    public void setA(boolean[][] a) {
        A = new boolean[N+1][N+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=N; j++) {
                A[i][j] = a[i-1][j-1];
            }
        }
    }

    public String getSep() {
        return sep;
    }

    public void setB(int[] input, String dim){
        B = new Benifit(input, dim);
    }

    public void setB(int[][] input, String dim1, String dim2){
        B = new Benifit(input, dim1, dim2);
    }

    public void setB(int[][][] input){
        B = new Benifit(input);
    }

    public void setPi(int[] pi) {
        this.pi = pi;
    }

    public int[][] getF() {
        return f;
    }

    public String[][] getPath() {
        return path;
    }

    public int getN() {
        return N;
    }

    public int getT() {
        return T;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setBestTForNode(int[] bestTForNode) {
        this.bestTForNode = bestTForNode;
    }

    public int[] getBestTForNode() {
        return bestTForNode;
    }

    public void train(CheckPath cp) {

        if(A==null) {
            System.out.println("Matrix A is not set");
            return;
        }

        if(B==null) {
            System.out.println("Matrix B is not set");
            return;
        }

        //t==0时刻
        if(pi==null) {

            path[0][0] = "";

            for(int i=1; i<=N; i++) {
                A[0][i] = true;
            }
        }else{
            for(int i : pi) {
                A[0][i] = true;
            }
        }


        for(int t=1; t<=getT(); t++) {
            //t时刻
            for(int toNode=1; toNode<=N; toNode++) {
                //t时刻到达toNode节点
                int best_gain = optType=="min" ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                int best_fromNode = -1;

                for(int fromNode=0; fromNode<=N; fromNode++) {
                    //t时刻从fromNode节点到达toNode节点
                    if(!A[fromNode][toNode] || path[t-1][fromNode]==null) continue;

                    if(cp!=null && !cp.checkPath(this, t, toNode, fromNode)) continue;

                    int gain = f[t-1][fromNode] + B.getB(t, fromNode, toNode);
                    boolean betterGain = optType=="min" ? gain < best_gain : gain > best_gain;
                    if(betterGain) {
                        best_gain = gain;
                        best_fromNode = fromNode;
                    }

                }

                if(best_fromNode!=-1) {
                    //best_fromNode为0表示没有找到任何fromNode可以到达toNode
                    f[t][toNode] = best_gain;
                    if(path[t-1][best_fromNode]=="")
                        path[t][toNode] = toNode + "";
                    else
                        path[t][toNode] = path[t-1][best_fromNode] + getSep() + toNode;
                }
            }
//			System.out.println(Arrays.toString(this.bestTForNode));

            boolean allNull = true;
            for(String p : path[t]) {
                if(p!=null) {
                    allNull = false;
                    break;
                }
            }

            if(allNull) {
                T = t-1;//提前结束
                break;
            }

        }



    }

    public void evaluate(Evaluator ev) {

        if(ev==null) {
            this.setBestT(getT());
            if(target==-1) {
                setBestResult(optType=="max" ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                for(int i=1; i<=N; i++) {
                    if(path[getBestT()][i]==null) continue;
                    boolean betterF = optType=="max" ? f[getBestT()][i] > getBestResult() : f[getBestT()][i] < getBestResult();
                    if(betterF) {
                        setBestResult(f[getBestT()][i]);
                        this.setBestN(i);
                    }
                }
            }else {
                this.setBestN(target);
                this.setBestResult(f[getBestT()][getBestN()]);
            }

        }else {
            ev.evaluate(this);
        }

    }

    public void output() {

        for(int t=1; t<=T; t++) {
            System.out.println("第" + t + "次");

            System.out.print("最佳收益(路径)：");
            for(int i=1; i<=N; i++) {

                String p = path[t][i];
                //				System.out.print("Node"+i+": ");
                if(p==null) {
                    //					System.out.print("-   ");
                }else{
                    System.out.print(f[t][i]+"(" + p + ")   ");
                }

            }
            System.out.println();

            System.out.println();
        }

        System.out.println(String.format("整体最优结果为%d, 步长为%d, 最优路径（之一）为：%s",
                this.getBestResult(), this.getBestT(), this.path[getBestT()][getBestN()]));
    }

    public String getOptType() {
        return optType;
    }

    public int getBestT() {
        return bestT;
    }

    public void setBestT(int bestT) {
        this.bestT = bestT;
    }

    public int getBestN() {
        return bestN;
    }

    public void setBestN(int bestN) {
        this.bestN = bestN;
    }

    public void setT(int t) {
        T = t;
    }

    public int getBestResult() {
        return bestResult;
    }

    public void setBestResult(int bestResult) {
        this.bestResult = bestResult;
    }

    class Benifit {

        private int dims;
        private boolean step;
        private boolean start;
        private boolean end;
        private int B1[];
        private int B2[][];
        private int B3[][][];

        Benifit(int[] input, String dim){

            dims = 1;
            setDim(dim);

            //0号节点
            B1 = new int[N+1];
            for(int i=1; i<=N; i++) {
                B1[i] = input[i-1];
            }

        }


        Benifit(int[][] input, String dim1, String dim2){
            setDim(dim1);
            setDim(dim2);
            dims = 2;

            //0号节点
            if(step) {
                //与step有关
                B2 = new int[getT()+1][N+1];
                for(int t=1; t<=getT(); t++) {
                    for(int i=1; i<=N; i++) {
                        B2[t][i] = input[t-1][i-1];
                    }
                }
            }else {
                //与step无关
                B2 = new int[N+1][N+1];
                for(int i=1; i<=N; i++) {
                    for(int j=1; j<=N; j++) {
                        B2[i][j] = input[i-1][j-1];
                    }
                }
            }
        }


        Benifit(int[][][] input){
            dims = 3;
            step = true;
            start = true;
            end = true;

            //0号节点
            B3 = new int[getT()+1][N+1][N+1];//int[T][N][N]，收益矩阵，B[t][i][j]表示时刻t,选择节点i且上一时刻选择节点j带来的收益增量
            for(int t=1; t<=getT(); t++) {
                for(int i=1; i<=N; i++) {
                    for(int j=1; j<=N; j++) {
                        B3[t][i][j] = input[t-1][i-1][j-1];
                    }
                }
            }
        }

        public int getB(int t, int i, int j) {
            //time t from i to j
            if(dims==1) {
                if(step)
                    return B1[t];
                else if(start)
                    return B1[i];
                else
                    return B1[j];
            }else if(dims==2) {
                if(step) {
                    if(start)
                        return B2[t][i];
                    else
                        return B2[t][j];
                }else {
                    return B2[i][j];
                }
            }else {
                return B3[t][i][j];
            }
        }


        private void setDim(String dim) {
            if(dim=="step")
                step = true;
            else if(dim=="start")
                start = true;
            else if(dim=="end")
                end = true;
            else//TODO throw Exceptions
                ;
        }


    }

    public static void main() {
        case1();
        System.out.println("\n\n==========================");
        case2();
        System.out.println("\n\n==========================");
        case3();
    }

    private static void case1() {

        int input [] = {10, 6, 7, 14, 12};//输入分数

        int N=5, T=5;//节点数，时间T

        boolean A[][] = new boolean[N][N];//转移矩阵A

        for(int i=0;i<N;i++) {//按要求输入A
            for(int j=0; j<N; j++) {
                if(j>i+1 || j<i-1)
                    A[i][j]=true;
            }
        }

        DynamicPrograming dp = new DynamicPrograming(N, T, "max");

        dp.setA(A);

        dp.setB(input, "end");

        //dp.setPi(new int[] {1});

        List<Integer>[] rejectedNodes = new ArrayList[N+1];

        for(int i=1; i<=N; i++) {
            //这里恰好与A相同
            rejectedNodes[i] = new ArrayList<Integer>();
            rejectedNodes[i].add(i-1);
            rejectedNodes[i].add(i);
            rejectedNodes[i].add(i+1);
        }

        dp.train(new CheckPathTypeRejectNodes(rejectedNodes));

        dp.evaluate(null);

        dp.output();

    }

    private static void case2() {

        int input [][] = {
                {7, 0, 0, 0, 0},
                {3, 8, 0, 0, 0},
                {8, 1, 0, 0, 0},
                {2, 7, 4, 4, 0},
                {4, 5, 2, 6, 5}};

        int N = 5;
        int T = 5;

        boolean A[][] = new boolean[N][N];
        for(int i=0;i<N;i++) {
            A[i][i] = true;
            if(i<=N-2) A[i][i+1] = true;
        }

        DynamicPrograming dp = new DynamicPrograming(N, T, "max");

        dp.setA(A);

        dp.setB(input, "step", "end");

        dp.setPi(new int[] {1});

        dp.train(null);

        dp.evaluate(null);

        dp.output();

    }

    private static void case3() {
        int N = 5;
        int T = 1000;
        int source = 1;
        int target = 4;

        int [][] input = new int[N][N];
        input[0][1]=2;
        input[0][2]=6;
        input[0][4]=10;
        input[1][2]=3;
        input[1][3]=7;
        input[2][3]=1;
        input[2][4]=15;
        input[3][4]=2;

        boolean A[][] = new boolean[N][N];
        for(int i=0;i<N;i++) {
            for(int j=i;j<N;j++) {
                if(input[i][j]>0) {
                    A[i][j] = true;
                    A[j][i] = true;
                    input[j][i] = input[i][j];
                }
            }
        }

        DynamicPrograming dp = new DynamicPrograming(N, T, "min");

        dp.setA(A);

        dp.setB(input, "start", "end");

        dp.setPi(new int[] {source});
        dp.setTarget(target);

        dp.train(new CheckPathTypeShortestPath());

        dp.evaluate(new EvaluatorTypeShortestPath());

        dp.output();

    }

}

@FunctionalInterface
interface CheckPath {
    boolean checkPath(DynamicPrograming dp, int t, int toNode, int fromNode);
}
@FunctionalInterface
interface Evaluator {
    void evaluate(DynamicPrograming dp);
}

class CheckPathTypeRejectNodes implements CheckPath {

    private List<Integer>[] rejectedNodes;

    public CheckPathTypeRejectNodes(List<Integer>[] rejectedNodes) {
        this.rejectedNodes = rejectedNodes;
    }

    @Override
    public boolean checkPath(DynamicPrograming dp, int t, int toNode, int fromNode) {
        String path = dp.getPath()[t-1][fromNode];

        for(int node : rejectedNodes[toNode]) {
            if(path.startsWith(node+dp.getSep()))
                return false;//出现在第一个位置
            else if(path.endsWith(dp.getSep()+node))
                return false;//出现在最后
            else if(path.indexOf(dp.getSep()+node+dp.getSep())>-1)
                return false; //出现在中间
        }
        return true;
    }

}

class CheckPathTypeShortestPath implements CheckPath {

    @Override
    public boolean checkPath(DynamicPrograming dp, int t, int toNode, int fromNode) {
        if(fromNode==0)
            return true;

        int tbest = dp.getBestTForNode()[fromNode];
        if(tbest==0) {
            dp.getBestTForNode()[fromNode] = t-1;
            return true;
        }else if(tbest==t-1) {
            return true;
        }


        boolean newRecord = dp.getOptType()=="max" ?
                dp.getF()[t-1][fromNode]>dp.getF()[tbest][fromNode] :
                dp.getF()[t-1][fromNode]<dp.getF()[tbest][fromNode];
        if(newRecord) {
            //tbest==0：第一次做fromNode
            dp.getBestTForNode()[fromNode] = t-1;
            return true;
        }

        return false;
    }

}

class EvaluatorTypeShortestPath implements Evaluator {

    @Override
    public void evaluate(DynamicPrograming dp) {
        int bestT = dp.getBestTForNode()[dp.getTarget()];
        dp.setBestT(bestT);
        dp.setBestN(dp.getTarget());
        dp.setBestResult(dp.getF()[bestT][dp.getTarget()]);

        for(int t=dp.getT(); t>0; t--) {
            if(dp.getPath()[t][dp.getTarget()]==null)
                dp.setT(dp.getT() - 1);
            else
                break;
        }



    }

}