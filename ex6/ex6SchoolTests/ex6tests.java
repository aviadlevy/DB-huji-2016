import java.io.*;
import java.nio.channels.FileLockInterruptionException;
import java.sql.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Date;

/**
 * java -cp /usr/share/java/postgresql.jar:. ex6tests
 * javac ex6tests.java
 * run with: java -cp /usr/share/java/postgresql.jar:. ex6tests
 * 
 */
public class ex6tests {
	
		//***********************************************************TO FILL!!!!************************************************************
		
		//the directory that contains the submitted zip 
		private static String _submission_path = "TESTS DIRECTORY PATH" + "/submissions"; 
		
		//current date
		private static int _currentYear = 116; //current year minus 1900, for example: 2016 - 1900 = 116.
		private static int _currentMonth = 0; //current month minus 1, for example: jan = 0.
		private static int _currentDay = 12; //current day of month, for example 12.
		
		//**********************************************************************************************************************************
		
	
        private static String _userName = null;
        private static Runtime _rt = null;
        private static Connection _con = null;
        private static Statement _stmt = null;
        private static ResultSet _rs = null;
        private static BufferedWriter _outFile= null;
        private static String _zipFileNames = "CheckMySavings.java closeCustomer.sql doAction.sql dropFunctions.sql newCustomer.sql newSaving.sql triggerA.sql triggerB.sql triggerC.sql triggerD.sql";
		private static String[] _functionNames = {"closeCustomer", "doAction", "newCustomer", "newSaving"};               
        private static String _current_check = _submission_path + "/current";
		private static boolean _dropExist = true;


        /**
         * runs the tests, then run JDBC.java         
         * @throws IOException
         */
        public static void main(String[] args) throws IOException {                
                File dir = new File(_submission_path);
                File[] submissions = dir.listFiles();
                _outFile = new BufferedWriter(new FileWriter("ex6Codes",true));
                for (int i=0; i < submissions.length; i++){
                        try {
                                System.out.println("------------------ PL-pgSQL PreSubmit start on: " + submissions[i].getName()+"----------------");
                                String fileName = submissions[i].getName();
                                _outFile.write(fileName.substring(0,fileName.indexOf("_"))+":\n");
                                InitMain(_submission_path +"/"+ submissions[i].getName());

                                Q1test();
                                Q2test();
                                Q3test();
                                Q4test();
                                triggerAtest();
                                triggerBtest();
                                triggerCtest();
                                //triggerDtest();
                                JdbcTest();
								if (_dropExist)
								{
									dropTest();
								}
                                endMain();

                                _outFile.write("\n");
                                System.out.println("test ended");
								
                        } catch (FileLockInterruptionException e) {
                        	System.out.println("bad zip struct.");
                        } catch (Exception e) {
                                System.out.println("got exception in main");
                                _outFile.write("\tFT000001\n");
                                e.printStackTrace();
                        }
                      finally {
                                try{
                                        if(_stmt != null) _stmt.close();
                                        if(_con != null) _con.close();
                                        if(_rs != null) _rs.close();
                                } catch (Exception e) {
                                        System.out.println("got exception in close");
                                        _outFile.write("\tFT000001\n");
                                        e.printStackTrace();
                                }
                        }
                }
                _outFile.close();
        }

        /**
         * uses dropFunctions.sql
		 * Drop functions, triggers and tables. 
		 * Close all the connections.
         * @throws Exception
         */
        private static void endMain() throws Exception {
			
                int ev;
				ev = _rt.exec("psql -hdbcourse public -f " + _submission_path +"/df.sql").waitFor();
                ev = _rt.exec("psql -hdbcourse public -f " + _submission_path +"/DropTables.sql").waitFor();
                if (ev!=0){
                    System.out.println("Problem with dropTables.sql.");
                    _outFile.write("\tFT000002\n");
                    throw new FileNotFoundException();
                }                              
                System.out.println("removing files:");
                execCmd("rm -rfv "+_current_check);
        }
		
