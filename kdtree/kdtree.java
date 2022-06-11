import java.util.*;
import java.io.*;
public class kdtree{

    public static TreeNode rootnode;

    public static void main(String[] args){
        try{
        ArrayList<Integer> x_coord = new ArrayList<Integer>();
        ArrayList<Integer> y_coord = new ArrayList<Integer>();
        FileInputStream restStream = new FileInputStream("restaurants.txt");
        Scanner s = new Scanner(restStream);
        s.nextLine();
        while(s.hasNextLine()){
            String line = s.nextLine();
            int i = 0;
            while(true){
                if(line.charAt(i) == ','){
                    break;
                }
                i++;
            }
            x_coord.add(Integer.valueOf(line.substring(0,i)));
            y_coord.add(Integer.valueOf(line.substring(i+1)));
        }
    
        rootnode = build(x_coord, y_coord, 1);

        List<Integer> que_x_coord = new ArrayList<Integer>();
        List<Integer> que_y_coord = new ArrayList<Integer>();
        FileInputStream queStream = new FileInputStream("queries.txt");
        Scanner s2 = new Scanner(queStream);
        s2.nextLine();
        while(s2.hasNextLine()){
            String line = s2.nextLine();
            int i = 0;
            while(true){
                if(line.charAt(i) == ','){
                    break;
                }
                i++;
            }
            que_x_coord.add(Integer.valueOf(line.substring(0,i)));
            que_y_coord.add(Integer.valueOf(line.substring(i+1)));
        }
        for(int j = 0; j<que_y_coord.size(); j++) {
            int x_start = que_x_coord.get(j) - 100;
            int y_start = que_y_coord.get(j) - 100;
            int x_end = que_x_coord.get(j) + 100;
            int y_end = que_y_coord.get(j) + 100;

            int res = query(x_start, y_start, x_end, y_end, rootnode);

            FileOutputStream out = new FileOutputStream("output.txt", true);
            PrintStream p = new PrintStream(out);
            p.print(res + "\n");
        }
        }catch(FileNotFoundException e){
            System.out.println("FileNotFound!");
        }
    }

