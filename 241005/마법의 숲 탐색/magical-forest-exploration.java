import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	static int R, C, K;
	static int[][] map;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static Queue<Integer> q;
	static int[] dr = new int[] {-1, 0, 1, 0, 0};
	static int[] dc = new int[] {0, 1, 0, -1, 0};

	public static void main(String[] args) throws IOException {
		init();

		int result = exe();

		System.out.println(result);
	}

	static int exe() {
		int result = 0;

		for(int k=1; k<=K; k++) {
			int c = q.poll();
			int d = q.poll();

			Area area = new Area(k, c, d);

			move(area);

			if(area.r < 1) { // 몸 일부가 숲을 벗어난 상태
				initMap(); // 맵 초기화
				continue;
			}

			markArea(area);

			result += getScore(area);
		}

		return result;
	}

	static int getScore(Area area) { // BFS
		int result = area.r;
		boolean[][] isVisited = new boolean[R][C];
		isVisited[area.r][area.c] = true;

		Queue<Integer> bq = new LinkedList<>();
		bq.offer(area.r);
		bq.offer(area.c);

		while (!bq.isEmpty()) {
			int r = bq.poll();
			int c = bq.poll();

			for (int i = 0; i < 4; i++) {
				int nr = r + dr[i];
				int nc = c + dc[i];
				if(!isValidPoint(nr, nc) || map[nr][nc] == 0 || isVisited[nr][nc]) { // 맵 밖의 영역 이거나 골렘 영역이 아니거나 방문한 곳
					continue;
				}

				if(Math.abs(map[r][c]) != Math.abs(map[nr][nc]) && map[r][c] > 0) { // 다른 골렘의 영역이고, 현재 출구도 아닌경우
					continue;
				}
				isVisited[nr][nc] = true;
				bq.offer(nr);
				bq.offer(nc);

				result = Math.max(result, nr);

			}
		}

		return result + 1;
	}

	static void markArea(Area area) {
		for (int i = 0; i < 5; i++) {
			int nr = area.r + dr[i];
			int nc = area.c + dc[i];
			map[nr][nc] = area.id;
			if (area.d == i) {
				map[nr][nc] *= -1;
			}
		}
	}

	static void move(Area area) {
		while (true) {
			if(isValidArea(area.r + 1, area.c)) {
				area.r += 1;
				continue;
			}

			if (
				isValidArea(area.r, area.c - 1)
				&& isValidArea(area.r + 1, area.c - 1)
			) {
				area.r += 1;
				area.c -= 1;
				area.d = (area.d + 3) % 4;
				continue;
			}

			if (
				isValidArea(area.r, area.c + 1)
				&& isValidArea(area.r + 1, area.c + 1)
			) {
				area.r += 1;
				area.c += 1;
				area.d = (area.d + 1) % 4;
				continue;
			}
			return;
		}
	}

	static void initMap() {
		map = new int[R][C];
	}

	static void init() throws IOException {
		StringTokenizer st = new StringTokenizer(br.readLine());
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		q = new LinkedList<>();

		initMap();

		for(int i=0; i<K; i++) {
			st = new StringTokenizer(br.readLine());
			q.offer(Integer.parseInt(st.nextToken())-1);
			q.offer(Integer.parseInt(st.nextToken()));
		}
	}

	static boolean isValidPoint(int r, int c) {
		return 0<=r && r<R && 0<=c && c<C;
	}

	static boolean isValidArea(int r, int c) {
		for(int i=0; i<5; i++) {
			int nr = r + dr[i];
			int nc = c + dc[i];
			if(nr < 0) {
				continue;
			}
			if(!isValidPoint(nr, nc) || map[nr][nc] != 0) {
				return false;
			}
		}

		return true;
	}

	static class Area {
		int id, r, c;
		int d;

		public Area(int id, int c, int d) {
			this.id = id;
			this.r = -2;
			this.c = c;
			this.d = d;
		}
	}
}