		/**
         * checks the dropFunctions.sql file.
         * @throws Exception
         */
		private static void dropTest() throws Exception {
			Process p = _rt.exec("psql -hdbcourse public -f " + _current_check +"/dropFunctions.sql");
			if (p.waitFor() == 1){
					System.out.println("Error in dropFunctions.sql file.");
					_outFile.write("\tDF000000\n");                 
			}
			for (int i=0; i < _functionNames.length; i++) 
			{
				String query = "select exists(select * from pg_proc where proname = '" + _functionNames[i] + "');";
				_rs = _stmt.executeQuery(query);
                if (!_rs.next() || _rs.getString(1).equals("t"))
				{
					System.out.println("ERROR in dropFunctions.sql file.");
                    _outFile.write("\tDF000000\n");
				}
			}			
		}
			
        /**
         * init sql and unzip
         * @throws Exception
         */
        private static void InitMain(String zipName) throws Exception {
			int ev;
			_userName = System.getProperty("user.name");
			_rt = Runtime.getRuntime();

			try {
					Class.forName("org.postgresql.Driver");
					_con = DriverManager.getConnection("jdbc:postgresql://dbcourse/public?user=" + _userName);
			}
			catch (ClassNotFoundException e) {
					System.out.println("couldn't find the class name.");
					return;
			}
			catch (SQLException e) {
					System.out.println("unable to connect.");
					return;
			}

			_stmt = _con.createStatement();

			execCmd("rm -rfv "+_current_check);
			
			int badZip = 0;
			System.out.println("unziping files:");
			if(cheakZipFiles(zipName)==0){
				badZip = 1;				
			}

			File f = new File(zipName);
			f.renameTo(new File( zipName.replace(" ","_")));
			execCmd("unzip " + zipName.replace(" ", "_") + " -d " + _current_check);
			
			if ((badZip==1) && new File(_current_check + "/CloseCustomer.sql").exists())
			{
				ev = _rt.exec("cp " + _current_check + "/CloseCustomer.sql " + _current_check + "/closeCustomer.sql").waitFor();
				if (ev!=0){
					System.out.println("Problem with DropFunctions.sql tabels.");
					_outFile.write("\tFT000002 Problem with CloseCustomer.sql.\n");
					throw new FileNotFoundException();
				}
				badZip = 0;
			}
			
			if ((badZip==1) && new File(_current_check + "/checkMySavings.java").exists())
			{
				ev = _rt.exec("cp " + _current_check + "/checkMySavings.java " + _current_check + "/CheckMySavings.java").waitFor();
				if (ev!=0){
					System.out.println("Problem with checkMySavings.java tabels.");
					_outFile.write("\tFT000002 Problem with checkMySavings.java.\n");
					throw new FileNotFoundException();
				}
				badZip = 0;
			}
			if (badZip == 1)
			{
				_outFile.write("\tFT000002\n");
				throw new FileNotFoundException();
			}
                
			ev = _rt.exec("psql -hdbcourse public -f " + _submission_path +"/df.sql").waitFor();
			ev = _rt.exec("psql -hdbcourse public -f " + _submission_path +"/DropTables.sql").waitFor();			
			ev = _rt.exec("psql -hdbcourse public -f " + _submission_path + "/createEx6Tables.sql").waitFor();
			if (ev!=0){
					System.out.println("Problem with createEx6Tables.sql tabels.");
					_outFile.write("\tFT000002 Problem with createEx6Tables.sql tabels.\n");
					throw new FileNotFoundException();
			}

			if (new File(_current_check + "/helperFunctions.sql").exists())
			{
				ev = _rt.exec("psql -hdbcourse public -f " + _current_check + "/helperFunctions.sql").waitFor();
				if (ev!=0){
					System.out.println("Problem with helperFunctions.sql tabels.");
					_outFile.write("\tFT000002 Problem with helperFunctions.sql.\n");
					throw new FileNotFoundException();
				}
			}
        }
		
        /**
         *
         * @param zipName
         * @return 1 if the zip is ok
         */
        private static int cheakZipFiles(String zipName) throws Exception{
        	int inList;
			_dropExist = true;
        	String[] shouldBe = _zipFileNames.split(" ");
            System.out.println("zipName:" + zipName);
        	ZipFile zipFile = new ZipFile(zipName);
            Enumeration zipEntries = zipFile.entries();

            for(int i=0; i < shouldBe.length; i++ ){
                if (zipFile.getEntry(shouldBe[i])==null)
                {

					if (shouldBe[i].equals("dropFunctions.sql"))
					{
						_outFile.write("\tFTR00007 " + "\n");
						_dropExist = false;
						return 1;
					}
					zipFile.close();
					_outFile.write("\tFTR00001 " + shouldBe[i] + "\n");
					return 0;
                }
            }

            zipFile.close();
            return 1;
		}

