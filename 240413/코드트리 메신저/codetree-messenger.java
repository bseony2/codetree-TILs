import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
	static int Q, N, D;
	static int[] parents, authority;
	static Node[] nodes;
	static boolean[] alarm;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer st;
	public static void main(String[] args) throws IOException {
		st = new StringTokenizer(br.readLine(), " ");
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		
		nodes = new Node[N+1];
		parents = new int[N+1];
		authority = new int[N+1];
		alarm = new boolean[N+1];
		Arrays.fill(alarm, true);
		
		while(Q --> 0) {
			st = new StringTokenizer(br.readLine(), " ");
			int order = Integer.parseInt(st.nextToken());
			
			switch (order) {
			case 100:
				init();
				break;
			case 200:
				changeAlarmSetting();
				break;
				
			case 300:
				changeAuthority();
				break;
				
			case 400:
				changeParent();
				break;
				
			case 500:
				printAmount();
				break;
			}
		}
	}
	private static void printAmount() {
		int id = Integer.parseInt(st.nextToken());
		Node node = nodes[id];
		
		int result = dfs(node.childrens[0], 1) + dfs(node.childrens[1], 1);
		
		System.out.println(result);
	}
	
	private static int dfs(Node node, int depth) {
		
		if(node == null || !node.alarm) return 0;
		int nodeId = node.id;
		
		Node child1 = node.childrens[0];
		Node child2 = node.childrens[1];
		
		int score = 0;
		if(child1 != null) {
			score += dfs(child1, depth + 1);
		}
		if(child2 != null) {
			score += dfs(child2, depth + 1);
		}
		
		if (depth <= node.power) {
			score += 1;
		}
		
		return score;
	}
	private static void changeParent() {
		int c1 = Integer.parseInt(st.nextToken());
		int c2 = Integer.parseInt(st.nextToken());
		
		Node target1 = nodes[c1];
		Node target2 = nodes[c2];
		
		Node parent1 = nodes[target1.parent];
		Node parent2 = nodes[target2.parent];
		
		if(parent1 != null) {
			if(parent1.childrens[0] == target1) {
				parent1.childrens[0] = target2;
			}
			else {
				parent1.childrens[1] = target2;
			}
		}
		
		if(parent2 != null) {
			if(parent2.childrens[0] == target2) {
				parent2.childrens[0] = target1;
			}
			else {
				parent2.childrens[1] = target1;
			}
		}
		target1.parent = parent2 == null ? 0 : parent2.id;
		target2.parent = parent1 == null ? 0 : parent1.id;
		
	}
	private static void changeAuthority() {
		int id = Integer.parseInt(st.nextToken());
		int power = Integer.parseInt(st.nextToken());
		
		Node node = nodes[id];
		node.power = power;
	}
	private static void changeAlarmSetting() {
		int id = Integer.parseInt(st.nextToken());
		Node node = nodes[id];
		node.alarm = !node.alarm;
	}
	
	private static void init() {
		for(int i=1; i<=N; i++) {
			parents[i] = Integer.parseInt(st.nextToken());
		}
		
		for(int i=1; i<=N; i++) {
			authority[i] = Integer.parseInt(st.nextToken());
		}
		
		for(int i=1; i<=N; i++) {
			int parent = parents[i];
			int auth = authority[i];
			
			Node node = new Node(i, parent, auth);
			nodes[i] = node;
		}
		
		for(Node node : nodes) {
			if(node == null || node.parent == 0) continue;
			
			Node parent = nodes[node.parent];
			
			if(parent.childrens[0] == null) {
				parent.childrens[0] = node;
			}
			else {
				parent.childrens[1] = node;
			}
		}
	}
	
	static class Node {
		int id,parent, power;
		boolean alarm;
		Node[] childrens;
		public Node(int id, int parent, int power) {
			super();
			this.id = id;
			this.parent = parent;
			this.power = power;
			this.alarm = true;
			childrens = new Node[2];
		}
		
		
	}

}