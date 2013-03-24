public class Test {
	static int a = 10; // intConst
	private static double d = 0.1;
	public double value = (((0.943 - 0.3239)));

	/*
	 * Ira is a monkey
	 */
	public static int myFunc1(String args) {

		while ((a + d)*d < 200) {
			if (d == 0.1) {
				a = a + 5;
			} else {
				a = a - 5;
			}
		}

		// create objects
		Integer t1 = new Integer(1);
		Integer t2 = new Integer(2).compareTo(new Integer(5));
		Integer t3 = new Integer(3).intValue();

		// call methods
		Test.myFunc2(5, 10);
		double b = myFunc2(3, 4);

		return a;
	}

	private static double myFunc2(double b, int c) {
		if (true) {
			b = b + (d - a);
		} else{
			
		}
		if (c < 2) {
			d = d * 2;
		} else {
			c = 0;
		}
		return d * b;
	}
}
