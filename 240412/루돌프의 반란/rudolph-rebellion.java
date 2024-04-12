import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class Main {
	static int N,M, P, C, D;
	static int[][] map;
	static Santa[] santas = new Santa[31];
	static int[] dr = new int[] {-1, 0, 1, 0, -1, -1, 1, 1};
	static int[] dc = new int[] {0, 1, 0, -1, -1, 1, 1, -1};
	static int deerR, deerC;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer st;
	public static void main(String[] args) throws IOException {
		init();

		while(M --> 0) {
			Santa target = findSanta();
			
			int d = deerMove(target);
			
			if(isCrush()) { // 루돌프가 충돌
				crush(d, true);
			}
			
			santaMove();
			
			if(isOver()) {
				break;
			}
			
	
			afterTurn();
		}
		
		printScore();
	}
	
	private static void crush(int d, boolean isDeer) {
		Santa santa = santas[map[deerR][deerC]];
		santa.panicTurn = 1;
		int power = 0;
		if(isDeer) {
			power = C;
		} else {
			power = D;
		}
		santa.score += power;
		
		interact(santa, d, power);
	}

	private static void interact(Santa santa, int d, int power) {
		map[santa.r][santa.c] = 0;
		int nr = santa.r + dr[d]*power;
		int nc = santa.c + dc[d]*power;
		
		if(!isValidPoint(nr, nc)) {
			santa.out = true;
			return;
		}
		
		if(map[nr][nc] != 0) {
			interact(santas[map[nr][nc]], d, 1);
		}
		santa.r = nr;
		santa.c = nc;
		map[nr][nc] = santa.id;
	}

	private static void santaMove() {
		for(Santa santa: santas) {
			if(santa == null || santa.out || santa.isPanic()) continue;
			int d = moveToDeer(santa);
			if(isCrush()) {
				d = (d+2) % 4;
				crush(d, false);
			}
		}
		
	}
	
	private static int moveToDeer(Santa santa) {
		int baseDis = getDistance(santa.r, santa.c, deerR, deerC);
		
		int d = -1;
		int r = santa.r;
		int c = santa.c;
		
		for(int i=0; i<4; i++) {
			int nr = santa.r + dr[i];
			int nc = santa.c + dc[i];
			
			if(!isValidPoint(nr, nc) || map[nr][nc] != 0) continue;
			
			int dis = getDistance(nr, nc, deerR, deerC);
			if(dis < baseDis) {
				r = nr;
				c = nc;
				d = i;
				baseDis = dis;
			}
		}
		map[santa.r][santa.c] = 0;
		santa.r = r;
		santa.c = c;
		map[r][c] = santa.id;
		
		return d;
	}

	private static int deerMove(Santa target) {
		int baseDis = Integer.MAX_VALUE;
		int r=0, c=0;
		int d = 8;
		for(int i=0; i<8; i++) {
			int nr = deerR + dr[i];
			int nc = deerC + dc[i];
			if(!isValidPoint(nr, nc)) continue;
			int dis = getDistance(nr, nc, target.r, target.c);
			
			if(dis < baseDis) {
				r = nr;
				c = nc;
				baseDis = dis;
				d = i;
			}
		}
		deerR = r;
		deerC = c;
		
		return d;
	}



	private static Santa findSanta() {
		int minDis = Integer.MAX_VALUE;
		Santa selectedSanta = null;
		for(Santa santa : santas) {
			if(santa == null || santa.out) continue;
			
			int dis = getDistance(deerR, deerC, santa.r, santa.c);
			if(dis > minDis) continue;
			
			if(dis < minDis) {
				minDis = dis;
				selectedSanta = santa;
			}
			
			if(dis == minDis && santa.r > selectedSanta.r) {
				selectedSanta = santa;
			}
			
			if(dis == minDis && santa.r == selectedSanta.r && santa.c > selectedSanta.c) {
				selectedSanta = santa;
			}
		}
		return selectedSanta;
	}

	private static void init() throws IOException {
		st = new StringTokenizer(br.readLine(), " ");
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		
		st = new StringTokenizer(br.readLine(), " ");
		deerR = Integer.parseInt(st.nextToken())-1;
		deerC = Integer.parseInt(st.nextToken())-1;
		
		for(int i=0; i<P; i++) {
			st = new StringTokenizer(br.readLine(), " ");
			int id = Integer.parseInt(st.nextToken());
			int r = Integer.parseInt(st.nextToken())-1;
			int c = Integer.parseInt(st.nextToken())-1;
			
			Santa santa = new Santa(id, r, c);
			santas[id] = santa;
			map[r][c] = id;
		}
		
	}
	
	private static boolean isCrush() {
		return map[deerR][deerC] != 0;
	}
	
	private static void afterTurn() {
		for(Santa santa: santas) {
			if(santa == null || santa.out) continue;
			
			santa.panicTurn -= 1;
			santa.score += 1;
		}
		
	}
	
	private static boolean isOver() {
		for(Santa santa: santas) {
			if(santa == null) continue;
			
			if(!santa.out) return false;
		}
		
		return true;
	}
	
	private static void printScore() {
		ArrayList<String> result = new ArrayList<String>();
		
		for(Santa santa: santas) {
			if(santa == null) continue;
			result.add(String.valueOf(santa.score));
		}
		
		System.out.println(String.join(" ", result));
	}
	
	static int getDistance(int r1, int c1, int r2, int c2) {
		return((int)Math.pow(r1-r2, 2) + (int)Math.pow(c1-c2, 2));
	}

	static boolean isValidPoint(int r, int c) {
		return 0<=r && 0<=c && r<N && c<N;
	}
	static class Santa{
		int id,r, c, score, panicTurn;
		boolean out;
		public Santa(int id, int r, int c) {
			this.id = id;
			this.r= r;
			this.c = c;
			this.panicTurn = -1;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		
		public boolean isPanic() {
			return panicTurn >= 0;
		}
		
	}
	
	static class Point{
		int r, c;

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

		public Point(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}
		
	}
}