		/**
         * this func is called before a test
         * @throws Exception
         */
        private static void InitTest(String fileName) throws Exception{
			if(fileName.length() >0 ){
				Process p = _rt.exec("psql -hdbcourse public -f "+ _current_check + "/" + fileName);
				if (p.waitFor() == 1){
					System.out.println("Problem with "+fileName);
					_outFile.write("\tFT000003\n");
					throw new FileNotFoundException();
				}
				System.out.println("start test "+ fileName);
			}
        }

        /**
         * test 1 - tests newUser function
         * @throws Exception
         */
        private static void Q1test() throws Exception{
			String fileName = "newCustomer.sql";
			try {						
				InitTest(fileName);
				cheakFunct("select * from newCustomer(1,'shani', 'pass', -5);", 1,"q1t1");
				cheakFunct("select * from newCustomer(1,'shani', 'pass', -5);", -1,"q1t2");
				cheakFunct("select Balance from AccountBalance where AccountNum = 1;", 0,"q1t3");
				cheakFunct("select * from newCustomer(7,'miki', 'mouse', -50);", 2,"q1t4");
				cheakFunct("select * from newCustomer(100,'dog', 'cool', -10);", 3,"q1t5");
			} catch (Exception e) {
				//should only happen in output file exception
				System.out.println("got exception in " + fileName);
				e.printStackTrace();
			}
        }

        /**
         * runs an sql func and cheak results of type int.
         * @param run
         * @param expectedRes 
         * @param errorCode
         * @throws IOException
         */
        private static void cheakFunct(String run, int expectedRes, String errorCode) throws IOException {
			try {
				_rs = _stmt.executeQuery(run);
				
				if (!_rs.next() || _rs.getInt(1)!= expectedRes){
						
						System.out.println("ERROR in "+run+" got: "+_rs.getInt(1) + " writing: " +errorCode);						
						_outFile.write("\t"+errorCode + "\n");
				}				
			} catch (Exception e) {
				_outFile.write("\t"+ errorCode +"\n");
				e.printStackTrace();
			}

        }
		
		/**
         * runs an sql func and cheak results of type String.
         * @param run
         * @param expectedRes 
         * @param errorCode
         * @throws IOException
         */
        private static void cheakFunct2(String run, String expectedRes, String errorCode) throws IOException {
			try {
				_rs = _stmt.executeQuery(run);

				if (!_rs.next() || !_rs.getString(1).equals(expectedRes)){
						
						System.out.println("ERROR in "+run+" got: "+_rs.getString(1)+" writing: " +errorCode);						
						_outFile.write("\t"+ errorCode +"\n");
				}
				
			} catch (Exception e) {
				_outFile.write("\t"+ errorCode + "\n");
				e.printStackTrace();
			}
        }

		/**
         * runs an sql func and cheak results of type Date.
         * @param run
         * @param expectedRes 
         * @param errorCode
         * @throws IOException
         */
         private static void cheakFunct3(String run, Date expectedRes, String errorCode) throws IOException {
			try {
				_rs = _stmt.executeQuery(run);
				_rs.next();                        
				Date date = _rs.getDate(1);				
				
				if (!date.toLocaleString().equals(expectedRes.toLocaleString()))
				{
					_outFile.write("\t"+errorCode+"\n");
				}
			} catch (Exception e) {
				_outFile.write("\t"+errorCode+"\n");
				e.printStackTrace();
			}
        }

	/**
	 * test 2 - tests closeUser function
	 * @throws Exception
	 */
	private static void Q2test() throws Exception{
		String fileName = "closeCustomer.sql";
		try {
			
			_rt.exec("psql -hdbcourse public -f "+ _submission_path + "/t2h.sql").waitFor();
			InitTest(fileName);
			cheakFunct("select * from closeCustomer(7);", 2,"q2t1");
			cheakFunct2("select AccountStatus from Customers where CustomerID = 7;", "close","q2t2");
			cheakFunct("select * from closeCustomer(7);", -1, "q2t3");			
			cheakFunct("select * from newCustomer(1000,'moshe', 'cohen', -30);", 4,"q2t5");
			cheakFunct("select * from newCustomer(7,'miki', 'mouse', -50);", 2,"q2t6");
			cheakFunct("select Balance from AccountBalance where AccountNum = 2;", 0,"q2t7");
			cheakFunct("select * from closeCustomer(105);", -1,"q2t8");
			cheakFunct("select * from closeCustomer(1000);", 4,"q2t1");
		} catch (Exception e) {
			System.out.println("got exception in " + fileName);
			e.printStackTrace();
		}
	}