    public static TreeNode build(ArrayList<Integer> x_coord, ArrayList<Integer> y_coord, Integer level){
        if(x_coord.size() == 0){
            return null;
        }

        else if(x_coord.size() == 1){
            TreeNode node = new TreeNode();
            node.x_min = x_coord.get(0);
            node.y_min = y_coord.get(0);
            node.x_max = x_coord.get(0);
            node.y_max = y_coord.get(0);
            node.isLeaf = true;
            node.level = level;
            node.numberLeaves = 1;
            return node;
        }
        else if(level % 2 == 1){
            ArrayList<Integer> ls = new ArrayList<Integer>();
            for(int i = 0; i<x_coord.size(); i++){
                ls.add(x_coord.get(i));
            }
            ls = sortfn(ls);
            ArrayList<Integer> x1 = new ArrayList<Integer>();
            ArrayList<Integer> y1 = new ArrayList<Integer>();
            ArrayList<Integer> x2 = new ArrayList<Integer>();
            ArrayList<Integer> y2 = new ArrayList<Integer>();
            TreeNode node = new TreeNode();
            for(int i=0;i<(ls.size()+1)/2;i++){
                int j = ls.get(i);
                int k = 0;
                while(x_coord.get(k) != j){
                    k++;
                }
                x1.add(ls.get(i));
                y1.add(y_coord.get(k));
                x_coord.remove(k);
                y_coord.remove(k);
            }
            for(int i=(ls.size()+1)/2;i<ls.size();i++){
                int j = ls.get(i);
                int k = 0;
                while(x_coord.get(k) != j){
                    k++;
                }
                x2.add(ls.get(i));
                y2.add(y_coord.get(k));
                x_coord.remove(k);
                y_coord.remove(k);
            }
            node.left = build(x1,y1,level+1);
            if(node.left != null) node.left.parent = node;
            node.right = build(x2,y2,level+1);
            if(node.right != null) node.right.parent = node;
            if(node.left == null){
                node.x_min = node.right.x_min;
                node.y_min = node.right.y_min;
                node.x_max = node.right.x_max;
                node.y_max = node.right.y_max;
                node.numberLeaves = node.right.numberLeaves;
            }

            else if(node.right == null){
                node.x_min = node.left.x_min;
                node.x_max = node.left.x_max;
                node.y_min = node.left.y_min;
                node.y_max = node.left.y_max;
                node.numberLeaves = node.left.numberLeaves;
            }

            else{
                node.x_min = Math.min(node.left.x_min, node.right.x_min);
                node.x_max = Math.max(node.left.x_max, node.right.x_max);
                node.y_min = Math.min(node.left.y_min, node.right.y_min);
                node.y_max = Math.max(node.left.y_max, node.right.y_max);
                node.numberLeaves = node.left.numberLeaves + node.right.numberLeaves;
            }
            node.level = level;
            return node;
        }
        else{
           ArrayList<Integer> ls = new ArrayList<Integer>();
            for(int i = 0; i<y_coord.size(); i++){
                ls.add(y_coord.get(i));
            }
            ls = sortfn(ls);
            ArrayList<Integer> x1 = new ArrayList<Integer>();
            ArrayList<Integer> y1 = new ArrayList<Integer>();
            ArrayList<Integer> x2 = new ArrayList<Integer>();
            ArrayList<Integer> y2 = new ArrayList<Integer>();
            TreeNode node = new TreeNode();
            for(int i=0;i<ls.size()/2;i++){
                int j = ls.get(i);
                int k = 0;
                while(y_coord.get(k) != j){
                    k++;
                }
                x1.add(x_coord.get(k));
                y1.add(ls.get(i));
                x_coord.remove(k);
                y_coord.remove(k);
            }
            for(int i=ls.size()/2;i<ls.size();i++){
                int j = ls.get(i);
                int k = 0;
                while(y_coord.get(k) != j){
                    k++;
                }
                x2.add(x_coord.get(k));
                y2.add(ls.get(i));
                x_coord.remove(k);
                y_coord.remove(k);
            }
            node.left = build(x1,y1,level+1);
            if(node.left != null) node.left.parent = node;
            node.right = build(x2,y2,level+1);
            if(node.right != null) node.right.parent = node;
            node.level = level;
            if(node.left == null){
                node.x_min = node.right.x_min;
                node.y_min = node.right.y_min;
                node.x_max = node.right.x_max;
                node.y_max = node.right.y_max;
                node.numberLeaves = node.right.numberLeaves;
            }

            else if(node.right == null){
                node.x_min = node.left.x_min;
                node.x_max = node.left.x_max;
                node.y_min = node.left.y_min;
                node.y_max = node.left.y_max;
                node.numberLeaves = node.left.numberLeaves;
            }

            else{
                node.x_min = Math.min(node.left.x_min, node.right.x_min);
                node.x_max = Math.max(node.left.x_max, node.right.x_max);
                node.y_min = Math.min(node.left.y_min, node.right.y_min);
                node.y_max = Math.max(node.left.y_max, node.right.y_max);
                node.numberLeaves = node.left.numberLeaves + node.right.numberLeaves;
            }
            return node;
        }
    }

    public static Integer query(Integer x_start, Integer y_start, Integer x_end, Integer y_end, TreeNode node){
        int num1 = 0;
        
        if(node == null){
            return 0;
        }

        if(node.x_min >= x_start && node.x_max <= x_end && node.y_min >= y_start && node.y_max <= y_end){
            num1 += node.numberLeaves;
            return num1;
        }
        
        if((node.level % 2 == 0) && (node.x_max < x_start || node.x_min > x_end)){
            return num1;
        }
        if((node.level % 2 == 1) && (node.y_max < y_start || node.y_min > y_end)){
            return num1;
        }
        num1 += query(x_start, y_start, x_end, y_end, node.left);
        num1 += query(x_start, y_start, x_end, y_end, node.right);

        return num1;
    }

    public static ArrayList<Integer> sortfn(ArrayList<Integer> l){
        if(l.size() == 1){
            return l;
        }
        ArrayList<Integer> l1 = new ArrayList<Integer>();
        ArrayList<Integer> l2 = new ArrayList<Integer>();

        for(int i = 0; i < l.size()/2 ; i++){
            l1.add(l.get(i));
        }
        for(int i=l.size()/2;i<l.size();i++){
            l2.add(l.get(i));
        }

        l1 = sortfn(l1);
        l2 = sortfn(l2);

        ArrayList<Integer> list = new ArrayList<Integer>();

        int i=0,j=0;
        while(i<l1.size() && j<l2.size()){
            if(l1.get(i) < l2.get(j)){
                list.add(l1.get(i));
                i++;
            }
            else{
                list.add(l2.get(j));
                j++;
            }
        }
        while(i != l1.size()){
            list.add(l1.get(i));
            i++;
        }
        while(j != l2.size()){
            list.add(l2.get(j));
            j++;
        }

        return list;
    }
}

class TreeNode{

    public int x_min;
    public int y_min;
    public int x_max;
    public int y_max;
    public TreeNode left;
    public TreeNode right;
    public TreeNode parent;
    public int level;
    public boolean isLeaf = false;
    public int numberLeaves;

}