import java.io.*;
import java.util.*;
import java.lang.Comparable;

public class Main {

	static int N, M, K;
	static Tower[][] towerMap;
	static int[] dr = new int[] {0, 1, 0, -1, -1, -1, 1, 1};
	static int[] dc = new int[] {1, 0, -1, 0, 1, -1, 1, -1};
	static ArrayList<Tower> notRepair;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws IOException {
		init();
		
		Comparator<Tower> weak = new Comparator<Tower>() {

			@Override
			public int compare(Tower o1, Tower o2) {
				// TODO Auto-generated method stub
				if(o1.power != o2.power) return o1.power - o2.power;
				if(o1.time != o2.time) return o2.time - o1.time;
				if(o1.r+o1.c != o2.r+o2.c) return o2.r+o2.c-o1.r-o1.c;
				return o2.c - o1.c;
			}
			
		};
		
		Comparator<Tower> strong = new Comparator<Tower>() {
			public int compare(Tower o1, Tower o2) {
				if(o1.power != o2.power) return o2.power - o1.power;
				if(o1.time != o2.time) return o1.time - o2.time;
				if(o1.r+o1.c != o2.r+o2.c) return o1.r+o1.c-o2.r-o2.c;
				return o1.c - o2.c;
			}
		};
		int time = 1;
		while(K --> 0) {
			// 1. 공격자 선정
			Tower attacker = getTower(weak);
			attacker.time = time;
			attacker.power += N+M;
			
			// 2. 타겟 선정
			Tower target = getTower(strong);
			
			// 포탑이 하나만 남음
			if(attacker == target) break;
			
			// 3. 레이저 가능 확인
			Point[][] history = getAttackWay(attacker, target);
			
			notRepair = new ArrayList<>();
			notRepair.add(target);
			notRepair.add(attacker);
			// 3-1. 레이저 공격
			if(history != null) {
				laserAttack(attacker, target, history);
			}
			
			// 3-2. 포탄 공격
			if(history == null) {
				bombAttack(attacker, target);
			}
			
			// 4. 포탑 부서짐
			destroy();
			
			// 5. 포탑 정비
			repair();
			time += 1;
		}
		
		long result = Long.MIN_VALUE;
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<M; j++) {
				Tower tower = towerMap[i][j];
				if(tower == null) continue;
				
				result = Math.max(result, tower.power);
			}
		}
		
		System.out.println(result);
	}
	
	private static void repair() {
		for(int i=0; i<N; i++) {
			for(int j=0; j<M; j++) {
				Tower tower = towerMap[i][j];
				if(tower == null) continue;
				
				tower.power += 1;
			}
		}
		
		for(Tower tower : notRepair) {
			tower.power -= 1;
		}
		
	}

	private static void destroy() {
		for(int i=0; i<N; i++) {
			for(int j=0; j<M; j++) {
				Tower tower = towerMap[i][j];
				if(tower == null) continue;
				
				if(tower.power <= 0) towerMap[i][j] = null;
			}
		}
		
	}

	private static void bombAttack(Tower attacker, Tower target) {
		target.power -= attacker.power;
		int r = target.r;
		int c = target.c;
		for(int i=0; i<8; i++) {
			int nr = r + dr[i];
			int nc = c + dr[i];
			
			if(nr == N) nr = 0;
			if(nr == -1) nr = N-1;
			if(nc == M) nc = 0;
			if(nc == -1) nc = M-1;
			
			Tower next = towerMap[nr][nc];
			if(next == attacker || next == null) continue;
			
			next.power -= attacker.power/2;
			notRepair.add(next);
		}
	}

	private static void laserAttack(Tower attacker, Tower target, Point[][] history) {
		target.power -= attacker.power;
		Point point = new Point(target.r, target.c);
		
		while(true) {
			Point next = history[point.r][point.c];
			if(next.r == attacker.r && next.c == attacker.c) return;
			
			Tower tower = towerMap[next.r][next.c];
			notRepair.add(tower);
			tower.power -= attacker.power/2;
			point = next;
		}
		
	}

	static Point[][] getAttackWay(Tower attacker, Tower target) {
		boolean[][] isVisited = new boolean[N][M];
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(attacker.r, attacker.c));
		isVisited[attacker.r][attacker.c] = true;
		Point[][] history = new Point[N][M];
		
		while(!queue.isEmpty()) {
			Point curPoint = queue.poll();
			int r = curPoint.r;
			int c = curPoint.c;
			for(int i=0; i<4; i++) {
				int nr = r + dr[i];
				int nc = c + dc[i];
				
				if(nr == N) nr = 0;
				if(nr == -1) nr = N-1;
				if(nc == M) nc = 0;
				if(nc == -1) nc = M-1;
				
				if(towerMap[nr][nc] != null && !isVisited[nr][nc]) {
					history[nr][nc] = curPoint;
					isVisited[nr][nc] = true;
					if( target ==towerMap[nr][nc]) {
						return history;
					}
					queue.add(new Point(nr, nc));
				}
			}
		}
		return null;
	}
	
	static Tower getTower(Comparator<Tower> flag) {
		PriorityQueue<Tower> queue = new PriorityQueue<Tower>(flag);
		for(int i=0; i<N; i++) {
			for(int j=0; j<M; j++) {
				if(towerMap[i][j] != null) {
					queue.add(towerMap[i][j]);
				}
			}
		}
		
		return queue.poll();
	}

	
	static void init() throws IOException {
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		towerMap = new Tower[N][M];
		
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine(), " ");
			for(int j=0; j<M; j++) {
				int power = Integer.parseInt(st.nextToken());
				if(power == 0) continue;
				towerMap[i][j] = new Tower(power, i, j);
			}
		}
	}

	static class Tower{
		int power, time, r, c;
		boolean canUpgrade;
		
		public Tower(int power, int r, int c) {
			this.power = power;
			this.r = r;
			this.c = c;
			this.time = 0;
			this.canUpgrade = true;
		}
	}

	static class Point {
		int r, c;
		public Point(int r, int c) {
			this.r = r;
			this.c = c;
		}
		@Override
		public int hashCode() {
			return Objects.hash(c, r);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			return c == other.c && r == other.r;
		}
		
		
	}
}