# fuzzy_search
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Trie {

class Node {
    HashMap<Character, Node> children;
    boolean end;
    public Node(boolean b){
        children = new HashMap<Character, Trie.Node>();
        end = false;
    }
}
private Node root;
public Trie(){
    root = new Node(false);
}
public static void main(String args[]){
	Trie tr = new Trie();
	try (BufferedReader br = new BufferedReader(new FileReader("test.txt")))
	{

		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);
			 String[] tokens = sCurrentLine.split(" ");
			 int i;
			 for(i=0;i< tokens.length;i++)
			tr.add(tokens[i]);
		}

	} catch (IOException e) {
		e.printStackTrace();
	} 

    
    System.out.println(tr.search("ingineeer"));
    System.out.println(tr.search("pla"));
}
private boolean search(String word) {
    Node mynode = root;
    int n = word.length();
    for(int i=0;i<n;i++){
        char ch = word.charAt(i);
        if(mynode.children.get(ch) == null){
            return false;
        }
        else {
            mynode = mynode.children.get(ch);
            if(i==n-1 && mynode.end == true){
                return true;
            }

        }
    }
    return false;
}
private void add(String word) {
    Node mynode = root;
    int n = word.length();
    for(int i=0;i<n;i++){
        char ch = word.charAt(i);
        if(mynode.children.containsKey(ch)){
            mynode = mynode.children.get(ch);
        }
        else {
        	mynode.children.put(ch, new Node(false));
            Node temp = mynode.children.get(ch);
            if(i == n-1){
                temp.end = true;
            }
            mynode = temp;
            System.out.println(ch + "      " + mynode.end);

        }
    }
}

}
