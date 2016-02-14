/**
 * Created by aviadle
 */
import java.sql.*;
import java.util.Calendar;

public class CheckMySavings {
    Connection _con;
    Statement _stm;
    ResultSet _res;

    public void init(String username){
        try {
            Class.forName("org.postgresql.Driver");
            _con = DriverManager.getConnection("jdbc:postgresql://dbcourse/public?user=" + username);
            _stm = _con.createStatement();
        } catch (Exception e) {
            System.out.println("Fail to init\nExiting...");
            System.exit(-1);
        }
    }

    public double checkMySavings(int ANum, Date oDate) {
        try {
            _res = _stm.executeQuery("SELECT * FROM savings WHERE accountnum = " + ANum);
			double sumSaving = 0;
            if(!_res.isBeforeFirst()) {
                _res = _stm.executeQuery("SELECT * FROM customers WHERE accountnum = " + ANum);
                if(!_res.isBeforeFirst())
                    throw new NoCustomerException();
                else {
                    _res.next();
                    if(_res.getString("accountstatus").equals("close"))
                        throw new NoCustomerException();
                    return sumSaving;
                }
            }
            while(_res.next()) {
                double numOfYears = getVar("NumOfYears");
		        double depoAmount = getVar("deposit");
		        double interest = getVar("interest");
		        int[] depoArr = getYearMonthDay(getDepoDate());
		        int[] openArr = getYearMonthDay(oDate);

		        if(depoArr[0] + numOfYears > openArr[0] || (depoArr[0] + numOfYears == openArr[0] && depoArr[1] > openArr[1]) ||
		                (depoArr[0] + numOfYears == openArr[0] && depoArr[1] == openArr[1] && depoArr[2] > openArr[2])) {
		            if(openArr[0] - depoArr[0] >= 0 && ((openArr[1] - depoArr[1] > 0) || (openArr[1] - depoArr[1] == 0 && openArr[2] - depoArr[2] >= 0)))
		                sumSaving += depoAmount * (openArr[0] - depoArr[0] + 1);
		            else if(openArr[0] - depoArr[0] > 0)
		                sumSaving += depoAmount * (openArr[0] - depoArr[0]);
		            else throw new Exception();
		        }
		        else {
		            sumSaving += calcInterest(numOfYears, interest, depoAmount);
		        }
            }
            if(sumSaving != 0)	return sumSaving;
            else throw new Exception();
        }
        catch (SQLException e)
        {
            System.out.println("Somthing went wrong when trying to get data from DB\nExiting...");
            return -1;
        }
        catch (NoCustomerException e) {
            System.out.println("Customer does not exists or close");
            return -1;
        }
        catch (Exception e) {
            System.out.println("Somthing went wrong when trying to calculate your interest\nExiting...");
            return -1;
        }
    }

    private class NoCustomerException extends Exception {}
	
	/**
	* calculate the interest recursevly, according to the formula given on ex description
	*/
    private double calcInterest(double totalYears, double interest, double depoAmount) {
        if(totalYears == 1)
        {
            return depoAmount * (1 + interest);
        }
        else
        {
            return (depoAmount + calcInterest(totalYears - 1, interest, depoAmount)) * (1 + interest);
        }
    }


    private int[] getYearMonthDay(Date date)
    {
        int[] dateArr = new int[3];
        Calendar calDepoDate = Calendar.getInstance();
        calDepoDate.setTime(date);
        dateArr[0] = calDepoDate.get(Calendar.YEAR);
        dateArr[1] = calDepoDate.get(Calendar.MONTH) + 1;
        dateArr[2] = calDepoDate.get(Calendar.DAY_OF_MONTH);
        return dateArr;
    }

    private double getVar(String colName) throws SQLException {
        return _res.getDouble(colName);
    }

    private Date getDepoDate() throws SQLException {
        return _res.getDate("DepositDate");
    }

    public void close()
    {
        try {
            _stm.close();
            _res.close();
            _con.close();
        } catch (SQLException e) {
            System.out.println("Fail to close\nExiting...");
            System.exit(-1);
        }
    }
}

