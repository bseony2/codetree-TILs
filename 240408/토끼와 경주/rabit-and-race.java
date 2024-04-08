import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

	static int N, M, Q;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static PriorityQueue<Rabbit> rabbitQueue = new PriorityQueue<Rabbit>();
	static int dr[] = new int[] {-1, 0, 1, 0};
	static int dc[] = new int[] {0, 1, 0, -1};
	static Rabbit[] rabbits = new Rabbit[10000001];
	public static void main(String[] args) throws IOException{
		
		Q = Integer.parseInt(br.readLine());
		
		while(Q--> 0) {
			StringTokenizer st = new StringTokenizer(br.readLine(), " ");
			
			doSomething(st);
		}
	}
	
	static void doSomething(StringTokenizer st) {
		int order = Integer.parseInt(st.nextToken());
		
		switch(order) {
			case 100:
				init(st);
				break;
			case 200:
				race(st);
				break;
			case 300:
				modify(st);
				break;
			case 400:
				printMaxScore();
				break;
		}
	}
	
	static void init(StringTokenizer st) {
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		int P = Integer.parseInt(st.nextToken());
		
		while(P-->0) {
			int id = Integer.parseInt(st.nextToken());
			int dist = Integer.parseInt(st.nextToken());
			Rabbit rabbit = new Rabbit(id, dist);
			rabbits[id] = rabbit;
			rabbitQueue.add(rabbit);
			
		}
	}
	
	static void race(StringTokenizer st) {
		int K = Integer.parseInt(st.nextToken());
		int S = Integer.parseInt(st.nextToken());
		
		while(K--> 0) {
			Rabbit rabbit = rabbitQueue.poll();
			rabbit.isJump = true;
			rabbit.count += 1;
			move(rabbit);
			int plus = rabbit.r + rabbit.c + 2;
			for(Rabbit temp : rabbitQueue) {
				temp.score += plus;
			}
			rabbitQueue.add(rabbit);
		}
		
		PriorityQueue<Rabbit> tempQueue = new PriorityQueue<Rabbit>(new Comparator<Rabbit>(){

			@Override
			public int compare(Rabbit o1, Rabbit o2) {
				if(o1.r + o1.c != o2.r + o2.c) return o2.r + o2.c - o1.r - o1.c;
				if(o1.r != o2.r) return o2.r - o1.r;
				if(o1.c != o2.c) return o2.c - o1.c;
				return o2.id - o1.id;
			}
			
		});
		
		for(Rabbit temp: rabbitQueue) {
			if(temp.isJump) {
				tempQueue.add(temp);
				temp.isJump = false;
			}
		}
		
		Rabbit temp = tempQueue.peek();
		temp.score += S;
	}
	
	static void move(Rabbit rabbit) {
		PriorityQueue<RabbitPoint> pointQueue = new PriorityQueue<RabbitPoint>();
		
		pointQueue.add(move(rabbit, 0));
		pointQueue.add(move(rabbit, 1));
		pointQueue.add(move(rabbit, 2));
		pointQueue.add(move(rabbit, 3));
		
		RabbitPoint next = pointQueue.peek();
		rabbit.r = next.r;
		rabbit.c = next.c;
	}
	
	static RabbitPoint move(Rabbit rabbit, int d) {
		int r = rabbit.r;
		int c = rabbit.c;
		int dist = rabbit.dist;
		if(d == 0 || d == 2) {
			dist %= N*2 - 2;
		}
		else {
			dist %= M*2 -2;
		}
		
		while(dist != 0) {
			int limit = getLimit(d, r, c);
			
			if(limit >= dist) {
				r += dr[d] * dist;
				c += dc[d] * dist;
				dist = 0;
			}
			else {
				r += dr[d] * limit;
				c += dc[d] * limit;
				d = (d+2) % 4;
				dist -= limit;
			}
		}
		
		return new RabbitPoint(r, c);
	}
	
	private static int getLimit(int d, int r, int c) {
		int limit;
		
		if(d == 0) {
			limit = r;
		}
		else if(d == 1) {
			limit = M - c-1;
		}
		else if(d == 2) {
			limit = N - r - 1;
		}
		else {
			limit = c;
		}
		return limit;
	}
	
	private static void modify(StringTokenizer st) {
		int id = Integer.parseInt(st.nextToken());
		int L = Integer.parseInt(st.nextToken());
		rabbits[id].dist *= L;
	}
	
	private static void printMaxScore() {
		long max = Long.MIN_VALUE;
		for(Rabbit rabbit : rabbitQueue) {
			max = Math.max(rabbit.score, max);
		}
		
		System.out.println(max);
	}
	
	static boolean isValidPoint(int r, int c) {
		return 0 <= r && 0 <= c && r < N && c < M;
	}

	static class RabbitPoint implements Comparable<RabbitPoint>{
		int r, c;
		public RabbitPoint(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		public int compareTo(RabbitPoint point) {
			if(this.r + this.c != point.r + point.c) return point.r + point.c - this.r - this.c;
			if(this.r != point.r) return point.r - this.r;
			return point.c - this.r;
		}
	}
	static class Rabbit implements Comparable<Rabbit>{
		
		int id, dist, count, r, c;
		long score;
		boolean isJump;
		public Rabbit(int id, int dist) {
			this.id = id;
			this.dist = dist;
			this.count = 0;
			this.score = 0;
			this.r = 0;
			this.c = 0;
			this.isJump = false;
		}
		@Override
		public int compareTo(Rabbit rabbit) {
			if(this.count != rabbit.count) return this.count - rabbit.count;
			if(this.r + this.c != rabbit.r + rabbit.c) return this.r + this.c - rabbit.r - rabbit.c;
			if(this.r != rabbit.r) return this.r - rabbit.r;
			if(this.c != rabbit.c) return this.c - rabbit.c;
			return this.id - rabbit.id;
		}
	}
}