package ucc.com.safetyapplication;

/**
 * Created by Damian on 9/03/2016.
 */
public class Worker {
    public String name, phoneNumber, employeeId;
    public boolean inDanger;

    public Worker() {

    }

    public Worker(String name, String phoneNumber, String employeeId, boolean inDanger) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.employeeId = employeeId;
        this.inDanger = inDanger;
    }

    public String getName() { return name; }

    public String getPhoneNumber() {
            return phoneNumber;
        }

    public String getEmployeeId() {
            return employeeId;
        }

    public boolean getDanger() { return inDanger; }
}
