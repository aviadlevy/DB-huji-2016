import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

public class JDBC {
	private static String _userName = null;
	private static BufferedWriter _outFile = null;

	public static void main(String[] args) throws IOException {
		_outFile = new BufferedWriter(new FileWriter("ex6Codes", true));
		CheckMySavings che = null;

		try {
			System.out.println("JDBC PreSubmit start");
			_userName = System.getProperty("user.name");

			che = new CheckMySavings();
			che.init(_userName);
			//31.12.2012
			cheakFunctIntArray(che, 3, new Date(112, 0, 31), 175, "jdbct1");
			//1.1.2021
			cheakFunctIntArray(che, 3, new Date(121, 0, 1), 1727.72117672, "jdbct2");
			//31.1.2018
			cheakFunctIntArray(che, 3, new Date(118, 0, 1), 620.20025, "jdbct3");

		} catch (Exception e) {
			System.out.println("got exception in main JDBC");
			_outFile.write("\tFT000004\n");
			e.printStackTrace(System.out);
		} finally {
			try {
				if (che != null) che.close();
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		_outFile.close();
	}

	private static void cheakFunctIntArray(CheckMySavings che, int id, Date d, double saving,
										   String errorCode) throws Exception {

		try {
			
			double temp = che.checkMySavings(id, d);
			if (Math.round(temp) != Math.round(saving)) {
				
				System.out.println("ERROR in JDBC got: "+ temp + " writing: " +errorCode);
				_outFile.write("\t" + errorCode + "\n");

			}

		} catch (Exception e) {
			_outFile.write("\t" + errorCode + "\n");
			e.printStackTrace(System.out);

		}

	}
}
