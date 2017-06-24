package utils;


public class LinearModularEquationSolver {

	private static int[] table = {
			0, 1, 129, 86, 193, 103, 43, 147, 225, 200, 180, 187, 150,
			178, 202, 120, 241, 121, 100, 230, 90, 49, 222, 190, 75, 72,
			89, 238, 101, 195, 60, 199, 249, 148, 189, 235, 50, 132, 115,
			145, 45, 163, 153, 6, 111, 40, 95, 175, 166, 21, 36, 126, 173,
			97, 119, 243, 179, 248, 226, 61, 30, 59, 228, 102, 253, 87,
			74, 234, 223, 149, 246, 181, 25, 169, 66, 24, 186, 247, 201,
			244, 151, 165, 210, 96, 205, 127, 3, 65, 184, 26, 20, 209, 176,
			152, 216, 46, 83, 53, 139, 135, 18, 28, 63, 5, 215, 164, 177,
			245, 188, 224, 250, 44, 218, 116, 124, 38, 113, 134, 159, 54,
			15, 17, 158, 140, 114, 220, 51, 85, 255, 2, 172, 206, 37, 143,
			117, 99, 240, 242, 203, 98, 123, 144, 219, 133, 141, 39, 213,
			7, 33, 69, 12, 80, 93, 42, 252, 194, 229, 239, 122, 118, 204,
			174, 211, 41, 105, 81, 48, 237, 231, 73, 192, 254, 130, 52,
			161, 47, 92, 106, 13, 56, 10, 71, 233, 191, 88, 232, 76, 11,
			108, 34, 23, 183, 170, 4, 155, 29, 198, 227, 196, 31, 9, 78,
			14, 138, 160, 84, 131, 221, 236, 91, 82, 162, 217, 146, 251,
			104, 94, 212, 112, 142, 125, 207, 22, 68, 109, 8, 58, 197, 62,
			156, 19, 168, 185, 182, 67, 35, 208, 167, 27, 157, 136, 16, 137,
			55, 79, 107, 70, 77, 57, 32, 110, 214, 154, 64, 171, 128, 256
	};
	
	public static int[] solve(int[][] m, int mod) {
		stagger(m, mod);
		int[] ans = new int[m[0].length - 1]; 
		for (int i = m.length - 1; i >= 0; i--) {
			ans[i] = clear(m, ans, i, mod);
		}
		return ans;
	}
	
	public static int clear(int[][] m, int[] ans, int i, int mod) {
		int sum = 0;
		for (int h = i + 1; h < m[0].length - 1; h++) {
			sum += ans[h] * m[i][h];
		}
		return reduce(invMult(m[i][i], mod) * (m[i][m[0].length - 1] - sum), mod);
	}
	
	public static int invMult(int a, int mod) {
		if (mod == 257) {			
			return table[reduce(a, mod)];
		} else {
			for (int i = 1; i < mod; i++) {
				if (reduce(reduce(a, mod) * i, mod) == 1) {
					return i;
				}
			}
			throw new IllegalStateException();
		}
	}
	
	public static void stagger(int[][] m, int mod) {
		for (int i = 0; i < m.length - 1; i++) {
			int b = reduce(m[i][i], mod);
			for (int k = i + 1; k < m.length; k++) {
				int a = reduce(m[k][i], mod);
				for (int h = i; h < m[0].length; h++) {
					m[k][h] = reduce(m[k][h] * b - m[i][h] * a, mod);
				}
			}
		}
	}
	
	public static int reduce(int v, int mod) {
		if (v < mod && v >= 0) {
			return v;
		}
		if (v < 0) {
			return v + ((int) Math.ceil(-v / (double) mod)) * mod;
		} else {
			return v - (v / mod) * mod;
		}
	}
}
