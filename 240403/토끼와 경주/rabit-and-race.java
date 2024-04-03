import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {

	static int Q;
	static int N;
	static int M;

	static BufferedReader br;
	static Queue<Rabbit> queue;
	static Rabbit[] rabbits;
	static int[] dr = new int[] {-1, 0, 1, 0};
	static int[] dc = new int[] {0, 1, 0, -1};

	public static void main(String args[]) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));
		queue = new PriorityQueue<Rabbit>();
		rabbits = new Rabbit[10000001];
		Q = Integer.parseInt(br.readLine());
		for (int i = 0; i < Q; i++) {
			getCommand(Arrays.stream(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray());
		}
	}

	private static void getCommand(int[] commands) {
		switch (commands[0]) {
			case 100:
				init(commands);
				break;
			case 200:
				move(commands);
				break;
			case 300:
				upgradeRabbit(commands);
				break;
			case 400:
				printScore();
				break;
		}
	}

	private static void init(int[] commands) {
		N = commands[1];
		M = commands[2];
		int cnt = commands[3];

		for (int i = 4; i < 4 + cnt * 2; i += 2) {
			int id = commands[i];
			int dis = commands[i + 1];
			Rabbit rabbit = new Rabbit(id, dis);
			queue.add(rabbit);
			rabbits[id] = rabbit;
		}
	}

	private static void move(int[] commands) {
		int K = commands[1];
		int S = commands[2];

		for (int i = 0; i < K; i++) {
			Rabbit rabbit = queue.poll();
			rabbit.jmpCnt += 1;
			rabbit.isMove = true;

			// int[] upMove = move(rabbit, 0);
			// int[] rightMove = move(rabbit, 1);
			// int[] downMove = move(rabbit, 2);
			// int[] leftMove = move(rabbit, 3);

			int[] nextPoint = comparePoint(comparePoint(move(rabbit, 0), move(rabbit, 1)),
				comparePoint(move(rabbit, 2), move(rabbit, 3)));

			rabbit.r = nextPoint[0];
			rabbit.c = nextPoint[1];

			int point = nextPoint[0] + nextPoint[1] + 2;

			for (Rabbit temp : queue) {
				temp.score = temp.score + point;
			}

			queue.add(rabbit);
		}

		PriorityQueue<Rabbit> tempQueue = new PriorityQueue<Rabbit>(new Comparator<Rabbit>() {
			@Override
			public int compare(Rabbit o1, Rabbit o2) {
				if (o1.r + o1.c != o2.r + o2.c)
					return (o2.r + o2.c) - (o1.r + o1.c);
				if (o1.r != o2.r)
					return o2.r - o1.r;
				if (o1.c != o2.c)
					return o2.c - o1.c;
				return o2.id - o1.id;
			}
		});
		for (Rabbit temp : queue) {
			if (temp.isMove) {
				tempQueue.add(temp);
			}
		}
		Rabbit rabbit = tempQueue.peek();
		rabbit.score += S;

		for (Rabbit temp : queue) {
			temp.isMove = false;
		}
	}

	private static int[] move(Rabbit rabbit, int d) {
		int distance = rabbit.dis;
		if (d == 0 || d == 2) {
			distance = distance % (N * 2 - 2);
		} else {
			distance = distance % (M * 2 - 2);
		}

		int r = rabbit.r;
		int c = rabbit.c;

		for (int i = 0; i < distance; i++) {
			int nr = r + dr[d];
			int nc = c + dc[d];

			if (!isValidPoint(nr, nc)) {
				d = (d + 2) % 4;
				nr = r + dr[d];
				nc = c + dc[d];
			}
			r = nr;
			c = nc;
		}

		return new int[] {r, c};
	}

	private static void upgradeRabbit(int[] commands) {
		int id = commands[1];
		int l = commands[2];

		Rabbit rabbit = rabbits[id];
		rabbit.dis = rabbit.dis * l;
	}

	private static void printScore() {
		int max = Integer.MIN_VALUE;
		for (Rabbit rabbit : queue) {
			max = Math.max(max, rabbit.getScore());
		}
		System.out.println(max);
	}

	private static boolean isValidPoint(int nr, int nc) {
		return 0 <= nr && 0 <= nc && nr < N && nc < M;
	}

	private static int[] comparePoint(int[] point1, int[] point2) {
		if ((point1[0] + point1[1]) != (point2[0] + point2[1]))
			return (point1[0] + point1[1]) > (point2[0] + point2[1]) ? point1 : point2;
		if (point1[0] != point2[0])
			return point1[0] > point2[0] ? point1 : point2;
		return point1[1] > point2[1] ? point1 : point2;
	}
}

class Rabbit implements Comparable<Rabbit> {
	int id;
	int jmpCnt;
	int r;
	int c;
	int dis;
	int score;
	boolean isMove;

	public Rabbit(int id, int dis) {
		this.id = id;
		this.jmpCnt = 0;
		this.r = 0;
		this.c = 0;
		this.dis = dis;
		this.score = 0;
		this.isMove = false;
	}

	public int getScore() {
		return this.score;
	}

	@Override
	public int compareTo(Rabbit rabbit) {
		if (this.jmpCnt != rabbit.jmpCnt)
			return this.jmpCnt - rabbit.jmpCnt;
		if (this.r + this.c != rabbit.r + rabbit.c)
			return (this.r + this.c) - (rabbit.r + rabbit.c);
		if (this.r != rabbit.r)
			return this.r - rabbit.r;
		if (this.c != rabbit.c)
			return this.c - rabbit.c;
		return this.id - rabbit.id;
	}
}