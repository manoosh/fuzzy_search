import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Trie {

	static class TrieNode {
		static int counter = 0;
		int id;
		Map<Character, TrieNode> children = new TreeMap<Character, TrieNode>();
		boolean leaf;
		TrieNode parent;
		char fromParent;
		float contxt;
		public Map<TrieNode, Integer> activeNodes = new HashMap<Trie.TrieNode, Integer>();
		Map<TrieNode, Integer> piactiveNodes = new HashMap<Trie.TrieNode, Integer>();

		public TrieNode(TrieNode p, char x) {
			this.id = counter;
			counter++;
			parent = p;

			fromParent = x;// holding current node character
		}

		public TrieNode(TrieNode p, char x, float contxt) {
			this.id = counter;
			counter++;
			parent = p;
			this.contxt = contxt;
			fromParent = x;// holding current node character
		}

		Map<TrieNode, Integer> buildActiveNodes(int depth) {

			Map<TrieNode, Integer> parentActiveNodes = parent.activeNodes;
			// deletion
			// add all p active node to this, with distance +1 if possible
			for (TrieNode n : parentActiveNodes.keySet()) {
				int l = parentActiveNodes.get(n);
				if (n.id == parent.id || l < depth)
					activeNodes.put(n, parentActiveNodes.get(n) + 1);
				if (n.fromParent == fromParent) {
					getDescendant(activeNodes, depth, l);
				}
				for (TrieNode c : n.children.values()) {
					if (c == this)
						continue;
					// insertion
					if (c.fromParent == fromParent) {// we have a match
						c.getDescendant(activeNodes, depth, l);
					} else if (l <= depth) {

						int m = min(l + 1, activeNodes.get(c));
						if (m <= depth)
							activeNodes.put(c, m);
					}

				}

			}

			// add myself & my Descendant
			activeNodes.put(this, 0);
			return activeNodes;
		}

		private int min(int i, Object v) {
			if (v == null)
				return i;
			int vv = (Integer) v;
			if (vv > i)
				return i;
			return vv;
		}

		private Map<TrieNode, Integer> getDescendant(Map<TrieNode, Integer> descendents, int depth, int k) {
			class pair {
				public TrieNode n;
				public int depth;

				public pair(TrieNode n, int depth) {
					this.n = n;
					this.depth = depth;
				}
			}

			ArrayList<pair> queue = new ArrayList<pair>();
			queue.add(new pair(this, k));
			if (k > depth)
				return descendents;
			descendents.put(this, k);
			while (!queue.isEmpty()) {
				// get the first node of the queue
				pair p = queue.remove(0);
				// add children to the queue
				if (p.depth < depth) {
					for (TrieNode c : p.n.children.values()) {
						Object v = descendents.get(c);
						int vv = min(p.depth + 1, v);
						if (vv <= depth) {
							descendents.put(c, vv);
							queue.add(new pair(c, vv));
						}
					}
				}
			}
			return descendents;
		}

		@Override
		public String toString() {

			return "TrieNode [id=" + id + "]";
		}
	}

	// builds all activenodes
	public void buildActiveNodes(TrieNode root, int depth) {
		root(root, root.activeNodes, 0, depth);
		children(root, depth);
	}

	private static void children(TrieNode root, int depth) {
		for (char c : root.children.keySet()) {
			root.children.get(c).buildActiveNodes(depth);
			children(root.children.get(c), depth);
		}
	}

	static void root(TrieNode node, Map<TrieNode, Integer> activeNodes, int depth, int limit) {
		if (depth > limit)
			return;

		activeNodes.put(node, depth);

		for (char c : node.children.keySet()) {
			root(node.children.get(c), activeNodes, depth + 1, limit);
		}

	}

	private static Map<TrieNode, Integer> IncrementalBuildActiveNode(char ch, Map<TrieNode, Integer> cparentActiveNodes,
			int depth) {
		Map<TrieNode, Integer> curactiveNodes = new HashMap<Trie.TrieNode, Integer>();
		// deletion
		// add all p active node to this, with distance +1 if possible
		for (TrieNode n : cparentActiveNodes.keySet()) {
			int l = cparentActiveNodes.get(n);

			if (l < depth)
				curactiveNodes.put(n, cparentActiveNodes.get(n) + 1);

			for (TrieNode c : n.children.values()) {

				// insertion
				if (c.fromParent == ch) {// we have a match
					c.getDescendant(curactiveNodes, depth, l);
				} else if (l <= depth) {

					int m = c.min(l + 1, curactiveNodes.get(c));
					if (m <= depth)
						curactiveNodes.put(c, m);
				}

			}

		}

		return curactiveNodes;

	}

	// Entry point for matching
	public static Set<String> GetSimilarStrings(String s, int k) {
		Set<String> similarWords = new HashSet<String>();
		for (int tau = 0; tau <= 10; tau++) {

			similarWords.addAll(matchString(trie, s, tau));
			if (similarWords.size() >= k)
				break;
		}
		return similarWords;
	}

	private static List<String> matchString(TrieNode root, String s, int depth) {
		root(root, root.activeNodes, 0, depth);

		TrieNode v = root;
		Map<TrieNode, Integer> activenodes = v.activeNodes;

		TrieNode next = v;
		int indx = 0;
		boolean b = false;
		TrieNode k;
		for (char ch : s.toCharArray()) {
			next = v.children.get(ch);
			indx++;
			if (next == null) {
				b = true;
				break;
			}

			k = next;
			List<TrieNode> myparents = new Vector<TrieNode>();
			List<TrieNode> invertedList = new Vector<TrieNode>();
			while (k.parent != v) {
				myparents.add(k.parent);
				k = k.parent;
			}

			for (int i = myparents.size() - 1; i >= 0; i--) {
				invertedList.add(myparents.get(i));
			}

			for (TrieNode y : invertedList) {

				// activenodes=y.buildActiveNodes(depth);
				activenodes = IncrementalBuildActiveNode(y.fromParent, activenodes, depth);
			}
			// activenodes=next.buildActiveNodes(depth);
			activenodes = IncrementalBuildActiveNode(ch, activenodes, depth);
			v = next;

		}

		if (b == true) {

			for (int i = indx - 1; i < s.length(); i++) {

				char ch = s.charAt(i);

				activenodes = IncrementalBuildActiveNode(ch, activenodes, depth);
				

			}
		}
	

		String sim = null;
		
		List<String> similarWords = new Vector<String>();
		for (TrieNode t : activenodes.keySet()) {
			if (t.leaf == true) {

				sim = "";
				
				while (t.id != 0) {
					char c = t.fromParent;

					sim = c + sim;
					t = t.parent;

				}

			
				similarWords.add(sim);
				
			}
		}

		return similarWords;
	}

	private static TrieNode insertString(TrieNode root, String s) {

		TrieNode v = root;

		TrieNode next = v;
		for (char ch : s.toCharArray()) {
			next = v.children.get(ch);

			if (next == null)
				v.children.put(ch, next = new TrieNode(v, ch));

			v = next;
		}

		v.leaf = true;
		return v;
	}

	public static void Init(String fileName) {
		trie = new TrieNode(null, '\0', 0);
		try {
			File file = new File(fileName);
			FileInputStream fIn = new FileInputStream(file);
			BufferedReader in = new BufferedReader(new InputStreamReader(fIn));

			while (true) {
				String line = in.readLine();
				if (line == null || line.equals(""))
					break;

				insertString(trie, line);

			}
		} catch (Exception e) {

			trie = null;
		}

	}

	static TrieNode trie;

}