	/**
	 * tests 3 - doAction function
	 * @throws Exception
	 */
	private static void Q3test() throws Exception{
		String fileName = "doAction.sql";
		try {
			
			_rt.exec("psql -hdbcourse public -f "+ _submission_path + "/t3h.sql").waitFor();
			InitTest(fileName);
			cheakFunct("select * from doAction(100, 'receive', '2015-7-4',100);", 1,"q3t1");
			cheakFunct("select Balance from AccountBalance where AccountNum = 3;", 100,"q3t2");
			cheakFunct("select * from doAction(1000, 'receive', '2015-7-4',100);", -1,"q3t3");
			cheakFunct("select * from doAction(105, 'receive', '2015-7-4',100);", -1,"q3t4");
			cheakFunct("select * from doAction(7, 'payment', '2015-7-4',-25);", 2,"q3t5");
			cheakFunct("select Balance from AccountBalance where AccountNum = 2;", -25,"q3t6");

		} catch (Exception e) {
			System.out.println("got exception in " + fileName);
			e.printStackTrace();
		}
	}

	/**
	 * tests 4 - newSaving function
	 * @throws Exception
	 */
	private static void Q4test() throws Exception{
		String fileName = "newSaving.sql";
		try {

			_rt.exec("psql -hdbcourse public -f "+ _submission_path + "/t4h.sql").waitFor();
			InitTest(fileName);
			cheakFunct("select * from newSaving(100, 50, '2010-1-1',10,0.2);", 1,"q4t1");
			cheakFunct("select Balance from AccountBalance where AccountNum = 3;", 50,"q4t2");
			cheakFunct("select * from newSaving(1000, 50, '2010-1-1',10,0.2);", -1,"q4t3");
			cheakFunct("select * from newSaving(105, 50, '2010-1-1',10,0.2);", -1,"q4t4");
			cheakFunct("select * from newSaving(100, 25, '2012-1-1',5,0.1);", 2,"q4t5");

		} catch (Exception e) {
			System.out.println("got exception in " + fileName);
			e.printStackTrace();
		}
	}

	/**
	 * tests Trigger A
	 * @throws Exception
	 */
	private static void triggerAtest() throws Exception{
		String fileName = "triggerA.sql";
		try {

			_rt.exec("psql -hdbcourse public -f "+ _submission_path + "/tAh.sql").waitFor();
			InitTest(fileName);
			try {
				_rs = _stmt.executeQuery("select * from closeCustomer(7);");
			}
			catch (Exception e)
			{
				//correct exception!
			}
			cheakFunct2("select AccountStatus from Customers where CustomerID = 7;", "open","qAt1");
			_stmt.executeQuery("select * from doAction(7, 'receive', '2015-7-4',100);");
			_rs = _stmt.executeQuery("select * from closeCustomer(7);");
			cheakFunct2("select AccountStatus from Customers where CustomerID = 7;", "close","qAt2");
			cheakFunct("select Amount from Actions where AccountNum = 2 and ActionName='close';", -75, "qAt3");
		} catch (Exception e) {
			System.out.println("got exception in " + fileName);
			_outFile.write("\t"+"Q4B00R01"+"\n");
			e.printStackTrace();
		}
	}

	/**
	 * tests Trigger B
	 * @throws Exception
	 */
	private static void triggerBtest() throws Exception {
		String fileName = "triggerB.sql";
		try {
				InitTest(fileName);
			try {
				_stmt.executeQuery("select * from doAction(1, 'payment', '2015-7-4',-25);");
			}
			catch (Exception e)
			{
				//correct exception!
			}
		   cheakFunct("select Balance from AccountBalance where AccountNum = 1;", 0,"qBt1");
		   try {
				 _stmt.executeQuery("select * from doAction(100, 'receive', '2015-7-4',5);");
		   }
		   catch (Exception e)
		   {

		   }
		   cheakFunct("select Balance from AccountBalance where AccountNum = 3;", 30, "qBt2");

			try {
					_stmt.executeQuery("select * from doAction(1, 'payment', '2015-7-4',-3);");
			}
			catch (Exception e)
			{

			}
			cheakFunct("select Balance from AccountBalance where AccountNum = 1;", -3, "qBt3");
		} catch (Exception e) {
				System.out.println("got exception in " + fileName);
				_outFile.write("\t" + "Q4B00R01" + "\n");
				e.printStackTrace();
		}
	}

