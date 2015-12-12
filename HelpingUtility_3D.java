import java.util.*;
import java.io.*;


public class HelpingUtility_3D {
	
	private Random rand = null;

	/** list of input parameters */
	private Vector allParamSet = null;

	
	public HelpingUtility_3D() {
		rand = new Random(12345);
		allParamSet = new Vector();
	}

	
	public void setRandSeed(int seed) {
		rand = new Random(seed);
	}

	
	public void setRandomSeed() {
		rand = new Random();
	}

	
	public double getNextRandomNumber() {
		return rand.nextDouble();
	}

	
	public Vector getParamSet(int index) {
		if (index < allParamSet.size())
			return (Vector) allParamSet.get(index);

		return null;
	}

	
	public void readParameterFromFile(String parameterFile) {

		StringTokenizer theTokenizer;
		String parameter, value;
		Vector paramSet = new Vector();
		try {
			BufferedReader paramReader = new BufferedReader(new FileReader(
					parameterFile));

			String paramLine = paramReader.readLine();

			while (paramLine != null) {
				if (paramLine.equalsIgnoreCase("")) {
					allParamSet.add(paramSet);
					paramSet = new Vector();
					System.out.println("Added: ");

				} else {

					theTokenizer = new StringTokenizer(paramLine, " =;,:",
							false);
					if (theTokenizer.countTokens() >= 2) {
						parameter = theTokenizer.nextToken();
						value = theTokenizer.nextToken();
						paramSet.add(new ParamDTO_3D(parameter, value));
					}
				}
				paramLine = paramReader.readLine();
				System.out.println("ReadLine: " + paramLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public int getConvertedCycle(int cycle, double factor) {
		return (int) Math.floor((double) (cycle) * factor);
	}

}
