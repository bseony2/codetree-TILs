import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Main {
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
	static final int MAP_SIZE = 5;
	static int[][] map = new int[MAP_SIZE][MAP_SIZE];
	static int K, M;
	static Queue<Integer> pieces = new LinkedList<>();
	static int[] dr = new int[] {-1, 0, 1, 0};
	static int[] dc = new int[] {0, 1, 0, -1};
	
	public static void main(String[] args) throws IOException {
		init();
		
		for(int k=0; k<K; k++) {
			int result = exe();
			
			if(result == 0) {
				break;
			}
			bw.write(result + " ");
		}
		
		bw.flush();
	}
	
	static int exe() {
		int result = 0;
		
		Matrix nextMap = doFind(); // 첫 탐사
		
		// 새로운 맵으로 변경
		map = getNewMap(nextMap.r, nextMap.c, nextMap.d);
		
		while(true) {
			// 탐사 시작
			int score = calScore(map);
			
			result += score;
						
			// 탐사를 마친 후이니 유물조각을 채우기
			fill();
			
			// 유물 획득이 불가능하면 턴 종료
			if(score == 0) {
				break;
			}
			
		}
		
		return result;
	}
	
	static void fill() {
		for(int i=0; i<MAP_SIZE; i++) {
			for(int j=0; j<MAP_SIZE; j++) {
				int r = 4-j;
				int c = i;
				
				if(map[r][c] == 0) {
					map[r][c] = pieces.poll();
				}
			}
		}
	}
	
	static Matrix doFind() {
		PriorityQueue<Matrix> pq = new PriorityQueue<>();
		for(int i=1; i<4; i++) {
			for(int j=1; j<4; j++) {
				pq.offer(new Matrix(i, j, 1));
				pq.offer(new Matrix(i, j, 2));
				pq.offer(new Matrix(i, j, 3));
			}
		}
		
		return pq.poll();
	}
	
	static int[][] copyMap(int[][] target) {
		int[][] newMap = new int[target.length][target[0].length];
		for(int i=0; i<MAP_SIZE; i++) {
			System.arraycopy(target[i], 0, newMap[i], 0, MAP_SIZE);
		}
		return newMap;
	}
	
	static int[][] getNewMap(int r, int c, int d) {
		int[][] newMap = copyMap(map);
		
		for(int x=0; x<d; x++) {
			turn(newMap, r, c);
		}
		
		return newMap;
	}
	
	static void turn(int[][] target, int r, int c) {
		int[][] temp = copyMap(target);
		
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				target[r-1+i][c-1+j] = temp[r+1-j][c-1+i];
			}
		}
	}
	
	static int calScore(int[][] target) {
		int result = 0;
		
		boolean[][] isVisited = new boolean[MAP_SIZE][MAP_SIZE];
		for(int i=0; i<MAP_SIZE; i++) {
			for(int j=0; j<MAP_SIZE; j++) {
				if(!isVisited[i][j]) {
					result += bfs(target, isVisited, i, j, target[i][j]);
				}
			}
		}
		
		return result;
	}
	
	private static int bfs(int[][] target, boolean[][] isVisited, int i, int j, int flag) {
		int[] temp = new int[] {i, j};
		isVisited[i][j] = true;
		List<int[]> points = new ArrayList<int[]>();
		Queue<int[]> q = new LinkedList<int[]>();
		points.add(temp);
		q.offer(temp);
		
		while(!q.isEmpty()) {
			int[] current = q.poll();
			int r = current[0];
			int c = current[1];
			
			for(int d=0; d<4; d++) {
				int nr = r + dr[d];
				int nc = c + dc[d];
				
				if(!isValidPoint(nr, nc) || isVisited[nr][nc] || target[nr][nc] != flag) {
					continue;
				}
				
				isVisited[nr][nc] = true;
				int[] next = new int[] {nr, nc};
				points.add(next);
				q.offer(next);
			}
		}
		
		if(points.size() >= 3) {
			for(int[] current : points) {
				target[current[0]][current[1]] = 0;
			}
		} else {
			return 0;
		}
		
		return points.size();
	}
	
	static void init() throws IOException {
		StringTokenizer st = new StringTokenizer(br.readLine());
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		for(int i=0; i<MAP_SIZE; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<MAP_SIZE; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		pieces.addAll(Arrays.stream(br.readLine().split(" ")).map(Integer::parseInt).collect(Collectors.toList()));
	}
	
	static boolean isValidPoint(int r, int c) {
		return 0<=r && r<MAP_SIZE && 0<=c && c<MAP_SIZE;
	}
	
	static class Matrix implements Comparable<Matrix>{
		int[][] ownMap;
		int r, c, d, score;
		
		public Matrix(int r, int c, int d) {
			this.r = r;
			this.c = c;
			this.d = Math.abs(d);
			ownMap = getNewMap(r, c, d);
			
			this.score = calScore(ownMap);
		}

		@Override
		public int compareTo(Matrix matrix) {
			if(this.score != matrix.score) return matrix.score - this.score;		// 1. 가치가 가장 높은것
			if(this.d != matrix.d) return this.d - matrix.d;						// 2. 회전각도가 작은 방법
			if(this.c != matrix.c) return this.c - matrix.c;						// 3. 열이 가장 작은 경우
			return this.r - matrix.r;												// 4. 행이 가장 작은 경우
		}
	}

}