	/**
	 * tests Trigger C
	 * @throws Exception
	 */
	private static void triggerCtest() throws Exception{
		String fileName = "triggerC.sql";
		try {
				InitTest(fileName);
				Date currentDate = new Date(_currentYear, _currentMonth, _currentDay);
				_stmt.executeQuery("select * from newSaving(100, 1, '2014-1-1',2,0.1);");
				cheakFunct3("select ActionDate from Actions where AccountNum = 3 and Amount=-1;", currentDate, "qCt1");
				cheakFunct3("select DepositDate from Savings where AccountNum = 3 and Deposit = 1;", currentDate, "qCt2");
		} catch (Exception e) {
				System.out.println("got exception in " + fileName);
				_outFile.write("\t"+"Q4B00R01"+"\n");
				e.printStackTrace();
		}
	}

	/**
	 * tests Trigger D
	 * @throws Exception
	 */
	private static void triggerDtest() throws Exception{
		String fileName = "triggerD.sql";
		try {
				InitTest(fileName);
				_stmt.executeQuery("select * from doAction(100, 'receive', '2015-7-4',20);");
				cheakFunct("select count(AccountNum) from Top10Customers;", 1, "qDt1");
				cheakFunct("select max(AccountNum) from Top10Customers;",3,"qDt2");
		} catch (Exception e) {
				System.out.println("got exception in " + fileName);
				_outFile.write("\t"+"Q4B00R01"+"\n");
				e.printStackTrace();
		}
	}



	/**
	 * tests JDBC
	 * @throws Exception
	 */
	private static void JdbcTest() throws Exception{
		
		_rt.exec("psql -hdbcourse public -f "+ _submission_path + "/df.sql").waitFor();
		_rt.exec("psql -hdbcourse public -f "+ _submission_path + "/jh.sql").waitFor();
		_outFile.close();
		try {
			System.out.println("calling JDBC test");
			execCmd("cp " + _current_check +"/CheckMySavings.java CheckMySavings.java");
			int wasErrors = execCmd("javac CheckMySavings.java JDBC.java");
			wasErrors += execCmd("java -cp /usr/share/java/postgresql.jar:. JDBC");
			execCmd("rm CheckMySavings.java CheckMySavings.class");
			
			if (wasErrors > 0)
			{
				_outFile = new BufferedWriter(new FileWriter("ex6Codes",true));
				_outFile.write("\t"+"JDCB0ERR"+"\n");			
			}
			   
		} catch (Exception e) {
			
			_outFile = new BufferedWriter(new FileWriter("ex6Codes",true));
			_outFile.write("\t"+"JDCB0ERR"+"\n");			
		} 
		
	}
	/**
	 * execute a java class and return the output (the printed one).
	 * @param cmdStr
	 * @return 1 if there were errors, 0 if not.
	 * @throws Exception
	 */
	private static int execCmd(String cmdStr) throws Exception {
		String retOut="";

		_outFile.close();		

		Process proc = _rt.exec(cmdStr);

		_outFile = new BufferedWriter(new FileWriter("ex6Codes",true));

		BufferedReader stdInput = new BufferedReader(new
						InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
						InputStreamReader(proc.getErrorStream()));

		// read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
				retOut = retOut+s+"\n";
		}

		int wasErrors=0;
		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
				retOut = retOut+s+"\n";
				if(s.startsWith("Note")==false) wasErrors=1;
		}
		System.out.print(retOut);
		return wasErrors;	   

	}

	/**
	 * prints file in dir
	 */
	/*
	private static void printFilesInDir() {
			System.out.println();
			System.out.println("print files in dir for test:");
			String files;
			File folder = new File(".");
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++){
					files = listOfFiles[i].getName();
					System.out.println(files);
			}
			System.out.println();

	}*/

}
